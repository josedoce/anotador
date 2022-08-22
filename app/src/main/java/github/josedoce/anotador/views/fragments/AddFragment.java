package github.josedoce.anotador.views.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;

import github.josedoce.anotador.R;
import github.josedoce.anotador.annotations.Senhador;
import github.josedoce.anotador.context.AnotadorContext;
import github.josedoce.anotador.database.DBAnnotations;
import github.josedoce.anotador.database.DBHelper;
import github.josedoce.anotador.model.Annotation;

public class AddFragment extends Fragment {
    private String status = "create";
    private int id;
    private DBHelper db;
    private DBAnnotations dbAnnotations;
    private ViewHolder mViewHolder = new ViewHolder();
    private BottomNavigationView bottomNavigationView;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();
    String sdate = sdf.format(date);
    String[] now = sdate.split(" ");
    String vdate = now[0];
    String vhour = now[1];

    public AddFragment(BottomNavigationView bnv){
        this.bottomNavigationView = bnv;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View F = inflater.inflate(R.layout.add_fragment_layout, container, false);

        db = new DBHelper(getContext());
        dbAnnotations = new DBAnnotations(db);

        //references
        mViewHolder.tv_date = F.findViewById(R.id.tv_date);
        mViewHolder.tv_hour = F.findViewById(R.id.tv_hour);
        mViewHolder.tv_date.setText(vdate);
        mViewHolder.tv_hour.setText(vhour);

        mViewHolder.bt_cancel = F.findViewById(R.id.bt_cancel);
        mViewHolder.bt_create = F.findViewById(R.id.bt_create);

        mViewHolder.et_title = F.findViewById(R.id.et_title);
        mViewHolder.et_description = F.findViewById(R.id.et_description);
        mViewHolder.et_email = F.findViewById(R.id.et_email);
        mViewHolder.et_password = F.findViewById(R.id.et_password);
        mViewHolder.et_url = F.findViewById(R.id.et_url);

        //update logic
        if(getArguments() != null){
            status = getArguments().getString("status");
            id = Integer.parseInt(getArguments().getString("id"));
            Cursor cursor = dbAnnotations.selectById(id);
            cursor.moveToFirst();
            Annotation annotation = new Annotation(cursor);

            //decrypt
            Context context = getActivity();
            if(context != null){
                AnotadorContext anotadorContext = (AnotadorContext) context.getApplicationContext();
                Senhador.createDecryptedModel(anotadorContext.getUser(), annotation);
            }

            mViewHolder.bt_create.setText("editar");
            setText(mViewHolder.et_title, annotation.getTitle());
            setText(mViewHolder.et_description, annotation.getDescription());
            setText(mViewHolder.et_email, annotation.getEmail());
            setText(mViewHolder.et_password, annotation.getPassword());
            setText(mViewHolder.et_url, annotation.getUrl());
        }else{
            mViewHolder.bt_create.setText("criar");
        }

        //cancel
        mViewHolder.bt_cancel.setOnClickListener((view)->{
            bottomNavigationView.setSelectedItemId(R.id.ic_annotations);
        });

        //save
        mViewHolder.bt_create.setOnClickListener((view)->{

            EditText[] editTexts = {
                    mViewHolder.et_title,
                    mViewHolder.et_description,
                    mViewHolder.et_email,
                    mViewHolder.et_password,
                    mViewHolder.et_email
            };

            if(!isValid(editTexts)){
                Toast.makeText(getContext(), "Todos os campos são necessários.", Toast.LENGTH_LONG).show();
                return;
            }

            Annotation annotation = new Annotation(
                    getText(mViewHolder.et_title),
                    getText(mViewHolder.et_description),
                    getText(mViewHolder.et_email),
                    getText(mViewHolder.et_password),
                    getText(mViewHolder.et_url),
                    vdate,
                    vhour
            );

            //encrypt
            Context context = getActivity();
            if(context != null){
                AnotadorContext anotadorContext = (AnotadorContext) context.getApplicationContext();
                Senhador.createEncryptedModel(anotadorContext.getUser(), annotation);
            }

            if(status.equals("create")){
                dbAnnotations.create(annotation);
                Toast.makeText(getContext(), "Salvo com sucesso.", Toast.LENGTH_LONG).show();
                bottomNavigationView.setSelectedItemId(R.id.ic_annotations);
            }
            if(status.equals("edit")){
                annotation.setId(id);
                showDeleteDialogActions(annotation);
            }
        });
        return F;
    }

    private void showDeleteDialogActions(Annotation annotation){
        Context context = requireContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setMessage("Deseja editar esta anotação ?")
                .setPositiveButton("sim", (dialog, which) -> {
                    long res = dbAnnotations.update(annotation);;
                    if(res != 0){
                        Toast.makeText(getContext(), "Editado com sucesso.", Toast.LENGTH_LONG).show();
                        bottomNavigationView.setSelectedItemId(R.id.ic_annotations);
                    }else{
                        Toast.makeText(context, "Não foi editado!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("não",((dialog, which) -> {}));
        builder.show();
    }

    public boolean isValid(EditText[] ets){
        int countError = 0;
        for(EditText et : ets){
            if(et.getText().toString().trim().isEmpty()){
                countError++;
            }
        }

        if(countError != 0){
            return false;
        }else{
            return true;
        }
    }

    public String getText(EditText et){
        return et.getText().toString();
    }

    public void setText(EditText et, String value){
        et.setText(value);
    }

    public static class ViewHolder {
        TextView
                tv_date,
                tv_hour;

        EditText
                et_title,
                et_description,
                et_email,
                et_password,
                et_url;
        Button
                bt_create,
                bt_cancel;
    }
}
