package com.example.chillguy.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefHelper {

    private static final String PREF_NAME   = "ChillGuyPrefs";
    private static final String KEY_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME  = "username";
    private static final String KEY_EMAIL     = "email";
    private static final String KEY_PASSWORD  = "password";
    private static final String KEY_DARK_THEME = "isDarkTheme";

    private final SharedPreferences prefs;

    public SharedPrefHelper(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    public void register(String username, String email, String password) {
        prefs.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_EMAIL,    email)
                .putString(KEY_PASSWORD, password)
                .apply();
    }

    public void setLoggedIn(boolean status) {
        prefs.edit().putBoolean(KEY_LOGGED_IN, status).apply();
    }

    public boolean checkLogin(String username, String password) {
        String savedUsername = prefs.getString(KEY_USERNAME, "");
        String savedPassword = prefs.getString(KEY_PASSWORD, "");
        return savedUsername.equalsIgnoreCase(username)
                && savedPassword.equals(password);
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
        boolean savedTheme    = isDarkTheme();
        String  savedUsername = getUsername();
        String  savedEmail    = getEmail();
        String  savedPassword = prefs.getString(KEY_PASSWORD, "");

        prefs.edit().clear().apply();

        prefs.edit()
                .putString(KEY_USERNAME,  savedUsername)
                .putString(KEY_EMAIL,     savedEmail)
                .putString(KEY_PASSWORD,  savedPassword)
                .putBoolean(KEY_DARK_THEME, savedTheme)
                .apply();
    }

    public void setDarkTheme(boolean isDark) {
        prefs.edit().putBoolean(KEY_DARK_THEME, isDark).apply();
    }

    public boolean isDarkTheme() {
        return prefs.getBoolean(KEY_DARK_THEME, false);
    }
}