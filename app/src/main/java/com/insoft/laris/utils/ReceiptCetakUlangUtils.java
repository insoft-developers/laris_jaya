
package com.insoft.laris.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.insoft.laris.model.DetailStruk;
import com.insoft.laris.model.DetailStrukItem;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

public class ReceiptCetakUlangUtils {

    private static final int LEBAR_KARAKTER = 32;

    private static final DecimalFormat FORMAT_RUPIAH;

    static {
        DecimalFormatSymbols symbols =
                new DecimalFormatSymbols(new Locale("id", "ID"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');

        FORMAT_RUPIAH = new DecimalFormat("#,##0", symbols);
    }

    private ReceiptCetakUlangUtils() {
    }

    public static String buatTeksStruk(DetailStruk data) {

        if (data == null) {
            return "";
        }

        StringBuilder struk = new StringBuilder();

        struk.append(tengah(aman(Constants.namaToko))).append("\n");
        struk.append(tengah(aman(Constants.alamatToko))).append("\n");
        struk.append(tengah(aman(Constants.hpToko))).append("\n");
        struk.append(tengah("STRUK PENJUALAN")).append("\n");
        struk.append(garisTebal());

        String pelanggan = data.getNm_pelanggan();

        if (pelanggan == null || pelanggan.trim().isEmpty()) {
            pelanggan = "Pelanggan Umum";
        }

        struk.append("Pelanggan: ")
                .append(potongNilai(pelanggan, 21))
                .append("\n");

        if (data.getTelepon() != null &&
                !data.getTelepon().trim().isEmpty()) {
            struk.append("Telepon  : ")
                    .append(potongNilai(data.getTelepon(), 21))
                    .append("\n");
        }

        struk.append("No. Nota : ")
                .append(potongNilai(data.getNota(), 21))
                .append("\n");

        struk.append("Tanggal  : ")
                .append(potongNilai(formatTanggal(data.getTanggal()), 21))
                .append("\n");

        String kasir = data.getNmPengguna();

        if (kasir == null || kasir.trim().isEmpty()) {
            kasir = "-";
        }

        struk.append("Kasir    : ")
                .append(potongNilai(kasir, 21))
                .append("\n");

        struk.append(garisTebal());
        struk.append("DETAIL BELANJA\n");
        struk.append(garisTipis());

        List<DetailStrukItem> items = data.getItems();

        int nomor = 1;

        if (items != null) {
            for (DetailStrukItem item : items) {

                long subtotal = item.getSubtotal();

                if (subtotal <= 0) {
                    subtotal = item.getJumlah() * item.getHarga();
                }

                long diskon = item.getDisk();

                if (diskon <= 0) {
                    diskon = Math.max(
                            0,
                            subtotal - item.getTotal()
                    );
                }

                long total = item.getTotal();

                if (total <= 0) {
                    total = Math.max(0, subtotal - diskon);
                }

                struk.append(
                        bungkusTeks(
                                nomor++ + ". " +
                                        aman(item.getNm_barang()),
                                LEBAR_KARAKTER
                        )
                );

                struk.append(kiriKanan(
                        "  " + item.getJumlah() +
                                " x " + formatRupiah(item.getHarga()),
                        formatRupiah(subtotal)
                ));

                if (diskon > 0) {
                    struk.append(kiriKanan(
                            "  Diskon",
                            "-" + formatRupiah(diskon)
                    ));

                    struk.append(kiriKanan(
                            "  Total",
                            formatRupiah(total)
                    ));
                }

                struk.append(garisTipis());
            }
        }

        struk.append("RINGKASAN PEMBAYARAN\n");
        struk.append(garisTipis());

        struk.append(kiriKanan(
                "Subtotal",
                formatRupiah(data.getSubtotal())
        ));

        struk.append(kiriKanan(
                "Total Diskon",
                "-" + formatRupiah(data.getTotal_discount())
        ));

        struk.append(garisTipis());

        struk.append(kiriKanan(
                "TOTAL",
                formatRupiah(data.getBelanja())
        ));

        struk.append(kiriKanan(
                "Pembayaran",
                formatRupiah(data.getBayar())
        ));

        if (data.getBelanja() > data.getBayar()) {

            long belumDibayar = data.getKembali() > 0
                    ? data.getKembali()
                    : data.getBelanja() - data.getBayar();

            struk.append(kiriKanan(
                    "Belum Dibayar",
                    formatRupiah(belumDibayar)
            ));

        } else {

            long kembalian = data.getKembali() > 0
                    ? data.getKembali()
                    : data.getBayar() - data.getBelanja();

            struk.append(kiriKanan(
                    "Kembalian",
                    formatRupiah(kembalian)
            ));
        }

        struk.append(garisTebal());
        struk.append(tengah("TERIMA KASIH")).append("\n");
        struk.append(tengah("TELAH BERBELANJA DI KAMI")).append("\n");
        struk.append(garisTebal());
        struk.append("\n\n\n");

        return struk.toString();
    }


    public static Bitmap buatGambarStruk(DetailStruk data) {

        String teks = buatTeksStruk(data);
        String[] daftarBaris = teks.split("\n", -1);

        // Ukuran dasar sebelum diperbesar 2x
        int lebarDasar = 900;
        int paddingHorizontal = 35;
        int paddingVertical = 44;
        int tinggiBaris = 56;

        // Resolusi akhir 2x
        int scale = 2;
        int lebar = lebarDasar * scale;

        int tinggiDasar = paddingVertical * 2
                + Math.max(daftarBaris.length, 1) * tinggiBaris;

        int tinggi = tinggiDasar * scale;

        // Batas keamanan bitmap
        if (tinggi > 60000) {
            throw new IllegalArgumentException(
                    "Struk terlalu panjang untuk dibuat menjadi satu gambar."
            );
        }

        Bitmap bitmap = Bitmap.createBitmap(
                lebar,
                tinggi,
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);

        canvas.drawColor(android.graphics.Color.WHITE);

        // Semua proses gambar dilakukan pada skala 2x
        canvas.scale(scale, scale);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setColor(android.graphics.Color.BLACK);
        paint.setTextSize(38f);
        paint.setTypeface(Typeface.MONOSPACE);
        paint.setSubpixelText(true);
        paint.setHinting(Paint.HINTING_ON);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        float y = paddingVertical + 38f;

        for (String baris : daftarBaris) {

            canvas.drawText(
                    baris,
                    paddingHorizontal,
                    y,
                    paint
            );

            y += tinggiBaris;
        }

        return bitmap;
    }


    public static Uri simpanGambarStruk(
            Context context,
            DetailStruk data
    ) throws Exception {

        Bitmap bitmap = buatGambarStruk(data);

        File folder = new File(
                context.getCacheDir(),
                "struk_penjualan"
        );

        if (!folder.exists() && !folder.mkdirs()) {
            bitmap.recycle();
            throw new Exception("Gagal membuat folder struk");
        }

        String nota = data.getNota() == null
                ? "struk"
                : data.getNota().replaceAll(
                "[^a-zA-Z0-9_-]", "_"
        );

        File file = new File(
                folder,
                "struk_cetak_ulang_" + nota + ".png"
        );

        try (FileOutputStream outputStream =
                     new FileOutputStream(file)) {

            boolean berhasil = bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    outputStream
            );

            if (!berhasil) {
                throw new Exception("Gagal menyimpan gambar struk");
            }

            outputStream.flush();

        } finally {
            bitmap.recycle();
        }

        return FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".fileprovider",
                file
        );
    }

