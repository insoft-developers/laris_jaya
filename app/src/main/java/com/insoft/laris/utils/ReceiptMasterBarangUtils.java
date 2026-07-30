package com.insoft.laris.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class ReceiptMasterBarangUtils {

    private static final int LEBAR_KARAKTER = 32;

    private ReceiptMasterBarangUtils() {
    }

    public static String buatTeksStruk(
            SQLiteDatabase database
    ) throws Exception {

        String query =
                "SELECT kd_barang, nm_barang, stok, " +
                        "harga_jual, harga_reseller " +
                        "FROM master_barang " +
                        "ORDER BY nm_barang ASC";

        try (Cursor cursor = database.rawQuery(query, null)) {

            if (cursor.getCount() == 0) {
                throw new Exception(
                        "Data master barang masih kosong"
                );
            }

            String tanggalCetak =
                    new SimpleDateFormat(
                            "dd-MM-yyyy HH:mm",
                            Locale.getDefault()
                    ).format(new Date());

            StringBuilder struk = new StringBuilder();

            /*
             * HEADER TOKO
             */
            struk.append(tengah(Constants.namaToko))
                    .append("\n");

            if (Constants.alamatToko != null
                    && !Constants.alamatToko.trim().isEmpty()) {

                struk.append(
                        bungkusTeks(
                                Constants.alamatToko,
                                LEBAR_KARAKTER
                        )
                ).append("\n");
            }

            if (Constants.hpToko != null
                    && !Constants.hpToko.trim().isEmpty()) {

                struk.append(tengah(Constants.hpToko))
                        .append("\n");
            }

            struk.append(garisTebal())
                    .append("\n");

            struk.append(tengah("DAFTAR MASTER BARANG"))
                    .append("\n");

            struk.append(garisTebal())
                    .append("\n");

            struk.append(
                    kiriKanan(
                            "Tanggal",
                            tanggalCetak
                    )
            ).append("\n");

            struk.append(
                    kiriKanan(
                            "Jumlah Barang",
                            cursor.getCount() + " jenis"
                    )
            ).append("\n");

            struk.append(garisTebal())
                    .append("\n");

            long totalStok = 0;
            int nomor = 1;

            while (cursor.moveToNext()) {

                String kodeBarang =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "kd_barang"
                                )
                        );

                String namaBarang =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "nm_barang"
                                )
                        );

                long stok =
                        cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                        "stok"
                                )
                        );

                long hargaJual =
                        cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                        "harga_jual"
                                )
                        );

                long hargaReseller =
                        cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                        "harga_reseller"
                                )
                        );

                totalStok += stok;

                /*
                 * Nama barang.
                 */
                struk.append(
                        bungkusTeks(
                                nomor + ". " + aman(namaBarang),
                                LEBAR_KARAKTER
                        )
                ).append("\n");

                /*
                 * Kode barang.
                 */
                struk.append(
                        bungkusTeks(
                                "   Kode: " + aman(kodeBarang),
                                LEBAR_KARAKTER
                        )
                ).append("\n");

                /*
                 * Informasi stok.
                 */
                struk.append(
                        kiriKanan(
                                "   Stok",
                                String.valueOf(stok)
                        )
                ).append("\n");

                /*
                 * Harga jual.
                 */
                struk.append(
                        kiriKanan(
                                "   Harga",
                                formatRupiah(hargaJual)
                        )
                ).append("\n");

                /*
                 * Harga reseller hanya tampil
                 * jika nilainya lebih dari nol.
                 */
                if (hargaReseller > 0) {
                    struk.append(
                            kiriKanan(
                                    "   Reseller",
                                    formatRupiah(hargaReseller)
                            )
                    ).append("\n");
                }

                struk.append(garisTipis())
                        .append("\n");

                nomor++;
            }

            /*
             * RINGKASAN
             */
            struk.append(
                    kiriKanan(
                            "TOTAL JENIS",
                            cursor.getCount()
                                    + " barang"
                    )
            ).append("\n");

            struk.append(
                    kiriKanan(
                            "TOTAL STOK",
                            String.valueOf(totalStok)
                    )
            ).append("\n");

            struk.append(garisTebal())
                    .append("\n");

            struk.append(tengah("AKHIR DAFTAR BARANG"))
                    .append("\n");

            struk.append(garisTebal())
                    .append("\n\n\n\n");

            return struk.toString();
        }
    }

    public static Bitmap buatGambarStruk(
            SQLiteDatabase database
    ) throws Exception {

        String teks = buatTeksStruk(database);

        String[] daftarBaris =
                teks.split("\n", -1);

        int lebarBitmap = 384;
        int paddingHorizontal = 18;
        int paddingVertical = 22;
        int tinggiBaris = 27;

        int tinggiBitmap =
                paddingVertical * 2
                        + Math.max(
                        daftarBaris.length,
                        1
                ) * tinggiBaris;

        Bitmap bitmap = Bitmap.createBitmap(
                lebarBitmap,
                tinggiBitmap,
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        Paint paint = new Paint(
                Paint.ANTI_ALIAS_FLAG
        );

        paint.setColor(Color.BLACK);
        paint.setTextSize(19f);
        paint.setTypeface(Typeface.MONOSPACE);

        float posisiY =
                paddingVertical + 19f;

        for (String baris : daftarBaris) {
            canvas.drawText(
                    baris,
                    paddingHorizontal,
                    posisiY,
                    paint
            );

            posisiY += tinggiBaris;
        }

        return bitmap;
    }

    public static Uri simpanGambarStruk(
            Context context,
            SQLiteDatabase database
    ) throws Exception {

        File folder = new File(
                context.getCacheDir(),
                "master_barang"
        );

        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException(
                    "Folder master barang gagal dibuat"
            );
        }

        String namaFile =
                new SimpleDateFormat(
                        "yyyyMMdd_HHmmss",
                        Locale.getDefault()
                ).format(new Date());

        File file = new File(
                folder,
                "master_barang_"
                        + namaFile
                        + ".png"
        );

        Bitmap bitmap =
                buatGambarStruk(database);

        try (
                FileOutputStream outputStream =
                        new FileOutputStream(file)
        ) {
            boolean berhasil =
                    bitmap.compress(
                            Bitmap.CompressFormat.PNG,
                            100,
                            outputStream
                    );

            if (!berhasil) {
                throw new IOException(
                        "Gambar master barang gagal disimpan"
                );
            }

            outputStream.flush();

        } finally {
            bitmap.recycle();
        }

        return FileProvider.getUriForFile(
                context,
                context.getPackageName()
                        + ".fileprovider",
                file
        );
    }

    private static String formatRupiah(long nilai) {
        NumberFormat formatter =
                NumberFormat.getNumberInstance(
                        new Locale("id", "ID")
                );

        formatter.setMaximumFractionDigits(0);
        formatter.setMinimumFractionDigits(0);

        return "Rp " + formatter.format(nilai);
    }

    private static String kiriKanan(
            String kiri,
            String kanan
    ) {
        kiri = kiri == null ? "" : kiri;
        kanan = kanan == null ? "" : kanan;

        int jumlahSpasi =
                LEBAR_KARAKTER
                        - kiri.length()
                        - kanan.length();

        if (jumlahSpasi < 1) {
            return bungkusTeks(
                    kiri,
                    LEBAR_KARAKTER
            ) + "\n" + rataKanan(kanan);
        }

        return kiri
                + ulang(" ", jumlahSpasi)
                + kanan;
    }

    private static String tengah(String teks) {
        String nilai = aman(teks);

        if (nilai.length() >= LEBAR_KARAKTER) {
            return nilai.substring(
                    0,
                    LEBAR_KARAKTER
            );
        }

        int spasiKiri =
                (LEBAR_KARAKTER - nilai.length())
                        / 2;

        return ulang(" ", spasiKiri)
                + nilai;
    }

    private static String rataKanan(String teks) {
        String nilai = aman(teks);

        if (nilai.length() >= LEBAR_KARAKTER) {
            return nilai.substring(
                    nilai.length()
                            - LEBAR_KARAKTER
            );
        }

        return ulang(
                " ",
                LEBAR_KARAKTER - nilai.length()
        ) + nilai;
    }

    private static String bungkusTeks(
            String teks,
            int lebar
    ) {
        if (teks == null || teks.trim().isEmpty()) {
            return "-";
        }

        String[] kata =
                teks.trim().split("\\s+");

        StringBuilder hasil =
                new StringBuilder();

        StringBuilder baris =
                new StringBuilder();

        for (String item : kata) {
            int panjangBaru =
                    baris.length()
                            + item.length()
                            + (baris.length() > 0 ? 1 : 0);

            if (panjangBaru > lebar
                    && baris.length() > 0) {

                if (hasil.length() > 0) {
                    hasil.append("\n");
                }

                hasil.append(baris);
                baris.setLength(0);
            }

            if (baris.length() > 0) {
                baris.append(" ");
            }

            baris.append(item);
        }

        if (baris.length() > 0) {
            if (hasil.length() > 0) {
                hasil.append("\n");
            }

            hasil.append(baris);
        }

        return hasil.toString();
    }

    private static String garisTebal() {
        return ulang(
                "=",
                LEBAR_KARAKTER
        );
    }

    private static String garisTipis() {
        return ulang(
                "-",
                LEBAR_KARAKTER
        );
    }

    private static String ulang(
            String teks,
            int jumlah
    ) {
        StringBuilder hasil =
                new StringBuilder();

        for (int i = 0; i < jumlah; i++) {
            hasil.append(teks);
        }

        return hasil.toString();
    }

    private static String aman(String nilai) {
        if (nilai == null || nilai.trim().isEmpty()) {
            return "-";
        }

        return nilai.trim();
    }
}