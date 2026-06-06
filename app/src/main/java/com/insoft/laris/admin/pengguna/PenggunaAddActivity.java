package com.insoft.laris.admin.pengguna;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.insoft.laris.R;
import com.insoft.laris.admin.MasterPelangganActivity;
import com.insoft.laris.admin.PelangganAddActivity;
import com.insoft.laris.json.GeneralResponseJson;
import com.insoft.laris.json.PelangganRequestJson;
import com.insoft.laris.json.PelangganResponseJson;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PenggunaAddActivity extends AppCompatActivity {
    private Spinner spnLevel;
    private EditText etUsername, etPassword, etName, etAlamat, etTelepon;
    private Button btnSimpan;
    private RegisterAPI api;
    private ProgressBar loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pengguna);
        spnLevel = findViewById(R.id.spn_level);
        api = UtilsAPI.getApiService();
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etName = findViewById(R.id.et_name);
        etAlamat = findViewById(R.id.et_alamat);
        etTelepon = findViewById(R.id.et_telepon);
        btnSimpan = findViewById(R.id.btn_simpan);
        loading = findViewById(R.id.loading);

        spnLevelInit();

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etUsername.getText().toString().isEmpty()) {
                    etUsername.setError("Username Tidak Boleh Kosong...!");
                }
                else if(etPassword.getText().toString().isEmpty()) {
                    etPassword.setError("Password Tidak Boleh Kosong...!");
                }
                else if(etPassword.getText().toString().trim().length() < 6) {
                    etPassword.setError("Password tidak boleh kurang dari 6 karakter...!");
                }
                else if(etName.getText().toString().isEmpty()) {
                    etName.setError("Nama Lengkap Tidak Boleh Kosong...!");
                }
                else {
                    String selectedLevel = spnLevel.getSelectedItem().toString();
                    submit(
                            etUsername.getText().toString(),
                            etPassword.getText().toString().trim(),
                            selectedLevel,
                            etName.getText().toString().trim(),
                            etAlamat.getText().toString(),
                            etTelepon.getText().toString()

                    );
                }
            }
        });

    }

    private void spnLevelInit() {
        String[] level = {
                "Administrator",
                "Operator"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                level
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spnLevel.setAdapter(adapter);
    }

    private  void submit(
            String username,
            String password,
            String level,
            String name,
            String alamat,
            String telepon
        ) {

        loading.setVisibility(View.VISIBLE);
        PenggunaRequestJson param = new PenggunaRequestJson();
        param.setNm_pengguna(username);
        param.setPassword(password);
        param.setLevel(level);
        param.setNama(name);
        param.setAlamat(alamat);
        param.setTelepon(telepon);

        api.tambahPengguna(param).enqueue(new Callback<GeneralResponseJson>() {
            @Override
            public void onResponse(Call<GeneralResponseJson> call, Response<GeneralResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    if(resultcode.equalsIgnoreCase("00")) {
                        Intent intent = new Intent(PenggunaAddActivity.this, PenggunaActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<GeneralResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(PenggunaAddActivity.this, t.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}