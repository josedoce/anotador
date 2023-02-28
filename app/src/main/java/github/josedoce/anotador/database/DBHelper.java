package github.josedoce.anotador.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static int version = 1;
    private static final String dbName = "anotador.db";
    private static String[] sql = {
            "CREATE TABLE User(login TEXT PRIMARY KEY, password TEXT);",
            //"INSERT INTO User VALUES('jose', '123');",
            "CREATE TABLE Annotations(id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR(255), description TEXT, email VARCHAR(255), password VARCHAR(255), url TEXT, date VARCHAR(20), fieldType VARCHAR(255));",
            "CREATE TABLE Fields(id INTEGER PRIMARY KEY AUTOINCREMENT, annotationId INTEGER, label VARCHAR(255), value TEXT, type INTEGER);"


    };

    private static String[] upgrade = {
            "DROP TABLE IF EXISTS User;",
            "DROP TABLE IF EXISTS Annotations;",
            "DROP TABLE IF EXISTS Fields;"
    };

    public DBHelper(@Nullable Context context) {
        super(context, dbName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String s : sql) {
            db.execSQL(s);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        version++;
        for(int i = 0; i <= upgrade.length; i++){
            db.execSQL(upgrade[i]);
        }
        onCreate(db);
    }

    public static void deleteDatabase(Context context){

        //https://stackoverflow.com/questions/40237579/delete-database-android-sqlite
        context.deleteDatabase(dbName);
    }


}
