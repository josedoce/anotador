package github.josedoce.anotador.service;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import github.josedoce.anotador.annotations.Senhador;
import github.josedoce.anotador.database.DBAnnotations;
import github.josedoce.anotador.database.DBHelper;
import github.josedoce.anotador.model.Annotation;
import github.josedoce.anotador.model.Field;

public class AnnotationService {
    private final String password;
    private final DBHelper dbHelper;
    private final DBAnnotations db;
    private final FieldService fieldService;
    public AnnotationService(Context context, String password) {
        this.dbHelper = new DBHelper(context.getApplicationContext());
        this.db = new DBAnnotations(dbHelper);
        this.fieldService = new FieldService(context, password);
        this.password = password;
    }

    public void destroy(){
        this.dbHelper.close();
        this.fieldService.destroy();
    }

    public Annotation findAnnotationBy(int id){
        Annotation annotation;
        if(password == null || password.isEmpty()) return null;
        Cursor cursor1 = db.selectById(id);
        cursor1.moveToFirst();
        if(cursor1.getCount() == 1){
            annotation = new Annotation(cursor1);
            Senhador.createDecryptedModel(password, annotation);
        }else{
            annotation = null;
        }
        return annotation;
    }

    public boolean createAnnotation(Annotation annotation){
        if(password == null || password.isEmpty()) return false;
        Senhador.createEncryptedModel(password, annotation);
        //handle create annotations and fields
        if(annotation.getFieldList()!=null && annotation.getFieldList().size() > 0){
            for(Field field : annotation.getFieldList()){
                Senhador.createEncryptedModel(password, field);
            }
            long status = db.create(annotation);
            return status != -1;
        }
        long status = db.create(annotation);
        return status != -1;
    }

    public List<Annotation> listAllAnnotations(){
        if(password == null || password.isEmpty()) return null;
        List<Annotation> annotationList = null;
        Cursor cursor = db.selectAll();
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            annotationList = new ArrayList<>();
            do {
                Annotation annotation = new Annotation(cursor);
                Senhador.createDecryptedModel(password, annotation);
                annotationList.add(annotation);
            }while(cursor.moveToNext());
        }else{
            annotationList = new ArrayList<>();
        }
        return annotationList;
    }

    public boolean deleteAnnotationBy(int id){
        long res = db.delete(id);
        if(res!=0){
            fieldService.deleteFieldsByAnnotationId(id);
            return true;
        }else{
           return false;
        }
    }

    public boolean updateAnnotation(Annotation annotation) {
        if(password == null || password.isEmpty()) return false;
        Senhador.createEncryptedModel(password, annotation);
        long res = db.update(annotation);
        Senhador.createDecryptedModel(password, annotation);
        return res != 0;
    }
}
