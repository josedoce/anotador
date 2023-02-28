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

public class DialogInfo extends Dialog implements View.OnClickListener {
    public static final int INFO = 0;
    public static final int ERROR = 1;
    public static final int SUCCESS = 2;

    @SuppressLint("SetTextI18n")
    public DialogInfo(@NonNull Context context, String title, String message, int type) {
        super(context);
        setContentView(R.layout.info_dialog);
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setDimAmount(0.7f);
        }

        Button btn_agree = findViewById(R.id.btn_agree);
        TextView tv_info = findViewById(R.id.tv_info);
        TextView tv_header = findViewById(R.id.tv_header);
        tv_header.setText(title);
        tv_info.setText(message);

        btn_agree.setOnClickListener(this);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_agree) {
            dismiss();
        }
    }
}