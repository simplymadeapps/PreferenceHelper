package com.simplymadeapps.preferencehelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

public class PreferenceHelper {

    protected static SharedPreferences preferences;
    protected static SharedPreferences.Editor editor;

    private PreferenceHelper() { }

    public static void init(Context context) {
        if(preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }

        if(editor == null) {
            editor = preferences.edit();
        }
    }

    private static <T> boolean isTypePrimitive(Class<T> type) {
        return type.equals(Integer.class) ||
                type.equals(Boolean.class) ||
                type.equals(Float.class) ||
                type.equals(Long.class);
    }

    public static <T> void put(@NonNull String key, T value) {
        put(key, value, null);
    }

    public static <T> void put(@NonNull String key, T value, Class<T> type) {
        if (editor == null) {
            throw new IllegalStateException("You must call PreferenceHelper.init() before any other PreferenceHelper methods");
        }

        if(key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        if(value == null && type == null) {
            throw new IllegalArgumentException("You must specify the stored object type when storing a null value using put(String key, T value, Class<T> type)");
        }

        Class<T> instanceType;

        if(value == null) {
            if(isTypePrimitive(type)) {
                throw new IllegalArgumentException("Null primitive types (boolean, int, long, float) cannot be stored");
            }

            instanceType = type;
        }
        else {
            // We don't have to worry about a type mismatch because the compiler enforces the T value to match
            // For example, put("key", "string", Integer.class) would not compile
            instanceType = (Class<T>) value.getClass();
        }

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
            throw new IllegalArgumentException("Object type cannot be stored into preferences - " + value.getClass());
        }

        editor.commit();
    }

    public static <T> T get(@NonNull String key, T fallback) {
        return get(key, fallback, null);
    }

    public static <T> T get(@NonNull String key, T fallback, Class<T> type) {
        if (preferences == null) {
            throw new IllegalStateException("You must call PreferenceHelper.init() before any other PreferenceHelper methods");
        }

        if(key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        if(fallback == null && type == null) {
            throw new IllegalArgumentException("You must specify the object type when retrieving a null value using get(String key, T fallback, Class<T> type)");
        }

        Class<T> instanceType;

        if(fallback == null) {
            if(isTypePrimitive(type)) {
                throw new IllegalArgumentException("Null primitive types (boolean, int, long, float) cannot be retrieved");
            }

            instanceType = type;
        }
        else {
            // We don't have to worry about a type mismatch because the compiler enforces the T value to match
            // For example, get("key", "string", Integer.class) would not compile
            instanceType = (Class<T>) fallback.getClass();
        }

        if(String.class.isAssignableFrom(instanceType)) {
            // We assume a null fallback is a string because the other primitive types cannot be null
            return (T) preferences.getString(key, (String) fallback);
        }
        else if(Integer.class.isAssignableFrom(instanceType)) {
            return (T) (Integer) preferences.getInt(key, (Integer) fallback);
        }
        else if(Boolean.class.isAssignableFrom(instanceType)) {
            return (T) (Boolean) preferences.getBoolean(key, (Boolean) fallback);
        }
        else if(Long.class.isAssignableFrom(instanceType)) {
            // Reminder: if using hardcoded numbers we specify that is a literal long value (ex 0L or 0l)
            return (T) (Long) preferences.getLong(key, (Long) fallback);
        }
        else if(Float.class.isAssignableFrom(instanceType)) {
            // Reminder: if using hardcoded numbers we specify that is a literal float value (ex 0F or 0f)
            return (T) (Float) preferences.getFloat(key, (Float) fallback);
        }
        else if(Set.class.isAssignableFrom(instanceType)) {
            // We need to create a new set from the current set so it has a different memory address (https://stackoverflow.com/a/14034804/6754511)
            return (T) new HashSet<>(preferences.getStringSet(key, (Set<String>) fallback));
        }
        else {
            throw new IllegalArgumentException("Object type cannot be retrieved from preferences - " + fallback.getClass());
        }
    }
}
