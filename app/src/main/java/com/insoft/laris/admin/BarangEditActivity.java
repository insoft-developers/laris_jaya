package com.insoft.laris.admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.insoft.laris.R;
import com.insoft.laris.json.SimpanProdukRequestJson;
import com.insoft.laris.json.SimpanProdukResponseJson;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarangEditActivity extends AppCompatActivity {
    private EditText etBarcode, etNamaBarang, etHargaBeli;
    private EditText etKonversi, etHargaJual, etHargaJualKarton;
    private EditText etHargaMember, etHargaMemberKarton;
    private EditText etHargaFreelance, etHargaFreelanceKarton;
    private Button btnSimpan;

    private ProgressBar loading;
    private RegisterAPI registerAPI;

    private ImageButton btnbarcode;


    private String kodebarang_, barcode_, namabarang_;
    private int hargabeli_, konversi_, hargajual_, hargajualkarton_, hargamember_, hargamemberkarton_, hargafreelance_, hargafreelancekarton_;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang_edit);
        registerAPI = UtilsAPI.getApiService();
        etBarcode = findViewById(R.id.et_barcode);
        etNamaBarang = findViewById(R.id.et_nama_barang);
        etHargaBeli = findViewById(R.id.et_harga_beli);
        etKonversi = findViewById(R.id.et_konversi);
        etHargaJual = findViewById(R.id.et_harga_jual);
        etHargaJualKarton = findViewById(R.id.et_harga_jual_karton);
        etHargaMember = findViewById(R.id.et_harga_member);
        etHargaMemberKarton = findViewById(R.id.et_harga_member_karton);
        etHargaFreelance = findViewById(R.id.et_harga_freelance);
        etHargaFreelanceKarton = findViewById(R.id.et_harga_freelance_karton);
        btnSimpan = findViewById(R.id.btn_simpan_barang);

        btnbarcode = findViewById(R.id.btnbarcode);
        loading = findViewById(R.id.loading);

        kodebarang_ = getIntent().getStringExtra("_kodebarang");
        barcode_ = getIntent().getStringExtra("_barcode");
        namabarang_ = getIntent().getStringExtra("_namabarang");
        hargabeli_ = getIntent().getIntExtra("_hargabeli", 0);
        konversi_ = getIntent().getIntExtra("_konversi", 0);
        hargajual_ = getIntent().getIntExtra("_hargajual", 0);
        hargajualkarton_ = getIntent().getIntExtra("_hargajualkarton", 0);
        hargamember_ = getIntent().getIntExtra("_hargamember", 0);
        hargamemberkarton_ = getIntent().getIntExtra("_hargamemberkarton", 0);
        hargafreelance_ = getIntent().getIntExtra("_hargafreelance", 0);
        hargafreelancekarton_ = getIntent().getIntExtra("_hargafreelancekarton", 0);


        etBarcode.setText(barcode_);
        etNamaBarang.setText(namabarang_);
        etHargaBeli.setText(String.valueOf(hargabeli_));
        etKonversi.setText(String.valueOf(konversi_));
        etHargaJual.setText(String.valueOf(hargajual_));
        etHargaJualKarton.setText(String.valueOf(hargajualkarton_ * konversi_));
        etHargaMember.setText(String.valueOf(hargamember_));
        etHargaMemberKarton.setText(String.valueOf(hargamemberkarton_* konversi_));
        etHargaFreelance.setText(String.valueOf(hargafreelance_));
        etHargaFreelanceKarton.setText(String.valueOf(hargafreelancekarton_ * konversi_));


        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etBarcode.getText().toString().isEmpty()) {
                    etBarcode.setError("Barcode Tidak Boleh Kosong...!");
                } else if(etNamaBarang.getText().toString().isEmpty()) {
                    etNamaBarang.setError("Nama Barang Tidak Boleh Kosong...!");
                } else if(etHargaBeli.getText().toString().isEmpty()) {
                    etHargaBeli.setError("Harga Beli Tidak Boleh Kosong...!");
                } else if(etKonversi.getText().toString().isEmpty()) {
                    etKonversi.setError("Konversi Tidak Boleh Kosong...!");
                } else if(etHargaJual.getText().toString().isEmpty()) {
                    etHargaJual.setError("Harga Jual Tidak Boleh Kosong...!");
                } else if(etHargaJualKarton.getText().toString().isEmpty()) {
                    etHargaJualKarton.setError("Harga Jual Karton Tidak Boleh Kosong...!");
                } else if(etHargaMember.getText().toString().isEmpty()) {
                    etHargaMember.setError("Harga Member Tidak Boleh Kosong...!");
                } else if(etHargaMemberKarton.getText().toString().isEmpty()) {
                    etHargaMemberKarton.setError("Harga Member Karton Tidak Boleh Kosong...!");
                } else if(etHargaFreelance.getText().toString().isEmpty()) {
                    etHargaFreelance.setError("Harga Freelance Tidak Boleh Kosong...!");
                } else if(etHargaFreelanceKarton.getText().toString().isEmpty()) {
                    etHargaFreelanceKarton.setError("Harga Freelance Karton Tidak Boleh Kosong...!");
                } else {
                    submitNewProduct();
                }

            }
        });

        etHargaJual.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(etKonversi.getText().toString().isEmpty()) {
                    etHargaJual.setError("Konversi Tidak Boleh Kosong...!");
                } else {
                    int _hargaJual = 0;
                    if(s.toString().isEmpty()) {
                        _hargaJual = 0;
                    } else {
                        _hargaJual = Integer.parseInt(s.toString());
                    }

                    int _konversi = Integer.parseInt(etKonversi.getText().toString());

                    int _hargaJualKarton = _hargaJual * _konversi;
                    etHargaJualKarton.setText(String.valueOf(_hargaJualKarton));


                }
            }
        });


        etHargaMember.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(etKonversi.getText().toString().isEmpty()) {
                    etHargaMember.setError("Konversi Tidak Boleh Kosong...!");
                } else {
                    int _konversi = Integer.parseInt(etKonversi.getText().toString());
                    int _hargaMember = 0;
                    if(s.toString().isEmpty()) {
                        _hargaMember = 0;
                    } else {
                        _hargaMember = Integer.parseInt(s.toString());
                    }

                    int _hargaMemberKarton = _hargaMember * _konversi;
                    etHargaMemberKarton.setText(String.valueOf(_hargaMemberKarton));

                }
            }
        });


        etHargaFreelance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(etKonversi.getText().toString().isEmpty()) {
                    etHargaFreelance.setError("Konversi Tidak Boleh Kosong...!");
                } else {
                    int _konversi = Integer.parseInt(etKonversi.getText().toString());
                    int _hargaFreelance = 0;
                    if(s.toString().isEmpty()) {
                        _hargaFreelance = 0;
                    } else {
                        _hargaFreelance = Integer.parseInt(s.toString());
                    }

                    int _hargaFreelanceKarton = _hargaFreelance * _konversi;
                    etHargaFreelanceKarton.setText(String.valueOf(_hargaFreelanceKarton));

                }
            }
        });

        btnbarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BarangEditActivity.this, BarcodeActivity.class );
                startActivityForResult(intent, 300);
            }
        });

    }

    private void submitNewProduct() {
        loading.setVisibility(View.VISIBLE);
        SimpanProdukRequestJson param = new SimpanProdukRequestJson();
        param.setKd_barang(kodebarang_);
        param.setBarcode(etBarcode.getText().toString());
        param.setHarga_beli(Integer.parseInt(etHargaBeli.getText().toString()));
        param.setHarga_freelance(Integer.parseInt(etHargaFreelance.getText().toString()));
        param.setHarga_freelance_karton(Integer.parseInt(etHargaFreelanceKarton.getText().toString()));
        param.setHarga_jual(Integer.parseInt(etHargaJual.getText().toString()));
        param.setHarga_jual_karton(Integer.parseInt(etHargaJualKarton.getText().toString()));
        param.setHarga_member(Integer.parseInt(etHargaMember.getText().toString()));
        param.setHarga_member_karton(Integer.parseInt(etHargaMemberKarton.getText().toString()));
        param.setKonversi(Integer.parseInt(etKonversi.getText().toString()));
        param.setNama_barang(etNamaBarang.getText().toString());

        registerAPI.update_produk(param).enqueue(new Callback<SimpanProdukResponseJson>() {
            @Override
            public void onResponse(Call<SimpanProdukResponseJson> call, Response<SimpanProdukResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    if(resultcode.equalsIgnoreCase("00")) {
                        Intent intent = new Intent(BarangEditActivity.this, MasterBarangActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<SimpanProdukResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(BarangEditActivity.this, t.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 300) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("hasil_scan");
                etBarcode.setText(result);
            }

        }
    }
}