package com.insoft.laris.admin.pengguna;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.insoft.laris.R;
import com.insoft.laris.adapter.ItemMasterPelanggan;
import com.insoft.laris.admin.MasterBarangActivity;
import com.insoft.laris.admin.MasterPelangganActivity;
import com.insoft.laris.admin.PelangganAddActivity;
import com.insoft.laris.admin.PelangganEditActivity;
import com.insoft.laris.json.CustomerRequestJson;
import com.insoft.laris.json.CustomerResponseJson;
import com.insoft.laris.json.GeneralResponseJson;
import com.insoft.laris.json.HapusProdukRequestJson;
import com.insoft.laris.json.HapusProdukResponseJson;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PenggunaActivity extends AppCompatActivity implements PenggunaInterface {
    private RecyclerView rv;
    private RegisterAPI api;
    private List<PenggunaModel> penggunaList;

    private FloatingActionButton fabTambah;
    private EditText etCari;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengguna);
        api = UtilsAPI.getApiService();

        rv = findViewById(R.id.rv_pengguna);
        etCari = findViewById(R.id.etcari);
        fabTambah = findViewById(R.id.fab_tambah);
        loading = findViewById(R.id.loading);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);



        fetch_data("");


        fabTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PenggunaActivity.this, PenggunaAddActivity.class);
                startActivity(intent);
            }
        });

        etCari.addTextChangedListener(new TextWatcher() {
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


    }

    private void fetch_data(String s) {
        loading.setVisibility(View.VISIBLE);
        PenggunaRequestJson param = new PenggunaRequestJson();
        param.setKata_cari(s);

        api.get_daftar_pengguna(param).enqueue(new Callback<PenggunaResponseJson>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<PenggunaResponseJson> call, Response<PenggunaResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String res = response.body().getResultcode();
                    if(res.equalsIgnoreCase("00")) {
                        penggunaList = response.body().getData();
                        PenggunaItem item = new PenggunaItem(PenggunaActivity.this, penggunaList, PenggunaActivity.this);
                        item.notifyDataSetChanged();
                        rv.setAdapter(item);

                    }
                }
            }

            @Override
            public void onFailure(Call<PenggunaResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(PenggunaActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void pilihPengguna(int position) {
        int id = penggunaList.get(position).getId();
        String kd_pengguna = penggunaList.get(position).getKd_pengguna();
        String nm_pengguna = penggunaList.get(position).getNm_pengguna();
        String name = penggunaList.get(position).getNama();
        String level = penggunaList.get(position).getLevel();
        String alamat = penggunaList.get(position).getAlamat();
        String telepon = penggunaList.get(position).getTelepon();



        Intent intent = new Intent(PenggunaActivity.this, PenggunaEditActivity.class);
        intent.putExtra("user_id", id);
        intent.putExtra("user_code", kd_pengguna);
        intent.putExtra("user_name", nm_pengguna);
        intent.putExtra("full_name", name);
        intent.putExtra("level", level);
        intent.putExtra("alamat", alamat);
        intent.putExtra("telepon", telepon);
        startActivity(intent);
    }

    @Override
    public void hapusPengguna(int posisi) {
        showDialogHapus(String.valueOf(penggunaList.get(posisi).getId()));
    }

    private void showDialogHapus(String kode) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(PenggunaActivity.this);
        builder1.setMessage("Hapus Item Ini..?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "YA",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        conHapusItem(Integer.parseInt(kode));
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

    private void conHapusItem(int kode) {
        loading.setVisibility(View.VISIBLE);
        PenggunaRequestJson param = new PenggunaRequestJson();
        param.setId(kode);
        api.deletePengguna(param).enqueue(new Callback<GeneralResponseJson>() {
            @Override
            public void onResponse(Call<GeneralResponseJson> call, Response<GeneralResponseJson> response) {
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
            public void onFailure(Call<GeneralResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
            }
        });
    }
}