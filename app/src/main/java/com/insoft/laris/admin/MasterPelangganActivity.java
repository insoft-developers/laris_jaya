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
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.insoft.laris.Interface.masterPelangganInterface;
import com.insoft.laris.R;
import com.insoft.laris.adapter.ItemMasterPelanggan;
import com.insoft.laris.json.CustomerRequestJson;
import com.insoft.laris.json.CustomerResponseJson;
import com.insoft.laris.json.PelangganRequestJson;
import com.insoft.laris.json.PelangganResponseJson;
import com.insoft.laris.model.Pelanggan;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MasterPelangganActivity extends AppCompatActivity implements masterPelangganInterface {

    private RecyclerView rvcustomer;
    private EditText etcari;
    private RegisterAPI registerAPI;
    private List<Pelanggan> pelangganList;
    private ProgressBar loading;
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_pelanggan_activity);
        rvcustomer = findViewById(R.id.rvcustomer);
        etcari = findViewById(R.id.etcari);
        loading = findViewById(R.id.loading);
        fab = findViewById(R.id.fab_tambah);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvcustomer.setLayoutManager(llm);



        fetch_data("");


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MasterPelangganActivity.this, PelangganAddActivity.class);
                startActivity(intent);
            }
        });

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

    }

    private void fetch_data(String s) {
        loading.setVisibility(View.VISIBLE);
        CustomerRequestJson param = new CustomerRequestJson();
        param.setKata_cari(s);
        registerAPI = UtilsAPI.getApiService();
        registerAPI.get_customer(param).enqueue(new Callback<CustomerResponseJson>() {
            @Override
            public void onResponse(Call<CustomerResponseJson> call, Response<CustomerResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String res = response.body().getResultcode();
                    if(res.equalsIgnoreCase("00")) {
                        pelangganList = response.body().getData();
                        ItemMasterPelanggan item = new ItemMasterPelanggan(MasterPelangganActivity.this , pelangganList, MasterPelangganActivity.this);
                        item.notifyDataSetChanged();
                        rvcustomer.setAdapter(item);

                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(MasterPelangganActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void pilih_pelanggan(int position) {

        int id_pelanggan = pelangganList.get(position).getId();
        String kd_pelanggan = pelangganList.get(position).getKd_pelanggan();
        String nm_pelanggan = pelangganList.get(position).getNm_pelanggan();
        String alamat_pelanggan = pelangganList.get(position).getAlamat();
        String phne = pelangganList.get(position).getTelepon();


        Intent intent = new Intent(MasterPelangganActivity.this, PelangganEditActivity.class);
        intent.putExtra("customer_id", id_pelanggan);
        intent.putExtra("customer_code", kd_pelanggan);
        intent.putExtra("customer_name", nm_pelanggan);
        intent.putExtra("customer_address", alamat_pelanggan);
        intent.putExtra("customer_phne", phne);
        startActivity(intent);

    }

    @Override
    public void hapus_pelanggan(int posisi) {
        int id =  pelangganList.get(posisi).getId();

        showDialogHapus(id);
    }

    private void showDialogHapus(int cust_id) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(MasterPelangganActivity.this);
        builder1.setMessage("Hapus Pelanggan Ini..?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "YA",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        conHapusItem(cust_id);
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

    private void conHapusItem(int id) {

        loading.setVisibility(View.VISIBLE);
        PelangganRequestJson param = new PelangganRequestJson();
        param.setId(id);
        registerAPI.hapus_pelanggan(param).enqueue(new Callback<PelangganResponseJson>() {
            @Override
            public void onResponse(Call<PelangganResponseJson> call, Response<PelangganResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    if(resultcode.equalsIgnoreCase("00")) {
                        fetch_data("");
                    }
                }

            }

            @Override
            public void onFailure(Call<PelangganResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
            }
        });
    }

}