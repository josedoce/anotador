package github.josedoce.anotador.views;

import static java.lang.String.format;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import github.josedoce.anotador.R;
import github.josedoce.anotador.adapter.EditAdapter;
import github.josedoce.anotador.handler.DialogAddField;
import github.josedoce.anotador.handler.DialogDecision;
import github.josedoce.anotador.handler.DialogInfo;
import github.josedoce.anotador.handler.DialogProgress;
import github.josedoce.anotador.model.Annotation;
import github.josedoce.anotador.model.Field;
import github.josedoce.anotador.service.AnnotationService;
import github.josedoce.anotador.service.FieldService;
import github.josedoce.anotador.tasks.TesteTask;
import github.josedoce.anotador.utils.PreferenceManager;

public class EditActivity extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler();
    private List<Field> fieldList;
    private List<Integer> listToRemoveField;
    private Annotation annotation;
    private EditAdapter editAdapter;
    private Context context;
    private TextView tv_date, tv_hour;
    private EditText et_annotation, et_description;
    private Button button_modal, bt_cancel, bt_edit;
    private ListView recyclerView;
    private AnnotationService annotationService;
    private FieldService fieldService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.edit_layout);
        context = getBaseContext();
        PreferenceManager pm = PreferenceManager.getInstance(getApplicationContext());
        String password = pm.getString("userpassword", null);
        annotationService = new AnnotationService(this, password);
        fieldService = new FieldService(this, password);
        et_annotation = findViewById(R.id.et_title);
        et_description = findViewById(R.id.et_description);
        tv_date = findViewById(R.id.tv_date);
        tv_hour = findViewById(R.id.tv_hour);
        bt_cancel = findViewById(R.id.bt_cancel);
        bt_edit = findViewById(R.id.bt_edit);
        recyclerView = findViewById(R.id.list_fields);
        button_modal = findViewById(R.id.bt_create_field);

        Intent intent1 = getIntent();
        int annotationId = intent1.getExtras().getInt("annotationId");

        annotation = annotationService.findAnnotationBy(annotationId);

        et_annotation.setText(annotation.getTitle());
        et_description.setText(annotation.getDescription());
        tv_date.setText(format("%s - ",annotation.getDate()));
        tv_hour.setText(annotation.getHour());

        fieldList = new ArrayList<>();

        listToRemoveField = new ArrayList<>();
        fieldList.addAll(fieldService.listAllFieldsRelatedTo(annotation));

        editAdapter = new EditAdapter(this, fieldList);

        //event to remove field
        editAdapter.setOnItemSelectedToRemove(field -> {
            if(field.getId() != -1)
                listToRemoveField.add(field.getId());
            fieldList.remove(field);
            Toast.makeText(this, "Excluido", Toast.LENGTH_LONG).show();
        });

        recyclerView.setAdapter(editAdapter);

        //event to save field
        button_modal.setOnClickListener((view)->{
            DialogAddField dialogAddField = new DialogAddField(this);
            dialogAddField.setOnCreateField(field -> {
                Field newField = new Field();
                newField.setId(-1);
                newField.setLabel(field.getLabel());
                newField.setType(field.getType());
                newField.setAnnotationId(annotationId);
                newField.setValue("");
                fieldList.add(newField);
                editAdapter.notifyDataSetChanged();
            });
            dialogAddField.show();
        });

        bt_cancel.setOnClickListener((view)->{
            goToDetailActivity();
        });

        bt_edit.setOnClickListener((view)->{
            List<String> fieldValues = new ArrayList<>();
            fieldValues.add(et_description.getText().toString());
            fieldValues.add(et_annotation.getText().toString());
            List<String> fieldsToSaveValues = new ArrayList<>();
            for(Field f : fieldList)
                fieldsToSaveValues.add(f.getValue());
            fieldValues.addAll(fieldsToSaveValues);

            if(!isValid(fieldValues.toArray(new String[0]))){
                DialogInfo dialogInfo = new DialogInfo(this, "Campos vazios","Preencha todos os campos.",DialogInfo.INFO);
                dialogInfo.show();
                return;
            }

            DialogDecision dialogDecision = new DialogDecision(this, "Aviso", "Deseja mesmo editar ?");
            dialogDecision.setOnAgree(() -> {
                TesteTask tt = new TesteTask();
                DialogProgress dialogProgress = new DialogProgress(this);
                tt.setOnStartedTask(() -> {
                    handler.post(dialogProgress::show);
                });
                tt.setTaskBody(() -> {
                    for(Field field : fieldList){
                        if(field.getId() == -1){
                            field.setId(null);
                            fieldService.createField(field);
                        } else {
                            fieldService.updateField(field);
                        }
                    }
                    annotationService.updateAnnotation(annotation);

                    //remove fields in db
                    for(Integer id : listToRemoveField){
                        fieldService.deleteFieldBy(id);
                    }
                });
                tt.setOnFinishedTask(() -> handler.post(()->{
                    dialogProgress.dismiss();
                    goToDetailActivity();
                }));

                executor.execute(tt);
                executor.shutdown();
            });
            dialogDecision.show();
        });

    }

    private boolean isValid(String[] ets){
        int countError = 0;
        for(String et : ets){
            if(et.trim().isEmpty()){
                countError++;
            }
        }

        if(countError != 0){
            return false;
        }else{
            return true;
        }
    }

    private void goToDetailActivity(){
        Intent intent = new Intent(context, DetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("annotationId", annotation.getId());
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tv_date = null;
        tv_hour = null;
        et_annotation = null;
        et_description = null;
        button_modal = null;
        bt_cancel = null;
        bt_edit = null;
        recyclerView = null;
    }
}
