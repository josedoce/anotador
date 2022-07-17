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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen_layout);
        SplashScreen.handler(new SplashScreen(this));
    }

    private static class SplashScreen implements Runnable {
        private final DBUser dbUser;
        public static void handler(SplashScreen splashScreen){
            new Handler().postDelayed(splashScreen, 3000);
        }

        Context ctx;
        public SplashScreen(Context ctx){
            this.ctx = ctx;
            DBHelper db = new DBHelper(ctx);
            dbUser = new DBUser(db);
        }

        @Override
        public void run() {
            Cursor c = this.dbUser.selectAll();
            c.moveToFirst();
            Intent intent;

            if(c.getCount() > 0){
                intent = new Intent(ctx, SignInActivity.class);
            }else{
                intent = new Intent(ctx, SignUpActivity.class);
            }
            this.ctx.startActivity(intent);
        }

    }
}