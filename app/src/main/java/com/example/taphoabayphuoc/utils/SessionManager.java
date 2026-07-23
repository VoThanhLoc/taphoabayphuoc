package com.example.taphoabayphuoc.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "taphoabayphuoc";

    private static final String KEY_LOGIN = "is_login";

    private static final String KEY_UID = "uid";

    private final SharedPreferences preferences;
    private static final String KEY_EMAIL = "email";

    public void saveEmail(String email) {
        preferences.edit().putString(KEY_EMAIL, email).apply();
    }

    public String getEmail() {
        return preferences.getString(KEY_EMAIL, "");
    }
    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setLogin(boolean login) {
        preferences.edit().putBoolean(KEY_LOGIN, login).apply();
    }

    public boolean isLogin() {
        return preferences.getBoolean(KEY_LOGIN, false);
    }

    public void saveUid(String uid) {
        preferences.edit().putString(KEY_UID, uid).apply();
    }

    public String getUid() {
        return preferences.getString(KEY_UID, "");
    }

    public void logout() {
        preferences.edit().clear().apply();
    }
}