    private static String formatRupiah(long nilai) {
        return "Rp " + FORMAT_RUPIAH.format(nilai);
    }

    private static String kiriKanan(String kiri, String kanan) {

        int spasi = LEBAR_KARAKTER -
                kiri.length() -
                kanan.length();

        if (spasi < 1) {
            return kiri + "\n" +
                    rataKanan(kanan, LEBAR_KARAKTER);
        }

        return kiri + ulang(" ", spasi) +
                kanan + "\n";
    }

    private static String tengah(String teks) {

        if (teks == null) {
            teks = "";
        }

        if (teks.length() >= LEBAR_KARAKTER) {
            return teks.substring(0, LEBAR_KARAKTER);
        }

        int spasi = (LEBAR_KARAKTER - teks.length()) / 2;

        return ulang(" ", spasi) + teks;
    }

    private static String rataKanan(String teks, int lebar) {

        if (teks.length() >= lebar) {
            return teks;
        }

        return ulang(" ", lebar - teks.length()) + teks;
    }

    private static String bungkusTeks(String teks, int lebar) {

        StringBuilder hasil = new StringBuilder();

        if (teks == null) {
            return "\n";
        }

        for (int i = 0; i < teks.length(); i += lebar) {
            hasil.append(
                    teks,
                    i,
                    Math.min(i + lebar, teks.length())
            ).append("\n");
        }

        return hasil.toString();
    }

