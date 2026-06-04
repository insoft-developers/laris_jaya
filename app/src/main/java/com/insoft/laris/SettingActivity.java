package com.insoft.laris;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.insoft.laris.utils.SessionServer;

import java.util.HashMap;

public class SettingActivity extends AppCompatActivity {
    private TextView txtserverdipakai;
    private EditText txtisiserver;
    private Button btnsimpan;
    private SessionServer sessionServer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sessionServer = new SessionServer(this);
        txtisiserver = findViewById(R.id.txtisiserver);
        txtserverdipakai = findViewById(R.id.txtserverdipakai);
        btnsimpan = findViewById(R.id.btnsimpan);

        HashMap<String,String> sserv = sessionServer.getSessionData();
        txtserverdipakai.setText(sserv.get(sessionServer.DOMAIN));

        btnsimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtisiserver.getText().toString().isEmpty()){
                    txtisiserver.setError("Tidak Boleh Kosong...");
                } else {
                    sessionServer.createSession(txtisiserver.getText().toString());
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }
        });

    }

    private void keluarAplikasi() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingActivity.this);
        builder1.setMessage("Apakah Anda Ingin Keluar Dari Aplikasi..?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "YA",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        builder1.setNegativeButton(
                "TIDAK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onBackPressed() {
        keluarAplikasi();

    }
}