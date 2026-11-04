package com.insoft.laris;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.insoft.laris.model.Produk;
import com.insoft.laris.utils.MyDatabaseHelper;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.SessionPelanggan;

import java.util.HashMap;
import java.util.List;

public class EditActivity extends AppCompatActivity {

    private TextView tvJenisHarga;

    private TextView kodebarang, namabarang, satuan, konversi;
    private EditText etjumlah, discount_persen, discount;
    private Button btnhapus, btnupdate;
    MyDatabaseHelper db;
    private List<Produk> dataproduk;
    private RegisterAPI registerAPI;
    private SessionPelanggan sessionPelanggan;

    private RadioGroup radioHarga;
    private RadioButton radioUmum;
    private RadioButton radioReseller;
    private RadioButton radioFreelance;

    private int price_type = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        db = new MyDatabaseHelper(this);
        sessionPelanggan = new SessionPelanggan(this);
        kodebarang = findViewById(R.id.kodebarang);
        namabarang = findViewById(R.id.namabarang);
        satuan = findViewById(R.id.satuan);
        konversi = findViewById(R.id.konversi);
        discount_persen = findViewById(R.id.etdiscountpersen);
        discount = findViewById(R.id.etdiscount);
        etjumlah = findViewById(R.id.etjumlah);
        btnhapus = findViewById(R.id.btnhapus);
        btnupdate = findViewById(R.id.btnupdate) ;

        radioHarga = findViewById(R.id.radioHarga);
        radioUmum = findViewById(R.id.radioUmum);
        radioReseller = findViewById(R.id.radioReseller);
        radioFreelance = findViewById(R.id.radioFreelance);

        tvJenisHarga = findViewById(R.id.tvjenisharga);


        tvJenisHarga = findViewById(R.id.tvjenisharga);

        radioHarga.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.radioUmum) {

                price_type = 1;
                tvJenisHarga.setText("Menggunakan Harga Umum");

            } else if (checkedId == R.id.radioReseller) {

                price_type = 2;
                tvJenisHarga.setText("Menggunakan Harga Reseller");

            } else if (checkedId == R.id.radioFreelance) {

                price_type = 3;
                tvJenisHarga.setText("Menggunakan Harga Freelance");
            }
        });


        String itemcode = getIntent().getStringExtra("item_code");
        String itemname = getIntent().getStringExtra("item_name");
        String itemsatuan = getIntent().getStringExtra("item_satuan");
        String itemkonversi = getIntent().getStringExtra("item_konversi");
        String itemjumlah = getIntent().getStringExtra("item_jumlah");
        String itemdiscount = getIntent().getStringExtra("item_discount");


        kodebarang.setText(itemcode);
        namabarang.setText(itemname);
        satuan.setText(itemsatuan);
        konversi.setText(itemkonversi);

        etjumlah.setText(itemjumlah);
        discount.setText(itemdiscount);
        discount_persen.setText("");

        check_price_type(itemcode);




        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etjumlah.getText().toString().isEmpty()) {
                    etjumlah.setError("Jumlah Tidak Boleh Kosong");
                } else {
                    update_harga(itemcode);
                }
            }
        });

        btnhapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.hapusbaris(String.valueOf(itemcode));
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });



    }

    private void update_harga(String itemcode) {
        HashMap<String,String> pelanggan = sessionPelanggan.getSessionPelanggan();
        String cust_group = pelanggan.get(sessionPelanggan.CGRUP);
        Cursor cursor = db.get_barang_by_kode(itemcode);
        if (cursor != null && cursor.moveToFirst()) {
            String nama = cursor.getString(cursor.getColumnIndexOrThrow("nm_barang"));
            int harga = cursor.getInt(cursor.getColumnIndexOrThrow("harga_jual"));
            int datakonversi  = cursor.getInt(cursor.getColumnIndexOrThrow("konversi"));
            int jumlahbaru = Integer.parseInt(etjumlah.getText().toString());

            int hargaaktif = 0;
            if (price_type == 1) {

                // Harga Umum
                hargaaktif = cursor.getInt(
                        cursor.getColumnIndexOrThrow("harga_jual")
                );

            } else if (price_type == 2) {

                // Harga Reseller
                hargaaktif = cursor.getInt(
                        cursor.getColumnIndexOrThrow("harga_member")
                );

            } else {

                // Harga Freelance
                hargaaktif = cursor.getInt(
                        cursor.getColumnIndexOrThrow("harga_freelance")
                );
            }

            int totalbaru = hargaaktif * jumlahbaru;



            String inputDiskon = discount_persen.getText().toString().trim();
            String inputDiskonRupiah = discount.getText().toString().trim();

            int disc_rupiah = inputDiskonRupiah.isEmpty() ? 0 : Integer.parseInt(inputDiskonRupiah);
            int totalharga = totalbaru - disc_rupiah;

            int disc_persen = inputDiskon.isEmpty()
                    ? 0
                    : Integer.parseInt(inputDiskon);

            int disc_persen_value = (int) Math.round(
                    totalharga * disc_persen / 100.0
            );

            int total_discount = disc_rupiah + disc_persen_value;

            int total_setelah_discount = totalbaru - total_discount;



            db.updateitem(itemcode, jumlahbaru, hargaaktif, totalbaru, total_discount, total_setelah_discount, price_type);
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        cursor.close();


    }

    private void check_price_type(String itemcode) {

        Cursor cursor = db.periksadata(itemcode);

        if (cursor != null && cursor.moveToFirst()) {

            int savedPriceType = cursor.getInt(
                    cursor.getColumnIndexOrThrow("price_type")
            );

            if (savedPriceType == 2) {

                radioReseller.setChecked(true);
                tvJenisHarga.setText("Menggunakan Harga Reseller");

            } else if (savedPriceType == 3) {

                radioFreelance.setChecked(true);
                tvJenisHarga.setText("Menggunakan Harga Freelance");

            } else {

                radioUmum.setChecked(true);
                tvJenisHarga.setText("Menggunakan Harga Umum");
            }

            price_type = savedPriceType;
        }

        if (cursor != null) {
            cursor.close();
        }
    }
}