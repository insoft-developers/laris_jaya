package com.insoft.laris.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.insoft.laris.Interface.holdInterface;
import com.insoft.laris.R;
import com.insoft.laris.adapter.ItemHold;
import com.insoft.laris.json.HapusItemRequestJson;
import com.insoft.laris.json.HapusItemResponseJson;
import com.insoft.laris.json.HoldPenjualanResponseJson;
import com.insoft.laris.model.Hold;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListPenjualanActivity extends AppCompatActivity implements holdInterface {
    private List<Hold> datapenjualan;
    private RegisterAPI registerAPI;
    private RecyclerView rv;
    private ProgressBar loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_penjualan);
        registerAPI = UtilsAPI.getApiService();

        rv = findViewById(R.id.rv_penjualan);
        loading = findViewById(R.id.loading);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        rv.setLayoutManager(llm);
        fetch_data();

    }

    private void fetch_data() {
        loading.setVisibility(View.VISIBLE);
        registerAPI.hold_penjualan().enqueue(new Callback<HoldPenjualanResponseJson>() {
            @Override
            public void onResponse(Call<HoldPenjualanResponseJson> call, Response<HoldPenjualanResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    if(resultcode.equalsIgnoreCase("00")) {
                        datapenjualan = response.body().getData();
                        ItemHold itemHold = new ItemHold(ListPenjualanActivity.this, datapenjualan, ListPenjualanActivity.this);
                        itemHold.notifyDataSetChanged();
                        rv.setAdapter(itemHold);
                    }
                }
            }

            @Override
            public void onFailure(Call<HoldPenjualanResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(ListPenjualanActivity.this, t.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void pilihItem(int posisi) {
        Intent intent = new Intent(ListPenjualanActivity.this, PenjualanActivity.class);
        intent.putExtra("id_hold", datapenjualan.get(posisi).getId_hold());
        intent.putExtra("cust_name", datapenjualan.get(posisi).getCustomer_name());
        intent.putExtra("total_harga", datapenjualan.get(posisi).getTotal_penjualan());
        startActivity(intent);
    }

    @Override
    public void hapusItem(int posisi) {
        showDialogHapus(posisi);
    }

    private void showDialogHapus(int posisi) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ListPenjualanActivity.this);
        builder1.setMessage("Hapus Penjualan Ini..?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "YA",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        conHapusItem(posisi);
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

    private void conHapusItem(int posisi) {
        loading.setVisibility(View.VISIBLE);
        HapusItemRequestJson param = new HapusItemRequestJson();
        param.setId(datapenjualan.get(posisi).getId_hold());
        param.setType(2);
        registerAPI.hapus_item(param).enqueue(new Callback<HapusItemResponseJson>() {
            @Override
            public void onResponse(Call<HapusItemResponseJson> call, Response<HapusItemResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    if(resultcode.equalsIgnoreCase("00")) {
                        fetch_data();
                    }
                }
            }

            @Override
            public void onFailure(Call<HapusItemResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
            }
        });
    }
}