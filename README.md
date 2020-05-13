# Quick Periodic Job Scheduler
A more painless way to set/get from SharedPreferences.
# Getting Started
Add it in your root build.gradle at the end of repositories:
```
allprojects {
    repositories {
		maven { url 'https://jitpack.io' }
	}
}
```
Add the following to the dependencies section of app/build.gradle (check Releases for the latest version):
```
implementation 'com.github.simplymadeapps:PreferenceHelper:1.1.1'
```

# Usage
Init the library in the `Application.onCreate()` method.  You can also put this at that top of your start activity's `onCreate()`.  All that matters is `init()` is called before any other calls to the PreferenceHelper.
```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceHelper.init(this);
    }
}
```
Now that the library has been initialized, you can easily use `put()` and `get()`.
Storing an object: `PreferenceHelper.put("current_user_name", "John Doe");`
Getting an object: `String username = PreferenceHelper.get("current_user_name", "No Name");`

The library is using the object type of the input or fallback to determine what SharedPreference method to use (ex, putString, putLong, putInt, etc).
This can cause confusion when you pass in a null object as one of these parameters.  If you need to store a null object or retrieve an object with a null fallback, you should pass in that object type.
Storing a null string: `PreferenceHelper.put("current_user_name", null, String.class);`
Getting a null set: `Set<String> usernames = PreferenceHelper.get("all_names", null, Set.class);`
Note that primitive types (int, boolean, float, long) cannot be null.

You can also check if a value has been stored with: `boolean key_exists = PreferenceHelper.contains("some_key")`

The library can only store what the default SharedPreferences stores (String, int, boolean, float, long, Set<String>).  It currently does not support storing of a custom objects.  This may be expanded upon in the future.

# Contributing
1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

# License
The library is available as open source under the terms of the [MIT License](http://opensource.org/licenses/MIT).