    private static String potongNilai(String teks, int maksimal) {

        if (teks == null) {
            return "-";
        }

        if (teks.length() > maksimal) {
            return teks.substring(0, maksimal);
        }

        return teks;
    }

    private static String garisTebal() {
        return ulang("=", LEBAR_KARAKTER) + "\n";
    }

    private static String garisTipis() {
        return ulang("-", LEBAR_KARAKTER) + "\n";
    }

    private static String ulang(String teks, int jumlah) {

        StringBuilder hasil = new StringBuilder();

        for (int i = 0; i < jumlah; i++) {
            hasil.append(teks);
        }

        return hasil.toString();
    }

    private static String aman(String teks) {
        return teks == null ? "-" : teks;
    }

    private static String formatTanggal(String tanggal) {

        if (tanggal == null || tanggal.trim().isEmpty()) {
            return "-";
        }

        try {
            SimpleDateFormat input =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            Date date = input.parse(tanggal);

            if (date != null) {
                SimpleDateFormat output =
                        new SimpleDateFormat("dd-MM-yyyy", Locale.US);

                return output.format(date);
            }

        } catch (Exception ignored) {
        }

        return tanggal;
    }


    public static ArrayList<Uri> simpanGambarStrukPerHalaman(
            Context context,
            DetailStruk data
    ) throws Exception {

        final int ITEM_PER_HALAMAN = 15;

        if (data == null) {
            throw new Exception("Data struk kosong");
        }

        ArrayList<Uri> hasil = new ArrayList<>();

        List<DetailStrukItem> items = data.getItems();

        if (items == null) {
            items = new ArrayList<>();
        }

        int totalHalaman = Math.max(
                1,
                (int) Math.ceil(items.size() / (double) ITEM_PER_HALAMAN)
        );

        File folder = new File(
                context.getCacheDir(),
                "struk_penjualan"
        );

        if (!folder.exists() && !folder.mkdirs()) {
            throw new Exception("Gagal membuat folder struk");
        }

        String nota = data.getNota() == null
                ? "struk"
                : data.getNota().replaceAll(
                "[^a-zA-Z0-9_-]", "_"
        );

        for (int halaman = 0; halaman < totalHalaman; halaman++) {

            int mulai = halaman * ITEM_PER_HALAMAN;

            int sampai = Math.min(
                    mulai + ITEM_PER_HALAMAN,
                    items.size()
            );

            String teks = buatTeksHalaman(
                    data,
                    items,
                    mulai,
                    sampai,
                    halaman + 1,
                    totalHalaman
            );

            Bitmap bitmap = buatGambarHalaman(teks);

            File file = new File(
                    folder,
                    "struk_" + nota + "_halaman_"
                            + (halaman + 1) + ".png"
            );

            try (FileOutputStream output =
                         new FileOutputStream(file)) {

                if (!bitmap.compress(
                        Bitmap.CompressFormat.PNG,
                        100,
                        output
                )) {
                    throw new Exception("Gagal menyimpan halaman "
                            + (halaman + 1));
                }

                output.flush();

            } finally {
                bitmap.recycle();
            }

            Uri uri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".fileprovider",
                    file
            );

            hasil.add(uri);
        }

