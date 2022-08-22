package github.josedoce.anotador.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
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

import at.favre.lib.crypto.bcrypt.BCrypt;
import github.josedoce.anotador.R;
import github.josedoce.anotador.context.AnotadorContext;
import github.josedoce.anotador.database.DBHelper;
import github.josedoce.anotador.database.DBUser;
import github.josedoce.anotador.handler.Alert;
import github.josedoce.anotador.utils.Dio;

public class SignUpActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE_TO_READ_AND_WRITE = 101;
    private final ViewHolder mViewHolder = new ViewHolder();
    private Dio dio;
    private DBUser dbUser;
    private DBHelper db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        mViewHolder.et_login = findViewById(R.id.et_login);
        mViewHolder.et_password = findViewById(R.id.et_password);
        mViewHolder.tv_info = findViewById(R.id.tv_info);
        mViewHolder.tv_import = findViewById(R.id.tv_import);
        mViewHolder.bt_doRegister = findViewById(R.id.bt_doRegister);

        db = new DBHelper(this);
        dbUser = new DBUser(db);
        dio = new Dio(this);

        mViewHolder.tv_import.setOnClickListener((view)->{
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        });

        mViewHolder.bt_doRegister.setOnClickListener((view)->{
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
        if(cursor.getCount() == 0){
            String hash = BCrypt.withDefaults()
                    .hashToString(12, password.toCharArray());
            long res = dbUser.create(login, hash);
            if(res > 0){
                Toast.makeText(this, "Registrado com sucesso!", Toast.LENGTH_LONG).show();
                AnotadorContext auth = (AnotadorContext) getApplicationContext();
                auth.setUser(password);
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(this, "Não foi possivel registrar!", Toast.LENGTH_LONG).show();
            }
        }else {
            Alert alert = new Alert(mViewHolder.tv_info, "Ação impossivel.");
            Alert.handler(alert, 10000);
        }
    }

    public static class ViewHolder {
        Button bt_doRegister;
        EditText et_login;
        EditText et_password;
        TextView tv_info, tv_import;
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
