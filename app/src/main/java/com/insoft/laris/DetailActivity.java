package com.insoft.laris;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.insoft.laris.adapter.ItemDetail;
import com.insoft.laris.utils.BluetoothPrinter58mm2;
import com.insoft.laris.utils.MyDatabaseHelper;
import com.insoft.laris.utils.ReceiptPenjualanUtils;
import com.insoft.laris.utils.ReceiptPrintAdapter;
import com.insoft.laris.utils.SessionPelanggan;

import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DetailActivity extends AppCompatActivity {

    private RecyclerView rv_detail;
    private ProgressBar loading;
    ArrayList<HashMap<String,String>> list_data;
    private MyDatabaseHelper db;
    ItemDetail itemDetail;
    private Button btn_wa, btn_print;
    private SessionPelanggan sessionPelanggan;
    Locale localeID = new Locale("id", "ID");
    NumberFormat formatRupiah = NumberFormat.getInstance(localeID);
    private String notaMenungguPrint;
    private ActivityResultLauncher<String>
            bluetoothPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        bluetoothPermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        diberikan -> {
                            if (diberikan
                                    && notaMenungguPrint != null) {

                                tampilkanPilihanPrinterPenjualan(
                                        notaMenungguPrint
                                );

                            } else {
                                Toast.makeText(
                                        this,
                                        "Izin Bluetooth diperlukan",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
        db = new MyDatabaseHelper(this);
        rv_detail = findViewById(R.id.rv_detail);
        loading = findViewById(R.id.loading);
        btn_wa = findViewById(R.id.wa);
        btn_print = findViewById(R.id.print);
        sessionPelanggan = new SessionPelanggan(this);



        String nota = getIntent().getStringExtra("id_hold_intent");
        displayData(nota);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        rv_detail.setLayoutManager(llm);

        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                print_receipt(nota);
                prosesPrintPenjualan(nota);
            }
        });

        btn_wa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                whatsapp(nota);
                kirimStrukPenjualanWhatsApp(nota);
            }
        });

    }

    private void hapus_transaksi(String id_hold) {
        db.hapustransaksibyhold(id_hold);
        Intent intent = new Intent(DetailActivity.this, TransaksiActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void resend_item(String idhold) {
        db.hapussemua();

        Cursor cursor = db.displayItem(idhold);
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }else {
            while(cursor.moveToNext()) {
                db.tambahitem(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        Integer.parseInt(cursor.getString(5)),
                        Integer.parseInt(cursor.getString(6)),
                        Integer.parseInt(cursor.getString(7)),
                        Integer.parseInt(cursor.getString(8)),
                        cursor.getString(9),
                        Integer.parseInt(cursor.getString(10)),
                        Integer.parseInt(cursor.getString(11)),
                        Integer.parseInt(cursor.getString(12)),
                        1,0

                );

                sessionPelanggan.createSession(
                        cursor.getString(12),
                        cursor.getString(13),
                        cursor.getString(14),
                        cursor.getString(15)

                );
            }

            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void displayData(String id_hold) {

        list_data = new ArrayList<HashMap<String, String>>();
        Cursor cursor = db.tampilkan_penjualan_item(id_hold);
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }else {
            while(cursor.moveToNext()) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", cursor.getString(0));
                map.put("nm_barang", cursor.getString(1));
                map.put("total", cursor.getString(2));
                map.put("jumlah", cursor.getString(3));
                map.put("harga", cursor.getString(4));
                map.put("discount", cursor.getString(6));
                map.put("subtotal", cursor.getString(7));


                list_data.add(map);
                itemDetail = new ItemDetail(DetailActivity.this, list_data);
                itemDetail.notifyDataSetChanged();
                rv_detail.setAdapter(itemDetail);

            }
        }
    }


    private void print_receipt(String nota) {
        SQLiteDatabase database = db.getReadableDatabase();

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

        Cursor cursorHeader = database.rawQuery(
                queryHeader,
                new String[]{nota}
        );

        Cursor cursorDetail = database.rawQuery(
                queryDetail,
                new String[]{nota}
        );

        StringBuilder header = new StringBuilder();
        StringBuilder detail = new StringBuilder();
        StringBuilder footer = new StringBuilder();

        String garisTebal = "================================\n";
        String garisTipis = "--------------------------------\n";

        try {
            if (!cursorHeader.moveToFirst()) {
                Toast.makeText(
                        this,
                        "Data transaksi tidak ditemukan",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            /*
             * HEADER
             *
             * 0 = nota
             * 1 = tanggal
             * 2 = belanja
             * 3 = bayar
             * 4 = kembali
             * 5 = nama pelanggan
             * 6 = telepon
             * 7 = subtotal
             * 8 = total discount
             */

            String namaPelanggan = cursorHeader.getString(5);

            if (namaPelanggan == null || namaPelanggan.trim().isEmpty()) {
                namaPelanggan = "Pelanggan Umum";
            }

            header.append("        STRUK PENJUALAN\n");
            header.append(garisTebal);
            header.append("Pelanggan : ")
                    .append(namaPelanggan)
                    .append("\n");

            header.append("No. Nota  : ")
                    .append(cursorHeader.getString(0))
                    .append("\n");

            header.append("Tanggal   : ")
                    .append(cursorHeader.getString(1))
                    .append("\n");

            header.append(garisTebal);
            header.append("DETAIL BELANJA\n");
            header.append(garisTipis);

            /*
             * DETAIL PRODUK
             *
             * 0 = nama barang
             * 1 = jumlah
             * 2 = harga
             * 3 = total akhir
             * 4 = subtotal
             * 5 = diskon
             */

            int nomorUrut = 1;

            while (cursorDetail.moveToNext()) {
                String namaBarang = cursorDetail.getString(0);
                long jumlah = cursorDetail.getLong(1);
                long hargaSatuan = cursorDetail.getLong(2);
                long totalProduk = cursorDetail.getLong(3);
                long subtotalProduk = cursorDetail.getLong(4);
                long diskonProduk = cursorDetail.getLong(5);

                /*
                 * Jika kolom subtotal belum memiliki nilai,
                 * subtotal dihitung dari jumlah × harga.
                 */
                if (subtotalProduk <= 0) {
                    subtotalProduk = jumlah * hargaSatuan;
                }

                /*
                 * Jika kolom disk belum memiliki nilai,
                 * diskon dihitung dari subtotal - total.
                 */
                if (diskonProduk <= 0) {
                    diskonProduk = subtotalProduk - totalProduk;
                }

                if (diskonProduk < 0) {
                    diskonProduk = 0;
                }

                /*
                 * Jika total produk kosong,
                 * total dihitung dari subtotal - diskon.
                 */
                if (totalProduk <= 0) {
                    totalProduk = subtotalProduk - diskonProduk;
                }

                detail.append(nomorUrut)
                        .append(". ")
                        .append(namaBarang)
                        .append("\n");

                detail.append("   ")
                        .append(jumlah)
                        .append(" x ")
                        .append(formatRupiah.format(hargaSatuan))
                        .append("\n");

                detail.append("   Subtotal : ")
                        .append(formatRupiah.format(subtotalProduk))
                        .append("\n");

                detail.append("   Diskon   : -")
                        .append(formatRupiah.format(diskonProduk))
                        .append("\n");

                detail.append("   Total    : ")
                        .append(formatRupiah.format(totalProduk))
                        .append("\n");

                detail.append(garisTipis);

                nomorUrut++;
            }

            long subtotalTransaksi = cursorHeader.getLong(7);
            long totalDiskon = cursorHeader.getLong(8);
            long totalBelanja = cursorHeader.getLong(2);
            long pembayaran = cursorHeader.getLong(3);
            long kembalian = cursorHeader.getLong(4);

            /*
             * FOOTER DAN RINGKASAN PEMBAYARAN
             */

            footer.append("RINGKASAN PEMBAYARAN\n");
            footer.append(garisTipis);

            footer.append("Subtotal     : ")
                    .append(formatRupiah.format(subtotalTransaksi))
                    .append("\n");

            footer.append("Total Diskon : -")
                    .append(formatRupiah.format(totalDiskon))
                    .append("\n");

            footer.append(garisTipis);

            footer.append("TOTAL BELANJA: ")
                    .append(formatRupiah.format(totalBelanja))
                    .append("\n");

            footer.append("Pembayaran   : ")
                    .append(formatRupiah.format(pembayaran))
                    .append("\n");
            

            if(totalBelanja > pembayaran) {
                footer.append("Belum Dibayar    : ")
                        .append(formatRupiah.format(kembalian))
                        .append("\n");
            } else {
                footer.append("Kembalian    : ")
                        .append(formatRupiah.format(kembalian))
                        .append("\n");
            }


            footer.append(garisTebal);
            footer.append("       TERIMA KASIH\n");
            footer.append("  TELAH BERBELANJA DI KAMI\n");
            footer.append(garisTebal);

            PrintManager printManager =
                    (PrintManager) getSystemService(Context.PRINT_SERVICE);

            if (printManager == null) {
                Toast.makeText(
                        this,
                        "Layanan printer tidak tersedia",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            PrintAttributes.Builder builder =
                    new PrintAttributes.Builder();

            builder.setMediaSize(
                    PrintAttributes.MediaSize.NA_INDEX_4X6
            );

            builder.setMinMargins(
                    PrintAttributes.Margins.NO_MARGINS
            );

            builder.setColorMode(
                    PrintAttributes.COLOR_MODE_MONOCHROME
            );

            printManager.print(
                    "Struk Penjualan " + nota,
                    new ReceiptPrintAdapter(
                            this,
                            header.toString(),
                            detail.toString(),
                            footer.toString()
                    ),
                    builder.build()
            );

        } catch (Exception e) {
            Log.e("PRINT_RECEIPT", "Gagal mencetak struk", e);

            Toast.makeText(
                    this,
                    "Gagal mencetak struk: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

        } finally {
            cursorHeader.close();
            cursorDetail.close();
        }
    }
    private void whatsapp(String nota) {
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursorHeader = database.rawQuery(
                "SELECT p.nota, p.tanggal, p.belanja, p.bayar, p.kembali, c.nm_pelanggan, c.telepon, p.subtotal, p.total_dicount " +
                        "FROM penjualan p " +
                        "LEFT JOIN master_pelanggan c ON c.kd_pelanggan = p.kd_pelanggan " +
                        "WHERE p.nota = ?", new String[]{nota});

        Cursor cursorDetail = database.rawQuery(
                "SELECT nm_barang, jumlah, harga, total, subtotal, disk " +
                        "FROM penjualan_item WHERE nota = ?", new String[]{nota});




        StringBuilder struk = new StringBuilder();

        if (!cursorHeader.moveToFirst()) {
            cursorHeader.close();
            cursorDetail.close();
            database.close();

            Toast.makeText(this, "Data transaksi tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }


        String nomorPelanggan = formatNomor(cursorHeader.getString(6));

        String namaPelanggan = cursorHeader.getString(5);
        String nomorNota = cursorHeader.getString(0);
        String tanggal = cursorHeader.getString(1);

        long totalSubtotal = cursorHeader.getLong(7);
        long totalDiskon = cursorHeader.getLong(8);
        long totalBelanja = cursorHeader.getLong(2);
        long pembayaran = cursorHeader.getLong(3);
        long kembalian = cursorHeader.getLong(4);

        String garis = "---------------------------\n";

        struk.append("```\n");
        struk.append("        STRUK PEMBAYARAN\n");
        struk.append("===========================\n");
        struk.append("Pelanggan : ")
                .append(namaPelanggan == null || namaPelanggan.trim().isEmpty()
                        ? "Umum"
                        : namaPelanggan)
                .append("\n");

        struk.append("No. Nota  : ")
                .append(nomorNota)
                .append("\n");

        struk.append("Tanggal   : ")
                .append(tanggal)
                .append("\n");

        struk.append("===========================\n");
        struk.append("DETAIL BELANJA\n");
        struk.append(garis);

        int nomorUrut = 1;

        while (cursorDetail.moveToNext()) {

            /*
             * Data detail
             * Index 0 = Nama produk
             * Index 1 = Jumlah
             * Index 2 = Harga satuan
             * Index 7 = Total produk setelah diskon
             */

            String namaProduk = cursorDetail.getString(0);
            long jumlah = cursorDetail.getLong(1);
            long hargaSatuan = cursorDetail.getLong(2);
            long totalProduk = cursorDetail.getLong(3);

            // Subtotal sebelum diskon
            long subtotalProduk = jumlah * hargaSatuan;

            // Diskon dihitung dari subtotal dikurangi total produk
            long diskonProduk = subtotalProduk - totalProduk;

            // Mencegah nilai diskon menjadi minus
            if (diskonProduk < 0) {
                diskonProduk = 0;
            }

            struk.append(nomorUrut)
                    .append(". ")
                    .append(namaProduk)
                    .append("\n");

            struk.append("   ")
                    .append(jumlah)
                    .append(" x ")
                    .append(formatRupiah.format(hargaSatuan))
                    .append("\n");

            struk.append("   Subtotal : ")
                    .append(formatRupiah.format(subtotalProduk))
                    .append("\n");

            struk.append("   Diskon   : -")
                    .append(formatRupiah.format(diskonProduk))
                    .append("\n");

            struk.append("   Total    : ")
                    .append(formatRupiah.format(totalProduk))
                    .append("\n");

            struk.append(garis);

            nomorUrut++;
        }

        struk.append("RINGKASAN PEMBAYARAN\n");
        struk.append(garis);

        struk.append("Subtotal     : ")
                .append(formatRupiah.format(totalSubtotal))
                .append("\n");

        struk.append("Total Diskon : -")
                .append(formatRupiah.format(totalDiskon))
                .append("\n");

        struk.append(garis);

        struk.append("TOTAL BELANJA: ")
                .append(formatRupiah.format(totalBelanja))
                .append("\n");

        struk.append("Pembayaran   : ")
                .append(formatRupiah.format(pembayaran))
                .append("\n");


        if(totalBelanja > pembayaran) {
            struk.append("BLM BAYAR    : ")
                    .append(formatRupiah.format(kembalian))
                    .append("\n");
        } else {
            struk.append("Kembalian    : ")
                    .append(formatRupiah.format(kembalian))
                    .append("\n");
        }

        struk.append("===========================\n");
        struk.append("     TERIMA KASIH TELAH\n");
        struk.append("       BERBELANJA\n");
        struk.append("===========================\n");
        struk.append("```");

        cursorHeader.close();
        cursorDetail.close();
        database.close();

        String pesan = struk.toString();

        kirimWhatsapp(nomorPelanggan, pesan);
    }

    private void kirimWhatsapp(String nomorDb, String pesan) {
        String nomor = formatNomor(nomorDb);

        if (nomor == null || nomor.isEmpty()) {
            // Kalau kosong, munculkan dialog input
            showNomorDialog(pesan);
        } else if (!isNomorValid(nomor)) {
            // Kalau format tidak sesuai, munculkan dialog input
            showNomorDialog(pesan);
        } else {
            // Kalau valid → langsung kirim ke WA
            bukaWhatsapp(nomor, pesan);
        }
    }

    private void bukaWhatsapp(String nomor, String pesan) {
        try {
            String url = "https://wa.me/" + nomor + "?text=" + URLEncoder.encode(pesan, "UTF-8");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal membuka WhatsApp", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNomorDialog(String pesan) {
        AlertDialog
                .Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Masukkan Nomor WhatsApp");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        builder.setView(input);

        builder.setPositiveButton("Kirim", (dialog, which) -> {
            String nomorBaru = input.getText().toString().trim();
            String nomorFormat = formatNomor(nomorBaru);

            if (!isNomorValid(nomorFormat)) {
                Toast.makeText(this, "Nomor tidak valid", Toast.LENGTH_SHORT).show();
            } else {
                bukaWhatsapp(nomorFormat, pesan);
            }
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private boolean isNomorValid(String nomor) {
        return nomor.matches("^(62|0)[0-9]{8,15}$");
    }

    private String formatNomor(String nomor) {
        // buang spasi atau tanda - kalau ada
        nomor = nomor.replaceAll("[^0-9]", "");

        if (nomor.startsWith("0")) {
            return "62" + nomor.substring(1); // ganti 0 awal jadi 62
        }
        return nomor; // kalau sudah format internasional langsung return
    }

    private void cetakStrukPenjualan(
            BluetoothDevice printer,
            String nota
    ) {
        SQLiteDatabase database =
                db.getReadableDatabase();

        final String teksStruk;

        try {
            teksStruk =
                    ReceiptPenjualanUtils
                            .buatTeksStruk(
                                    database,
                                    nota
                            );

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        Toast.makeText(
                this,
                "Menghubungkan printer...",
                Toast.LENGTH_SHORT
        ).show();

        BluetoothPrinter58mm2.printText(
                printer,
                teksStruk,
                new BluetoothPrinter58mm2.PrintCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() ->
                                Toast.makeText(
                                        DetailActivity.this,
                                        "Struk berhasil dicetak",
                                        Toast.LENGTH_SHORT
                                ).show()
                        );
                    }

                    @Override
                    public void onError(String pesan) {
                        runOnUiThread(() ->
                                Toast.makeText(
                                        DetailActivity.this,
                                        "Print gagal: " + pesan,
                                        Toast.LENGTH_LONG
                                ).show()
                        );
                    }
                }
        );
    }


    private void prosesPrintPenjualan(String nota) {
        notaMenungguPrint = nota;

        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.S
                && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED) {

            bluetoothPermissionLauncher.launch(
                    Manifest.permission.BLUETOOTH_CONNECT
            );

            return;
        }

        tampilkanPilihanPrinterPenjualan(nota);
    }

    @SuppressLint("MissingPermission")
    private void tampilkanPilihanPrinterPenjualan(
            String nota
    ) {
        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(
                        Context.BLUETOOTH_SERVICE
                );

        BluetoothAdapter bluetoothAdapter =
                bluetoothManager == null
                        ? null
                        : bluetoothManager.getAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(
                    this,
                    "Perangkat tidak mendukung Bluetooth",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(
                    this,
                    "Aktifkan Bluetooth terlebih dahulu",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        Set<BluetoothDevice> perangkatTerpasang =
                bluetoothAdapter.getBondedDevices();

        if (perangkatTerpasang == null
                || perangkatTerpasang.isEmpty()) {

            Toast.makeText(
                    this,
                    "Belum ada printer Bluetooth yang dipasangkan",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        List<BluetoothDevice> daftarPrinter =
                new ArrayList<>(
                        perangkatTerpasang
                );

        String[] namaPrinter =
                new String[daftarPrinter.size()];

        for (int i = 0; i < daftarPrinter.size(); i++) {
            BluetoothDevice perangkat =
                    daftarPrinter.get(i);

            String nama = perangkat.getName();

            if (nama == null || nama.trim().isEmpty()) {
                nama = "Perangkat Bluetooth";
            }

            namaPrinter[i] =
                    nama
                            + "\n"
                            + perangkat.getAddress();
        }

        new AlertDialog.Builder(this)
                .setTitle("Pilih printer 58 mm")
                .setItems(
                        namaPrinter,
                        (dialog, posisi) -> {
                            BluetoothDevice printer =
                                    daftarPrinter.get(posisi);

                            cetakStrukPenjualan(
                                    printer,
                                    nota
                            );
                        }
                )
                .setNegativeButton(
                        "Batal",
                        null
                )
                .show();
    }


    private void kirimStrukPenjualanWhatsApp(
            String nota
    ) {
        SQLiteDatabase database =
                db.getReadableDatabase();

        try {
            Uri gambarUri =
                    ReceiptPenjualanUtils
                            .simpanGambarStruk(
                                    this,
                                    database,
                                    nota
                            );

            Intent kirimIntent =
                    new Intent(Intent.ACTION_SEND);

            kirimIntent.setType("image/png");

            kirimIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    gambarUri
            );

            kirimIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Struk penjualan nomor nota "
                            + nota
            );

            kirimIntent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            /*
             * Coba WhatsApp biasa.
             */
            Intent whatsappIntent =
                    new Intent(kirimIntent);

            whatsappIntent.setPackage(
                    "com.whatsapp"
            );

            try {
                startActivity(whatsappIntent);
                return;

            } catch (ActivityNotFoundException ignored) {
            }

            /*
             * Coba WhatsApp Business.
             */
            Intent whatsappBusinessIntent =
                    new Intent(kirimIntent);

            whatsappBusinessIntent.setPackage(
                    "com.whatsapp.w4b"
            );

            try {
                startActivity(
                        whatsappBusinessIntent
                );

                return;

            } catch (ActivityNotFoundException ignored) {
            }

            /*
             * WhatsApp tidak ada, buka menu share.
             */
            startActivity(
                    Intent.createChooser(
                            kirimIntent,
                            "Kirim struk penjualan"
                    )
            );

        } catch (Exception e) {
            Log.e(
                    "STRUK_WHATSAPP",
                    "Gagal mengirim struk",
                    e
            );

            Toast.makeText(
                    this,
                    "Gagal membuat struk: "
                            + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

}