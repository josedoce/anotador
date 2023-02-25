package github.josedoce.anotador.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import github.josedoce.anotador.R;
import github.josedoce.anotador.database.DBHelper;
import github.josedoce.anotador.database.DBUser;
import github.josedoce.anotador.tasks.TesteTask;

public class MainActivity extends AppCompatActivity {
    private DBUser dbUser;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.splash_screen_layout);
          /*
        TextView textView = findViewById(R.id.logo);
        //setContentView(R.layout.detail_annotation);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        TesteTask testeTask = new TesteTask(getApplicationContext(), textView);
        testeTask.setOnFinishedTask(() -> {
            textView.setText("Finalizou.");
        });

        //runOnUiThread() use em momentos apropriados, como em resposta a um evento do usuário ou a uma notificação.

        testeTask.setUiUpdater(new TesteTask.UiUpdater() {
            @Override
            public void runOnUiThread(Runnable runnable) {
                MainActivity.this.runOnUiThread(runnable);
            }
        });
        executor.execute(testeTask);
        executor.shutdown();
        */

        DBHelper db = new DBHelper(this);
        this.dbUser = new DBUser(db);

        //modificação no postDelayed
        handler.postDelayed(()->{
            Cursor c = this.dbUser.selectAll();
            c.moveToFirst();
            Intent intent;
            if(c.getCount() > 0){
                intent = new Intent(this, SignInActivity.class);
            }else{
                intent = new Intent(this, SignUpActivity.class);
            }

            configureApp();

            this.startActivity(intent);

            //this.startActivity(new Intent(this, DetailActivity.class));
        }, 3000);


    }

    private void configureApp(){
        //configurações
    }
}