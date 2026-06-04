package com.insoft.laris.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionTanggal {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LAPORAN";
    public static final String AWAL = "AWAL";
    public static final String AKHIR = "AKHIR";

    public SessionTanggal(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String awal, String akhir) {
        editor.putString(AWAL, awal);
        editor.putString(AKHIR, akhir);
        editor.apply();
    }


    public HashMap<String, String> getSessionTanggal() {
        HashMap<String, String> tanggal = new HashMap<>();
        tanggal.put(AWAL, sharedPreferences.getString(AWAL, null));
        tanggal.put(AKHIR, sharedPreferences.getString(AKHIR, null));
        return tanggal;
    }


    public void bersihkantanggal() {
        editor.clear();
        editor.commit();

    }
}
