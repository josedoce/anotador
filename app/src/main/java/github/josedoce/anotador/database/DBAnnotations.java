package github.josedoce.anotador.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import github.josedoce.anotador.model.Annotation;

public class DBAnnotations {
    public static final String TB_NAME = "Annotations";
    private final DBHelper dbHelper;
    public DBAnnotations(DBHelper db){
        this.dbHelper = db;
    }

    public long create(Annotation annotation){
        //id, title, description, email, password, url, date
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", annotation.getTitle());
        values.put("description", annotation.getDescription());
        values.put("email", annotation.getEmail());
        values.put("password", annotation.getPassword());
        values.put("url", annotation.getUrl());
        values.put("date", annotation.getDate()+","+annotation.getHour());
        return db.insert(TB_NAME, null, values);
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
        values.put("email", annotation.getEmail());
        values.put("password", annotation.getPassword());
        values.put("url", annotation.getUrl());
        values.put("date", annotation.getDate()+","+annotation.getHour());

        return db.update(TB_NAME, values, "id=?",new String[]{annotation.getId().toString()});
    }

    public Cursor selectById(Integer id) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TB_NAME+" WHERE id=?", new String[]{id.toString()});
    }
}
