package com.insoft.laris.utils;

import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Fungsi {

    public static int integ(String angka) {
        int angkabaru = Integer.parseInt(angka);
        return angkabaru;
    }

    public static String st(int angka) {
        String angkabaru = String.valueOf(angka);
        return angkabaru;
    }



    public void notif(String text, RelativeLayout rlnotif, TextView textnotif) {
        rlnotif.setVisibility(View.VISIBLE);
        textnotif.setText(text);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                rlnotif.setVisibility(View.GONE);
            }
        }, 3000);
    }
}
