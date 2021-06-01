package com.simplymadeapps.preferencehelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(Enclosed.class)
public class PreferenceHelperTests {

    @PrepareForTest({PreferenceManager.class})
    public static class InitTests extends PowerMockTest {

        @Test
        public void test_init_nullPreferences() {
            PreferenceHelper.preferences = null;
            PreferenceHelper.editor = mock(SharedPreferences.Editor.class);
            Context context = mock(Context.class);
            SharedPreferences preferences = mock(SharedPreferences.class);
            SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
            mockStatic(PreferenceManager.class);
            when(PreferenceManager.getDefaultSharedPreferences(context)).thenReturn(preferences);
            doReturn(editor).when(preferences).edit();

            PreferenceHelper.init(context);

            Assert.assertEquals(PreferenceHelper.preferences, preferences);
            Assert.assertEquals(PreferenceHelper.editor, editor);
        }

        @Test
        public void test_init_nullEditor() {
            PreferenceHelper.preferences = mock(SharedPreferences.class);
            PreferenceHelper.editor = null;
            Context context = mock(Context.class);
            SharedPreferences preferences = mock(SharedPreferences.class);
            SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
            mockStatic(PreferenceManager.class);
            when(PreferenceManager.getDefaultSharedPreferences(context)).thenReturn(preferences);
            doReturn(editor).when(preferences).edit();

            PreferenceHelper.init(context);

            Assert.assertEquals(PreferenceHelper.preferences, preferences);
            Assert.assertEquals(PreferenceHelper.editor, editor);
        }

        @Test
        public void test_init_notNull() {
            SharedPreferences preferences = mock(SharedPreferences.class);
            SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
            PreferenceHelper.preferences = preferences;
            PreferenceHelper.editor = editor;
            Context context = mock(Context.class);

            PreferenceHelper.init(context);

            Assert.assertEquals(PreferenceHelper.preferences, preferences);
            Assert.assertEquals(PreferenceHelper.editor, editor);
        }
    }

    @PrepareForTest({PreferenceManager.class})
    public static class ContainsTests extends PowerMockTest {

        @Test
        public void test_contains() {
            SharedPreferences preferences = mock(SharedPreferences.class);
            PreferenceHelper.preferences = preferences;
            doReturn(true).when(preferences).contains("key");

            boolean result = PreferenceHelper.contains("key");

            Assert.assertTrue(result);
        }
    }

    public static class RemoveTests {

        @Test
        public void test_remove() {
            SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
            PreferenceHelper.editor = editor;

            PreferenceHelper.remove("key");

            verify(editor, times(1)).remove("key");
            verify(editor, times(1)).commit();
        }
    }

    public static class ClearTests {

        @Test
        public void test_clear() {
            SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
            PreferenceHelper.editor = editor;

            PreferenceHelper.clear();

            verify(editor, times(1)).clear();
            verify(editor, times(1)).commit();
        }
    }

    public static class IsTypePrimitiveTests {

        @Test
        public void test_integer() throws Exception {
            boolean result = Whitebox.invokeMethod(PreferenceHelper.class, "isTypePrimitive", Integer.class);

            Assert.assertTrue(result);
        }

        @Test
        public void test_boolean() throws Exception {
            boolean result = Whitebox.invokeMethod(PreferenceHelper.class, "isTypePrimitive", Boolean.class);

            Assert.assertTrue(result);
        }

        @Test
        public void test_float() throws Exception {
            boolean result = Whitebox.invokeMethod(PreferenceHelper.class, "isTypePrimitive", Float.class);

            Assert.assertTrue(result);
        }

        @Test
        public void test_long() throws Exception {
            boolean result = Whitebox.invokeMethod(PreferenceHelper.class, "isTypePrimitive", Long.class);

            Assert.assertTrue(result);
        }

        @Test
        public void test_string() throws Exception {
            boolean result = Whitebox.invokeMethod(PreferenceHelper.class, "isTypePrimitive", String.class);

            Assert.assertFalse(result);
        }
    }

    public static class CheckForExceptionsTests {

        @Before
        public void beforeTest() {
            PreferenceHelper.preferences = mock(SharedPreferences.class);
            PreferenceHelper.editor = mock(SharedPreferences.Editor.class);
        }

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void check_preferencesNull() throws Exception {
            expectedException.expect(IllegalStateException.class);
            expectedException.expectMessage("You must call PreferenceHelper.init() before any other PreferenceHelper methods");
            PreferenceHelper.preferences = null;

            Whitebox.invokeMethod(PreferenceHelper.class, "checkForExceptions", "key", "value", String.class);
        }

