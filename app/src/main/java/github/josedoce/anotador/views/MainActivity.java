package github.josedoce.anotador.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;

import github.josedoce.anotador.R;
import github.josedoce.anotador.database.DBHelper;
import github.josedoce.anotador.database.DBUser;

public class MainActivity extends AppCompatActivity {
    private final DBUser dbUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen_layout);
    
        DBHelper db = new DBHelper(this);
        this.dbUser = new DBUser(db);
        Intent intent;
        //modificação no postDelayed
        new Handler().postDelayed(()->{
            Cursor c = this.dbUser.selectAll();
            c.moveToFirst();
            
            if(c.getCount() > 0){
                intent = new Intent(this, SignInActivity.class);
            }else{
                intent = new Intent(this, SignUpActivity.class);
            }
            this.startActivity(intent);
        }, 3000);
    }
}