package github.josedoce.anotador.views;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import github.josedoce.anotador.R;
import github.josedoce.anotador.database.DBHelper;

import github.josedoce.anotador.database.DBUser;
import github.josedoce.anotador.handler.Alert;
import github.josedoce.anotador.handler.DialogInfo;
import github.josedoce.anotador.handler.DialogProgress;
import github.josedoce.anotador.model.User;
import github.josedoce.anotador.tasks.TesteTask;
import github.josedoce.anotador.utils.PreferenceManager;

public class SignInActivity extends AppCompatActivity {
    private final Handler handler = new Handler();
    private ExecutorService executorService;
    private Button bt_doLogin;
    private EditText et_login, et_password;

    private DBUser dbUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.signin_layout);
        et_login = findViewById(R.id.et_login);
        et_password = findViewById(R.id.et_password);
        bt_doLogin = findViewById(R.id.bt_doLogin);

        DBHelper db = new DBHelper(this);
        dbUser = new DBUser(db);

        bt_doLogin.setOnClickListener((view)->{
            doLogin();
        });
    }

    @Override
    public void onBackPressed() {
        //https://www.codegrepper.com/code-examples/java/how+to+close+android+app+programmatically
        finishAffinity();
        System.exit(0);
    }

    private void openDialog(String title, String message){
        handler.post(()->{
            DialogInfo dialogInfo = new DialogInfo(this, title, message, DialogInfo.INFO);
            dialogInfo.show();
        });
    }

    private void doLogin(){
        String login = et_login.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        final boolean isLoginEmpty = login.isEmpty();
        final boolean isPasswordEmpty = password.isEmpty();

        if(isLoginEmpty || isPasswordEmpty){
            String alertMessage = "";
            if(isLoginEmpty)
                alertMessage = "O login é necessário.";
            if(isPasswordEmpty)
                alertMessage = "A senha é necessária.";
            if(isLoginEmpty && isPasswordEmpty)
                alertMessage = "Os campos login e senha são necessários.";

            DialogInfo dialogInfo = new DialogInfo(this, "Campo vazio", alertMessage, DialogInfo.INFO);
            dialogInfo.show();
            return;
        }
        TesteTask tt = new TesteTask();
        DialogProgress dialogProgress = new DialogProgress(this);
        tt.setOnStartedTask(()->handler.post(dialogProgress::show));
        tt.setTaskBody(()->{
            Cursor cursor = dbUser.selectByLogin(login);
            cursor.moveToFirst();
            if(cursor.getCount() == 1){
                User user = new User(cursor);
                if(!user.verify(password)){
                    openDialog("Erro de entrada","Senha errada, tente de novo.");
                    return;
                }
                PreferenceManager pm = PreferenceManager.getInstance(getApplicationContext());
                pm.putString("username", user.getLogin());
                pm.putString("userpassword", password);
                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                handler.post(()->{
                    startActivity(intent);
                    Toast.makeText(this, "Bem vindo "+user.getLogin(), Toast.LENGTH_LONG).show();
                });
            }else{
                openDialog("Erro de entrada", "Usuário inválido ou inexistente.");
            }

        });
        tt.setOnFinishedTask(()->{
            handler.post(dialogProgress::dismiss);
        });
        setTask(tt);
    }

    private void setTask(TesteTask tt) {
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(tt);
        executorService.shutdown();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bt_doLogin = null;
        et_login = null;
        et_password = null;
    }
}
