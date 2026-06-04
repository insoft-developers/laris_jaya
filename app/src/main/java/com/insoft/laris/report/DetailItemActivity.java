package com.insoft.laris.report;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.insoft.laris.R;
import com.insoft.laris.adapter.DetailAdapter;
import com.insoft.laris.json.DetailRequestJson;
import com.insoft.laris.json.DetailResponseJson;
import com.insoft.laris.model.Header;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class DetailItemActivity extends AppCompatActivity {

    private TextView tanggal, namacustomer, summary, invoices;
    private ImageView image;
    private RecyclerView rvitem;
    private ProgressBar loading;
    private RegisterAPI registerAPI;
    private List<Header> header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_item);
        registerAPI = UtilsAPI.getApiService();

        tanggal = findViewById(R.id.tanggal);
        namacustomer = findViewById(R.id.namacustomer);
        summary = findViewById(R.id.summary);


        loading = findViewById(R.id.loading);
        invoices = findViewById(R.id.invoice);
        rvitem = findViewById(R.id.rvitem);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvitem.setLayoutManager(llm);
        String invoice = getIntent().getStringExtra("invoice");
        fetchdata(invoice);

    }

    private void fetchdata(String invoice){
        loading.setVisibility(View.VISIBLE);
        DetailRequestJson param = new DetailRequestJson();
        param.setInvoice(invoice);
        registerAPI = UtilsAPI.getApiService();
        registerAPI.detailinvoice(param).enqueue(new Callback<DetailResponseJson>() {
            @Override
            public void onResponse(Call<DetailResponseJson> call, retrofit2.Response<DetailResponseJson> response) {
                loading.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    header = response.body().getData();
                    tanggal.setText(header.get(0).getTanggal());
                    invoices.setText(header.get(0).getNota());
                    namacustomer.setText(header.get(0).getNm_pelanggan()+" - "+header.get(0).getAlamat());
                    Locale localeID = new Locale("in", "ID");
                    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                    summary.setText("TOTAL : "+formatRupiah.format(header.get(0).getBelanja()));
                    DetailAdapter adapter = new DetailAdapter(DetailItemActivity.this, response.body().getItem());
                    adapter.notifyDataSetChanged();
                    rvitem.setAdapter(adapter);
                } else {
                    Log.d("REs", response.toString());
                }
            }

            @Override
            public void onFailure(Call<DetailResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(DetailItemActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}