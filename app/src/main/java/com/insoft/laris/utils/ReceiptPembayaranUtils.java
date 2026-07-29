package com.insoft.laris.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.insoft.laris.admin.pembayaran.Pembayaran;

public final class ReceiptPembayaranUtils {

    private static final int JUMLAH_KARAKTER = 32;

    private ReceiptPembayaranUtils() {
    }

    public static String buatTeksStruk(Pembayaran data) {
        StringBuilder struk = new StringBuilder();

        struk.append(tengah(Constants.namaToko)).append("\n");
        struk.append(tengah(Constants.alamatToko)).append("\n");
        struk.append(tengah("STRUK PEMBAYARAN")).append("\n");
        struk.append(garis()).append("\n");

        struk.append("No Bayar : ")
                .append(aman(data.getNo_pembayaran()))
                .append("\n");

        struk.append("Tanggal  : ")
                .append(formatTanggal(data.getTanggal()))
                .append("\n");

        struk.append("Nota     : ")
                .append(aman(data.getNota()))
                .append("\n");

        struk.append("Pelanggan: ")
                .append(aman(data.getNm_pelanggan()))
                .append("\n");

        struk.append(garis()).append("\n");

        struk.append(barisNominal(
                "Nilai Nota",
                formatRupiah(data.getNilai_nota())
        )).append("\n");

        struk.append(barisNominal(
                "Pembayaran",
                formatRupiah(data.getPembayaran())
        )).append("\n");

        struk.append(barisNominal(
                "Sisa",
                formatRupiah(data.getSisa())
        )).append("\n");

        struk.append(garis()).append("\n");

        if (data.getKeterangan() != null
                && !data.getKeterangan().trim().isEmpty()) {

            struk.append("Keterangan:\n");
            struk.append(potongBaris(
                    data.getKeterangan().trim()
            ));
            struk.append("\n");
        }

        struk.append(garis()).append("\n");
        struk.append(tengah("Terima kasih")).append("\n");
        struk.append(tengah("Simpan struk ini")).append("\n");
        struk.append("\n\n\n");

        return struk.toString();
    }

    private static String barisNominal(
            String label,
            String nominal
    ) {
        if (label == null) {
            label = "";
        }

        if (nominal == null) {
            nominal = "Rp 0";
        }

        int jumlahSpasi =
                JUMLAH_KARAKTER
                        - label.length()
                        - nominal.length();

        if (jumlahSpasi < 1) {
            return label + "\n" + rataKanan(nominal);
        }

        return label
                + ulang(" ", jumlahSpasi)
                + nominal;
    }

    private static String tengah(String text) {
        String nilai = aman(text);

        if (nilai.length() >= JUMLAH_KARAKTER) {
            return nilai.substring(0, JUMLAH_KARAKTER);
        }

        int spasiKiri =
                (JUMLAH_KARAKTER - nilai.length()) / 2;

        return ulang(" ", spasiKiri) + nilai;
    }

    private static String rataKanan(String text) {
        String nilai = aman(text);

        if (nilai.length() >= JUMLAH_KARAKTER) {
            return nilai.substring(
                    nilai.length() - JUMLAH_KARAKTER
            );
        }

        return ulang(
                " ",
                JUMLAH_KARAKTER - nilai.length()
        ) + nilai;
    }

    private static String potongBaris(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "-";
        }

        String[] kata = text.trim().split("\\s+");
        StringBuilder hasil = new StringBuilder();
        StringBuilder baris = new StringBuilder();

        for (String item : kata) {
            int panjangBaru =
                    baris.length()
                            + item.length()
                            + (baris.length() > 0 ? 1 : 0);

            if (panjangBaru > JUMLAH_KARAKTER) {
                hasil.append(baris).append("\n");
                baris.setLength(0);
            }

            if (baris.length() > 0) {
                baris.append(" ");
            }

            baris.append(item);
        }

        if (baris.length() > 0) {
            hasil.append(baris);
        }

        return hasil.toString();
    }

    private static String garis() {
        return ulang("-", JUMLAH_KARAKTER);
    }

    private static String ulang(String value, int count) {
        StringBuilder hasil = new StringBuilder();

        for (int i = 0; i < count; i++) {
            hasil.append(value);
        }

        return hasil.toString();
    }

    public static String aman(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }

        return value.trim();
    }

    public static String formatRupiah(long nilai) {
        NumberFormat formatter =
                NumberFormat.getCurrencyInstance(
                        new Locale("id", "ID")
                );

        formatter.setMaximumFractionDigits(0);
        formatter.setMinimumFractionDigits(0);

        return formatter.format(nilai)
                .replace("Rp", "Rp ");
    }

    private static String formatTanggal(String tanggal) {
        if (tanggal == null || tanggal.trim().isEmpty()) {
            return "-";
        }

        try {
            SimpleDateFormat input =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    );

            SimpleDateFormat output =
                    new SimpleDateFormat(
                            "dd-MM-yyyy",
                            Locale.getDefault()
                    );

            input.setLenient(false);

            Date date = input.parse(tanggal.trim());

            return date == null
                    ? "-"
                    : output.format(date);

        } catch (ParseException e) {
            return tanggal;
        }
    }

    public static Bitmap buatGambarStruk(Pembayaran data) {
        String teks = buatTeksStruk(data);

        String[] baris = teks.split("\n", -1);

        int lebar = 384;
        int padding = 20;
        int tinggiBaris = 27;
        int tinggi = padding * 2
                + Math.max(baris.length, 1) * tinggiBaris;

        Bitmap bitmap = Bitmap.createBitmap(
                lebar,
                tinggi,
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(19f);
        paint.setTypeface(Typeface.MONOSPACE);

        float posisiY = padding + 20;

        for (String item : baris) {
            canvas.drawText(
                    item,
                    padding,
                    posisiY,
                    paint
            );

            posisiY += tinggiBaris;
        }

        return bitmap;
    }

    public static Uri simpanGambarStruk(
            Context context,
            Pembayaran data
    ) throws IOException {

        File folder = new File(
                context.getCacheDir(),
                "struk_pembayaran"
        );

        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException(
                    "Folder struk tidak dapat dibuat"
            );
        }

        String nomor = data.getNo_pembayaran() == null
                ? String.valueOf(System.currentTimeMillis())
                : data.getNo_pembayaran()
                .replaceAll("[^a-zA-Z0-9_-]", "");

        File file = new File(
                folder,
                "struk_" + nomor + ".png"
        );

        Bitmap bitmap = buatGambarStruk(data);

        try (FileOutputStream output =
                     new FileOutputStream(file)) {

            boolean berhasil = bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    output
            );

            if (!berhasil) {
                throw new IOException(
                        "Gambar struk gagal disimpan"
                );
            }

            output.flush();
        } finally {
            bitmap.recycle();
        }

        return FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".fileprovider",
                file
        );
    }
}