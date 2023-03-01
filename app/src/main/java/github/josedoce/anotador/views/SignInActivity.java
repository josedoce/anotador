package github.josedoce.anotador.views;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import github.josedoce.anotador.R;
import github.josedoce.anotador.database.DBHelper;

import github.josedoce.anotador.database.DBUser;
import github.josedoce.anotador.handler.Alert;
import github.josedoce.anotador.model.User;
import github.josedoce.anotador.utils.PreferenceManager;

public class SignInActivity extends AppCompatActivity {
    private final ViewHolder mViewHolder = new ViewHolder();
    private DBUser dbUser;

    public static class ViewHolder {
        Button bt_doLogin;
        EditText et_login;
        EditText et_password;
        TextView tv_info;
        ProgressBar pb_signin;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.signin_layout);
        mViewHolder.et_login = findViewById(R.id.et_login);
        mViewHolder.et_password = findViewById(R.id.et_password);
        mViewHolder.tv_info = findViewById(R.id.tv_info);
        mViewHolder.bt_doLogin = findViewById(R.id.bt_doLogin);
        mViewHolder.pb_signin = findViewById(R.id.pb_signin);

        DBHelper db = new DBHelper(this);
        dbUser = new DBUser(db);

        mViewHolder.bt_doLogin.setOnClickListener((view)->{
            mViewHolder.bt_doLogin.setVisibility(View.GONE);
            mViewHolder.pb_signin.setVisibility(View.VISIBLE);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    doLogin();
                    mViewHolder.bt_doLogin.setVisibility(View.VISIBLE);
                    mViewHolder.pb_signin.setVisibility(View.GONE);
                }
            });
        });
    }

    @Override
    public void onBackPressed() {
        //https://www.codegrepper.com/code-examples/java/how+to+close+android+app+programmatically
        finishAffinity();
        System.exit(0);
    }

    private void doLogin(){
        String login = mViewHolder.et_login.getText().toString().trim();
        String password = mViewHolder.et_password.getText().toString().trim();
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

            Alert alert = new Alert(mViewHolder.tv_info, alertMessage);
            Alert.handler(alert, 10000);
            return;
        }
        Cursor cursor = dbUser.selectByLogin(login);
        cursor.moveToFirst();
        if(cursor.getCount() == 1){
            User user = new User(cursor);
            if(!user.verify(password)){
                Alert alert = new Alert(mViewHolder.tv_info, "Senha errada, tente de novo.");
                Alert.handler(alert, 10000);
                return;
            }
            PreferenceManager pm = PreferenceManager.getInstance(getApplicationContext());
            pm.putString("username", user.getLogin());
            pm.putString("userpassword", password);
            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Bem vindo "+user.getLogin(), Toast.LENGTH_LONG).show();
        }else{
            Alert alert = new Alert(mViewHolder.tv_info, "Usuário inválido ou inexistente.");
            Alert.handler(alert, 10000);
        }
    }
}
