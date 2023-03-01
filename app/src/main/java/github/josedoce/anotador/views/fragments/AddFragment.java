package github.josedoce.anotador.views.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import github.josedoce.anotador.R;
import github.josedoce.anotador.handler.DialogAddField;
import github.josedoce.anotador.handler.DialogInfo;
import github.josedoce.anotador.model.Annotation;
import github.josedoce.anotador.model.Field;
import github.josedoce.anotador.service.AnnotationService;
import github.josedoce.anotador.utils.PreferenceManager;
import github.josedoce.anotador.views.HomeActivity;

public class AddFragment extends Fragment {
    private List<View> viewList;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout ll_form_body;
    private TextView
            tv_date,
            tv_hour;
    private EditText
            et_title,
            et_description;
    private Button
            bt_create,
            bt_cancel;
    private AnnotationService annotationService;

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
            PreferenceManager pm = PreferenceManager.getInstance(getActivity().getApplicationContext());
            String password = pm.getString("userpassword", null);
            annotationService = new AnnotationService(this.getActivity(), password);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View F = inflater.inflate(R.layout.add_fragment_layout, container, false);
        viewList = new ArrayList<>();

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
                DialogInfo dialogInfo = new DialogInfo(getContext(), "Campos vazios","Titulo e descrição são necessários.",DialogInfo.INFO);
                dialogInfo.show();
                return;
            }

            Annotation annotation = new Annotation(
                    getText(et_title),
                    getText(et_description),
                    vdate,
                    vhour
            );

            annotation.setFieldList(fieldList);
            boolean isCreated = annotationService.createAnnotation(annotation);
            if(isCreated){
                DialogInfo dialogInfo2 = new DialogInfo(getContext(), "Sucesso","Salvo com sucesso.",DialogInfo.SUCCESS);
                dialogInfo2.setOnDismissListener(dialog -> {
                    bottomNavigationView.setSelectedItemId(R.id.ic_annotations);
                });
                dialogInfo2.show();
            }else{
                Toast.makeText(getContext(), "Não foi possivel salvar.", Toast.LENGTH_LONG).show();
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
                    long res = 0 ;//dbAnnotations.update(annotation);;
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

    private boolean isValid(EditText[] ets){
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
        annotationService.destroy();
    }

}