        @Test
        public void check_editorNull() throws Exception {
            expectedException.expect(IllegalStateException.class);
            expectedException.expectMessage("You must call PreferenceHelper.init() before any other PreferenceHelper methods");
            PreferenceHelper.editor = null;

            Whitebox.invokeMethod(PreferenceHelper.class, "checkForExceptions", "key", "value", String.class);
        }

        @Test
        public void check_keyNull() throws Exception {
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("Key cannot be null");

            Whitebox.invokeMethod(PreferenceHelper.class, "checkForExceptions", (String) null, "value", String.class);
        }

        @Test
        public void check_valueAndType_null() throws Exception {
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("You must specify the object type when storing or retrieving a null value using put(String key, T value, Class<T> type) or get(String key, T fallback, Class<T> type)");

            Whitebox.invokeMethod(PreferenceHelper.class, "checkForExceptions", "key", null, null);
        }

        @Test
        public void check_value_notNull() throws Exception {
            Whitebox.invokeMethod(PreferenceHelper.class, "checkForExceptions", "key", "value", null);

            // expectedException handles assertion
        }

        @Test
        public void check_valueNull_typeNotPrimitive() throws Exception {
            Whitebox.invokeMethod(PreferenceHelper.class, "checkForExceptions", "key", null, String.class);

            // expectedException handles assertion
        }

        @Test
        public void check_valueNull_typePrimitive() throws Exception {
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("Null primitive types (boolean, int, long, float) are invalid");

            Whitebox.invokeMethod(PreferenceHelper.class, "checkForExceptions", "key", null, Integer.class);
        }
    }

    public static class GetInstanceTypeTests {

        @Test
        public void test_valueNull() throws Exception {
            Class result = Whitebox.invokeMethod(PreferenceHelper.class, "getInstanceType", (String) null, String.class);

            Assert.assertEquals(result, String.class);
        }

        @Test
        public void test_valueExists() throws Exception {
            Class result = Whitebox.invokeMethod(PreferenceHelper.class, "getInstanceType", true, null);

            Assert.assertEquals(result, Boolean.class);
        }
    }

    @PrepareForTest({PreferenceHelper.class, Gson.class})
    public static class PutTests extends PowerMockTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Before
        public void beforeTest() {
            PreferenceHelper.preferences = mock(SharedPreferences.class);
            PreferenceHelper.editor = mock(SharedPreferences.Editor.class);
        }

        @Test
        public void test_put_checkForExceptions() {
            // We are unable to verify on static private methods
            // We will assert it is called by putting in data that would call out to checkForExceptions() method
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("Key cannot be null");

            PreferenceHelper.put(null, "string");

            verify(PreferenceHelper.editor, times(0)).commit();
        }

        @Test
        public void test_put_getInstanceType() {
            // We are unable to verify on static private methods
            // We will assert it is called by putting in data that would call out to getInstanceType() method

            PreferenceHelper.put("key", null, String.class);

            verify(PreferenceHelper.editor, times(1)).putString("key", null);
            verify(PreferenceHelper.editor, times(1)).commit();
        }

        @Test
        public void test_put_String() {
            PreferenceHelper.put("key", "string");

            verify(PreferenceHelper.editor, times(1)).putString("key", "string");
            verify(PreferenceHelper.editor, times(1)).commit();
        }

        @Test
        public void test_put_int() {
            PreferenceHelper.put("key", 1);

            verify(PreferenceHelper.editor, times(1)).putInt("key", 1);
            verify(PreferenceHelper.editor, times(1)).commit();
        }

        @Test
        public void test_put_boolean() {
            PreferenceHelper.put("key", false);

            verify(PreferenceHelper.editor, times(1)).putBoolean("key", false);
            verify(PreferenceHelper.editor, times(1)).commit();
        }

        @Test
        public void test_put_long() {
            PreferenceHelper.put("key", 1000L);

            verify(PreferenceHelper.editor, times(1)).putLong("key", 1000L);
            verify(PreferenceHelper.editor, times(1)).commit();
        }

        @Test
        public void test_put_float() {
            PreferenceHelper.put("key", 5.999F);

            verify(PreferenceHelper.editor, times(1)).putFloat("key", 5.999F);
            verify(PreferenceHelper.editor, times(1)).commit();
        }

        @Test
        public void test_put_set() {
            Set<String> set = new HashSet<>();
            set.add("entry1");

            PreferenceHelper.put("key", set);

            verify(PreferenceHelper.editor, times(1)).putStringSet("key", set);
            verify(PreferenceHelper.editor, times(1)).commit();
        }

