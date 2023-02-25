package github.josedoce.anotador.handler;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import github.josedoce.anotador.R;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;

import java.util.ArrayList;
import java.util.List;

public class DialogAddField extends Dialog implements View.OnClickListener {
    private OnCreateField onCreateField;
    private Field f1;
    @SuppressLint("SetTextI18n")
    public DialogAddField(@NonNull Context context) {
        super(context);
        f1 = new Field();
        setContentView(R.layout.new_field_dialog);

        Button btn_fechar = findViewById(R.id.btn_fechar);
        Button btn_new_field = findViewById(R.id.btn_new_field);
        EditText et_field_label = findViewById(R.id.et_field_label);
        TextView tv_info = findViewById(R.id.tv_info);

        List<String> dataSpiner = new ArrayList<>();
        dataSpiner.add("Texto");
        dataSpiner.add("Email");
        dataSpiner.add("Numérico");
        dataSpiner.add("Link");
        dataSpiner.add("Senha");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, dataSpiner);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AppCompatSpinner spinner = findViewById(R.id.app_compat_spinner);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int inputType;
                switch (dataSpiner.get(position)){
                    case "Email":
                        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
                        break;
                    case "Numérico":
                        inputType = InputType.TYPE_CLASS_NUMBER;
                        break;
                    case "Link":
                        inputType = InputType.TYPE_TEXT_VARIATION_URI;
                        break;
                    case "Senha":
                        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD;
                        break;
                    default:
                        inputType = InputType.TYPE_CLASS_TEXT;
                }
                f1.setType(inputType);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        btn_fechar.setOnClickListener(this);

        btn_new_field.setOnClickListener((view)->{
            if(onCreateField == null) return;
            String textLabel = et_field_label.getText().toString();
            if(textLabel.trim().isEmpty()) {
                setInfo(tv_info, "Existe campo sem nome ? \uD83E\uDD14 ");
                return;
            }
            f1.setLabel(textLabel);
            onCreateField.make(f1);
            dismiss();
        });
    }

    public static class Field {
        private int type;
        private String label;

        public int getType() {
            return type;
        }
        public void setType(int type) {
            this.type = type;
        }
        public String getLabel() {
            return label;
        }
        public void setLabel(String label) {
            this.label = label;
        }
    }

    @FunctionalInterface
    public interface OnCreateField {
        void make(Field field);
    }

    public void setOnCreateField(OnCreateField onCreateField){
        this.onCreateField = onCreateField;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_fechar) {
            dismiss();
        }
    }

    private void setInfo(TextView textView, String message){
        textView.setText(message);
        textView.setVisibility(View.VISIBLE);
        new Handler().postDelayed(()->{
            textView.setVisibility(View.GONE);
        }, 10000);
    }
}
