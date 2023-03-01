package github.josedoce.anotador.handler;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import github.josedoce.anotador.R;

public class DialogDecision extends Dialog implements View.OnClickListener {
    private OnAgree onAgree;
    public DialogDecision(@NonNull Context context, String title, String message){
        super(context);
        setContentView(R.layout.decision_dialog);
        TextView tv_header = findViewById(R.id.tv_header);
        TextView tv_info = findViewById(R.id.tv_info);
        tv_header.setText(title);
        tv_info.setText(message);
        Button btn_agree = findViewById(R.id.btn_agree);
        Button btn_cancel = findViewById(R.id.btn_cancel);
        btn_agree.setOnClickListener((view)->{
            if(onAgree == null) return;
            onAgree.onAgree();
            dismiss();
        });
        btn_cancel.setOnClickListener((view)->{
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
