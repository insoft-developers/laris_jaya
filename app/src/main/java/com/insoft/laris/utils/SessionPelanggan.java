package com.insoft.laris.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionPelanggan {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "PELANGGAN";
    public static final String CCODE = "CUSTOMER_CODE";
    public static final String CNAME = "CUSTOMER_NAME";
    public static final String CADDRESS = "CUSTOMER_ADDRESS";
    public static final String CGRUP = "GRUP";


    public SessionPelanggan(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String id, String fullname, String alamat, String grup) {

        editor.putString(CCODE, id);
        editor.putString(CNAME, fullname);
        editor.putString(CADDRESS, alamat);
        editor.putString(CGRUP, grup);
        editor.apply();
    }


    public HashMap<String, String> getSessionPelanggan() {
        HashMap<String, String> pelanggan = new HashMap<>();
        pelanggan.put(CCODE, sharedPreferences.getString(CCODE, null));
        pelanggan.put(CNAME, sharedPreferences.getString(CNAME, null));
        pelanggan.put(CADDRESS, sharedPreferences.getString(CADDRESS, null));
        pelanggan.put(CGRUP, sharedPreferences.getString(CGRUP, null));
        return pelanggan;
    }


    public void logout_pelanggan() {
        editor.clear();
        editor.commit();

    }
}
