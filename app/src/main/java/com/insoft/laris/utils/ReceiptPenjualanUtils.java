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
import java.util.Locale;

public final class ReceiptPenjualanUtils {

    private static final int LEBAR_KARAKTER = 32;

    private ReceiptPenjualanUtils() {
    }

    public static String buatTeksStruk(
            SQLiteDatabase database,
            String nota
    ) throws Exception {

        String queryHeader =
                "SELECT p.nota, p.tanggal, p.belanja, p.bayar, p.kembali, " +
                        "c.nm_pelanggan, c.telepon, p.subtotal, p.total_dicount " +
                        "FROM penjualan p " +
                        "LEFT JOIN master_pelanggan c " +
                        "ON c.kd_pelanggan = p.kd_pelanggan " +
                        "WHERE p.nota = ?";

        String queryDetail =
                "SELECT nm_barang, jumlah, harga, total, subtotal, disk " +
                        "FROM penjualan_item " +
                        "WHERE nota = ?";

        try (
                Cursor cursorHeader = database.rawQuery(
                        queryHeader,
                        new String[]{nota}
                );

                Cursor cursorDetail = database.rawQuery(
                        queryDetail,
                        new String[]{nota}
                )
        ) {
            if (!cursorHeader.moveToFirst()) {
                throw new Exception(
                        "Data transaksi tidak ditemukan"
                );
            }

            String noNota = aman(cursorHeader.getString(0));
            String tanggal = aman(cursorHeader.getString(1));
            String namaPelanggan = cursorHeader.getString(5);
            String telepon = cursorHeader.getString(6);

            if (namaPelanggan == null
                    || namaPelanggan.trim().isEmpty()) {

                namaPelanggan = "Pelanggan Umum";
            }

            long totalBelanja = cursorHeader.getLong(2);
            long pembayaran = cursorHeader.getLong(3);
            long nilaiKembali = cursorHeader.getLong(4);
            long subtotalTransaksi = cursorHeader.getLong(7);
            long totalDiskon = cursorHeader.getLong(8);

            StringBuilder struk = new StringBuilder();

            /*
             * HEADER STRUK
             */
            struk.append(tengah(Constants.namaToko))
                    .append("\n");
            struk.append(tengah(Constants.alamatToko))
                    .append("\n");
            struk.append(tengah(Constants.hpToko))
                    .append("\n");

            struk.append(tengah("STRUK PENJUALAN"))
                    .append("\n");

            struk.append(garisTebal())
                    .append("\n");

            struk.append("Pelanggan: ")
                    .append(potongNilai(namaPelanggan, 21))
                    .append("\n");

            if (telepon != null && !telepon.trim().isEmpty()) {
                struk.append("Telepon  : ")
                        .append(potongNilai(
                                telepon.trim(),
                                21
                        ))
                        .append("\n");
            }

            struk.append("No. Nota : ")
                    .append(potongNilai(noNota, 21))
                    .append("\n");

            struk.append("Tanggal  : ")
                    .append(potongNilai(tanggal, 21))
                    .append("\n");

            struk.append(garisTebal())
                    .append("\n");

            struk.append("DETAIL BELANJA")
                    .append("\n");

            struk.append(garisTipis())
                    .append("\n");

            /*
             * DETAIL BARANG
             */
            int nomorUrut = 1;

            while (cursorDetail.moveToNext()) {
                String namaBarang =
                        aman(cursorDetail.getString(0));

                long jumlah =
                        cursorDetail.getLong(1);

                long hargaSatuan =
                        cursorDetail.getLong(2);

                long totalProduk =
                        cursorDetail.getLong(3);

                long subtotalProduk =
                        cursorDetail.getLong(4);

                long diskonProduk =
                        cursorDetail.getLong(5);

                if (subtotalProduk <= 0) {
                    subtotalProduk =
                            jumlah * hargaSatuan;
                }

                if (diskonProduk <= 0) {
                    diskonProduk =
                            subtotalProduk - totalProduk;
                }

                if (diskonProduk < 0) {
                    diskonProduk = 0;
                }

                if (totalProduk <= 0) {
                    totalProduk =
                            subtotalProduk - diskonProduk;
                }

                /*
                 * Nama barang dibuat beberapa baris
                 * agar tidak melewati 32 karakter.
                 */
                struk.append(
                        bungkusTeks(
                                nomorUrut + ". " + namaBarang,
                                LEBAR_KARAKTER
                        )
                ).append("\n");

                String qtyHarga =
                        "  " + jumlah
                                + " x "
                                + formatRupiah(hargaSatuan);

                /*
                 * Subtotal item diletakkan di sebelah kanan
                 * baris jumlah × harga.
                 */
                struk.append(
                        kiriKanan(
                                qtyHarga,
                                formatRupiah(subtotalProduk)
                        )
                ).append("\n");

                /*
                 * Baris Diskon dan Total hanya ditampilkan
                 * apabila item benar-benar memiliki diskon.
                 */
                if (diskonProduk > 0) {

                    struk.append(
                            kiriKanan(
                                    "  Diskon",
                                    "-" + formatRupiah(diskonProduk)
                            )
                    ).append("\n");

                    struk.append(
                            kiriKanan(
                                    "  Total",
                                    formatRupiah(totalProduk)
                            )
                    ).append("\n");
                }

                struk.append(garisTipis())
                        .append("\n");

                nomorUrut++;
            }

            /*
             * RINGKASAN
             */
            struk.append("RINGKASAN PEMBAYARAN")
                    .append("\n");

            struk.append(garisTipis())
                    .append("\n");

            struk.append(
                    kiriKanan(
                            "Subtotal",
                            formatRupiah(subtotalTransaksi)
                    )
            ).append("\n");

            struk.append(
                    kiriKanan(
                            "Total Diskon",
                            "-" + formatRupiah(totalDiskon)
                    )
            ).append("\n");

            struk.append(garisTipis())
                    .append("\n");

            struk.append(
                    kiriKanan(
                            "TOTAL",
                            formatRupiah(totalBelanja)
                    )
            ).append("\n");

            struk.append(
                    kiriKanan(
                            "Pembayaran",
                            formatRupiah(pembayaran)
                    )
            ).append("\n");

            if (totalBelanja > pembayaran) {
                long belumDibayar =
                        nilaiKembali > 0
                                ? nilaiKembali
                                : totalBelanja - pembayaran;

                struk.append(
                        kiriKanan(
                                "Belum Dibayar",
                                formatRupiah(belumDibayar)
                        )
                ).append("\n");

            } else {
                long kembalian =
                        nilaiKembali > 0
                                ? nilaiKembali
                                : pembayaran - totalBelanja;

                struk.append(
                        kiriKanan(
                                "Kembalian",
                                formatRupiah(kembalian)
                        )
                ).append("\n");
            }

            struk.append(garisTebal())
                    .append("\n");

            struk.append(tengah("TERIMA KASIH"))
                    .append("\n");

            struk.append(
                    tengah("TELAH BERBELANJA DI KAMI")
            ).append("\n");

            struk.append(garisTebal())
                    .append("\n\n\n");

            return struk.toString();
        }
    }

