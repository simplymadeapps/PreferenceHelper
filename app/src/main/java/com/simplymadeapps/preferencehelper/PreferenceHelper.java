package com.simplymadeapps.preferencehelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class PreferenceHelper {

    protected static SharedPreferences preferences;
    protected static SharedPreferences.Editor editor;

    public static void init(Context context) {
        if(preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }

        if(editor == null) {
            editor = preferences.edit();
        }
    }

    public static <T> void put(String key, T value) {
        if (editor == null) {
            throw new IllegalStateException("You must call PreferenceHelper.init() before any other PreferenceHelper methods");
        }

        if(key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        if(value == null || value instanceof String) {
            // We assume a null value is a string because the other primitive types cannot be null and we will not be storing null sets
            editor.putString(key, (String) value);
        }
        else if(value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        }
        else if(value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        }
        else if(value instanceof Long) {
            // Reminder: if using hardcoded numbers we specify that is a literal long value (ex 0L or 0l)
            editor.putLong(key, (Long) value);
        }
        else if(value instanceof Float) {
            // Reminder: if using hardcoded numbers we specify that is a literal float value (ex 0F or 0f)
            editor.putFloat(key, (Float) value);
        }
        else if(value instanceof Set) {
            // Be careful when working with Sets - they work differently than the other types (https://stackoverflow.com/a/14034804/6754511)
            editor.putStringSet(key, (Set<String>) value);
        }
        else {
            throw new IllegalArgumentException("Object type cannot be stored into preferences - " + value.getClass());
        }

        editor.commit();
    }

    public static <T> T get(String key, T fallback) {
        if (preferences == null) {
            throw new IllegalStateException("You must call PreferenceHelper.init() before any other PreferenceHelper methods");
        }

        if(key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        if(fallback == null || fallback instanceof String) {
            // We assume a null fallback is a string because the other primitive types cannot be null
            return (T) preferences.getString(key, (String) fallback);
        }
        else if(fallback instanceof Integer) {
            return (T) (Integer) preferences.getInt(key, (Integer) fallback);
        }
        else if(fallback instanceof Boolean) {
            return (T) (Boolean) preferences.getBoolean(key, (Boolean) fallback);
        }
        else if(fallback instanceof Long) {
            // Reminder: if using hardcoded numbers we specify that is a literal long value (ex 0L or 0l)
            return (T) (Long) preferences.getLong(key, (Long) fallback);
        }
        else if(fallback instanceof Float) {
            // Reminder: if using hardcoded numbers we specify that is a literal float value (ex 0F or 0f)
            return (T) (Float) preferences.getFloat(key, (Float) fallback);
        }
        else if(fallback instanceof Set) {
            // We need to create a new set from the current set so it has a different memory address (https://stackoverflow.com/a/14034804/6754511)
            return (T) new HashSet<>(preferences.getStringSet(key, (Set<String>) fallback));
        }
        else {
            throw new IllegalArgumentException("Object type cannot be retrieved from preferences - " + fallback.getClass());
        }
    }
}
