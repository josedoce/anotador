package github.josedoce.anotador.handler;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import github.josedoce.anotador.R;

import androidx.annotation.NonNull;

public class DialogActions extends Dialog implements View.OnClickListener {
    private OnAgree onAgree;
    public DialogActions(@NonNull Context context, String title, String message){
        super(context);
        setContentView(R.layout.actions_dialog);
        TextView tv_header = findViewById(R.id.tv_header);
        TextView tv_info = findViewById(R.id.tv_info);
        tv_header.setText(title);
        tv_info.setText(message);
        Button btn_agree = findViewById(R.id.btn_agree);
        btn_agree.setOnClickListener((view)->{
            if(onAgree == null) return;
            onAgree.onAgree();
            dismiss();
        });
    }
    public interface OnAgree {
        void onAgree();
    }
    public void setOnAgree(OnAgree onAgree){
        this.onAgree = onAgree;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return false;
    }

    @Override
    public void onClick(View v) {}
}
