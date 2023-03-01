package github.josedoce.anotador.handler;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import github.josedoce.anotador.R;

public class DialogProgress extends Dialog implements View.OnClickListener {
    public static final int INFO = 0;
    public static final int ERROR = 1;
    public static final int SUCCESS = 2;

    public DialogProgress(@NonNull Context context) {
        super(context);
        setContentView(R.layout.loading);
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setDimAmount(0.7f);
        }

    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return false;
    }

    @Override
    public void onClick(View v) {}

    @Override
    public void onBackPressed() {}
}
