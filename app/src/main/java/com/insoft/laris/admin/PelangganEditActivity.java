package com.insoft.laris.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.insoft.laris.R;
import com.insoft.laris.json.PelangganRequestJson;
import com.insoft.laris.json.PelangganResponseJson;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PelangganEditActivity extends AppCompatActivity {
    private EditText et_kode, et_nama, et_alamat, et_phne;
    private Button btn_simpan;
    private ProgressBar loading;
    private int id_pelanggan;
    private RegisterAPI registerAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelanggan_edit);

        et_kode = findViewById(R.id.et_kode);
        et_nama = findViewById(R.id.et_nama);
        et_alamat = findViewById(R.id.et_alamat);
        et_phne = findViewById(R.id.et_phone);

        btn_simpan = findViewById(R.id.btn_simpan);
        loading = findViewById(R.id.loading);
        id_pelanggan = getIntent().getIntExtra("customer_id", 0);
        et_kode.setText(getIntent().getStringExtra("customer_code"));
        et_nama.setText(getIntent().getStringExtra("customer_name"));
        et_alamat.setText(getIntent().getStringExtra("customer_address"));
        et_phne.setText(getIntent().getStringExtra("customer_phne"));




        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_nama.getText().toString().isEmpty()) {
                    et_nama.setError("Nama Tidak Boleh Kosong...!");
                }
                else if(et_alamat.getText().toString().isEmpty()) {
                    et_alamat.setError("Alamat Tidak Boleh Kosong...!");
                } else {
                    submit(et_nama.getText().toString(), et_alamat.getText().toString(), et_phne.getText().toString());
                }
            }
        });

    }

    private  void submit(String nama, String alamat, String phne) {
        registerAPI = UtilsAPI.getApiService();
        loading.setVisibility(View.VISIBLE);
        PelangganRequestJson param = new PelangganRequestJson();
        param.setId(id_pelanggan);
        param.setNama(nama);
        param.setAlamat(alamat);
        param.setPhone(phne);
        registerAPI.update_pelanggan(param).enqueue(new Callback<PelangganResponseJson>() {
            @Override
            public void onResponse(Call<PelangganResponseJson> call, Response<PelangganResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    if(resultcode.equalsIgnoreCase("00")) {
                        Intent intent = new Intent(PelangganEditActivity.this, MasterPelangganActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<PelangganResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(PelangganEditActivity.this, t.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}