package com.insoft.laris;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.insoft.laris.admin.ListPenjualanActivity;
import com.insoft.laris.admin.MasterBarangActivity;
import com.insoft.laris.admin.MasterPelangganActivity;
import com.insoft.laris.admin.SalesReportActivity;
import com.insoft.laris.admin.pengguna.PenggunaActivity;
import com.insoft.laris.json.BarangResponseJson;
import com.insoft.laris.json.CustomerRequestJson;
import com.insoft.laris.json.CustomerResponseJson;
import com.insoft.laris.model.Pelanggan;
import com.insoft.laris.model.Produk;
import com.insoft.laris.utils.MyDatabaseHelper;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity {
    private ProgressBar loading;
    private Button btnpenjualan, btnbarang, btnlaporan, btnpelanggan, btn_sync, btnPengguna;
    MyDatabaseHelper db;
    private List<Produk> dataProduk;
    private List<Pelanggan> dataPelanggan;

    private RegisterAPI registerAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        db = new MyDatabaseHelper(this);
        btnpenjualan = findViewById(R.id.btn_penjualan);
        btnbarang = findViewById(R.id.btn_barang);
        btnlaporan = findViewById(R.id.btn_laporan);
        btnpelanggan = findViewById(R.id.btn_pelangggan);
        btnlaporan = findViewById(R.id.btn_laporan);
        btnPengguna = findViewById(R.id.btn_pengguna);
        btn_sync = findViewById(R.id.btn_sync);
        loading = findViewById(R.id.loading);

        btn_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_product_list();
            }
        });

        btnlaporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, SalesReportActivity.class);
                startActivity(intent);
            }
        });

        btnPengguna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, PenggunaActivity.class);
                startActivity(intent);
            }
        });


        btnpenjualan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, ListPenjualanActivity.class);
                startActivity(intent);
            }
        });

        btnpelanggan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, MasterPelangganActivity.class);
                startActivity(intent);

            }
        });

        btnbarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, MasterBarangActivity.class);
                startActivity(intent);
            }
        });
    }


    private void get_product_list() {
        registerAPI = UtilsAPI.getApiService();
        loading.setVisibility(View.VISIBLE);
        CustomerRequestJson param = new CustomerRequestJson();
        param.setKata_cari("");
        registerAPI.product_list(param).enqueue(new Callback<BarangResponseJson>() {
            @Override
            public void onResponse(Call<BarangResponseJson> call, Response<BarangResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    if(resultcode.equalsIgnoreCase("00")) {
                        dataProduk = response.body().getData();
                        if (dataProduk != null && !dataProduk.isEmpty()) {

                            db.clear_master_barang();

                            for (Produk p : dataProduk) {
                                db.insert_master_barang(
                                        p.getKd_barang(),
                                        p.getBarcode(),
                                        p.getNm_barang(),
                                        p.getKd_kategori(),
                                        p.getHarga_beli(),
                                        p.getHarga_jual(),
                                        p.getSatuan(),
                                        p.getStok(),
                                        p.getKonversi(),
                                        p.getHj(),
                                        p.getHarga_member(),
                                        p.getDiskon_member(),
                                        p.getKd_supplier(),
                                        p.getDiskon()
                                );
                            }

                            db.close();

                            Toast.makeText(AdminActivity.this, "DataProduk berhasil di sync: " + dataProduk.size(), Toast.LENGTH_LONG).show();
                            get_pelanggan_list();
                        } else {
                            Log.e("SYNC", "DataProduk kosong / null");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<BarangResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void get_pelanggan_list() {
        loading.setVisibility(View.VISIBLE);
        CustomerRequestJson param = new CustomerRequestJson();
        param.setKata_cari("");
        registerAPI.get_customer(param).enqueue(new Callback<CustomerResponseJson>() {
            @Override
            public void onResponse(Call<CustomerResponseJson> call, Response<CustomerResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    if(resultcode.equalsIgnoreCase("00")) {
                        dataPelanggan = response.body().getData();
                        if (dataPelanggan != null && !dataPelanggan.isEmpty()) {

                            db.clear_master_pelanggan();

                            for (Pelanggan p : dataPelanggan) {
                                db.insert_master_pelanggan(
                                        p.getKd_pelanggan(),
                                        p.getNm_pelanggan(),
                                        p.getAlamat(),
                                        p.getKontak(),
                                        p.getGrup(),
                                        p.getTelepon()
                                );
                            }

                            db.close();
                            Toast.makeText(AdminActivity.this, "DataPelanggan berhasil di sync: " + dataPelanggan.size(), Toast.LENGTH_LONG).show();
                        } else {
                            Log.e("SYNC", "DataPelanggan kosong / null");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
            }
        });
    }
}