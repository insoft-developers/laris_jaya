package com.insoft.laris.admin.supplier.tambah;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.insoft.laris.R;
import com.insoft.laris.admin.supplier.SupplierActivity;
import com.insoft.laris.admin.supplier.SupplierItem;
import com.insoft.laris.admin.supplier.SupplierRequestJson;
import com.insoft.laris.admin.supplier.SupplierResponseJson;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupplierTambahActivity extends AppCompatActivity {

    private EditText etNamaSupplier, etKontakSupplier, etTeleponSupplier, etAlamatSupplier;
    private MaterialButton btnBatal, btnSimpanSupplier;
    private TextInputLayout layoutNamaSupplier,layoutKontakSupplier, layoutTeleponSupplier,layoutAlamatSupplier;

    private RegisterAPI api;

    private String METHOD;
    private String supplierCode, supplierName, contact, address, phone;

    private TextView labeltambah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_tambah_supplier);
         api = UtilsAPI.getApiService();
         etNamaSupplier = findViewById(R.id.etNamaSupplier);
         etKontakSupplier = findViewById(R.id.etKontakSupplier);
         etTeleponSupplier = findViewById(R.id.etTeleponSupplier);
         etAlamatSupplier = findViewById(R.id.etAlamatSupplier);
         btnBatal = findViewById(R.id.btnBatal);
         btnSimpanSupplier = findViewById(R.id.btnSimpanSupplier);
         layoutNamaSupplier = findViewById(R.id.layoutNamaSupplier);
         layoutKontakSupplier = findViewById(R.id.layoutKontakSupplier);
         layoutTeleponSupplier = findViewById(R.id.layoutTeleponSupplier);
         layoutAlamatSupplier = findViewById(R.id.layoutAlamatSupplier);
          labeltambah = findViewById(R.id.labeltambah);

         METHOD = getIntent().getStringExtra("METHOD");
         if(Objects.equals(METHOD, "edit")) {
             supplierCode = getIntent().getStringExtra("kd_supplier");
             supplierName = getIntent().getStringExtra("nm_supplier");
             contact = getIntent().getStringExtra("kontak");
             address = getIntent().getStringExtra("alamat");
             phone = getIntent().getStringExtra("telepon");

             etNamaSupplier.setText(supplierName);
             etKontakSupplier.setText(contact);
             etTeleponSupplier.setText(phone);
             etAlamatSupplier.setText(address);
             labeltambah.setText("Edit Supplier");

         } else {
             etNamaSupplier.setText("");
             etKontakSupplier.setText("");
             etTeleponSupplier.setText("");
             etAlamatSupplier.setText("");
             labeltambah.setText("Tambah Supplier");
         }

        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


         btnSimpanSupplier.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(etNamaSupplier.getText().toString().isEmpty()) {
                     layoutNamaSupplier.setError("Nama Supplier Harus diisi!");
                 } else if(etAlamatSupplier.getText().toString().isEmpty()) {
                     layoutAlamatSupplier.setError("Alamat Harus diisi!");
                 } else {

                         submit(etNamaSupplier.getText().toString(),
                                 etKontakSupplier.getText().toString(),
                                 etTeleponSupplier.getText().toString(),
                                 etAlamatSupplier.getText().toString()
                         );


                 }

             }
         });
     }

    private void submit(String name, String kontak, String telepon, String alamat) {


        loading();
        SupplierTambahRequestJson param = new SupplierTambahRequestJson();
        param.setKd_supplier(supplierCode);
        param.setNm_supplier(name);
        param.setKontak(kontak);
        param.setTelepon(telepon);
        param.setAlamat(alamat);

        Call<SupplierTambahResponseJson> call;

        if (Objects.equals(METHOD, "add")) {
            call = api.supplier_tambah(param);
        } else {
            call = api.supplier_update(param);
        }

        call.enqueue(new Callback<SupplierTambahResponseJson>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<SupplierTambahResponseJson> call, Response<SupplierTambahResponseJson> response) {
               unload();
                if(response.isSuccessful()) {
                    String res = response.body().getResultcode();
                    if(res.equalsIgnoreCase("00")) {
                        Toast.makeText(SupplierTambahActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<SupplierTambahResponseJson> call, Throwable t) {
               unload();
                Toast.makeText(SupplierTambahActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

     private void loading() {
        btnSimpanSupplier.setText("Loading....");
        btnSimpanSupplier.setEnabled(false);
     }

    private void unload() {
        btnSimpanSupplier.setText("Simpan");
        btnSimpanSupplier.setEnabled(true);
    }
}
