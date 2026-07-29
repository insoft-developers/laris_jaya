package com.insoft.laris.admin.pembayaran;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.insoft.laris.R;
import com.insoft.laris.admin.piutang.PiutangActivity;
import com.insoft.laris.admin.piutang.PiutangItem;
import com.insoft.laris.admin.piutang.PiutangRequestJson;
import com.insoft.laris.admin.piutang.PiutangResponseJson;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PembayaranActivity  extends AppCompatActivity {
    private RegisterAPI api;
    private SearchView searchView;
    private EditText etTanggalMulai;
    private EditText etTanggalSelesai;
    private MaterialButton btnResetTanggal, btnTambahPembayaran;
    private RecyclerView rvPembayaran;
    private ProgressBar loading;
    private LinearLayout layoutDataKosong;
    private TextView tvPesanKosong;
    private TextView tvTotalPembayaran;

    private List<Pembayaran> pembayaranList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran);
        api = UtilsAPI.getApiService();

        searchView = findViewById(R.id.searchView);
        etTanggalMulai = findViewById(R.id.etTanggalMulai);
        etTanggalSelesai = findViewById(R.id.etTanggalSelesai);
        btnResetTanggal = findViewById(R.id.btnResetTanggal);
        btnTambahPembayaran = findViewById(R.id.btnTambahPembayaran);
        rvPembayaran = findViewById(R.id.rvPembayaran);

        loading = findViewById(R.id.loading);

        layoutDataKosong = findViewById(R.id.layoutDataKosong);
        tvPesanKosong = findViewById(R.id.tvPesanKosong);
        tvTotalPembayaran = findViewById(R.id.tvTotalPembayaran);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvPembayaran.setLayoutManager(llm);

        fetch_data("", "", "", "" );

        btnTambahPembayaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PembayaranActivity.this, PiutangActivity.class);
                startActivity(intent);
            }
        });



    }

    private void fetch_data(String s, String awal, String akhir, String nota ) {
        loading.setVisibility(View.VISIBLE);
        PembayaranRequestJson param = new PembayaranRequestJson();
        param.setCari(s);
        param.setAwal(awal);
        param.setAkhir(akhir);
        param.setNota(nota);

        api.pembayaranList(param).enqueue(new Callback<PembayaranResponseJson>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<PembayaranResponseJson> call, Response<PembayaranResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String res = response.body().getResultcode();
                    if(res.equalsIgnoreCase("00")) {
                        pembayaranList = response.body().getData();
//                        hitungTotalSisaHutang(piutangList);

                        PembayaranItem item = new PembayaranItem(PembayaranActivity.this, pembayaranList);
                        item.notifyDataSetChanged();
                        rvPembayaran.setAdapter(item);

                    }
                }
            }

            @Override
            public void onFailure(Call<PembayaranResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(PembayaranActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
