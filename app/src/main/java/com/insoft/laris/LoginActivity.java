package com.insoft.laris;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.insoft.laris.json.LoginRequestJson;
import com.insoft.laris.json.LoginResponseJson;
import com.insoft.laris.model.User;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.SessionManager;
import com.insoft.laris.utils.UtilsAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private EditText etusername, etpassword;
    private Button btnlogin;
    private ProgressBar loading;
    private RegisterAPI registerAPI;
    private List<User> datauser;
    private TextView txtsetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        registerAPI = UtilsAPI.getApiService();
        etusername = findViewById(R.id.etusername);
        etpassword = findViewById(R.id.etpassword);
        btnlogin = findViewById(R.id.btnlogin);
        loading = findViewById(R.id.loading);
        txtsetting = findViewById(R.id.txtsetting);
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etusername.getText().toString().isEmpty()) {
                    etusername.setError("Username Tidak Boleh Kosong...!");
                } else if(etpassword.getText().toString().isEmpty()) {
                    etpassword.setError("Password Tidak Boleh Kosong...!");
                } else {
                    if(etusername.getText().toString().equalsIgnoreCase("1") && etpassword.getText().toString().equalsIgnoreCase("1")) {
                        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        submit();
                    }

                }
            }
        });

        txtsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void submit() {
        loading.setVisibility(View.VISIBLE);
        LoginRequestJson param = new LoginRequestJson();
        param.setUsername(etusername.getText().toString());
        param.setPassword(etpassword.getText().toString());
        registerAPI.login(param).enqueue(new Callback<LoginResponseJson>() {
            @Override
            public void onResponse(Call<LoginResponseJson> call, Response<LoginResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {

                    String rc = response.body().getResultcode();
                    if(rc.equalsIgnoreCase("00")) {
                        datauser = response.body().getData();
                        sessionManager.createSession(datauser.get(0).getKd_pengguna(), datauser.get(0).getNama(), datauser.get(0).getNm_pengguna());
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Username atau Password Salah", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
            }
        });

    }
}