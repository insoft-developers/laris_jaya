package com.insoft.laris;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.insoft.laris.adapter.ItemTransaksi;
import com.insoft.laris.json.SalesRequestJson;
import com.insoft.laris.json.SalesResponseJson;
import com.insoft.laris.model.Sales;
import com.insoft.laris.model.SalesItem;
import com.insoft.laris.utils.MyDatabaseHelper;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransaksiActivity extends AppCompatActivity {
    private RecyclerView rv_transaksi;
    private ProgressBar loading;
    ArrayList<HashMap<String,String>> list_data;
    private ItemTransaksi adapter;
    private MyDatabaseHelper db;
    private FloatingActionButton fab_hapus_transaksi;
    private RegisterAPI registerAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);

        db = new MyDatabaseHelper(this);
        rv_transaksi = findViewById(R.id.rv_transaksi);
        loading = findViewById(R.id.loading);
        fab_hapus_transaksi = findViewById(R.id.fab_hapus_transaksi);

        display_data();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        rv_transaksi.setLayoutManager(llm);

        fab_hapus_transaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kirim_penjualan_ke_server();
            }
        });
    }


    private void kirim_penjualan_ke_server() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(TransaksiActivity.this);
        builder1.setMessage("Apakah Anda Ingin Mengirim data penjualan ke Server..?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "YA",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        kirim();
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);

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


    private void display_data() {
        list_data = new ArrayList<HashMap<String, String>>();
        Cursor cursor = db.tampilkan_penjualan();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }else {
            while(cursor.moveToNext()) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", cursor.getString(0));
                map.put("customer_name", cursor.getString(1));
                map.put("customer_address", cursor.getString(2));
                map.put("total", cursor.getString(3));
                map.put("tanggal", cursor.getString(4));
                map.put("nota", cursor.getString(5));
                map.put("subtotal", cursor.getString(6));
                map.put("discount", cursor.getString(7));
                map.put("kembalian", cursor.getString(8));
                map.put("pembayaran", cursor.getString(9));

                list_data.add(map);
                adapter = new ItemTransaksi(TransaksiActivity.this, list_data);
                adapter.notifyDataSetChanged();
                rv_transaksi.setAdapter(adapter);

            }
        }
    }

    private void kirim() {
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursorPenjualan = database.rawQuery("SELECT * FROM penjualan", null);
        List<Sales> listPenjualan = new ArrayList<>();
        if(cursorPenjualan.moveToFirst()){
            do{
                Sales pj = new Sales();
                pj.nota = cursorPenjualan.getString(cursorPenjualan.getColumnIndexOrThrow("nota"));
                pj.kd_pelanggan = cursorPenjualan.getString(cursorPenjualan.getColumnIndexOrThrow("kd_pelanggan"));
                pj.keterangan = cursorPenjualan.getString(cursorPenjualan.getColumnIndexOrThrow("keterangan"));
                pj.tanggal = cursorPenjualan.getString(cursorPenjualan.getColumnIndexOrThrow("tanggal"));
                pj.belanja = cursorPenjualan.getInt(cursorPenjualan.getColumnIndexOrThrow("belanja"));
                pj.bayar = cursorPenjualan.getInt(cursorPenjualan.getColumnIndexOrThrow("bayar"));
                pj.donasi = cursorPenjualan.getInt(cursorPenjualan.getColumnIndexOrThrow("donasi"));
                pj.kembali = cursorPenjualan.getInt(cursorPenjualan.getColumnIndexOrThrow("kembali"));
                pj.kd_user = cursorPenjualan.getString(cursorPenjualan.getColumnIndexOrThrow("kd_user"));
                pj.depo = cursorPenjualan.getInt(cursorPenjualan.getColumnIndexOrThrow("depo"));
                pj.bank_deposit = cursorPenjualan.getInt(cursorPenjualan.getColumnIndexOrThrow("bank_deposit"));
                pj.total_discount = cursorPenjualan.getInt(cursorPenjualan.getColumnIndexOrThrow("total_dicount"));
                pj.subtotal = cursorPenjualan.getInt(cursorPenjualan.getColumnIndexOrThrow("subtotal"));
                pj.tempo_hari = cursorPenjualan.getInt(cursorPenjualan.getColumnIndexOrThrow("tempo_hari"));
                if(cursorPenjualan.getString(cursorPenjualan.getColumnIndexOrThrow("jatuh_tempo")) == null || cursorPenjualan.getString(cursorPenjualan.getColumnIndexOrThrow("jatuh_tempo")).isEmpty() ) {
                    pj.jatuh_tempo = "";
                } else {
                    pj.jatuh_tempo = cursorPenjualan.getString(cursorPenjualan.getColumnIndexOrThrow("jatuh_tempo"));
                }

                pj.status_pembayaran = cursorPenjualan.getString(cursorPenjualan.getColumnIndexOrThrow("status_pembayaran"));

                Cursor cursorDetail = database.rawQuery("SELECT * FROM penjualan_item WHERE nota = ?", new String[]{pj.nota});
                List<SalesItem> listItem = new ArrayList<>();
                if (cursorDetail.moveToFirst()) {
                    do {
                        SalesItem it = new SalesItem();
                        it.nota = cursorDetail.getString(cursorDetail.getColumnIndexOrThrow("nota"));
                        it.kd_barang = cursorDetail.getString(cursorDetail.getColumnIndexOrThrow("kd_barang"));
                        it.barcode = cursorDetail.getString(cursorDetail.getColumnIndexOrThrow("barcode"));
                        it.nm_barang = cursorDetail.getString(cursorDetail.getColumnIndexOrThrow("nm_barang"));
                        it.satuan = cursorDetail.getString(cursorDetail.getColumnIndexOrThrow("satuan"));
                        it.jumlah = cursorDetail.getInt(cursorDetail.getColumnIndexOrThrow("jumlah"));
                        it.harga = cursorDetail.getInt(cursorDetail.getColumnIndexOrThrow("harga"));
                        it.modal = cursorDetail.getInt(cursorDetail.getColumnIndexOrThrow("modal"));
                        it.total = cursorDetail.getInt(cursorDetail.getColumnIndexOrThrow("total"));
                        it.status = cursorDetail.getInt(cursorDetail.getColumnIndexOrThrow("status"));
                        it.disk = cursorDetail.getInt(cursorDetail.getColumnIndexOrThrow("disk"));
                        it.price_type = cursorDetail.getInt(cursorDetail.getColumnIndexOrThrow("price_type"));
                        it.subtotal = cursorDetail.getInt(cursorDetail.getColumnIndexOrThrow("subtotal"));

                        listItem.add(it);
                    } while (cursorDetail.moveToNext());
                }
                cursorDetail.close();

                pj.item = listItem;
                listPenjualan.add(pj);
            }while (cursorPenjualan.moveToNext());
        }
        cursorPenjualan.close();
        db.close();
        registerAPI = UtilsAPI.getApiService();
        SalesRequestJson param = new SalesRequestJson();
        param.setPenjualan(listPenjualan);
        registerAPI.kirim_penjualan_ke_server(param).enqueue(new Callback<SalesResponseJson>() {
            @Override
            public void onResponse(Call<SalesResponseJson> call, Response<SalesResponseJson> response) {
                if(response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    Log.d("cek data", resultcode);
                    if(resultcode.equalsIgnoreCase("00")) {
                        Toast.makeText(getApplicationContext(), "Sync sukses!", Toast.LENGTH_SHORT).show();
                        // Hapus data lokal setelah sukses
                        SQLiteDatabase database = db.getWritableDatabase();
                        database.delete("penjualan_item", null, null);
                        database.delete("penjualan", null, null);
                        database.close();

                    } else {
                        Toast.makeText(getApplicationContext(), "Gagal: " + response.message().toString(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Gagal: " + response.message().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SalesResponseJson> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}