package com.insoft.laris.report;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.insoft.laris.R;
import com.insoft.laris.adapter.PerInvoiceAdapter;
import com.insoft.laris.json.PerInvoiceRequestJson;
import com.insoft.laris.json.PerInvoiceResponseJson;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.SessionManager;
import com.insoft.laris.utils.UtilsAPI;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

public class PerInvoiceActivity extends AppCompatActivity {

    private RecyclerView rv;
    private ProgressBar loading;
    private SessionManager sessionManager;
    private RegisterAPI registerAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_per_invoice);
        sessionManager = new SessionManager(this);
        registerAPI = UtilsAPI.getApiService();
        rv = findViewById(R.id.rv);
        loading = findViewById(R.id.loading);

        LinearLayoutManager layoutManager = new LinearLayoutManager(PerInvoiceActivity.this);
        rv.setLayoutManager(layoutManager);

        HashMap<String,String> users = sessionManager.getSessionData();

        String iduser = getIntent().getStringExtra("userid");

        String sekarang = getIntent().getStringExtra("sekarang");
        fetch_data(iduser, sekarang);
    }

    private void fetch_data(String iduser, String sekarang) {
        loading.setVisibility(View.VISIBLE);
        PerInvoiceRequestJson param = new PerInvoiceRequestJson();
        param.setIduser(iduser);
        param.setSekarang(sekarang);

        registerAPI = UtilsAPI.getApiService();
        registerAPI.perinvoice(param).enqueue(new Callback<PerInvoiceResponseJson>() {
            @Override
            public void onResponse(Call<PerInvoiceResponseJson> call, retrofit2.Response<PerInvoiceResponseJson> response) {
                loading.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    PerInvoiceAdapter adapter = new PerInvoiceAdapter(PerInvoiceActivity.this, response.body().getData());
                    adapter.notifyDataSetChanged();
                    rv.setAdapter(adapter);
                } else {
                    Log.d("REs", response.toString());
                }
            }

            @Override
            public void onFailure(Call<PerInvoiceResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(PerInvoiceActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}