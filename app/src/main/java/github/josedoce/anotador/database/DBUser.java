package github.josedoce.anotador.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import static java.lang.String.format;

public class DBUser {
    public static final String TB_NAME = "User";
    private final DBHelper dbHelper;

    public DBUser(DBHelper db){
        dbHelper = db;
    }

    public long create(String login, String password) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("login", login);
        values.put("password", password);
        return db.insert(TB_NAME, null, values);
    }

    public Cursor selectAll(){
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        final String query = format("SELECT * FROM %s",TB_NAME) ;
        return db.rawQuery(query, null);
    }

    public Cursor selectByLogin(String login){
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        final String query = format("SELECT * FROM %s WHERE login=?", TB_NAME);
        return db.rawQuery(query, new String[]{login});
    }
}