    public static Bitmap buatGambarStruk(
            SQLiteDatabase database,
            String nota
    ) throws Exception {

        String teks = buatTeksStruk(
                database,
                nota
        );

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
            SQLiteDatabase database,
            String nota
    ) throws Exception {

        File folder = new File(
                context.getCacheDir(),
                "struk_penjualan"
        );

        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException(
                    "Folder struk gagal dibuat"
            );
        }

        String namaNota =
                nota == null
                        ? String.valueOf(
                        System.currentTimeMillis()
                )
                        : nota.replaceAll(
                        "[^a-zA-Z0-9_-]",
                        ""
                );

        File file = new File(
                folder,
                "struk_penjualan_"
                        + namaNota
                        + ".png"
        );

        Bitmap bitmap = buatGambarStruk(
                database,
                nota
        );

        try (
                FileOutputStream outputStream =
                        new FileOutputStream(file)
        ) {
            boolean berhasil = bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    outputStream
            );

            if (!berhasil) {
                throw new IOException(
                        "Gambar struk gagal disimpan"
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

    public static String formatRupiah(long nilai) {
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
            return kiri
                    + "\n"
                    + rataKanan(kanan);
        }

        return kiri
                + ulang(" ", jumlahSpasi)
                + kanan;
    }

    private static String tengah(String text) {
        String nilai = aman(text);

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

    private static String rataKanan(String text) {
        String nilai = aman(text);

        if (nilai.length() >= LEBAR_KARAKTER) {
            return nilai.substring(
                    nilai.length() - LEBAR_KARAKTER
            );
        }

        return ulang(
                " ",
                LEBAR_KARAKTER - nilai.length()
        ) + nilai;
    }

    private static String bungkusTeks(
            String text,
            int lebar
    ) {
        if (text == null || text.trim().isEmpty()) {
            return "-";
        }

        String[] kata =
                text.trim().split("\\s+");

        StringBuilder hasil =
                new StringBuilder();

        StringBuilder baris =
                new StringBuilder();

        for (String item : kata) {
            int panjangBaru =
                    baris.length()
                            + item.length()
                            + (baris.length() > 0 ? 1 : 0);

            if (panjangBaru > lebar) {
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

    private static String potongNilai(
            String nilai,
            int maksimal
    ) {
        String hasil = aman(nilai);

        if (hasil.length() <= maksimal) {
            return hasil;
        }

        return hasil.substring(
                0,
                maksimal
        );
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
            String nilai,
            int jumlah
    ) {
        StringBuilder hasil =
                new StringBuilder();

        for (int i = 0; i < jumlah; i++) {
            hasil.append(nilai);
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
