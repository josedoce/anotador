package github.josedoce.anotador.handler;

import android.os.Handler;
import android.widget.TextView;

public class Alert implements Runnable {

    private final TextView tv;
    private final String text;

    public Alert(TextView tv, String text){
        this.tv = tv;
        this.text = text;
    }


    public static void handler(Alert alert, int delay){
        alert.tv.setText(alert.text);
        new Handler().postDelayed(alert, delay);
    }

    @Override
    public void run() {
        this.tv.setText("");
    }
}