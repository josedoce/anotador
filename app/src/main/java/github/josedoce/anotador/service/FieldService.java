package github.josedoce.anotador.service;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import github.josedoce.anotador.annotations.Senhador;
import github.josedoce.anotador.database.DBFields;
import github.josedoce.anotador.database.DBHelper;
import github.josedoce.anotador.model.Annotation;
import github.josedoce.anotador.model.Field;

public class FieldService {
    private final String password;
    private final DBHelper dbHelper;
    private final DBFields db;
    public FieldService(Context context, String password) {
        this.dbHelper = new DBHelper(context.getApplicationContext());
        this.db = new DBFields(dbHelper);
        this.password = password;
    }

    public void destroy(){
        this.dbHelper.close();
    }

    public boolean createField(Field field){
        if(password == null || password.isEmpty()) return false;
        Senhador.createEncryptedModel(password, field);
        long res = db.create(field);
        return res != 0;
    }

    public List<Field> listAllFieldsRelatedTo(Annotation annotation){
        List<Field> fieldList = new ArrayList<>();;
        if(password == null || password.isEmpty()) return null;
        Log.d("Erro", "Ta rodando...");
        Cursor cursor2 = db.selectAllById(annotation.getId());
        cursor2.moveToFirst();
        if(cursor2.getCount() > 0){
            do {
                Field field = new Field(cursor2);
                Senhador.createDecryptedModel(password, field);
                fieldList.add(field);
            }while(cursor2.moveToNext());
        }else{
            return fieldList;
        }
        return fieldList;
    }

    public boolean updateField(Field field) {
        if(password == null || password.isEmpty()) return false;

        Senhador.createEncryptedModel(password, field);
        long res = db.update(field);
        return res != 0;
    }

    public boolean deleteFieldsByAnnotationId(int id){
        long res = db.deleteByAnnotationId(id);
        return res != 0;
    }

    public boolean deleteFieldBy(int id){
        long res = db.deleteBy(id);
        return res != 0;
    }

}
