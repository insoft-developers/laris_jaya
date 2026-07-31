package com.insoft.laris.utils;

import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Fungsi {

    public static String formatTanggal(String tanggal) {
        if (tanggal == null || tanggal.trim().isEmpty()) {
            return "-";
        }

        try {
            SimpleDateFormat formatDatabase =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            SimpleDateFormat formatTampilan =
                    new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

            Date date = formatDatabase.parse(tanggal);

            return date != null ? formatTampilan.format(date) : "-";

        } catch (ParseException e) {
            return tanggal;
        }
    }

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
