package com.insoft.laris.admin;

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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.insoft.laris.Interface.masterBarangInterface;
import com.insoft.laris.R;
import com.insoft.laris.adapter.ItemMasterBarang;
import com.insoft.laris.json.BarangResponseJson;
import com.insoft.laris.json.CustomerRequestJson;
import com.insoft.laris.json.HapusProdukRequestJson;
import com.insoft.laris.json.HapusProdukResponseJson;
import com.insoft.laris.model.Produk;
import com.insoft.laris.utils.BluetoothPrinter58mm2;
import com.insoft.laris.utils.Constants;
import com.insoft.laris.utils.MasterBarangPrintAdapter;
import com.insoft.laris.utils.MyDatabaseHelper;
import com.insoft.laris.utils.ReceiptMasterBarangUtils;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MasterBarangActivity extends AppCompatActivity implements masterBarangInterface {
    private RegisterAPI registerAPI;
    private ProgressBar loading;
    private EditText etcari;
    private RecyclerView rvbarang;
    private FloatingActionButton fabtambah, fabprint, fabwa;

    private MyDatabaseHelper db;

    private String teksMasterBarangMenungguPrint;

    private ActivityResultLauncher<String>
            bluetoothPermissionLauncher;

    private List<Produk> dataProduk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_barang);
        registerAPI = UtilsAPI.getApiService();
        db = new MyDatabaseHelper(this);
        loading = findViewById(R.id.loading);
        etcari = findViewById(R.id.etcari);
        rvbarang = findViewById(R.id.rvbarang);
        fabtambah = findViewById(R.id.fab_tambah);
        fabprint = findViewById(R.id.fab_print);
        fabwa = findViewById(R.id.fab_wa);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        rvbarang.setLayoutManager(llm);


        bluetoothPermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        diberikan -> {
                            if (diberikan
                                    && teksMasterBarangMenungguPrint != null) {

                                tampilkanPilihanPrinterMasterBarang(
                                        teksMasterBarangMenungguPrint
                                );

                            } else {
                                Toast.makeText(
                                        this,
                                        "Izin Bluetooth diperlukan untuk mencetak",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );



        fetch_data("");

        etcari.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                fetch_data(s.toString());
            }
        });

        fabtambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MasterBarangActivity.this, BarangAddActivity.class);
                startActivity(intent);
            }
        });


        fabprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prosesPrintMasterBarang();
            }
        });

        fabwa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kirimMasterBarangWhatsApp();
            }
        });
    }

    private void prosesPrintMasterBarang() {
        SQLiteDatabase database =
                db.getReadableDatabase();

        try {
            teksMasterBarangMenungguPrint =
                    ReceiptMasterBarangUtils
                            .buatTeksStruk(database);

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

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

        tampilkanPilihanPrinterMasterBarang(
                teksMasterBarangMenungguPrint
        );
    }


    @SuppressLint("MissingPermission")
    private void tampilkanPilihanPrinterMasterBarang(
            String teksStruk
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

                            cetakMasterBarang(
                                    printer,
                                    teksStruk
                            );
                        }
                )
                .setNegativeButton("Batal", null)
                .show();
    }

    private void cetakMasterBarang(
            BluetoothDevice printer,
            String teksStruk
    ) {
        Toast.makeText(
                this,
                "Mencetak master barang...",
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
                                        MasterBarangActivity.this,
                                        "Master barang berhasil dicetak",
                                        Toast.LENGTH_SHORT
                                ).show()
                        );
                    }

                    @Override
                    public void onError(String pesan) {
                        runOnUiThread(() ->
                                Toast.makeText(
                                        MasterBarangActivity.this,
                                        "Print gagal: " + pesan,
                                        Toast.LENGTH_LONG
                                ).show()
                        );
                    }
                }
        );
    }


    private void kirimMasterBarangWhatsApp() {
        SQLiteDatabase database =
                db.getReadableDatabase();

        try {
            Uri gambarUri =
                    ReceiptMasterBarangUtils
                            .simpanGambarStruk(
                                    this,
                                    database
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
                    "Daftar master barang "
                            + Constants.namaToko
            );

            kirimIntent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

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

            startActivity(
                    Intent.createChooser(
                            kirimIntent,
                            "Kirim daftar master barang"
                    )
            );

        } catch (Exception e) {
            Log.e(
                    "MASTER_BARANG_WA",
                    "Gagal membuat daftar barang",
                    e
            );

            Toast.makeText(
                    this,
                    "Gagal membuat daftar barang: "
                            + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void fetch_data(String s) {
        loading.setVisibility(View.VISIBLE);
        CustomerRequestJson param = new CustomerRequestJson();
        param.setKata_cari(s);
        registerAPI.daftar_barang(param).enqueue(new Callback<BarangResponseJson>() {
            @Override
            public void onResponse(Call<BarangResponseJson> call, Response<BarangResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    if(resultcode.equalsIgnoreCase("00")) {
                        dataProduk = response.body().getData();
                        ItemMasterBarang itemMasterBarang = new ItemMasterBarang(MasterBarangActivity.this, dataProduk, MasterBarangActivity.this);
                        itemMasterBarang.notifyDataSetChanged();
                        rvbarang.setAdapter(itemMasterBarang);
                    }
                }
            }

            @Override
            public void onFailure(Call<BarangResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void pilihProduk(int posisi) {
        Intent intent = new Intent(MasterBarangActivity.this, BarangEditActivity.class);
        intent.putExtra("_kodebarang", dataProduk.get(posisi).getKd_barang());
        intent.putExtra("_barcode", dataProduk.get(posisi).getBarcode());
        intent.putExtra("_namabarang", dataProduk.get(posisi).getNm_barang());
        intent.putExtra("_hargabeli", dataProduk.get(posisi).getHarga_beli());
        intent.putExtra("_konversi", dataProduk.get(posisi).getKonversi());
        intent.putExtra("_hargajual", dataProduk.get(posisi).getHarga_jual());
        intent.putExtra("_hargajualkarton", dataProduk.get(posisi).getHj());
        intent.putExtra("_hargamember", dataProduk.get(posisi).getHarga_member());
        intent.putExtra("_hargamemberkarton", dataProduk.get(posisi).getDiskon_member());
        intent.putExtra("_hargafreelance", dataProduk.get(posisi).getHarga_freelance());
        intent.putExtra("_hargareseller", dataProduk.get(posisi).getHarga_reseller());
        intent.putExtra("_stok", dataProduk.get(posisi).getStok());
        intent.putExtra("_hargafreelancekarton", dataProduk.get(posisi).getHarga_karton_freelance());
        startActivity(intent);

    }

    @Override
    public void hapusProduk(int posisi) {
        showDialogHapus(dataProduk.get(posisi).getKd_barang());
    }

    private void showDialogHapus(String kode) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MasterBarangActivity.this);
        builder1.setMessage("Hapus Item Ini..?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "YA",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        conHapusItem(kode);
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "TIDAK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void conHapusItem(String kode) {
        loading.setVisibility(View.VISIBLE);
        HapusProdukRequestJson param = new HapusProdukRequestJson();
        param.setKodebarang(kode);
        registerAPI.hapus_produk(param).enqueue(new Callback<HapusProdukResponseJson>() {
            @Override
            public void onResponse(Call<HapusProdukResponseJson> call, Response<HapusProdukResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    if(resultcode.equalsIgnoreCase("00")) {
                        Intent intent = getIntent();
                        startActivity(intent);
                    }
                }

            }

            @Override
            public void onFailure(Call<HapusProdukResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
            }
        });
    }
}