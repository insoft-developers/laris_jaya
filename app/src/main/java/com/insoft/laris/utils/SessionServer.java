package com.insoft.laris.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionServer {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "SERVER";
    public static final String DOMAIN = "DOMAIN";



    public SessionServer(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String domain) {
        editor.putString(DOMAIN, domain);
        editor.apply();
    }

    public HashMap<String, String> getSessionData() {
        HashMap<String, String> dom = new HashMap<>();
        dom.put(DOMAIN, sharedPreferences.getString(DOMAIN, null));

        return dom;
    }

}
