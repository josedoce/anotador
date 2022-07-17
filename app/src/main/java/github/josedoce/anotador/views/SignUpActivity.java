package github.josedoce.anotador.views;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import at.favre.lib.crypto.bcrypt.BCrypt;
import github.josedoce.anotador.R;
import github.josedoce.anotador.database.DBHelper;
import github.josedoce.anotador.database.DBUser;
import github.josedoce.anotador.handler.Alert;

public class SignUpActivity extends AppCompatActivity {
    private final ViewHolder mViewHolder = new ViewHolder();
    private DBUser dbUser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        mViewHolder.et_login = findViewById(R.id.et_login);
        mViewHolder.et_password = findViewById(R.id.et_password);
        mViewHolder.tv_info = findViewById(R.id.tv_info);
        mViewHolder.bt_doRegister = findViewById(R.id.bt_doRegister);

        DBHelper db = new DBHelper(this);
        dbUser = new DBUser(db);

        mViewHolder.bt_doRegister.setOnClickListener((view)->{
            doRegister();
        });
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
        TextView tv_info;
    }
}
