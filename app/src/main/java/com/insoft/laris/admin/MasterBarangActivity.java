package com.insoft.laris.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.insoft.laris.Interface.masterBarangInterface;
import com.insoft.laris.R;
import com.insoft.laris.adapter.ItemMasterBarang;
import com.insoft.laris.json.BarangResponseJson;
import com.insoft.laris.json.CustomerRequestJson;
import com.insoft.laris.json.HapusProdukRequestJson;
import com.insoft.laris.json.HapusProdukResponseJson;
import com.insoft.laris.model.Produk;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MasterBarangActivity extends AppCompatActivity implements masterBarangInterface {
    private RegisterAPI registerAPI;
    private ProgressBar loading;
    private EditText etcari;
    private RecyclerView rvbarang;
    private FloatingActionButton fabtambah;
    private List<Produk> dataProduk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_barang);
        registerAPI = UtilsAPI.getApiService();
        loading = findViewById(R.id.loading);
        etcari = findViewById(R.id.etcari);
        rvbarang = findViewById(R.id.rvbarang);
        fabtambah = findViewById(R.id.fab_tambah);
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