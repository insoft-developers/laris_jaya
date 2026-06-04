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

import com.insoft.laris.Interface.pelangganInterface;
import com.insoft.laris.adapter.ItemPelanggan;
import com.insoft.laris.model.Pelanggan;
import com.insoft.laris.utils.MyDatabaseHelper;
import com.insoft.laris.utils.RegisterAPI;

import java.util.ArrayList;
import java.util.List;

public class CustomerActivity extends AppCompatActivity implements pelangganInterface {
    private RecyclerView rvcustomer;
    private EditText etcari;
    private RegisterAPI registerAPI;
    private List<Pelanggan> pelangganList;
    private ProgressBar loading;
    MyDatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        db = new MyDatabaseHelper(this);
        rvcustomer = findViewById(R.id.rvcustomer);
        etcari = findViewById(R.id.etcari);
        loading = findViewById(R.id.loading);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvcustomer.setLayoutManager(llm);

        get_cust_data("");

        etcari.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                get_cust_data(s.toString());
            }
        });

    }




    private void get_cust_data(String cari) {
        List<Pelanggan> list = new ArrayList<>();

        Cursor cursor = db.tampilkan_master_pelanggan(cari);

        if (cursor.moveToFirst()) {
            do {
                Pelanggan p = new Pelanggan();
                p.setKd_pelanggan(cursor.getString(cursor.getColumnIndexOrThrow("kd_pelanggan")));
                p.setNm_pelanggan(cursor.getString(cursor.getColumnIndexOrThrow("nm_pelanggan")));
                p.setAlamat(cursor.getString(cursor.getColumnIndexOrThrow("alamat")));
                p.setKontak(cursor.getString(cursor.getColumnIndexOrThrow("contact")));
                p.setGrup(cursor.getString(cursor.getColumnIndexOrThrow("grup")));
                p.setTelepon(cursor.getString(cursor.getColumnIndexOrThrow("telepon")));
                list.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        pelangganList = list;
        ItemPelanggan item = new ItemPelanggan(CustomerActivity.this , list, CustomerActivity.this);
        item.notifyDataSetChanged();
        rvcustomer.setAdapter(item);
    }

    @Override
    public void pilih_pelanggan(int position) {
        String kd_pelanggan = pelangganList.get(position).getKd_pelanggan();
        String nm_pelanggan = pelangganList.get(position).getNm_pelanggan();
        String alamat_pelanggan = pelangganList.get(position).getAlamat();
        String grup_pelanggan = pelangganList.get(position).getGrup();

        Intent intent = new Intent();
        intent.putExtra("customer_code", kd_pelanggan);
        intent.putExtra("customer_name", nm_pelanggan);
        intent.putExtra("customer_address", alamat_pelanggan);
        intent.putExtra("customer_group", grup_pelanggan);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


}