        return hasil;
    }


    private static String buatTeksHalaman(
            DetailStruk data,
            List<DetailStrukItem> items,
            int mulai,
            int sampai,
            int halaman,
            int totalHalaman
    ) {

        StringBuilder struk = new StringBuilder();

        struk.append(tengah(aman(Constants.namaToko))).append("\n");
        struk.append(tengah(aman(Constants.alamatToko))).append("\n");
        struk.append(tengah(aman(Constants.hpToko))).append("\n");
        struk.append(tengah("STRUK PENJUALAN")).append("\n");
        struk.append(garisTebal());

        struk.append(tengah(
                "HALAMAN " + halaman + "/" + totalHalaman
        )).append("\n");

        struk.append(garisTipis());

        String pelanggan = data.getNm_pelanggan();

        if (pelanggan == null || pelanggan.trim().isEmpty()) {
            pelanggan = "Pelanggan Umum";
        }

        struk.append("Pelanggan: ")
                .append(potongNilai(pelanggan, 21))
                .append("\n");

        if (data.getTelepon() != null &&
                !data.getTelepon().trim().isEmpty()) {

            struk.append("Telepon  : ")
                    .append(potongNilai(data.getTelepon(), 21))
                    .append("\n");
        }

        struk.append("No. Nota : ")
                .append(potongNilai(data.getNota(), 21))
                .append("\n");

        struk.append("Tanggal  : ")
                .append(potongNilai(
                        formatTanggal(data.getTanggal()), 21
                ))
                .append("\n");

        String kasir = data.getNmPengguna();

        if (kasir == null || kasir.trim().isEmpty()) {
            kasir = "-";
        }

        struk.append("Kasir    : ")
                .append(potongNilai(kasir, 21))
                .append("\n");

        struk.append(garisTebal());
        struk.append("DETAIL BELANJA\n");
        struk.append(garisTipis());

        for (int i = mulai; i < sampai; i++) {

            DetailStrukItem item = items.get(i);

            long subtotal = item.getSubtotal();

            if (subtotal <= 0) {
                subtotal = item.getJumlah() * item.getHarga();
            }

            long diskon = item.getDisk();

            if (diskon <= 0) {
                diskon = Math.max(
                        0,
                        subtotal - item.getTotal()
                );
            }

            long total = item.getTotal();

            if (total <= 0) {
                total = Math.max(0, subtotal - diskon);
            }

            struk.append(bungkusTeks(
                    (i + 1) + ". " + aman(item.getNm_barang()),
                    LEBAR_KARAKTER
            ));

            struk.append(kiriKanan(
                    "  " + item.getJumlah()
                            + " x " + formatRupiah(item.getHarga()),
                    formatRupiah(subtotal)
            ));

            if (diskon > 0) {

                struk.append(kiriKanan(
                        "  Diskon",
                        "-" + formatRupiah(diskon)
                ));

                struk.append(kiriKanan(
                        "  Total",
                        formatRupiah(total)
                ));
            }

            struk.append(garisTipis());
        }

        // Ringkasan hanya pada halaman terakhir
        if (halaman == totalHalaman) {

            struk.append("RINGKASAN PEMBAYARAN\n");
            struk.append(garisTipis());

            struk.append(kiriKanan(
                    "Subtotal",
                    formatRupiah(data.getSubtotal())
            ));

            struk.append(kiriKanan(
                    "Total Diskon",
                    "-" + formatRupiah(data.getTotal_discount())
            ));

            struk.append(garisTipis());

            struk.append(kiriKanan(
                    "TOTAL",
                    formatRupiah(data.getBelanja())
            ));

            struk.append(kiriKanan(
                    "Pembayaran",
                    formatRupiah(data.getBayar())
            ));

            if (data.getBelanja() > data.getBayar()) {

                long belumDibayar = data.getKembali() > 0
                        ? data.getKembali()
                        : data.getBelanja() - data.getBayar();

                struk.append(kiriKanan(
                        "Belum Dibayar",
                        formatRupiah(belumDibayar)
                ));

            } else {

                long kembalian = data.getKembali() > 0
                        ? data.getKembali()
                        : data.getBayar() - data.getBelanja();

                struk.append(kiriKanan(
                        "Kembalian",
                        formatRupiah(kembalian)
                ));
            }

            struk.append(garisTebal());
            struk.append(tengah("TERIMA KASIH")).append("\n");
            struk.append(tengah("TELAH BERBELANJA DI KAMI")).append("\n");
            struk.append(garisTebal());
        }

        return struk.toString();
    }


    private static Bitmap buatGambarHalaman(String teks) {

        String[] baris = teks.split("\n", -1);

        int lebar = 576;
        int padding = 24;
        int ukuranFont = 28;
        int jarakBaris = 42;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setColor(android.graphics.Color.BLACK);
        paint.setTextSize(ukuranFont);
        paint.setTypeface(Typeface.MONOSPACE);
        paint.setSubpixelText(true);
        paint.setHinting(Paint.HINTING_ON);

        int tinggi = padding * 2
                + baris.length * jarakBaris;

        Bitmap bitmap = Bitmap.createBitmap(
                lebar,
                tinggi,
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);

        canvas.drawColor(android.graphics.Color.WHITE);

        float y = padding + ukuranFont;

        for (String teksBaris : baris) {

            canvas.drawText(
                    teksBaris,
                    padding,
                    y,
                    paint
            );

            y += jarakBaris;
        }

        return bitmap;
    }

}
