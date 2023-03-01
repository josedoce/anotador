package github.josedoce.anotador.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.favre.lib.crypto.bcrypt.BCrypt;
import github.josedoce.anotador.R;
import github.josedoce.anotador.database.DBHelper;
import github.josedoce.anotador.database.DBUser;
import github.josedoce.anotador.handler.Alert;
import github.josedoce.anotador.handler.DialogInfo;
import github.josedoce.anotador.handler.DialogProgress;
import github.josedoce.anotador.tasks.TesteTask;
import github.josedoce.anotador.utils.Dio;
import github.josedoce.anotador.utils.PreferenceManager;

public class SignUpActivity extends AppCompatActivity {
    private ExecutorService executorService;
    private Handler handler = new Handler();
    private static final int STORAGE_PERMISSION_CODE_TO_READ_AND_WRITE = 101;
    private EditText et_login, et_password;
    private TextView tv_info, tv_import;
    private Button bt_doRegister;
    private Dio dio;
    private DBUser dbUser;
    private DBHelper db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.signup_layout);

        et_login = findViewById(R.id.et_login);
        et_password = findViewById(R.id.et_password);
        tv_info = findViewById(R.id.tv_info);
        tv_import = findViewById(R.id.tv_import);
        bt_doRegister = findViewById(R.id.bt_doRegister);

        db = new DBHelper(this);
        dbUser = new DBUser(db);
        dio = new Dio(this);

        tv_import.setOnClickListener((view)->{
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        });

        bt_doRegister.setOnClickListener((view)->{
            doRegister();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE_TO_READ_AND_WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showDialogActions();
            }
        }else{
            Toast.makeText(this, "Permissão negada, sinto muito.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
    }

    public void doRegister(){
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

        DialogProgress dialogProgress = new DialogProgress(this);
        TesteTask tt = new TesteTask();
        tt.setOnStartedTask(()->handler.post(dialogProgress::show));
        tt.setTaskBody(()->{
            Cursor cursor = dbUser.selectByLogin(login);
            cursor.moveToFirst();
            if(cursor.getCount() == 0){
                String hash = BCrypt.withDefaults()
                        .hashToString(12, password.toCharArray());
                long res = dbUser.create(login, hash);
                if(res > 0){
                    PreferenceManager pm = PreferenceManager.getInstance(getApplicationContext());
                    pm.putString("username", login);
                    pm.putString("userpassword", password);
                    Intent intent = new Intent(this, HomeActivity.class);
                    handler.post(()->{
                        startActivity(intent);
                        Toast.makeText(this, "Bem vindo "+login, Toast.LENGTH_LONG).show();
                    });
                }else{
                    openDialog("Erro de entrada", "Não foi possivel registrar, tente de novo.");
                }
            }else {
                openDialog("Erro de entrada", "Usuário já existente.");
            }
        });
        tt.setOnFinishedTask(()->handler.post(dialogProgress::dismiss));
        setTask(tt);
    }

    private void setTask(TesteTask tt) {
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(tt);
        executorService.shutdown();
    }

    private void openDialog(String title, String message){
        handler.post(()->{
            DialogInfo dialogInfo = new DialogInfo(this, title, message, DialogInfo.INFO);
            dialogInfo.show();
        });
    }

    private void showDialogActions(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage("Deseja mesmo importar os dados do dispositivo?")
                .setPositiveButton("sim", (dialog, which) -> {
                    dio.dioImport(db);
                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                })
                .setNegativeButton("não",((dialog, which) -> { dialog.dismiss(); }));
        builder.show();
    }

    public void checkPermission(String permission)
    {
        //check if has permissions
        if (ContextCompat.checkSelfPermission(SignUpActivity.this, permission) == PackageManager.PERMISSION_GRANTED) {
            showDialogActions();
        }else{
            requestStoragePermission();
        }
    }

    private void requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("Permição é necessária")
                    .setMessage("Precisamos de acesso ao armazenamento para importar os dados.")
                    .setPositiveButton("requisitar",(dialog, which) -> {
                        ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE_TO_READ_AND_WRITE);
                    })
                    .setNegativeButton("negar",(dialog, which) -> {
                        dialog.dismiss();
                    }).create().show();
        }else{
            ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE_TO_READ_AND_WRITE);
        }
    }
}
