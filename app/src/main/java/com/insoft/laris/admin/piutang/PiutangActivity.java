package com.insoft.laris.admin.piutang;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.insoft.laris.R;
import com.insoft.laris.admin.pengguna.PenggunaActivity;
import com.insoft.laris.admin.pengguna.PenggunaItem;
import com.insoft.laris.admin.pengguna.PenggunaRequestJson;
import com.insoft.laris.admin.pengguna.PenggunaResponseJson;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class PiutangActivity  extends AppCompatActivity {
    private SearchView searchPiutang;
    private RadioGroup radioFilterPiutang;
    private RecyclerView rvPiutang;
    private ProgressBar loadingPiutang;
    private LinearLayout layoutDataKosong;
    private TextView tvPesanKosong;
    private TextView tvTotalSisaHutang;

    private String kataPencarian = "";
    private String filterDipilih = "outstanding";

    private RegisterAPI api;
    private List<Piutang> piutangList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piutang);
        api = UtilsAPI.getApiService();

        searchPiutang =
                findViewById(R.id.searchPiutang);

        radioFilterPiutang =
                findViewById(R.id.radioFilterPiutang);

        rvPiutang =
                findViewById(R.id.rvPiutang);

        loadingPiutang =
                findViewById(R.id.loadingPiutang);

        layoutDataKosong =
                findViewById(R.id.layoutDataKosong);

        tvPesanKosong =
                findViewById(R.id.tvPesanKosong);

        tvTotalSisaHutang = findViewById(R.id.tvTotalSisaHutang);


        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvPiutang.setLayoutManager(llm);



        fetch_data("", filterDipilih);

        searchPiutang.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetch_data(query.trim(), filterDipilih);
                searchPiutang.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetch_data(newText.trim(), filterDipilih);
                return true;
            }
        });

        radioFilterPiutang.setOnCheckedChangeListener(
                (group, checkedId) -> {

                    if (checkedId == R.id.radioOutstanding) {
                        filterDipilih = "outstanding";

                    }
                    else if (checkedId == R.id.radioTempo) {
                        filterDipilih = "tempo";
                    }
                    else if (checkedId == R.id.radioSemua) {
                        filterDipilih = "all";
                    }

                    fetch_data(kataPencarian, filterDipilih);
                }
        );

    }

    private void hitungTotalSisaHutang(List<Piutang> daftarPiutang) {
        double total = 0;

        if (daftarPiutang != null) {
            for (Piutang item : daftarPiutang) {
                if (item == null) {
                    continue;
                }

                String nilaiSisa = String.valueOf(item.getSisa());

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

        tvTotalSisaHutang.setText(formatRupiah.format(total));
    }
    private void fetch_data(String s, String filter) {
        loadingPiutang.setVisibility(View.VISIBLE);
        PiutangRequestJson param = new PiutangRequestJson();
        param.setKata_cari(s);
        param.setFilter(filter);

        api.piutangList(param).enqueue(new Callback<PiutangResponseJson>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<PiutangResponseJson> call, Response<PiutangResponseJson> response) {
                loadingPiutang.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String res = response.body().getResultcode();
                    if(res.equalsIgnoreCase("00")) {
                        piutangList = response.body().getData();
                        hitungTotalSisaHutang(piutangList);

                        PiutangItem item = new PiutangItem(PiutangActivity.this, piutangList);
                        item.notifyDataSetChanged();
                        rvPiutang.setAdapter(item);

                    }
                }
            }

            @Override
            public void onFailure(Call<PiutangResponseJson> call, Throwable t) {
                loadingPiutang.setVisibility(View.GONE);
                Toast.makeText(PiutangActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
