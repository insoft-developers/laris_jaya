package com.insoft.laris;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.insoft.laris.Interface.barangInterface;
import com.insoft.laris.adapter.ItemBarang;
import com.insoft.laris.model.Produk;
import com.insoft.laris.utils.MyDatabaseHelper;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.SessionServer;

import java.util.ArrayList;
import java.util.List;

public class BarangActivity extends AppCompatActivity implements barangInterface {

    private RecyclerView rvbarang;
    private EditText etcari;
    private RegisterAPI registerAPI;
    private List<Produk> produkList;
    private ProgressBar loading;
    private SessionServer sessionServer;
    MyDatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang);
        db = new MyDatabaseHelper(this);
        rvbarang = findViewById(R.id.rvbarang);
        etcari = findViewById(R.id.etcari);
        loading = findViewById(R.id.loading);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvbarang.setLayoutManager(llm);
        sessionServer = new SessionServer(this);


        get_all_product("");

        etcari.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                get_all_product(s.toString());
            }
        });

    }


    private void get_all_product(String cari) {
        List<Produk> list = new ArrayList<>();

        Cursor cursor = db.tampilkan_master_barang(cari);

        if (cursor.moveToFirst()) {
            do {
                Produk p = new Produk();
                p.setKd_barang(cursor.getString(cursor.getColumnIndexOrThrow("kd_barang")));
                p.setBarcode(cursor.getString(cursor.getColumnIndexOrThrow("barcode")));
                p.setNm_barang(cursor.getString(cursor.getColumnIndexOrThrow("nm_barang")));
                p.setKd_kategori(cursor.getString(cursor.getColumnIndexOrThrow("kd_kategori")));
                p.setHarga_beli(cursor.getInt(cursor.getColumnIndexOrThrow("harga_beli")));
                p.setHarga_jual(cursor.getInt(cursor.getColumnIndexOrThrow("harga_jual")));
                p.setSatuan(cursor.getString(cursor.getColumnIndexOrThrow("satuan")));
                p.setStok(cursor.getInt(cursor.getColumnIndexOrThrow("stok")));
                p.setKonversi(cursor.getInt(cursor.getColumnIndexOrThrow("konversi")));
                p.setHj(cursor.getInt(cursor.getColumnIndexOrThrow("hj")));
                p.setHarga_member(cursor.getInt(cursor.getColumnIndexOrThrow("harga_member")));
                p.setDiskon_member(cursor.getInt(cursor.getColumnIndexOrThrow("diskon_member")));
                p.setKd_supplier(cursor.getString(cursor.getColumnIndexOrThrow("kd_supplier")));
                p.setDiskon(cursor.getInt(cursor.getColumnIndexOrThrow("diskon")));
                p.setHarga_reseller(cursor.getInt(cursor.getColumnIndexOrThrow("harga_reseller")));
                list.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        produkList = list;
        ItemBarang item = new ItemBarang(BarangActivity.this , list, BarangActivity.this);
        item.notifyDataSetChanged();
        rvbarang.setAdapter(item);
    }



    @Override
    public void pilih_barang(int position) {
        int konversi = produkList.get(position).getKonversi();
        String kodebarang = produkList.get(position).getKd_barang();
        int hargajual = produkList.get(position).getHarga_jual();
        int hargajualkarton = produkList.get(position).getHj();
        int hargagrosir = produkList.get(position).getHarga_member();
        int hargagrosirkarton = produkList.get(position).getDiskon_member();
        int hargafreelance = produkList.get(position).getHarga_freelance();
        int hargafreelancekarton = produkList.get(position).getHarga_karton_freelance();
        String barcode = produkList.get(position).getBarcode();
        String namabarang = produkList.get(position).getNm_barang();
        String satuan = produkList.get(position).getSatuan();
        int hargabeli = produkList.get(position).getHarga_beli();
        int diskon = produkList.get(position).getDiskon();

        Intent intent = new Intent();
        intent.putExtra("intent_konversi", konversi);
        intent.putExtra("intent_kodebarang", kodebarang);
        intent.putExtra("intent_harga_jual", hargajual);
        intent.putExtra("intent_harga_karton", hargajualkarton);
        intent.putExtra("intent_harga_grosir", hargagrosir);
        intent.putExtra("intent_grosir_karton", hargagrosirkarton);
        intent.putExtra("intent_harga_freelance", hargafreelance);
        intent.putExtra("intent_freelance_karton", hargafreelancekarton);
        intent.putExtra("intent_barcode", barcode);
        intent.putExtra("intent_nama_barang", namabarang);
        intent.putExtra("intent_satuan", satuan);
        intent.putExtra("intent_harga_beli", hargabeli);
        intent.putExtra("intent_diskon", diskon);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}