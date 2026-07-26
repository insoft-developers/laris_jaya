package com.insoft.laris;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.insoft.laris.model.Produk;
import com.insoft.laris.utils.MyDatabaseHelper;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.SessionPelanggan;

import java.util.HashMap;
import java.util.List;

public class EditActivity extends AppCompatActivity {
    private SwitchCompat switchHarga;
    private TextView tvHargaUmum;
    private TextView tvHargaReseller;
    private TextView tvJenisHarga;

    private boolean menggunakanHargaReseller = false;
    private TextView kodebarang, namabarang, satuan, konversi;
    private EditText etjumlah, discount_persen, discount;
    private Button btnhapus, btnupdate;
    MyDatabaseHelper db;
    private List<Produk> dataproduk;
    private RegisterAPI registerAPI;
    private SessionPelanggan sessionPelanggan;

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

        switchHarga = findViewById(R.id.switchharga);
        tvHargaUmum = findViewById(R.id.tvhargaumum);
        tvHargaReseller = findViewById(R.id.tvhargareseller);
        tvJenisHarga = findViewById(R.id.tvjenisharga);


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

        switchHarga.setOnCheckedChangeListener((buttonView, isChecked) -> {
            menggunakanHargaReseller = isChecked;

            if (isChecked) {
                tvJenisHarga.setText("Menggunakan Harga Reseller");

                tvHargaUmum.setTypeface(
                        null,
                        android.graphics.Typeface.NORMAL
                );

                tvHargaReseller.setTypeface(
                        null,
                        android.graphics.Typeface.BOLD
                );

                menggunakanHargaReseller = true;
                // Gunakan harga reseller
                // hargaDipilih = hargaReseller;

            } else {
                tvJenisHarga.setText("Menggunakan Harga Umum");

                tvHargaUmum.setTypeface(
                        null,
                        android.graphics.Typeface.BOLD
                );

                tvHargaReseller.setTypeface(
                        null,
                        android.graphics.Typeface.NORMAL
                );

                // Gunakan harga umum
                // hargaDipilih = hargaUmum;
                menggunakanHargaReseller = false;
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
            if(jumlahbaru >= datakonversi) {
                if(cust_group.equalsIgnoreCase("Reguler")) {
                    hargaaktif = cursor.getInt(cursor.getColumnIndexOrThrow("hj"));
                } else if(cust_group.equalsIgnoreCase("Grosir")){
                    hargaaktif = cursor.getInt(cursor.getColumnIndexOrThrow("diskon_member"));
                } else if(cust_group.equalsIgnoreCase("Freelancer")) {
                    hargaaktif = cursor.getInt(cursor.getColumnIndexOrThrow("diskon_member"));
                }
            } else {
                if(cust_group.equalsIgnoreCase("Reguler")) {
                    hargaaktif = cursor.getInt(cursor.getColumnIndexOrThrow("harga_jual"));
                } else if(cust_group.equalsIgnoreCase("Grosir")){
                    hargaaktif = cursor.getInt(cursor.getColumnIndexOrThrow("harga_member"));
                } else if(cust_group.equalsIgnoreCase("Freelancer")) {
                    hargaaktif = cursor.getInt(cursor.getColumnIndexOrThrow("harga_member"));
                }
            }

            int totalbaru = hargaaktif * jumlahbaru;

            int price_type = 1;
            if(menggunakanHargaReseller) {
                hargaaktif = cursor.getInt(cursor.getColumnIndexOrThrow("harga_reseller"));
                totalbaru = hargaaktif * jumlahbaru;
                price_type = 2;
            }

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
            int price_type = cursor.getInt(cursor.getColumnIndexOrThrow("price_type"));
            if(price_type == 2) {
                switchHarga.setChecked(true);
                menggunakanHargaReseller = true;
                tvHargaUmum.setTypeface(null, android.graphics.Typeface.BOLD);
                tvHargaReseller.setTypeface(null, android.graphics.Typeface.NORMAL);
                tvJenisHarga.setText("Menggunakan Harga Reseller");
            } else {
                switchHarga.setChecked(false);
                menggunakanHargaReseller = false;

                tvHargaUmum.setTypeface(null, android.graphics.Typeface.BOLD);
                tvHargaReseller.setTypeface(null, android.graphics.Typeface.NORMAL);
                tvJenisHarga.setText("Menggunakan Harga Umum");
            }
        }
        cursor.close();
    }
}