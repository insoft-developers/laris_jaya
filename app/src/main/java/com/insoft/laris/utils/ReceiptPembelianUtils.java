package com.insoft.laris.utils;


import com.insoft.laris.admin.pembelian.Pembelian;
import com.insoft.laris.admin.pembelian.PembelianDetail;

import java.text.DecimalFormat;
import java.util.List;

public final class ReceiptPembelianUtils {

    private static final int LEBAR_KARAKTER = 32;

    private ReceiptPembelianUtils() {
    }

    public static String buatStruk(
            Pembelian pembelian,
            List<PembelianDetail> daftarProduk,
            String namaSupplier,
            String namaPengguna
    ) {
        StringBuilder struk = new StringBuilder();

        /*
         * HEADER TOKO
         */
        tambahTeksTengah(struk, aman(Constants.namaToko));
        tambahTeksTengah(struk, aman(Constants.alamatToko));
        tambahTeksTengah(struk, aman(Constants.hpToko));

        struk.append(garisTebal()).append("\n");
        tambahTeksTengah(struk, "STRUK PEMBELIAN");
        struk.append(garisTebal()).append("\n");

        /*
         * INFORMASI PEMBELIAN
         */
        struk.append(
                kiriKanan(
                        "Nota",
                        aman(pembelian.getNota())
                )
        ).append("\n");

        struk.append(
                kiriKanan(
                        "Tanggal",
                        aman(pembelian.getTanggal())
                )
        ).append("\n");

        struk.append(
                kiriKanan(
                        "Supplier",
                        aman(namaSupplier)
                )
        ).append("\n");

        struk.append(
                kiriKanan(
                        "Kode",
                        aman(pembelian.getKd_supplier())
                )
        ).append("\n");

        struk.append(
                kiriKanan(
                        "Pengguna",
                        aman(namaPengguna)
                )
        ).append("\n");

        if (!aman(pembelian.getStatus()).isEmpty()) {
            struk.append(
                    kiriKanan(
                            "Status",
                            aman(pembelian.getStatus())
                    )
            ).append("\n");
        }

        struk.append(garisTebal()).append("\n");

        /*
         * DETAIL PRODUK
         */
        if (daftarProduk == null || daftarProduk.isEmpty()) {
            tambahTeksTengah(struk, "Tidak ada detail produk");
            struk.append(garisTipis()).append("\n");
        } else {
            int nomor = 1;

            for (PembelianDetail produk : daftarProduk) {
                String namaProduk = aman(produk.getNm_barang());

                if (namaProduk.isEmpty()) {
                    namaProduk = "Produk";
                }

                tambahTeksBungkus(
                        struk,
                        nomor + ". " + namaProduk
                );

                if (!aman(produk.getKd_barang()).isEmpty()) {
                    tambahTeksBungkus(
                            struk,
                            "   " + aman(produk.getKd_barang())
                    );
                }

                int jumlah = produk.getJumlah();
                int harga = produk.getHarga();
                int subtotalProduk = produk.getSubtotal();
                int diskonProduk = produk.getDiskon();
                int totalProduk = produk.getTotal();

                /*
                 * Jika subtotal dari API kosong,
                 * hitung dari jumlah dikali harga.
                 */
                if (subtotalProduk <= 0) {
                    subtotalProduk = jumlah * harga;
                }

                /*
                 * Jika total dari API kosong,
                 * hitung dari subtotal dikurangi diskon.
                 */
                if (totalProduk <= 0) {
                    totalProduk = subtotalProduk - diskonProduk;
                }

                String jumlahHarga =
                        "   "
                                + jumlah
                                + " x "
                                + formatRupiah(harga);

                struk.append(
                        kiriKanan(
                                jumlahHarga,
                                formatRupiah(subtotalProduk)
                        )
                ).append("\n");

                /*
                 * Diskon dan total produk hanya dicetak
                 * jika produk memiliki diskon.
                 */
                if (diskonProduk > 0) {
                    struk.append(
                            kiriKanan(
                                    "   Diskon",
                                    "-" + formatRupiah(diskonProduk)
                            )
                    ).append("\n");

                    struk.append(
                            kiriKanan(
                                    "   Total",
                                    formatRupiah(totalProduk)
                            )
                    ).append("\n");
                }

                struk.append(garisTipis()).append("\n");

                nomor++;
            }
        }

        /*
         * RINGKASAN PEMBELIAN
         */
        struk.append(
                kiriKanan(
                        "Subtotal",
                        formatRupiah(pembelian.getSubtotal())
                )
        ).append("\n");

        if (pembelian.getTotal_discount() > 0) {
            struk.append(
                    kiriKanan(
                            "Total Diskon",
                            "-"
                                    + formatRupiah(
                                    pembelian.getTotal_discount()
                            )
                    )
            ).append("\n");
        }

        struk.append(garisTebal()).append("\n");

        struk.append(
                kiriKanan(
                        "TOTAL",
                        formatRupiah(
                                pembelian.getTotal_pembelian()
                        )
                )
        ).append("\n");

        struk.append(garisTebal()).append("\n");

        /*
         * KETERANGAN
         */
        String keterangan =
                aman(pembelian.getKeterangan());

        if (!keterangan.isEmpty()) {
            struk.append("Keterangan:\n");

            tambahTeksBungkus(
                    struk,
                    keterangan
            );

            struk.append(garisTipis()).append("\n");
        }

        tambahTeksTengah(
                struk,
                "Barang telah diterima"
        );

        tambahTeksTengah(
                struk,
                "Terima kasih"
        );

        /*
         * Feed agar kertas keluar dari printer.
         */
        struk.append("\n\n\n");

        return struk.toString();
    }

