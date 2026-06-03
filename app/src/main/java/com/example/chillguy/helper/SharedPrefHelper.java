package com.example.chillguy.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefHelper {

    private static final String PREF_NAME = "ChillGuyPrefs";
    private static final String KEY_LOGGED_IN  = "isLoggedIn";
    private static final String KEY_USERNAME   = "username";
    private static final String KEY_EMAIL      = "email";
    private static final String KEY_DARK_THEME = "isDarkTheme";

    private final SharedPreferences prefs;

    public SharedPrefHelper(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setLoggedIn(boolean status, String username, String email) {
        prefs.edit()
                .putBoolean(KEY_LOGGED_IN, status)
                .putString(KEY_USERNAME,   username)
                .putString(KEY_EMAIL,      email)
                .apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "User");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public void logout() {
        boolean savedTheme = isDarkTheme();
        prefs.edit().clear().apply();
        setDarkTheme(savedTheme);
    }

    public void setDarkTheme(boolean isDark) {
        prefs.edit().putBoolean(KEY_DARK_THEME, isDark).apply();
    }

    public boolean isDarkTheme() {
        return prefs.getBoolean(KEY_DARK_THEME, false);
    }
}