package com.simplymadeapps.preferencehelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

public class PreferenceHelper {

    protected static SharedPreferences preferences;
    protected static SharedPreferences.Editor editor;

    private PreferenceHelper() { }

    @SuppressLint("CommitPrefEdits")
    public static void init(Context context) {
        if (preferences == null || editor == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
            editor = preferences.edit();
        }
    }

    public static boolean contains(String key) {
        return preferences.contains(key);
    }

    public static void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    private static <T> boolean isTypePrimitive(Class<T> type) {
        return type.equals(Integer.class) ||
                type.equals(Boolean.class) ||
                type.equals(Float.class) ||
                type.equals(Long.class);
    }

    private static <T> void checkForExceptions(String key, T value, Class<T> type) {
        if (preferences == null || editor == null) {
            throw new IllegalStateException("You must call PreferenceHelper.init() before any other PreferenceHelper methods");
        }

        if(key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        if(value == null && type == null) {
            throw new IllegalArgumentException("You must specify the object type when storing or retrieving a null value using put(String key, T value, Class<T> type) or get(String key, T fallback, Class<T> type)");
        }

        if(value == null && isTypePrimitive(type)) {
            throw new IllegalArgumentException("Null primitive types (boolean, int, long, float) are invalid");
        }
    }

    private static <T> Class<T> getInstanceType(T value, Class<T> type) {
        if(value == null) {
            return type;
        }
        else {
            // We don't have to worry about a type mismatch because the compiler enforces the T value to match
            // For example, put("key", "string", Integer.class) would not compile
            return (Class<T>) value.getClass();
        }
    }

    public static <T> void put(@NonNull String key, T value) {
        put(key, value, null);
    }

    public static <T> void put(@NonNull String key, T value, Class<T> type) {
        checkForExceptions(key, value, type);

        Class<T> instanceType = getInstanceType(value, type);

        if(String.class.isAssignableFrom(instanceType)) {
            editor.putString(key, (String) value);
        }
        else if(Integer.class.isAssignableFrom(instanceType)) {
            editor.putInt(key, (Integer) value);
        }
        else if(Boolean.class.isAssignableFrom(instanceType)) {
            editor.putBoolean(key, (Boolean) value);
        }
        else if(Long.class.isAssignableFrom(instanceType)) {
            // Reminder: if using hardcoded numbers we specify that is a literal long value (ex 0L or 0l)
            editor.putLong(key, (Long) value);
        }
        else if(Float.class.isAssignableFrom(instanceType)) {
            // Reminder: if using hardcoded numbers we specify that is a literal float value (ex 0F or 0f)
            editor.putFloat(key, (Float) value);
        }
        else if(Set.class.isAssignableFrom(instanceType)) {
            // Be careful when working with Sets - they work differently than the other types (https://stackoverflow.com/a/14034804/6754511)
            editor.putStringSet(key, (Set<String>) value);
        }
        else {
            // Store a custom non-primitive object as JSON string
            String objectAsJson = new Gson().toJson(value, instanceType);
            editor.putString(key, objectAsJson);
        }

        editor.commit();
    }

    public static <T> T get(@NonNull String key, T fallback) {
        return get(key, fallback, null);
    }

    public static <T> T get(@NonNull String key, T fallback, Class<T> type) {
        checkForExceptions(key, fallback, type);

        Class<T> instanceType = getInstanceType(fallback, type);

        if(String.class.isAssignableFrom(instanceType)) {
            // We assume a null fallback is a string because the other primitive types cannot be null
            return (T) preferences.getString(key, (String) fallback);
        }
        if(Integer.class.isAssignableFrom(instanceType)) {
            return (T) (Integer) preferences.getInt(key, (Integer) fallback);
        }
        if(Boolean.class.isAssignableFrom(instanceType)) {
            return (T) (Boolean) preferences.getBoolean(key, (Boolean) fallback);
        }
        if(Long.class.isAssignableFrom(instanceType)) {
            // Reminder: if using hardcoded numbers we specify that is a literal long value (ex 0L or 0l)
            return (T) (Long) preferences.getLong(key, (Long) fallback);
        }
        if(Float.class.isAssignableFrom(instanceType)) {
            // Reminder: if using hardcoded numbers we specify that is a literal float value (ex 0F or 0f)
            return (T) (Float) preferences.getFloat(key, (Float) fallback);
        }
        if(Set.class.isAssignableFrom(instanceType)) {
            // We need to create a new set from the current set so it has a different memory address (https://stackoverflow.com/a/14034804/6754511)
            return (T) new HashSet<>(preferences.getStringSet(key, (Set<String>) fallback));
        }

        // Retrieve a custom non-primitive object as JSON string
        String objectAsJson = preferences.getString(key, null);
        if(objectAsJson == null) {
            // No record exists for this key - return their fallback object
            return fallback;
        }

        return new Gson().fromJson(objectAsJson, instanceType);
    }
}
