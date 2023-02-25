package github.josedoce.anotador.views.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import github.josedoce.anotador.R;
import github.josedoce.anotador.annotations.Senhador;
import github.josedoce.anotador.context.AnotadorContext;
import github.josedoce.anotador.database.DBAnnotations;
import github.josedoce.anotador.database.DBFields;
import github.josedoce.anotador.database.DBHelper;
import github.josedoce.anotador.handler.DialogAddField;
import github.josedoce.anotador.handler.DialogInfo;
import github.josedoce.anotador.handler.DialogProgress;
import github.josedoce.anotador.model.Annotation;
import github.josedoce.anotador.model.Field;
import github.josedoce.anotador.views.HomeActivity;

public class AddFragment extends Fragment {
    private List<View> viewList;
    private String status = "create";
    private int id;
    private DBHelper db;
    private DBAnnotations dbAnnotations;
    private DBFields dbFields;

    private BottomNavigationView bottomNavigationView;
    private LinearLayout ll_form_body;
    TextView
            tv_date,
            tv_hour;

    EditText
            et_title,
            et_description;
    Button
            bt_create,
            bt_cancel;


    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private final Date date = new Date();
    private final String sdate = sdf.format(date);
    private final String[] now = sdate.split(" ");
    private final String vdate = now[0];
    private final String vhour = now[1];

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HomeActivity homeActivity = (HomeActivity) getActivity();
        if(homeActivity != null){
            this.bottomNavigationView = homeActivity.getBottomNavigationView();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View F = inflater.inflate(R.layout.add_fragment_layout, container, false);
        viewList = new ArrayList<>();
        db = new DBHelper(getContext());
        dbAnnotations = new DBAnnotations(db);
        dbFields = new DBFields(db);


        tv_date = F.findViewById(R.id.tv_date);
        tv_hour = F.findViewById(R.id.tv_hour);
        bt_cancel = F.findViewById(R.id.bt_cancel);
        bt_create = F.findViewById(R.id.bt_create);
        et_title = F.findViewById(R.id.et_title);
        et_description = F.findViewById(R.id.et_description);
        ll_form_body = F.findViewById(R.id.ll_form_body);

        tv_date.setText(vdate);
        tv_hour.setText(vhour);

        //F.setBackground(new ColorDrawable(Color.parseColor("red")));

        //Button nb = new Button(getContext());
        //nb.setText("Criar campo");
        //o layoutInflater vai injetar qualquer elemento(filho) no Pai.
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        Button button_modal = F.findViewById(R.id.bt_create_field);
        button_modal.setOnClickListener((view)->{
            DialogAddField dialogAddField = new DialogAddField(requireContext());
            dialogAddField.setOnCreateField(field -> {
                View fieldView = layoutInflater.inflate(R.layout.new_field, null);
                TextView fieldLabel = fieldView.findViewById(R.id.tv_label_name);
                EditText fieldLabelInput = fieldView.findViewById(R.id.et_label);
                //aqui a função irá cuidar de pegar os dados para salvar no banco de dados.

                ImageButton btnRemoveField = fieldView.findViewById(R.id.ib_remove_field);
                //it's the button to remove field
                btnRemoveField.setOnClickListener((v)->{
                    if(viewList.contains(fieldView)){
                        int index = viewList.indexOf(fieldView);
                        ll_form_body.removeView(viewList.get(index));
                        viewList.remove(index);
                    }
                });
                fieldLabel.setText(field.getLabel());
                fieldLabelInput.setInputType(field.getType());
                ll_form_body.addView(fieldView);
                viewList.add(fieldView);
            });
            dialogAddField.show();
        });

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

            bt_create.setText("editar");
            setText(et_title, annotation.getTitle());
            setText(et_description, annotation.getDescription());
        }else{
            bt_create.setText("criar");
        }

        //cancel
        bt_cancel.setOnClickListener((view)->{
            bottomNavigationView.setSelectedItemId(R.id.ic_annotations);
        });

        //save
        bt_create.setOnClickListener((view)->{
            List<Field> fieldList = new ArrayList<>();
            EditText[] editTexts = new EditText[viewList.size()+2];
            editTexts[0]= et_title;
            editTexts[1] = et_description;

            int count = 2;
            for(View fieldView : viewList){
                TextView fieldLabel = fieldView.findViewById(R.id.tv_label_name);
                EditText fieldLabelInput = fieldView.findViewById(R.id.et_label);
                editTexts[count] = fieldLabelInput;
                Field field = new Field();
                field.setLabel(fieldLabel.getText().toString());
                field.setValue(fieldLabelInput.getText().toString());
                field.setType(fieldLabelInput.getInputType());
                fieldList.add(field);
                count++;
            }

            if(!isValid(editTexts)){
                DialogInfo dialogInfo = new DialogInfo(getContext(), "Escreve ai man","Titulo e descrição do x9.",DialogInfo.INFO);
                dialogInfo.show();

                //Toast.makeText(getContext(), "Todos os campos são necessários.", Toast.LENGTH_LONG).show();
                return;
            }

            Annotation annotation = new Annotation(
                    getText(et_title),
                    getText(et_description),
                    vdate,
                    vhour
            );

            //encrypt
            Context context = getActivity();
            if(context != null){
                AnotadorContext anotadorContext = (AnotadorContext) context.getApplicationContext();
                if(fieldList.size() > 0){
                    for(Field field : fieldList){
                        Senhador.createEncryptedModel(anotadorContext.getUser(), field);
                    }
                }
                Senhador.createEncryptedModel(anotadorContext.getUser(), annotation);
            }

            if(status.equals("create")){
                long annotationId = dbAnnotations.create(annotation);
                if(annotationId != -1 && fieldList.size() > 0){
                    for(Field field: fieldList){
                        field.setAnnotationId((int) annotationId);
                    }
                    dbFields.create(fieldList);
                }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tv_date = null;
        tv_hour = null;
        et_title = null;
        et_description = null;
        bt_create = null;
        bt_cancel = null;
        bottomNavigationView = null;
        ll_form_body = null;
        viewList.clear();
    }

}
