package github.josedoce.anotador.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;

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
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View v = inflater.inflate(R.layout.row_detail, null, false);
        EditText et_field = v.findViewById(R.id.et_field);
        ImageButton bt_function = v.findViewById(R.id.bt_function);

        Field field = fieldList.get(position);
        et_field.setText(field.getValue());
        et_field.setInputType(field.getType());
        return v;
    }
}