    private static String formatRupiah(int nilai) {
        DecimalFormat format =
                new DecimalFormat("#,###");

        String hasil =
                format.format(Math.abs(nilai))
                        .replace(",", ".");

        return "Rp " + hasil;
    }

    private static String kiriKanan(
            String kiri,
            String kanan
    ) {
        kiri = aman(kiri);
        kanan = aman(kanan);

        /*
         * Jika teks kanan terlalu panjang,
         * potong sesuai lebar printer.
         */
        if (kanan.length() >= LEBAR_KARAKTER) {
            return potongTeks(
                    kanan,
                    LEBAR_KARAKTER
            );
        }

        int maksimalKiri =
                LEBAR_KARAKTER
                        - kanan.length()
                        - 1;

        if (kiri.length() > maksimalKiri) {
            kiri = potongTeks(
                    kiri,
                    maksimalKiri
            );
        }

        int jumlahSpasi =
                LEBAR_KARAKTER
                        - kiri.length()
                        - kanan.length();

        StringBuilder hasil =
                new StringBuilder(kiri);

        for (int i = 0; i < jumlahSpasi; i++) {
            hasil.append(" ");
        }

        hasil.append(kanan);

        return hasil.toString();
    }

    private static void tambahTeksTengah(
            StringBuilder hasil,
            String teks
    ) {
        teks = aman(teks);

        if (teks.isEmpty()) {
            return;
        }

        if (teks.length() > LEBAR_KARAKTER) {
            tambahTeksBungkus(
                    hasil,
                    teks
            );

            return;
        }

        int spasiKiri =
                (LEBAR_KARAKTER - teks.length()) / 2;

        for (int i = 0; i < spasiKiri; i++) {
            hasil.append(" ");
        }

        hasil.append(teks).append("\n");
    }

    private static void tambahTeksBungkus(
            StringBuilder hasil,
            String teks
    ) {
        teks = aman(teks);

        if (teks.isEmpty()) {
            return;
        }

        String[] kata = teks.split("\\s+");
        StringBuilder baris = new StringBuilder();

        for (String item : kata) {
            if (baris.length() == 0) {
                baris.append(item);

            } else if (
                    baris.length()
                            + 1
                            + item.length()
                            <= LEBAR_KARAKTER
            ) {
                baris.append(" ").append(item);

            } else {
                hasil.append(baris).append("\n");
                baris.setLength(0);
                baris.append(item);
            }
        }

        if (baris.length() > 0) {
            hasil.append(baris).append("\n");
        }
    }

    private static String garisTebal() {
        return ulangKarakter(
                "=",
                LEBAR_KARAKTER
        );
    }

    private static String garisTipis() {
        return ulangKarakter(
                "-",
                LEBAR_KARAKTER
        );
    }

    private static String ulangKarakter(
            String karakter,
            int jumlah
    ) {
        StringBuilder hasil =
                new StringBuilder();

        for (int i = 0; i < jumlah; i++) {
            hasil.append(karakter);
        }

        return hasil.toString();
    }

    private static String potongTeks(
            String teks,
            int maksimal
    ) {
        if (teks == null) {
            return "";
        }

        if (teks.length() <= maksimal) {
            return teks;
        }

        return teks.substring(0, maksimal);
    }

    private static String aman(String nilai) {
        return nilai == null ? "" : nilai.trim();
    }
}