package com.insoft.laris.admin.pembelian;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.insoft.laris.R;
import com.insoft.laris.admin.piutang.Piutang;
import com.insoft.laris.admin.piutang.PiutangActivity;
import com.insoft.laris.admin.piutang.PiutangItem;
import com.insoft.laris.admin.piutang.PiutangRequestJson;
import com.insoft.laris.admin.piutang.PiutangResponseJson;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PembelianActivity extends AppCompatActivity implements PembelianInterface {
    private RegisterAPI api;
    private List<Pembelian> pembelianList;

    private SearchView searchPembelian;
    private MaterialButton btnTambahPembelian;

    private TextView tvJumlahPembelian;
    private TextView tvFilterAktif;
    private TextView tvPesanKosong;
    private TextView tvTotalPembelian;

    private RecyclerView rvPembelian;
    private ProgressBar loadingPembelian;
    private LinearLayout layoutDataKosong;
    private MaterialCardView cardTotalPembelian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembelian);
        api = UtilsAPI.getApiService();

        searchPembelian =
                findViewById(R.id.searchPembelian);

        btnTambahPembelian =
                findViewById(R.id.btnTambahPembelian);

        tvJumlahPembelian =
                findViewById(R.id.tvJumlahPembelian);

        tvFilterAktif =
                findViewById(R.id.tvFilterAktif);

        rvPembelian =
                findViewById(R.id.rvPembelian);

        loadingPembelian =
                findViewById(R.id.loadingPembelian);

        layoutDataKosong =
                findViewById(R.id.layoutDataKosong);

        tvPesanKosong =
                findViewById(R.id.tvPesanKosong);

        cardTotalPembelian =
                findViewById(R.id.cardTotalPembelian);

        tvTotalPembelian =
                findViewById(R.id.tvTotalPembelian);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvPembelian.setLayoutManager(llm);

        fetch_data("");


        searchPembelian.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetch_data(query.trim());
                searchPembelian.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetch_data(newText.trim());
                return true;
            }
        });

    }



    private void fetch_data(String s) {
        loadingPembelian.setVisibility(View.VISIBLE);
        PembelianRequestJson param = new PembelianRequestJson();
        param.setCari(s);

        api.pembelian_list(param).enqueue(new Callback<PembelianResponseJson>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<PembelianResponseJson> call, Response<PembelianResponseJson> response) {
                loadingPembelian.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String res = response.body().getResultcode();
                    if(res.equalsIgnoreCase("00")) {
                        pembelianList = response.body().getData();
                        hitungTotalPembelian(pembelianList);

                        PembelianItem adapter = new PembelianItem(PembelianActivity.this, pembelianList, PembelianActivity.this);
                        adapter.notifyDataSetChanged();
                        rvPembelian.setAdapter(adapter);

                    }
                }
            }

            @Override
            public void onFailure(Call<PembelianResponseJson> call, Throwable t) {
                loadingPembelian.setVisibility(View.GONE);
                Toast.makeText(PembelianActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void hitungTotalPembelian(List<Pembelian> daftarPembelian) {
        double total = 0;

        if (daftarPembelian != null) {
            for (Pembelian item : daftarPembelian) {
                if (item == null) {
                    continue;
                }

                String nilaiSisa = String.valueOf(item.getTotal_pembelian());

                if (nilaiSisa != null && !nilaiSisa.trim().isEmpty()) {
                    try {
                        total += Double.parseDouble(nilaiSisa);
                    } catch (NumberFormatException ignored) {
                        // Abaikan nilai yang bukan angka
                    }
                }
            }
        }

        NumberFormat formatRupiah =
                NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        formatRupiah.setMaximumFractionDigits(0);
        formatRupiah.setMinimumFractionDigits(0);

        tvTotalPembelian.setText(formatRupiah.format(total));
    }

    @Override
    public void detail(int position) {
        Gson gson = new Gson();
        String json = gson.toJson(pembelianList.get(position));
        Intent intent = new Intent(PembelianActivity.this, PembelianDetailActivity.class);
        intent.putExtra("json", json);
        startActivity(intent);
    }

    @Override
    public void hapus(int position) {

    }
}
