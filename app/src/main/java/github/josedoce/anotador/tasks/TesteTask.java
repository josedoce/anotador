package github.josedoce.anotador.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

public class TesteTask implements Runnable {
    private OnFinishedTask onFinishedTask;
    private UiUpdater uiUpdater;
    private Context context;
    private Handler mainHandler;
    private TextView textView;

    public TesteTask(Context context, TextView textView) {
        this.textView = textView;
        this.context = context;
        mainHandler = new Handler(context.getMainLooper());
    }

    public interface OnFinishedTask {
        void onFinished();
    }
    public void setOnFinishedTask(OnFinishedTask onFinishedTask) {
        this.onFinishedTask = onFinishedTask;
    }

    public interface UiUpdater {
        void runOnUiThread(Runnable runnable);
    }

    public void setUiUpdater(UiUpdater uiUpdater){
        this.uiUpdater = uiUpdater;
    }

    @Override
    public void run() {
        for(int i = 0; i < 100; i++){
            int finalI = i;
            sleep(500);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    textView.setText(String.format("Anotador - %d", finalI));
                }
            });
        }

        mainHandler.post(()->this.onFinishedTask.onFinished());
    }

    private void sleep(int milis){
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
