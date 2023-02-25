package github.josedoce.anotador.views;

import static java.lang.String.format;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import github.josedoce.anotador.R;
import github.josedoce.anotador.adapter.DetailAdapter;
import github.josedoce.anotador.annotations.Senhador;
import github.josedoce.anotador.context.AnotadorContext;
import github.josedoce.anotador.database.DBAnnotations;
import github.josedoce.anotador.database.DBFields;
import github.josedoce.anotador.database.DBHelper;
import github.josedoce.anotador.model.Annotation;
import github.josedoce.anotador.model.Field;

public class DetailActivity extends AppCompatActivity {
    private final List<Field> fieldListOriginalCopy = new ArrayList<>();
    private Annotation annotation;
    private DetailAdapter detailAdapter;
    private DBHelper dbHelper;
    private DBFields dbFields;
    private DBAnnotations dbAnnotations;
    private Context context;
    private TextView
            tv_annotation,
            tv_description,
            tv_date,
            tv_hour;
    private AnotadorContext anotadorContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getBaseContext();
        dbHelper = new DBHelper(context);
        dbFields = new DBFields(dbHelper);
        dbAnnotations = new DBAnnotations(dbHelper);
        if(context != null){
            anotadorContext = (AnotadorContext) context.getApplicationContext();
        }

        //setContentView(R.layout.splash_screen_layout);
        setContentView(R.layout.detail_annotation);
        tv_annotation = findViewById(R.id.tv_title);
        tv_description = findViewById(R.id.tv_description);
        tv_date = findViewById(R.id.tv_date);
        tv_hour = findViewById(R.id.tv_hour);
        ListView recyclerView = findViewById(R.id.detailListView);
        Intent intent = getIntent();
        int annotationId = intent.getExtras().getInt("annotationId");
        //Toast.makeText(this, String.format("O id retornado foi: %d", annotationId), Toast.LENGTH_LONG).show();

        Cursor cursor1 = dbAnnotations.selectById(annotationId);
        cursor1.moveToFirst();
        if(cursor1.getCount() == 1){
            annotation = new Annotation(cursor1);
            if(anotadorContext != null){
                Senhador.createDecryptedModel(anotadorContext.getUser(), annotation);
            }
        }

        tv_annotation.setText(annotation.getTitle());
        tv_description.setText(annotation.getDescription());
        tv_date.setText(format("%s - ",annotation.getDate()));
        tv_hour.setText(annotation.getHour());

        Cursor cursor2 = dbFields.selectAllById(annotationId);
        cursor2.moveToFirst();
        if(cursor2.getCount() > 0){
            do {
                Field field = new Field(cursor2);
                if(anotadorContext != null){
                    Senhador.createDecryptedModel(anotadorContext.getUser(), field);
                }
                fieldListOriginalCopy.add(field);
            }while(cursor2.moveToNext());
        }
        detailAdapter = new DetailAdapter(this, fieldListOriginalCopy);
        recyclerView.setAdapter(detailAdapter);

    }
}