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

public class PelangganAddActivity extends AppCompatActivity {

    private EditText etnama, etalamat, etphone;
    private Button btn_simpan;
    private ProgressBar loading;
    private RegisterAPI registerAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelanggan_add);
        loading = findViewById(R.id.loading);
        etnama = findViewById(R.id.et_nama);
        etalamat = findViewById(R.id.et_alamat);
        btn_simpan = findViewById(R.id.btn_simpan);
        etphone = findViewById(R.id.et_phone);

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etnama.getText().toString().isEmpty()) {
                    etnama.setError("Nama Tidak Boleh Kosong...!");
                }
                else if(etalamat.getText().toString().isEmpty()) {
                    etalamat.setError("Alamat Tidak Boleh Kosong...!");
                }
                else if(etphone.getText().toString().isEmpty()) {
                    etphone.setError("Nomor Telepon Tidak Boleh Kosong...!");
                }
                else {
                    submit(etnama.getText().toString(), etalamat.getText().toString(), etphone.getText().toString());
                }
            }
        });
    }

    private  void submit(String nama, String alamat, String phone) {
        registerAPI = UtilsAPI.getApiService();
        loading.setVisibility(View.VISIBLE);
        PelangganRequestJson param = new PelangganRequestJson();
        param.setNama(nama);
        param.setAlamat(alamat);
        param.setPhone(phone);
        registerAPI.tambah_pelanggan(param).enqueue(new Callback<PelangganResponseJson>() {
            @Override
            public void onResponse(Call<PelangganResponseJson> call, Response<PelangganResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    if(resultcode.equalsIgnoreCase("00")) {
                        Intent intent = new Intent(PelangganAddActivity.this, MasterPelangganActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<PelangganResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(PelangganAddActivity.this, t.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}