        @Test
        public void test_put_custom() throws Exception {
            UUID uuid = UUID.randomUUID();
            Gson gson = mock(Gson.class);
            whenNew(Gson.class).withNoArguments().thenReturn(gson);
            doReturn("json").when(gson).toJson(uuid, UUID.class);

            PreferenceHelper.put("key", uuid);

            verify(PreferenceHelper.editor, times(1)).putString("key", "json");
            verify(PreferenceHelper.editor, times(1)).commit();
        }
    }

    @PrepareForTest({PreferenceHelper.class})
    public static class PutListTests extends PowerMockTest {

        @Test
        public void test_putList() throws Exception {
            spy(PreferenceHelper.class);
            List<UUID> list = new ArrayList<>();
            doNothing().when(PreferenceHelper.class, "put", "key", list, List.class);

            PreferenceHelper.putList("key", list);

            verifyPrivate(PreferenceHelper.class, times(1)).invoke("put", "key", list, List.class);
        }
    }

    @PrepareForTest({PreferenceHelper.class})
    public static class GetTests extends PowerMockTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Before
        public void beforeTest() {
            PreferenceHelper.preferences = mock(SharedPreferences.class);
            PreferenceHelper.editor = mock(SharedPreferences.Editor.class);
        }

        @Test
        public void test_get_checkForExceptions() {
            // We are unable to verify on static private methods
            // We will assert it is called by putting in data that would call out to checkForExceptions() method
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("Key cannot be null");

            PreferenceHelper.get(null, "string");

            verify(PreferenceHelper.preferences, times(0)).getString(anyString(), anyString());
            verify(PreferenceHelper.preferences, times(0)).getInt(anyString(), anyInt());
            verify(PreferenceHelper.preferences, times(0)).getBoolean(anyString(), anyBoolean());
            verify(PreferenceHelper.preferences, times(0)).getLong(anyString(), anyLong());
            verify(PreferenceHelper.preferences, times(0)).getFloat(anyString(), anyFloat());
            verify(PreferenceHelper.preferences, times(0)).getStringSet(anyString(), anySet());
        }

        @Test
        public void test_get_getInstanceType() {
            // We are unable to verify on static private methods
            // We will assert it is called by putting in data that would call out to getInstanceType() method
            doReturn("result").when(PreferenceHelper.preferences).getString("key", null);

            String result = PreferenceHelper.get("key", null, String.class);

            Assert.assertEquals(result, "result");
        }

        @Test
        public void test_get_String() {
            doReturn("result").when(PreferenceHelper.preferences).getString("key", "fallback");

            String result = PreferenceHelper.get("key", "fallback");

            Assert.assertEquals(result, "result");
        }

        @Test
        public void test_get_int() {
            doReturn(1).when(PreferenceHelper.preferences).getInt("key", 0);

            int result = PreferenceHelper.get("key", 0);

            Assert.assertEquals(result, 1);
        }

        @Test
        public void test_get_boolean() {
            doReturn(true).when(PreferenceHelper.preferences).getBoolean("key", false);

            boolean result = PreferenceHelper.get("key", false);

            Assert.assertEquals(result, true);
        }

        @Test
        public void test_get_long() {
            doReturn(100L).when(PreferenceHelper.preferences).getLong("key", 25L);

            long result = PreferenceHelper.get("key", 25L);

            Assert.assertEquals(result, 100L);
        }

        @Test
        public void test_get_float() {
            doReturn(7.75F).when(PreferenceHelper.preferences).getFloat("key", 0.5F);

            float result = PreferenceHelper.get("key", 0.5F);

            Assert.assertEquals(result, 7.75F, 0);
        }

        @Test
        public void test_get_set() {
            Set<String> fallback = new HashSet<>();
            fallback.add("fallback1");
            Set<String> expected = new HashSet<>();
            expected.add("entry1");
            expected.add("entry2");
            doReturn(expected).when(PreferenceHelper.preferences).getStringSet("key", fallback);

            Set<String> result = PreferenceHelper.get("key", fallback);

            // We want to assert that we did not get the expected list but a duplicate/identical list with a different memory address
            Assert.assertEquals(result == expected, false);
            Assert.assertEquals(result.size(), 2);
            Iterator<String> iterator = result.iterator();
            Assert.assertEquals(iterator.next(), "entry1");
            Assert.assertEquals(iterator.next(), "entry2");
            Assert.assertEquals(iterator.hasNext(), false);
        }

        @Test
        public void test_get_customObject() throws Exception {
            spy(PreferenceHelper.class);
            UUID fallback = UUID.randomUUID();
            UUID storedObject = UUID.randomUUID();
            doReturn(storedObject).when(PreferenceHelper.class, "getCustomObject", "key", fallback, UUID.class);

            UUID result = PreferenceHelper.get("key", fallback);

            Assert.assertEquals(storedObject, result);
        }
    }

    @PrepareForTest({PreferenceHelper.class, Gson.class, JsonSyntaxException.class})
    public static class GetCustomObjectTests extends PowerMockTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        UUID fallback;
        Gson gson;
        String key;

        @Before
        public void beforeTest() throws Exception {
            PreferenceHelper.preferences = mock(SharedPreferences.class);
            key = "key";
            fallback = UUID.randomUUID();
            gson = mock(Gson.class);
            whenNew(Gson.class).withNoArguments().thenReturn(gson);
        }

        @Test
        public void test_getCustomObject_fallback() throws Exception {
            doReturn(false).when(PreferenceHelper.preferences).contains(key);

            UUID result = Whitebox.invokeMethod(PreferenceHelper.class, "getCustomObject", key, fallback, UUID.class);

            Assert.assertEquals(result, fallback);
        }

        @Test
        public void test_getCustomObject_exists_withListException() throws Exception {
            doReturn(true).when(PreferenceHelper.preferences).contains(key);
            List<UUID> listFallback = new ArrayList<>();
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("Please use getList() instead of get() when retrieving a list of stored objects.");

            UUID result = Whitebox.invokeMethod(PreferenceHelper.class, "getCustomObject", key, listFallback, listFallback.getClass());

            // Assertion handled via expectedException
        }

        @Test
        public void test_getCustomObject_exists_noException() throws Exception {
            doReturn(true).when(PreferenceHelper.preferences).contains(key);
            doReturn("json").when(PreferenceHelper.preferences).getString(key, null);
            UUID storedUUID = UUID.randomUUID();
            doReturn(storedUUID).when(gson).fromJson("json", UUID.class);

            UUID result = Whitebox.invokeMethod(PreferenceHelper.class, "getCustomObject", key, fallback, UUID.class);

            Assert.assertEquals(result, storedUUID);
        }

        @Test
        public void test_getCustomObject_exists_withJsonException() throws Exception {
            doReturn(true).when(PreferenceHelper.preferences).contains(key);
            doReturn("json").when(PreferenceHelper.preferences).getString(key, null);
            JsonSyntaxException jsonSyntaxException = mock(JsonSyntaxException.class);
            doThrow(jsonSyntaxException).when(gson).fromJson("json", UUID.class);
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("The object stored at the specified key is not an instance of java.util.UUID");
            expectedException.expectCause(is(jsonSyntaxException));

            UUID result = Whitebox.invokeMethod(PreferenceHelper.class, "getCustomObject", key, fallback, UUID.class);

            // Assertion handled via expectedException
        }
    }

    @PrepareForTest({PreferenceHelper.class, Gson.class, RuntimeException.class})
    public static class GetListTests extends PowerMockTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        List<UUID> fallback;
        Gson gson;
        String key;

        @Before
        public void beforeTest() throws Exception {
            PreferenceHelper.preferences = mock(SharedPreferences.class);
            key = "key";
            fallback = new ArrayList<>();
            gson = mock(Gson.class);
            whenNew(Gson.class).withNoArguments().thenReturn(gson);
        }

        @Test
        public void test_getList_fallback() {
            doReturn(false).when(PreferenceHelper.preferences).contains(key);

            List<UUID> result = PreferenceHelper.getList(key, fallback, UUID[].class);

            Assert.assertEquals(result, fallback);
        }

        @Test
        public void test_getList_noException_null() {
            doReturn(true).when(PreferenceHelper.preferences).contains(key);
            doReturn("json").when(PreferenceHelper.preferences).getString(key, null);
            UUID[] fromJson = null;
            doReturn(fromJson).when(gson).fromJson("json", UUID[].class);

            List<UUID> result = PreferenceHelper.getList(key, fallback, UUID[].class);

            Assert.assertNull(result);
        }

        @Test
        public void test_getList_noException_notNull() {
            doReturn(true).when(PreferenceHelper.preferences).contains(key);
            doReturn("json").when(PreferenceHelper.preferences).getString(key, null);
            UUID[] fromJson = new UUID[] {UUID.randomUUID()};
            doReturn(fromJson).when(gson).fromJson("json", UUID[].class);

            List<UUID> result = PreferenceHelper.getList(key, fallback, UUID[].class);

            Assert.assertEquals(result.size(), 1);
            Assert.assertEquals(result.get(0), fromJson[0]);
        }

        @Test
        public void test_getList_exception() {
            doReturn(true).when(PreferenceHelper.preferences).contains(key);
            doReturn("invalid json").when(PreferenceHelper.preferences).getString(key, null);
            RuntimeException runtimeException = mock(RuntimeException.class);
            doThrow(runtimeException).when(gson).fromJson("invalid json", UUID[].class);
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("The object stored at the specified key is not a UUID[]");
            expectedException.expectCause(is(runtimeException));

            List<UUID> result = PreferenceHelper.getList(key, fallback, UUID[].class);

            // Assertion handled via expectedException
        }
    }
}
