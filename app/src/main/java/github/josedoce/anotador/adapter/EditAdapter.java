package github.josedoce.anotador.adapter;


import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import github.josedoce.anotador.R;
import github.josedoce.anotador.model.Field;

public class EditAdapter extends ArrayAdapter<Field> {
    private Context context;
    private List<Field> fieldList;
    private OnItemSelectedToRemove onItemSelectedToRemove;

    public EditAdapter(@NonNull Context context, List<Field> fieldList){
        super(context, R.layout.row_detail, fieldList);
        this.context = context;
        this.fieldList = fieldList;
    }

    @Override
    public int getCount() {
        return this.fieldList.size();
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Field field = fieldList.get(position);
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View v = inflater.inflate(R.layout.row_edit, null, false);
        EditText et_field = v.findViewById(R.id.et_field);
        TextView tv_label_name = v.findViewById(R.id.tv_label_name);
        ImageButton bt_delete = v.findViewById(R.id.bt_delete);
        et_field.setText(field.getValue());
        et_field.setInputType(field.getType());
        tv_label_name.setText(field.getLabel());

        et_field.addTextChangedListener(new TextWatcher() {
            private Timer timer = new Timer();
            private final long DELAY = 100;
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Cancela o temporizador anterior se ele ainda estiver em execução
                timer.cancel();
                // Inicia um novo temporizador para executar a ação após o tempo de espera
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        field.setValue(s.toString());
                    }
                }, DELAY);
            }
        });

        bt_delete.setOnClickListener((view)->{
            if(onItemSelectedToRemove == null) return;
            onItemSelectedToRemove.remove(field);
            this.notifyDataSetChanged();
        });
        return v;
    }

    public interface OnItemSelectedToRemove {
        void remove(Field field);
    }

    public void setOnItemSelectedToRemove(OnItemSelectedToRemove onItemSelectedToRemove){
        this.onItemSelectedToRemove = onItemSelectedToRemove;
    }

}
