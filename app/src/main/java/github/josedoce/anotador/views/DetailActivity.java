package github.josedoce.anotador.views;

import static java.lang.String.format;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import github.josedoce.anotador.R;
import github.josedoce.anotador.adapter.DetailAdapter;
import github.josedoce.anotador.context.AnotadorContext;
import github.josedoce.anotador.handler.DialogDecision;
import github.josedoce.anotador.model.Annotation;
import github.josedoce.anotador.model.Field;
import github.josedoce.anotador.service.AnnotationService;
import github.josedoce.anotador.service.FieldService;

public class DetailActivity extends AppCompatActivity {
    private List<Field> fieldList;
    private Annotation annotation;
    private DetailAdapter detailAdapter;

    private ImageButton ib_edite, ib_delete;
    private TextView
            tv_annotation,
            tv_description,
            tv_date,
            tv_hour;
    private ListView recyclerView;
    private AnotadorContext anotadorContext;
    private AnnotationService annotationService;
    private FieldService fieldService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_annotation);

        anotadorContext = (AnotadorContext) getApplicationContext();
        annotationService = new AnnotationService(this, anotadorContext.getUser());
        fieldService = new FieldService(this, anotadorContext.getUser());
        fieldList = new ArrayList<>();

        tv_annotation = findViewById(R.id.tv_title);
        tv_description = findViewById(R.id.tv_description);
        tv_date = findViewById(R.id.tv_date);
        tv_hour = findViewById(R.id.tv_hour);
        ib_edite = findViewById(R.id.ib_edite);
        ib_delete = findViewById(R.id.ib_delete);
        recyclerView = findViewById(R.id.detailListView);

        Intent intent1 = getIntent();
        int annotationId = intent1.getExtras().getInt("annotationId");
        annotation = annotationService.findAnnotationBy(annotationId);

        tv_annotation.setText(annotation.getTitle());
        tv_description.setText(annotation.getDescription());
        tv_date.setText(format("%s - ",annotation.getDate()));
        tv_hour.setText(annotation.getHour());

        ib_edite.setOnClickListener((view)->{
            Intent intent2 = new Intent(this, EditActivity.class);
            intent2.putExtra("annotationId", annotation.getId());
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent2);
        });

        ib_delete.setOnClickListener((view)->{
            DialogDecision dialogDecision = new DialogDecision(this, "Aviso", "Quer mesmo apagar esta anotação ?");
            dialogDecision.setOnAgree(() -> {
                boolean isDeleted = annotationService.deleteAnnotationBy(annotation.getId());
                if(isDeleted){
                    Toast.makeText(this, "Excluido!", Toast.LENGTH_SHORT).show();
                    Intent intent3 = new Intent(this, HomeActivity.class);
                    startActivity(intent3);
                }else{
                    Toast.makeText(this, "Não foi excluido!", Toast.LENGTH_SHORT).show();
                }
            });
            dialogDecision.show();
        });

        //fieldList.addAll(fieldService.listAllFieldsRelatedTo(annotation));
        detailAdapter = new DetailAdapter(this, fieldList);
        recyclerView.setAdapter(detailAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fieldList.clear();
        fieldList.addAll(fieldService.listAllFieldsRelatedTo(annotation));
        detailAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Resumido.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        annotationService.destroy();
        fieldService.destroy();
    }
}