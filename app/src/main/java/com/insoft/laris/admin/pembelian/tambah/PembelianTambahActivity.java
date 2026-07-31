package com.insoft.laris.admin.pembelian.tambah;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.insoft.laris.BarangActivity;
import com.insoft.laris.MainActivity;
import com.insoft.laris.R;
import com.insoft.laris.adapter.ItemAdapter;
import com.insoft.laris.admin.pembelian.PembelianActivity;
import com.insoft.laris.admin.pembelian.PembelianItem;
import com.insoft.laris.admin.pembelian.PembelianResponseJson;
import com.insoft.laris.admin.supplier.Supplier;
import com.insoft.laris.admin.supplier.SupplierRequestJson;
import com.insoft.laris.admin.supplier.SupplierResponseJson;
import com.insoft.laris.utils.MyDatabaseHelper;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PembelianTambahActivity extends AppCompatActivity {
    private MaterialToolbar toolbarTambahPembelian;

    private TextInputLayout layoutSupplier;
    private AutoCompleteTextView dropdownSupplier;
    private LinearLayout layoutKodeSupplier;
    private TextView tvKodeSupplier;

    private TextInputLayout layoutTanggal;
    private TextInputEditText etTanggal;

    private TextInputLayout layoutKeterangan;
    private TextInputEditText etKeterangan;

    private TextView tvJumlahBarang;
    private RecyclerView rvBarangPembelian;
    private ProgressBar loadingBarangPembelian;
    private LinearLayout layoutBarangKosong;

    private TextView tvSubtotal;
    private LinearLayout layoutTotalDiskon;
    private TextView tvTotalDiskon;
    private TextView tvTotalPembelian;

    private FloatingActionButton fabScanBarang;
    private FloatingActionButton fabTambahBarang;
    private MaterialButton btnSimpanPembelian;
    private RegisterAPI api;
    private final List<Supplier> daftarSupplier = new ArrayList<>();
    private ArrayAdapter<Supplier> adapterSupplier;
    private Supplier supplierTerpilih;
    private String kdSupplierTerpilih = "";
    MyDatabaseHelper db;
    ArrayList<HashMap<String, String>> list_data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_pembelian);
        db = new MyDatabaseHelper(this);
        api = UtilsAPI.getApiService();
        toolbarTambahPembelian = findViewById(R.id.toolbarTambahPembelian);
        layoutSupplier = findViewById(R.id.layoutSupplier);
        dropdownSupplier = findViewById(R.id.dropdownSupplier);
        layoutKodeSupplier = findViewById(R.id.layoutKodeSupplier);
        tvKodeSupplier = findViewById(R.id.tvKodeSupplier);
        layoutTanggal = findViewById(R.id.layoutTanggal);
        etTanggal = findViewById(R.id.etTanggal);
        layoutKeterangan = findViewById(R.id.layoutKeterangan);
        etKeterangan = findViewById(R.id.etKeterangan);
        tvJumlahBarang = findViewById(R.id.tvJumlahBarang);
        rvBarangPembelian = findViewById(R.id.rvBarangPembelian);
        loadingBarangPembelian = findViewById(R.id.loadingBarangPembelian);
        layoutBarangKosong = findViewById(R.id.layoutBarangKosong);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        layoutTotalDiskon = findViewById(R.id.layoutTotalDiskon);
        tvTotalDiskon = findViewById(R.id.tvTotalDiskon);
        tvTotalPembelian = findViewById(R.id.tvTotalPembelian);
        fabScanBarang = findViewById(R.id.fabScanBarang);
        fabTambahBarang = findViewById(R.id.fabTambahBarang);
        btnSimpanPembelian = findViewById(R.id.btnSimpanPembelian);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvBarangPembelian.setLayoutManager(llm);


        ambilDataSupplier();
        aturTanggalPembelian();
        displayData();

        fabTambahBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PembelianTambahActivity.this, BarangActivity.class);
                startActivityForResult(intent, 1200);
            }
        });
    }

    private void displayData() {
        list_data = new ArrayList<HashMap<String, String>>();
        Cursor cursor = db.tampilkan_tmp_pembelian();
        if(cursor.getCount() == 0){
            layoutBarangKosong.setVisibility(VISIBLE);
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }else {
            layoutBarangKosong.setVisibility(GONE);
            while(cursor.moveToNext()) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", cursor.getString(0));
                map.put("kd_barang", cursor.getString(1));
                map.put("barcode", cursor.getString(2));
                map.put("nm_barang", cursor.getString(3));
                map.put("satuan", cursor.getString(4));
                map.put("jumlah", cursor.getString(5));
                map.put("harga", cursor.getString(6));
                map.put("subtotal", cursor.getString(7));
                map.put("diskon", cursor.getString(8));
                map.put("total", cursor.getString(9));

                list_data.add(map);
                PembelianProductItem itemAdapter = new PembelianProductItem(PembelianTambahActivity.this, list_data);
                itemAdapter.notifyDataSetChanged();
                rvBarangPembelian.setAdapter(itemAdapter);

            }
        }

    }

    private void aturTanggalPembelian() {
        Calendar calendar = Calendar.getInstance();

        // Tampilkan tanggal hari ini saat Activity dibuka
        SimpleDateFormat formatTanggal =
                new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        etTanggal.setText(formatTanggal.format(calendar.getTime()));

        etTanggal.setOnClickListener(view -> tampilkanDatePicker());
    }


    private void tampilkanDatePicker() {
        Calendar calendar = Calendar.getInstance();

        // Gunakan tanggal yang sedang tampil jika tersedia
        String tanggalSekarang = ambilText(etTanggal);

        try {
            SimpleDateFormat formatTanggal =
                    new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

            formatTanggal.setLenient(false);

            Date tanggal = formatTanggal.parse(tanggalSekarang);

            if (tanggal != null) {
                calendar.setTime(tanggal);
            }

        } catch (ParseException ignored) {
            // Jika parsing gagal, gunakan tanggal hari ini
        }

        int tahun = calendar.get(Calendar.YEAR);
        int bulan = calendar.get(Calendar.MONTH);
        int hari = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        PembelianTambahActivity.this,
                        (datePicker, tahunDipilih, bulanDipilih, hariDipilih) -> {

                            Calendar tanggalTerpilih =
                                    Calendar.getInstance();

                            tanggalTerpilih.set(
                                    tahunDipilih,
                                    bulanDipilih,
                                    hariDipilih
                            );

                            SimpleDateFormat formatTanggal =
                                    new SimpleDateFormat(
                                            "dd-MM-yyyy",
                                            Locale.getDefault()
                                    );

                            etTanggal.setText(
                                    formatTanggal.format(
                                            tanggalTerpilih.getTime()
                                    )
                            );

                            layoutTanggal.setError(null);
                        },
                        tahun,
                        bulan,
                        hari
                );

        datePickerDialog.show();
    }


    private String ambilText(TextInputEditText editText) {
        if (editText == null || editText.getText() == null) {
            return "";
        }

        return editText.getText().toString().trim();
    }


    private void ambilDataSupplier() {
        layoutSupplier.setEnabled(false);
        dropdownSupplier.setHint("Mengambil data supplier...");

        SupplierRequestJson param = new SupplierRequestJson();
        param.setCari("");
        api.supplier_list(param).enqueue(new Callback<SupplierResponseJson>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<SupplierResponseJson> call, Response<SupplierResponseJson> response) {
                layoutSupplier.setEnabled(true);
                dropdownSupplier.setHint(null);
                if(response.isSuccessful()) {
                    String res = response.body().getResultcode();
                    if(res.equalsIgnoreCase("00")) {
                        List<Supplier> data =
                                response.body().getData();
                        daftarSupplier.clear();

                        if (data != null) {
                            daftarSupplier.addAll(data);
                        }

                        tampilkanSupplierKeDropdown();


                    }
                }
            }

            @Override
            public void onFailure(Call<SupplierResponseJson> call, Throwable t) {

                Toast.makeText(PembelianTambahActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void tampilkanSupplierKeDropdown() {
        adapterSupplier = new ArrayAdapter<>(
                PembelianTambahActivity.this,
                android.R.layout.simple_dropdown_item_1line,
                daftarSupplier
        );

        dropdownSupplier.setAdapter(adapterSupplier);

        dropdownSupplier.setOnItemClickListener(
                (parent, view, position, id) -> {

                    Supplier supplier =
                            (Supplier) parent.getItemAtPosition(position);

                    if (supplier == null) {
                        return;
                    }

                    supplierTerpilih = supplier;
                    kdSupplierTerpilih =
                            supplier.getKd_supplier();

                    dropdownSupplier.setText(
                            supplier.getNm_supplier(),
                            false
                    );

                    tvKodeSupplier.setText(
                            kdSupplierTerpilih
                    );

                    layoutSupplier.setError(null);
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && requestCode == 1200){

            String kodebarang = data.getStringExtra("intent_kodebarang");
            Cursor cursor = db.periksa_tmp_pembelian(kodebarang);

            if(cursor.getCount() == 0){

                db.insert_tmp_pembelian(
                        kodebarang,
                        data.getStringExtra("intent_barcode"),
                        data.getStringExtra("intent_nama_barang"),
                        data.getStringExtra("intent_satuan"),
                        1,
                        data.getIntExtra("intent_harga_beli", 0),
                        data.getIntExtra("intent_harga_beli", 0),
                        0,
                        data.getIntExtra("intent_harga_beli", 0)

                );
            } else {
                cursor.moveToFirst();
                int jumlah = Integer.parseInt(cursor.getString(5));
                int jumlahbaru = jumlah + 1;
                int hargabeli = data.getIntExtra("intent_harga_beli", 0);
                String barcode = data.getStringExtra("intent_barcode");

                int totalbaru = jumlahbaru * hargabeli;

                db.update_tmp_pembelian(kodebarang, barcode, data.getStringExtra("intent_nama_barang"),  data.getStringExtra("intent_satuan"), jumlahbaru, hargabeli, totalbaru, 0, totalbaru);
            }

            displayData();

        }
    }
}
