package com.insoft.laris.report;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.insoft.laris.R;
import com.insoft.laris.adapter.CetakUlangStrukAdapter;
import com.insoft.laris.model.RiwayatStruk;
import com.insoft.laris.json.RiwayatStrukRequestJson;
import com.insoft.laris.json.RiwayatStrukResponseJson;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.SessionManager;
import com.insoft.laris.utils.UtilsAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CetakUlangStrukActivity extends AppCompatActivity {

    private EditText etSearch;
    private RecyclerView rvStruk;
    private ProgressBar loading;
    private TextView tvEmpty, tvJumlah;

    private RegisterAPI registerAPI;
    private Call<RiwayatStrukResponseJson> currentCall;

    private CetakUlangStrukAdapter adapter;

    private final List<RiwayatStruk> dataStruk =
            new ArrayList<>();

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private Runnable searchRunnable;

    private String userid;
    private int idcabang;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cetak_ulang_struk);
        sessionManager = new SessionManager(this);
        etSearch = findViewById(R.id.etSearch);
        rvStruk = findViewById(R.id.rvStruk);
        loading = findViewById(R.id.loading);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvJumlah = findViewById(R.id.tvJumlah);

        registerAPI = UtilsAPI.getApiService();

        /*
         * TODO:
         * Ambil userid dan idcabang dari session login
         * yang sudah digunakan aplikasi Laris.
         */

        HashMap<String, String> user = sessionManager.getSessionData();
        userid = user.get(sessionManager.ID);
        idcabang = 1;

        rvStruk.setLayoutManager(
                new LinearLayoutManager(this)
        );


        adapter = new CetakUlangStrukAdapter(
                dataStruk,
                item -> {

                    Intent intent = new Intent(
                            CetakUlangStrukActivity.this,
                            DetailStrukActivity.class
                    );

                    intent.putExtra("nota", item.getNota());
                    intent.putExtra("userid", userid);
                    intent.putExtra("idcabang", idcabang);

                    startActivity(intent);
                }
        );


        rvStruk.setAdapter(adapter);

        // Ambil 25 transaksi terakhir.
        fetch_data("");

        // Pencarian dengan jeda 400 ms.
        etSearch.addTextChangedListener(
                new android.text.TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start,
                            int count, int after) {}

                    @Override
                    public void onTextChanged(
                            CharSequence s, int start,
                            int before, int count) {

                        if (searchRunnable != null) {
                            handler.removeCallbacks(searchRunnable);
                        }

                        String keyword = s.toString().trim();

                        searchRunnable = () -> fetch_data(keyword);

                        handler.postDelayed(searchRunnable, 400);
                    }

                    @Override
                    public void afterTextChanged(
                            android.text.Editable s) {}
                });
    }

    private void fetch_data(String search) {

        if (userid == null || userid.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    "Session user belum diatur",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (currentCall != null) {
            currentCall.cancel();
        }

        RiwayatStrukRequestJson param =
                new RiwayatStrukRequestJson();

        param.setIduser(userid);
        param.setIdcabang(idcabang);
        param.setSearch(search);
        param.setLimit(25);

        loading.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        currentCall = registerAPI.riwayat_struk(param);

        currentCall.enqueue(
                new Callback<RiwayatStrukResponseJson>() {

                    @Override
                    public void onResponse(
                            Call<RiwayatStrukResponseJson> call,
                            Response<RiwayatStrukResponseJson> response) {

                        if (call.isCanceled()) return;

                        loading.setVisibility(View.GONE);

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            List<RiwayatStruk> result =
                                    response.body().getData();

                            dataStruk.clear();

                            if (result != null) {
                                dataStruk.addAll(result);
                            }

                            adapter.notifyDataSetChanged();

                            tvJumlah.setText(
                                    dataStruk.size() + " Transaksi"
                            );

                            tvEmpty.setVisibility(
                                    dataStruk.isEmpty()
                                            ? View.VISIBLE
                                            : View.GONE
                            );

                        } else {
                            Log.e("RIWAYAT_STRUK",
                                    "Response: " + response.code());

                            Toast.makeText(
                                    CetakUlangStrukActivity.this,
                                    "Gagal mengambil riwayat struk",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<RiwayatStrukResponseJson> call,
                            Throwable t) {

                        if (call.isCanceled()) return;

                        loading.setVisibility(View.GONE);

                        Log.e("RIWAYAT_STRUK",
                                "Error: " + t.getMessage());

                        Toast.makeText(
                                CetakUlangStrukActivity.this,
                                "Koneksi gagal: " + t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);

        if (currentCall != null) {
            currentCall.cancel();
        }

        super.onDestroy();
    }
}
