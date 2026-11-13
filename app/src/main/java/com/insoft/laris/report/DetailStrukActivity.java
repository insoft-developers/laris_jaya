package com.insoft.laris.report;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.insoft.laris.R;
import com.insoft.laris.adapter.DetailStrukAdapter;
import com.insoft.laris.json.DetailStrukRequestJson;
import com.insoft.laris.json.DetailStrukResponseJson;
import com.insoft.laris.model.DetailStruk;
import com.insoft.laris.model.DetailStrukItem;


import com.insoft.laris.utils.BluetoothPrinter58mm2;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.ActivityNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.content.ClipData;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.insoft.laris.utils.ReceiptCetakUlangUtils;

public class DetailStrukActivity extends AppCompatActivity {

    private RecyclerView rv_detail;
    private ProgressBar loading;
    private Button btn_wa, btn_print;

    private RegisterAPI registerAPI;
    private Call<DetailStrukResponseJson> currentCall;

    private String nota;
    private String userid;
    private int idcabang;

    private DetailStruk detailStruk;

    private String notaMenungguPrint;

    private ActivityResultLauncher<String>
            bluetoothPermissionLauncher;

    private final NumberFormat formatRupiah =
            NumberFormat.getInstance(new Locale("id", "ID"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_struk);

        rv_detail = findViewById(R.id.rv_detail);
        loading = findViewById(R.id.loading);
        btn_wa = findViewById(R.id.wa);
        btn_print = findViewById(R.id.print);

        rv_detail.setLayoutManager(
                new LinearLayoutManager(this)
        );

        registerAPI = UtilsAPI.getApiService();

        nota = getIntent().getStringExtra("nota");
        userid = getIntent().getStringExtra("userid");
        idcabang = getIntent().getIntExtra("idcabang", 0);

        btn_wa.setEnabled(false);
        btn_print.setEnabled(false);

        bluetoothPermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        diberikan -> {
                            if (diberikan &&
                                    notaMenungguPrint != null) {

                                tampilkanPilihanPrinter();
                            } else {
                                Toast.makeText(
                                        this,
                                        "Izin Bluetooth diperlukan",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );

        btn_print.setOnClickListener(v ->
                prosesPrintPenjualan()
        );

        btn_wa.setOnClickListener(v ->
                kirimStrukWhatsApp()
        );

        if (nota == null || userid == null) {
            Toast.makeText(
                    this,
                    "Data transaksi tidak lengkap",
                    Toast.LENGTH_SHORT
            ).show();
            finish();
            return;
        }

        fetchDetailStruk();
    }

    // =====================================
    // AMBIL DETAIL DARI API
    // =====================================

    private void fetchDetailStruk() {

        DetailStrukRequestJson param =
                new DetailStrukRequestJson();

        param.setNota(nota);
        param.setIduser(userid);
        param.setIdcabang(idcabang);

        loading.setVisibility(View.VISIBLE);

        btn_wa.setEnabled(false);
        btn_print.setEnabled(false);

        currentCall = registerAPI.detail_struk(param);

        currentCall.enqueue(
                new Callback<DetailStrukResponseJson>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<DetailStrukResponseJson> call,
                            @NonNull Response<DetailStrukResponseJson> response) {

                        loading.setVisibility(View.GONE);

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()
                                && response.body().getData() != null) {

                            detailStruk = response.body().getData();

                            tampilkanDetail();

                            btn_wa.setEnabled(true);
                            btn_print.setEnabled(true);

                        } else {
                            Toast.makeText(
                                    DetailStrukActivity.this,
                                    "Gagal mengambil detail struk",
                                    Toast.LENGTH_LONG
                            ).show();

                            Log.e("DETAIL_STRUK",
                                    "HTTP: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<DetailStrukResponseJson> call,
                            @NonNull Throwable t) {

                        loading.setVisibility(View.GONE);

                        if (call.isCanceled()) return;

                        Toast.makeText(
                                DetailStrukActivity.this,
                                "Koneksi gagal: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                        Log.e("DETAIL_STRUK", "API Error", t);
                    }
                });
    }

    // =====================================
    // TAMPILKAN DETAIL BARANG
    // =====================================

    private void tampilkanDetail() {

        List<DetailStrukItem> items =
                detailStruk.getItems();

        if (items == null) {
            items = new ArrayList<>();
        }

        DetailStrukAdapter adapter =
                new DetailStrukAdapter(items);

        rv_detail.setAdapter(adapter);
    }

    // =====================================
    // FORMAT TEKS STRUK
    // =====================================

//    private String buatTeksStruk() {
//
//        StringBuilder struk = new StringBuilder();
//
//        String garis =
//                "--------------------------------\n";
//
//        String nama = detailStruk.getNm_pelanggan();
//
//        if (nama == null || nama.trim().isEmpty()) {
//            nama = "Pelanggan Umum";
//        }
//
//        struk.append("        STRUK PENJUALAN\n");
//        struk.append("================================\n");
//
//        struk.append("Pelanggan : ")
//                .append(nama).append("\n");
//
//        struk.append("No. Nota  : ")
//                .append(detailStruk.getNota()).append("\n");
//
//        struk.append("Tanggal   : ")
//                .append(detailStruk.getTanggal()).append("\n");
//
//        struk.append("================================\n");
//        struk.append("DETAIL BELANJA\n");
//        struk.append(garis);
//
//        int nomor = 1;
//
//        List<DetailStrukItem> items =
//                detailStruk.getItems();
//
//        if (items != null) {
//            for (DetailStrukItem item : items) {
//
//                long subtotal = item.getSubtotal();
//
//                if (subtotal <= 0) {
//                    subtotal = item.getJumlah() * item.getHarga();
//                }
//
//                long diskon = item.getDisk();
//
//                if (diskon <= 0) {
//                    diskon = Math.max(
//                            0,
//                            subtotal - item.getTotal()
//                    );
//                }
//
//                long total = item.getTotal();
//
//                if (total <= 0) {
//                    total = Math.max(0, subtotal - diskon);
//                }
//
//                struk.append(nomor++)
//                        .append(". ")
//                        .append(item.getNm_barang())
//                        .append("\n");
//
//                struk.append("   ")
//                        .append(item.getJumlah())
//                        .append(" x ")
//                        .append(formatRupiah.format(item.getHarga()))
//                        .append("\n");
//
//                struk.append("   Subtotal : ")
//                        .append(formatRupiah.format(subtotal))
//                        .append("\n");
//
//                struk.append("   Diskon   : -")
//                        .append(formatRupiah.format(diskon))
//                        .append("\n");
//
//                struk.append("   Total    : ")
//                        .append(formatRupiah.format(total))
//                        .append("\n");
//
//                struk.append(garis);
//            }
//        }
//
//        struk.append("RINGKASAN PEMBAYARAN\n");
//        struk.append(garis);
//
//        struk.append("Subtotal     : ")
//                .append(formatRupiah.format(
//                        detailStruk.getSubtotal()
//                )).append("\n");
//
//        struk.append("Total Diskon : -")
//                .append(formatRupiah.format(
//                        detailStruk.getTotal_discount()
//                )).append("\n");
//
//        struk.append(garis);
//
//        struk.append("TOTAL BELANJA: ")
//                .append(formatRupiah.format(
//                        detailStruk.getBelanja()
//                )).append("\n");
//
//        struk.append("Pembayaran   : ")
//                .append(formatRupiah.format(
//                        detailStruk.getBayar()
//                )).append("\n");
//
//        struk.append("Kembalian    : ")
//                .append(formatRupiah.format(
//                        detailStruk.getKembali()
//                )).append("\n");
//
//        struk.append("================================\n");
//        struk.append("       TERIMA KASIH\n");
//        struk.append("  TELAH BERBELANJA DI KAMI\n");
//        struk.append("================================\n");
//
//        return struk.toString();
//    }

    private String buatTeksStruk() {
        return ReceiptCetakUlangUtils.buatTeksStruk(detailStruk);
    }

    // =====================================
    // PRINT BLUETOOTH
    // =====================================

    private void prosesPrintPenjualan() {

        notaMenungguPrint = nota;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED) {

            bluetoothPermissionLauncher.launch(
                    Manifest.permission.BLUETOOTH_CONNECT
            );

            return;
        }

        tampilkanPilihanPrinter();
    }

    @SuppressLint("MissingPermission")
    private void tampilkanPilihanPrinter() {

        BluetoothManager manager =
                (BluetoothManager) getSystemService(
                        Context.BLUETOOTH_SERVICE
                );

        BluetoothAdapter adapter =
                manager == null ? null : manager.getAdapter();

        if (adapter == null) {
            Toast.makeText(
                    this,
                    "Bluetooth tidak tersedia",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (!adapter.isEnabled()) {
            Toast.makeText(
                    this,
                    "Aktifkan Bluetooth terlebih dahulu",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Set<BluetoothDevice> paired =
                adapter.getBondedDevices();

        if (paired == null || paired.isEmpty()) {
            Toast.makeText(
                    this,
                    "Belum ada printer yang dipasangkan",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        List<BluetoothDevice> printers =
                new ArrayList<>(paired);

        String[] names = new String[printers.size()];

        for (int i = 0; i < printers.size(); i++) {

            BluetoothDevice device = printers.get(i);

            String name = device.getName();

            if (name == null || name.trim().isEmpty()) {
                name = "Printer Bluetooth";
            }

            names[i] = name + "\n" + device.getAddress();
        }

        new AlertDialog.Builder(this)
                .setTitle("Pilih printer 58 mm")
                .setItems(names, (dialog, position) -> {

                    BluetoothDevice printer =
                            printers.get(position);

                    cetakStrukPenjualan(printer);

                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void cetakStrukPenjualan(
            BluetoothDevice printer) {

        String teksStruk = buatTeksStruk();

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
                                        DetailStrukActivity.this,
                                        "Struk berhasil dicetak",
                                        Toast.LENGTH_SHORT
                                ).show()
                        );
                    }

                    @Override
                    public void onError(String pesan) {
                        runOnUiThread(() ->
                                Toast.makeText(
                                        DetailStrukActivity.this,
                                        "Print gagal: " + pesan,
                                        Toast.LENGTH_LONG
                                ).show()
                        );
                    }
                }
        );
    }

    // =====================================
    // KIRIM STRUK WHATSAPP
    // =====================================



    private void kirimStrukWhatsApp() {

        try {

            ArrayList<Uri> gambarUris =
                    ReceiptCetakUlangUtils.simpanGambarStrukPerHalaman(
                            this,
                            detailStruk
                    );

            if (gambarUris.isEmpty()) {
                Toast.makeText(
                        this,
                        "Gambar struk kosong",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);

            intent.setType("image/png");

            intent.putParcelableArrayListExtra(
                    Intent.EXTRA_STREAM,
                    gambarUris
            );

            ClipData clipData = ClipData.newUri(
                    getContentResolver(),
                    "Struk Penjualan",
                    gambarUris.get(0)
            );

            for (int i = 1; i < gambarUris.size(); i++) {
                clipData.addItem(
                        new ClipData.Item(gambarUris.get(i))
                );
            }

            intent.setClipData(clipData);

            intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            startActivity(
                    Intent.createChooser(
                            intent,
                            "Kirim Struk Penjualan"
                    )
            );

        } catch (Exception e) {

            Log.e("STRUK_WA", "Gagal membuat struk", e);

            Toast.makeText(
                    this,
                    "Gagal membuat struk: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }



    private Uri simpanGambarStruk() throws Exception {

        Bitmap bitmap = buatBitmapStruk(buatTeksStruk());



        File folder = new File(getCacheDir(), "struk_whatsapp");

        if (!folder.exists() && !folder.mkdirs()) {
            bitmap.recycle();
            throw new IOException("Gagal membuat folder struk");
        }

        String namaNota = nota.replaceAll(
                "[^a-zA-Z0-9_-]",
                ""
        );

        File file = new File(
                folder,
                "struk_" + namaNota + ".png"
        );

        try (FileOutputStream output = new FileOutputStream(file)) {

            boolean berhasil = bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    output
            );

            if (!berhasil) {
                throw new IOException("Gagal menyimpan gambar PNG");
            }

            output.flush();

        } finally {
            bitmap.recycle();
        }

        return FileProvider.getUriForFile(
                this,
                getPackageName() + ".fileprovider",
                file
        );
    }

    private Bitmap buatBitmapStruk(String teks) {

        int lebar = 720;
        int padding = 35;
        int ukuranFont = 27;
        int jarakBaris = 42;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(ukuranFont);
        paint.setTypeface(Typeface.MONOSPACE);

        int lebarTeks = lebar - (padding * 2);

        List<String> daftarBaris = new ArrayList<>();

        String[] barisAsli = teks.split("\n", -1);

        for (String baris : barisAsli) {

            if (baris.isEmpty()) {
                daftarBaris.add("");
                continue;
            }

            StringBuilder barisAktif = new StringBuilder();

            String[] kata = baris.split(" ");

            for (String item : kata) {

                String calon = barisAktif.length() == 0
                        ? item
                        : barisAktif + " " + item;

                if (paint.measureText(calon) > lebarTeks
                        && barisAktif.length() > 0) {

                    daftarBaris.add(barisAktif.toString());
                    barisAktif = new StringBuilder(item);

                } else {

                    if (barisAktif.length() > 0) {
                        barisAktif.append(" ");
                    }

                    barisAktif.append(item);
                }
            }

            if (barisAktif.length() > 0) {
                daftarBaris.add(barisAktif.toString());
            }
        }

        int tinggi = padding * 2
                + daftarBaris.size() * jarakBaris;

        Bitmap bitmap = Bitmap.createBitmap(
                lebar,
                tinggi,
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        float posisiY = padding + ukuranFont;

        for (String baris : daftarBaris) {

            canvas.drawText(
                    baris,
                    padding,
                    posisiY,
                    paint
            );

            posisiY += jarakBaris;
        }

        return bitmap;
    }

    @Override
    protected void onDestroy() {

        if (currentCall != null) {
            currentCall.cancel();
        }

        super.onDestroy();
    }


}
