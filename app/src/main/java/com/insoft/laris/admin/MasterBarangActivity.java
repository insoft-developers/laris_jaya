package com.insoft.laris.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.insoft.laris.utils.MasterBarangPrintAdapter;
import com.insoft.laris.utils.MyDatabaseHelper;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MasterBarangActivity extends AppCompatActivity implements masterBarangInterface {
    private RegisterAPI registerAPI;
    private ProgressBar loading;
    private EditText etcari;
    private RecyclerView rvbarang;
    private FloatingActionButton fabtambah, fabprint;

    private MyDatabaseHelper db;

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
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        rvbarang.setLayoutManager(llm);


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
                printMasterBarang58mm();
            }
        });
    }

    private void printMasterBarang58mm() {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        SQLiteDatabase database = db.getReadableDatabase();

        Cursor cursor = database.rawQuery(
                "SELECT kd_barang, nm_barang, stok, harga_jual, harga_reseller " +
                        "FROM master_barang " +
                        "ORDER BY nm_barang ASC",
                null
        );

        ArrayList<String> barisCetak = new ArrayList<>();

        try {
            if (cursor.getCount() == 0) {
                Toast.makeText(
                        this,
                        "Data master barang masih kosong",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            String tanggalCetak = new SimpleDateFormat(
                    "dd-MM-yyyy HH:mm",
                    Locale.getDefault()
            ).format(new Date());

            barisCetak.add("      DAFTAR MASTER BARANG");
            barisCetak.add("================================");
            barisCetak.add("Tanggal : " + tanggalCetak);
            barisCetak.add("Jumlah  : " + cursor.getCount() + " barang");
            barisCetak.add("================================");

            int nomor = 1;
            long totalStok = 0;

            while (cursor.moveToNext()) {
                String kodeBarang = cursor.getString(
                        cursor.getColumnIndexOrThrow("kd_barang")
                );

                String namaBarang = cursor.getString(
                        cursor.getColumnIndexOrThrow("nm_barang")
                );

                long stok = cursor.getLong(
                        cursor.getColumnIndexOrThrow("stok")
                );

                long harga = cursor.getLong(
                        cursor.getColumnIndexOrThrow("harga_jual")
                );

                long hargaReseller = cursor.getLong(
                        cursor.getColumnIndexOrThrow("harga_reseller")
                );

                totalStok += stok;

                barisCetak.add(
                        nomor + ". " + potongTeks(namaBarang, 28)
                );

                barisCetak.add(
                        "   Stok     : " + stok
                );

                barisCetak.add(
                        "   Harga    : " + formatRupiah.format(harga)
                );

                barisCetak.add(
                        "   Reseller : " + formatRupiah.format(hargaReseller)
                );

                barisCetak.add("--------------------------------");

                nomor++;
            }

            barisCetak.add("TOTAL JENIS BARANG : " + cursor.getCount());
            barisCetak.add("TOTAL SELURUH STOK : " + totalStok);
            barisCetak.add("================================");
            barisCetak.add("");
            barisCetak.add("");

            jalankanPrintMasterBarang(barisCetak);

        } catch (Exception e) {
            Log.e(
                    "PRINT_MASTER_BARANG",
                    "Gagal mencetak data barang",
                    e
            );

            Toast.makeText(
                    this,
                    "Gagal mencetak: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

        } finally {
            cursor.close();
            database.close();
        }
    }


    private void jalankanPrintMasterBarang(
            ArrayList<String> barisCetak
    ) {
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

        PrintAttributes.MediaSize mediaSize58mm =
                new PrintAttributes.MediaSize(
                        "THERMAL_58MM",
                        "Thermal 58 mm",
                        2283,
                        7874
                );

        PrintAttributes printAttributes =
                new PrintAttributes.Builder()
                        .setMediaSize(mediaSize58mm)
                        .setMinMargins(
                                new PrintAttributes.Margins(
                                        20,
                                        20,
                                        20,
                                        20
                                )
                        )
                        .setColorMode(
                                PrintAttributes.COLOR_MODE_MONOCHROME
                        )
                        .setResolution(
                                new PrintAttributes.Resolution(
                                        "THERMAL_203_DPI",
                                        "Thermal 203 DPI",
                                        203,
                                        203
                                )
                        )
                        .build();

        printManager.print(
                "Master Barang",
                new MasterBarangPrintAdapter(
                        this,
                        barisCetak
                ),
                printAttributes
        );
    }


    private String potongTeks(String teks, int panjangMaksimal) {
        if (teks == null) {
            return "";
        }

        teks = teks.trim();

        if (teks.length() <= panjangMaksimal) {
            return teks;
        }

        if (panjangMaksimal <= 3) {
            return teks.substring(0, panjangMaksimal);
        }

        return teks.substring(
                0,
                panjangMaksimal - 3
        ) + "...";
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