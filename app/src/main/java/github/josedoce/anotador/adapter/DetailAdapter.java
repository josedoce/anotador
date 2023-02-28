package github.josedoce.anotador.adapter;


import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.InputType;
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

import github.josedoce.anotador.R;
import github.josedoce.anotador.model.Annotation;
import github.josedoce.anotador.model.Field;

public class DetailAdapter extends ArrayAdapter<Field> {
    private Context context;
    private List<Field> fieldList;

    public DetailAdapter(@NonNull Context context, List<Field> fieldList){
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
        View v = inflater.inflate(R.layout.row_detail, null, false);
        TextView tv_field = v.findViewById(R.id.tv_field);
        ImageButton bt_function = v.findViewById(R.id.bt_function);

        bt_function.setVisibility(View.GONE);
        if(InputType.TYPE_TEXT_VARIATION_PASSWORD == field.getType()){
            bt_function.setOnClickListener((view)->{
                showAndHidePassword(bt_function, tv_field);
            });
            bt_function.setVisibility(View.VISIBLE);
        }

        tv_field.setOnClickListener((view)->{
            copyText(tv_field);
        });

        tv_field.setText(field.getValue());
        tv_field.setInputType(field.getType());
        return v;
    }

    private void copyText(TextView textView){
        String text = textView.getText().toString();
        if(!text.isEmpty()){
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("key", text);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(context, "Copiado!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(context, "Não foi possivel copiar.", Toast.LENGTH_LONG).show();
        }
    }
    private static boolean isPasswordShown = false;
    private void showAndHidePassword(ImageButton button, TextView textView){
        if(!isPasswordShown){
            textView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            button.setImageResource(R.drawable.ic_close_eye_24);
            isPasswordShown = true;
        }else{
            textView.setTransformationMethod(PasswordTransformationMethod.getInstance());
            button.setImageResource(R.drawable.ic_open_eye_24);
            isPasswordShown = false;
        }
    }
}
