package github.josedoce.anotador.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import github.josedoce.anotador.model.Annotation;
import github.josedoce.anotador.model.Field;

public class DBAnnotations {
    public static final String TB_NAME = "Annotations";
    private final DBHelper dbHelper;
    public DBAnnotations(DBHelper db){
        this.dbHelper = db;
    }

    public long create(Annotation annotation){
        long status = -1;
        //id, title, description, email, password, url, date
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", annotation.getTitle());
        values.put("description", annotation.getDescription());
        values.put("date", annotation.getDate()+","+annotation.getHour());
        status = db.insert(TB_NAME, null, values);

        if(status == -1) return status;
        if(annotation.getFieldList() == null || annotation.getFieldList().size() == 0) return status;

        DBFields dbFields = new DBFields(this.dbHelper);
        //set annotationId
        for(Field field : annotation.getFieldList()){
            field.setAnnotationId((int)status);
        }

        status = dbFields.create(annotation.getFieldList());
        return status;
    }

    public Cursor selectAll(){
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TB_NAME, null);
    }

    public long delete(Integer id) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        return db.delete(TB_NAME, "id=?",new String[]{id.toString()});
    }

    public long update(Annotation annotation) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", annotation.getTitle());
        values.put("description", annotation.getDescription());
        //values.put("email", annotation.getEmail());
        //values.put("password", annotation.getPassword());
        //values.put("url", annotation.getUrl());
        values.put("date", annotation.getDate()+","+annotation.getHour());
        //values.put("fieldType", annotation.getFieldType());
        return db.update(TB_NAME, values, "id=?",new String[]{annotation.getId().toString()});
    }

    public Cursor selectById(Integer id) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TB_NAME+" WHERE id=?", new String[]{id.toString()});
    }
}
