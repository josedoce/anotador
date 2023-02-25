package github.josedoce.anotador.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import github.josedoce.anotador.model.Field;

public class DBFields {
    public static final String TB_R_NAME = "Fields";
    private DBHelper dbHelper;
    public DBFields(DBHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    public long create(Field field){
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        //id , annotationId, label, value, type
        ContentValues values = new ContentValues();
        values.put("id", field.getId());
        values.put("annotationId", field.getAnnotationId());
        values.put("label", field.getLabel());
        values.put("value", field.getValue());
        values.put("type", field.getType());

        return db.insert(TB_R_NAME, null, values);
    }

    public long create(List<Field> fieldList){
        long status = -1;
        for(Field field : fieldList)
            status = this.create(field);
        return status;
    }

    public Cursor selectAllById(Integer id) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TB_R_NAME+" WHERE annotationId=?", new String[]{id.toString()});
    }
}
