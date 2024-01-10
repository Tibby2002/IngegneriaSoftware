package com.example.cryptotracker.Supports;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String PREFERENCES_NAME = "CryptoTrackerPreferences";

    // Metodo per ottenere un'istanza delle SharedPreferences
    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    // Metodo per salvare una stringa nelle SharedPreferences
    public static void saveString(Context context, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.apply();
    }

    // Metodo per ottenere una stringa dalle SharedPreferences
    public static String getString(Context context, String key, String defaultValue) {
        return getSharedPreferences(context).getString(key, defaultValue);
    }

    // Metodo per salvare un boolean nelle SharedPreferences
    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    // Metodo per ottenere un boolean dalle SharedPreferences
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return getSharedPreferences(context).getBoolean(key, defaultValue);
    }

    // Metodo per rimuovere un dato dalle SharedPreferences
    public static void removeKey(Context context, String key) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(key);
        editor.apply();
    }

    // Metodo per cancellare tutte le SharedPreferences
    public static void clearPreferences(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }

    public static void saveAccessInformation(Context context, String name, String surname, String email) {
        clearPreferences(context);
        saveBoolean(context, "isLogged", true);
        saveString(context, "name", name);
        saveString(context, "surname", surname);
        saveString(context, "email", email);
    }
}
