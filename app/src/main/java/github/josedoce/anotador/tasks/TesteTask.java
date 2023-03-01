package github.josedoce.anotador.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class TesteTask implements Runnable {
    private OnStartedTask onStartedTask;
    private TaskBody taskBody;
    private OnFinishedTask onFinishedTask;

    public interface OnStartedTask {
        void onStarted();
    }

    public interface TaskBody {
        void content();
    }

    public interface OnFinishedTask {
        void onFinished();
    }

    public void setOnStartedTask(OnStartedTask onStartedTask){
        this.onStartedTask = onStartedTask;
    }

    public void setTaskBody(TaskBody taskBody){
        this.taskBody = taskBody;
    }

    public void setOnFinishedTask(OnFinishedTask onFinishedTask) {
        this.onFinishedTask = onFinishedTask;
    }

    @Override
    public void run() {
        if(onStartedTask!=null){
            onStartedTask.onStarted();
        }
        taskBody.content();
        if(onFinishedTask!=null){
            onFinishedTask.onFinished();
        }
    }

}
