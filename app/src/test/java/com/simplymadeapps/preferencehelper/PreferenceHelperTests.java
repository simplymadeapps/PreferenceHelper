package com.simplymadeapps.preferencehelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(Enclosed.class)
public class PreferenceHelperTests {

    @RunWith(PowerMockRunner.class)
    @PrepareForTest({PreferenceManager.class})
    public static class InitTests {

        @Test
        public void test_init_null() {
            PreferenceHelper.preferences = null;
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

    public static class PutTests {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Before
        public void beforeTest() {
            PreferenceHelper.editor = mock(SharedPreferences.Editor.class);
        }

        @Test
        public void test_put_notInitialized() {
            expectedException.expect(IllegalStateException.class);
            expectedException.expectMessage("You must call PreferenceHelper.init() before any other PreferenceHelper methods");
            PreferenceHelper.editor = null;

            PreferenceHelper.put("key", "value");

            // expectedException handles assertion
        }

        @Test
        public void test_put_nullKey() {
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("Key cannot be null");

            PreferenceHelper.put(null, "value");

            // expectedException handles assertion
            verify(PreferenceHelper.editor, times(0)).commit();
        }

        @Test
        public void test_put_doubleNull() {
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("You must specify the stored object type when storing a null value using put(String key, T value, Class<T> type)");

            PreferenceHelper.put("key", null);

            verify(PreferenceHelper.editor, times(0)).commit();
        }

        @Test
        public void test_put_nullValue_primitive() {
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("Null primitive types (boolean, int, long, float) cannot be stored");

            PreferenceHelper.put("key", null, Integer.class);

            verify(PreferenceHelper.editor, times(0)).commit();
        }

        @Test
        public void test_put_nullValue_notPrimitive() {
            PreferenceHelper.put("key", null, String.class);

            verify(PreferenceHelper.editor, times(1)).putString("key",null);
            verify(PreferenceHelper.editor, times(1)).commit();
        }

        @Test
        public void test_put_String() {
            PreferenceHelper.put("key", "string");

            verify(PreferenceHelper.editor, times(1)).putString("key","string");
            verify(PreferenceHelper.editor, times(1)).commit();
        }

        @Test
        public void test_put_int() {
            PreferenceHelper.put("key", 1);

            verify(PreferenceHelper.editor, times(1)).putInt("key",1);
            verify(PreferenceHelper.editor, times(1)).commit();
        }

        @Test
        public void test_put_boolean() {
            PreferenceHelper.put("key", false);

            verify(PreferenceHelper.editor, times(1)).putBoolean("key",false);
            verify(PreferenceHelper.editor, times(1)).commit();
        }

        @Test
        public void test_put_long() {
            PreferenceHelper.put("key", 1000L);

            verify(PreferenceHelper.editor, times(1)).putLong("key",1000L);
            verify(PreferenceHelper.editor, times(1)).commit();
        }

        @Test
        public void test_put_float() {
            PreferenceHelper.put("key", 5.999F);

            verify(PreferenceHelper.editor, times(1)).putFloat("key",5.999F);
            verify(PreferenceHelper.editor, times(1)).commit();
        }

        @Test
        public void test_put_set() {
            Set<String> set = new HashSet<>();
            set.add("entry1");

            PreferenceHelper.put("key", set);

            verify(PreferenceHelper.editor, times(1)).putStringSet("key",set);
            verify(PreferenceHelper.editor, times(1)).commit();
        }

        @Test
        public void test_put_unknown() {
            UUID uuid = UUID.randomUUID();
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("Object type cannot be stored into preferences - " + uuid.getClass());

            PreferenceHelper.put("key", uuid);

            // expectedException handles assertion
            verify(PreferenceHelper.editor, times(0)).commit();
        }
    }

    public static class GetTests {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Before
        public void beforeTest() {
            PreferenceHelper.preferences = mock(SharedPreferences.class);
        }

        @Test
        public void test_get_notInitialized() {
            expectedException.expect(IllegalStateException.class);
            expectedException.expectMessage("You must call PreferenceHelper.init() before any other PreferenceHelper methods");
            PreferenceHelper.preferences = null;

            PreferenceHelper.get("key","fallback");

            // expectedException handles assertion
        }

        @Test
        public void test_get_nullKey() {
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("Key cannot be null");

            PreferenceHelper.get(null, "fallback");

            // expectedException handles assertion
            verify(PreferenceHelper.preferences, times(0)).getString(null, "fallback");
        }

        @Test
        public void test_get_doubleNull() {
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("You must specify the object type when retrieving a null value using get(String key, T fallback, Class<T> type)");

            PreferenceHelper.get("key", null);

            verify(PreferenceHelper.preferences, times(0)).getString(anyString(), anyString());
        }

        @Test
        public void test_get_nullValue_primitive() {
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("Null primitive types (boolean, int, long, float) cannot be retrieved");

            PreferenceHelper.get("key", null, Integer.class);

            verify(PreferenceHelper.preferences, times(0)).getInt(anyString(), anyInt());
        }

        @Test
        public void test_get_nullValue_notPrimitive() {
            doReturn("result").when(PreferenceHelper.preferences).getString("key",null);

            String result = PreferenceHelper.get("key", null, String.class);

            Assert.assertEquals(result, "result");
        }

//        @Test
//        public void test_get_String() {
//            doReturn("result").when(PreferenceHelper.preferences).getString("key","fallback");
//
//            String result = PreferenceHelper.get("key", "fallback");
//
//            Assert.assertEquals(result, "result");
//        }
//
//        @Test
//        public void test_get_int() {
//            doReturn(1).when(PreferenceHelper.preferences).getInt("key",0);
//
//            int result = PreferenceHelper.get("key", 0);
//
//            Assert.assertEquals(result, 1);
//        }
//
//        @Test
//        public void test_get_boolean() {
//            doReturn(true).when(PreferenceHelper.preferences).getBoolean("key", false);
//
//            boolean result = PreferenceHelper.get("key", false);
//
//            Assert.assertEquals(result, true);
//        }
//
//        @Test
//        public void test_get_long() {
//            doReturn(100L).when(PreferenceHelper.preferences).getLong("key", 25L);
//
//            long result = PreferenceHelper.get("key", 25L);
//
//            Assert.assertEquals(result, 100L);
//        }
//
//        @Test
//        public void test_get_float() {
//            doReturn(7.75F).when(PreferenceHelper.preferences).getFloat("key", 0.5F);
//
//            float result = PreferenceHelper.get("key", 0.5F);
//
//            Assert.assertEquals(result, 7.75F, 0);
//        }

        @Test
        public void test_get_set() {
            Set<String> fallback = new HashSet<>();
            fallback.add("entry1");
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
        public void test_get_unknown() {
            UUID uuid = UUID.randomUUID();
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage("Object type cannot be retrieved from preferences - " + uuid.getClass());

            PreferenceHelper.get("key", uuid);

            // expectedException handles assertion
        }
    }
}
