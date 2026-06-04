package com.insoft.laris.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.insoft.laris.LoginActivity;
import com.insoft.laris.MainActivity;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final String ID = "ID";

    public static final String FULLNAME = "FULLNAME";
    public static final String USERNAME = "USERNAME";


    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String id, String fullname, String username) {
        editor.putBoolean(LOGIN, true);
        editor.putString(ID, id);
        editor.putString(FULLNAME, fullname);
        editor.putString(USERNAME, username);
        editor.apply();
    }

    public boolean isLoggin() {
        return sharedPreferences.getBoolean(LOGIN, false);
    }



    public void checkLogin(){
        if(this.isLoggin()){
            Intent i = new Intent(context, MainActivity.class);
            context.startActivity(i);
            ((LoginActivity)context).finish();
        }
    }


    public HashMap<String, String> getSessionData() {
        HashMap<String, String> user = new HashMap<>();
        user.put(ID, sharedPreferences.getString(ID, null));
        user.put(FULLNAME, sharedPreferences.getString(FULLNAME, null));
        user.put(USERNAME, sharedPreferences.getString(USERNAME, null));
        return user;
    }


    public void logout() {
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
        ((MainActivity) context).finish();
    }
}
