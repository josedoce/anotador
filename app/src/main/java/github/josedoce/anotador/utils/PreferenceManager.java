package github.josedoce.anotador.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static SharedPreferences sharedPreferences;
    private static PreferenceManager instance;

    private PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences("anotador_preferences", Context.MODE_PRIVATE);
    }

    public static PreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context);
        }
        return instance;
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

}
