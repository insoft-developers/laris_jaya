package com.insoft.laris.admin.pembelian.tambah;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.insoft.laris.BarangActivity;
import com.insoft.laris.MainActivity;
import com.insoft.laris.R;
import com.insoft.laris.adapter.ItemAdapter;
import com.insoft.laris.admin.pembelian.Pembelian;
import com.insoft.laris.admin.pembelian.PembelianActivity;
import com.insoft.laris.admin.pembelian.PembelianDetail;
import com.insoft.laris.admin.pembelian.PembelianItem;
import com.insoft.laris.admin.pembelian.PembelianResponseJson;
import com.insoft.laris.admin.supplier.Supplier;
import com.insoft.laris.admin.supplier.SupplierRequestJson;
import com.insoft.laris.admin.supplier.SupplierResponseJson;
import com.insoft.laris.json.SalesRequestJson;
import com.insoft.laris.json.SalesResponseJson;
import com.insoft.laris.model.Sales;
import com.insoft.laris.model.SalesItem;
import com.insoft.laris.utils.MyDatabaseHelper;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.SessionManager;
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

public class PembelianTambahActivity extends AppCompatActivity implements PembelianProductInterface {
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
    private SessionManager session;
    private int kirim_subtotal = 0;
    private int kirim_total_discount = 0;
    private int kirim_total_pembelian = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_pembelian);
        db = new MyDatabaseHelper(this);
        session = new SessionManager(this);
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
        bersihkanItemProduct();

        btnSimpanPembelian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tampilkanDialogSimpan();
            }
        });


        fabTambahBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PembelianTambahActivity.this, BarangActivity.class);
                startActivityForResult(intent, 1200);
            }
        });


    }

    private void bersihkanItemProduct() {
        db.clear_tmp_pembelian();
        displayData();
    }

    private void displayData() {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        list_data = new ArrayList<HashMap<String, String>>();
        Cursor cursor = db.tampilkan_tmp_pembelian();
        if(cursor.getCount() == 0){
            layoutBarangKosong.setVisibility(VISIBLE);
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }else {
            layoutBarangKosong.setVisibility(GONE);
            int subtotal_m = 0;
            int discount_m = 0;
            int total_m = 0;
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

                subtotal_m = subtotal_m + Integer.parseInt(cursor.getString(7));
                discount_m = discount_m + Integer.parseInt(cursor.getString(8));
                total_m = total_m + Integer.parseInt(cursor.getString(9));

                list_data.add(map);
                PembelianProductItem itemAdapter = new PembelianProductItem(PembelianTambahActivity.this, list_data, PembelianTambahActivity.this);
                itemAdapter.notifyDataSetChanged();
                rvBarangPembelian.setAdapter(itemAdapter);

            }

            tvSubtotal.setText(formatRupiah.format(subtotal_m));
            tvTotalDiskon.setText(formatRupiah.format(discount_m));
            tvTotalPembelian.setText(formatRupiah.format(total_m));

            kirim_subtotal = subtotal_m;
            kirim_total_discount = discount_m;
            kirim_total_pembelian = total_m;


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

    private void tampilkanDialogEdit(
            HashMap<String, String> item,
            int position
    ) {
        View dialogView = LayoutInflater.from(PembelianTambahActivity.this)
                .inflate(R.layout.dialog_edit_pembelian, null);

        TextView tvNamaBarang =
                dialogView.findViewById(R.id.tvNamaBarangEdit);

        TextInputEditText etJumlah =
                dialogView.findViewById(R.id.etJumlahEdit);

        TextInputEditText etHarga =
                dialogView.findViewById(R.id.etHargaEdit);

        TextInputEditText etDiskon =
                dialogView.findViewById(R.id.etDiskonEdit);

        tvNamaBarang.setText(item.get("nm_barang"));
        etJumlah.setText(item.get("jumlah"));
        etHarga.setText(item.get("harga"));
        etDiskon.setText(item.get("diskon"));

        AlertDialog dialog = new MaterialAlertDialogBuilder(PembelianTambahActivity.this)
                .setTitle("Edit Pembelian")
                .setView(dialogView)
                .setNegativeButton("Batal", null)
                .setPositiveButton("Simpan", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(view -> {

                        String jumlahText = ambilAngka(etJumlah);
                        String hargaText = ambilAngka(etHarga);
                        String diskonText = ambilAngka(etDiskon);

                        if (jumlahText.isEmpty()) {
                            etJumlah.setError("Jumlah harus diisi");
                            etJumlah.requestFocus();
                            return;
                        }

                        if (hargaText.isEmpty()) {
                            etHarga.setError("Harga harus diisi");
                            etHarga.requestFocus();
                            return;
                        }

                        long jumlah;
                        long harga;
                        long diskon;

                        try {
                            jumlah = Long.parseLong(jumlahText);
                            harga = Long.parseLong(hargaText);

                            diskon = diskonText.isEmpty()
                                    ? 0
                                    : Long.parseLong(diskonText);

                        } catch (NumberFormatException e) {
                            Toast.makeText(
                                    PembelianTambahActivity.this,
                                    "Nilai yang dimasukkan tidak valid",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        if (jumlah <= 0) {
                            etJumlah.setError("Jumlah minimal 1");
                            etJumlah.requestFocus();
                            return;
                        }

                        if (harga <= 0) {
                            etHarga.setError("Harga harus lebih dari 0");
                            etHarga.requestFocus();
                            return;
                        }

                        long subtotal = jumlah * harga;

                        if (diskon > subtotal) {
                            etDiskon.setError(
                                    "Diskon tidak boleh melebihi subtotal"
                            );
                            etDiskon.requestFocus();
                            return;
                        }

                        long total = subtotal - diskon;

                        String id = item.get("id");

                        if (id == null || id.trim().isEmpty()) {
                            Toast.makeText(
                                    PembelianTambahActivity.this,
                                    "ID barang tidak ditemukan",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }




                        boolean berhasil = db.update_tmp_pembelian(
                                item.get("kd_barang"),
                                item.get("barcode"),
                                item.get("nm_barang"),
                                item.get("satuan"), Integer.parseInt(String.valueOf(jumlah))
                                ,Integer.parseInt(String.valueOf(harga)),Integer.parseInt(String.valueOf(subtotal)),Integer.parseInt(String.valueOf(diskon)),Integer.parseInt(String.valueOf(total))
                        );

                        if (berhasil) {
                            /*
                             * Perbarui data dalam HashMap supaya tampilan
                             * RecyclerView langsung berubah.
                             */
                            item.put("jumlah", String.valueOf(jumlah));
                            item.put("harga", String.valueOf(harga));
                            item.put("subtotal", String.valueOf(subtotal));
                            item.put("diskon", String.valueOf(diskon));
                            item.put("total", String.valueOf(total));



                            displayData();

                            Toast.makeText(
                                    PembelianTambahActivity.this,
                                    "Data pembelian berhasil diperbarui",
                                    Toast.LENGTH_SHORT
                            ).show();

                            dialog.dismiss();

                        } else {
                            Toast.makeText(
                                    PembelianTambahActivity.this,
                                    "Data pembelian gagal diperbarui",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
        });

        dialog.show();
    }


    private void tampilkanDialogHapus(
            HashMap<String, String> item,
            int position
    ) {
        String namaBarang = item.get("nm_barang");
        String id = item.get("kd_barang");

        if (namaBarang == null || namaBarang.trim().isEmpty()) {
            namaBarang = "barang ini";
        }

        if (id == null || id.trim().isEmpty()) {
            Toast.makeText(
                    PembelianTambahActivity.this,
                    "ID barang tidak ditemukan",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(PembelianTambahActivity.this)
                .setTitle("Hapus Barang")
                .setMessage(
                        "Apakah Anda yakin ingin menghapus "
                                + namaBarang
                                + " dari daftar pembelian?"
                )
                .setNegativeButton("Batal", null)
                .setPositiveButton("Hapus", null)
                .create();

        String finalId = id;

        dialog.setOnShowListener(dialogInterface -> {

            // Tombol batal
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setOnClickListener(view -> dialog.dismiss());

            // Tombol hapus
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(view -> {

                        boolean berhasil = db.hapus_tmp_pembelian(finalId);

                        if (berhasil) {

                            displayData();
                            Toast.makeText(
                                    PembelianTambahActivity.this,
                                    "Barang berhasil dihapus",
                                    Toast.LENGTH_SHORT
                            ).show();

                            dialog.dismiss();

                        } else {
                            Toast.makeText(
                                    PembelianTambahActivity.this,
                                    "Barang gagal dihapus",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
        });

        dialog.show();
    }

    private String ambilAngka(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }

        return editText.getText()
                .toString()
                .trim()
                .replaceAll("[^0-9]", "");
    }

    @Override
    public void EditProduct(HashMap<String,String>items, int position) {
        tampilkanDialogEdit(items, position);
    }

    @Override
    public void HapusProduct(HashMap<String,String>items, int position) {
        tampilkanDialogHapus(items, position);
    }


    private void tampilkanDialogSimpan() {

        if (list_data == null || list_data.isEmpty()) {
            Toast.makeText(
                    PembelianTambahActivity.this,
                    "Belum ada barang dalam pembelian",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if(kdSupplierTerpilih.isEmpty()) {
            Toast.makeText(
                    PembelianTambahActivity.this,
                    "Supplier belum dipilih",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String subtotal = tvSubtotal.getText().toString();
        String diskon = tvTotalDiskon.getText().toString();
        String total = tvTotalPembelian.getText().toString();

        String pesan =
                "Jumlah barang : " + list_data.size() + "\n" +
                        "Subtotal       : " + subtotal + "\n" +
                        "Diskon         : " + diskon + "\n" +
                        "Total          : " + total + "\n\n" +
                        "Apakah data pembelian sudah benar?";

        AlertDialog dialog = new MaterialAlertDialogBuilder(
                PembelianTambahActivity.this
        )
                .setTitle("Simpan Pembelian")
                .setMessage(pesan)
                .setNegativeButton("Batal", null)
                .setPositiveButton("Simpan", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {

            // Tombol batal
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setOnClickListener(view -> dialog.dismiss());

            // Tombol simpan
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(view -> {



                        submitPembelian();





                    });
        });

        dialog.show();
    }


    private void submitPembelian() {

        HashMap<String,String> user = session.getSessionData();
        String userkode = user.get(session.ID);
        PembelianSImpanRequestJson param = new PembelianSImpanRequestJson();
        param.setKd_supplier(kdSupplierTerpilih);
        param.setTanggal(etTanggal.getText().toString());
        param.setKeterangan(etKeterangan.getText().toString());
        param.setKd_user(userkode);
        param.setStatus(0);
        param.setSubtotal(kirim_subtotal);
        param.setTotal_discount(kirim_total_discount);
        param.setTotal_pembelian(kirim_total_pembelian);

        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursorDetail = database.rawQuery("SELECT * FROM tmp_pembelian", null);
        List<PembelianDetail> listItem = new ArrayList<>();
        if (cursorDetail.moveToFirst()) {
            do {
                PembelianDetail it = new PembelianDetail();
                it.kd_barang = cursorDetail.getString(cursorDetail.getColumnIndexOrThrow("kd_barang"));
                it.barcode = cursorDetail.getString(cursorDetail.getColumnIndexOrThrow("barcode"));
                it.nm_barang = cursorDetail.getString(cursorDetail.getColumnIndexOrThrow("nm_barang"));
                it.satuan = cursorDetail.getString(cursorDetail.getColumnIndexOrThrow("satuan"));
                it.jumlah = cursorDetail.getInt(cursorDetail.getColumnIndexOrThrow("jumlah"));
                it.harga = cursorDetail.getInt(cursorDetail.getColumnIndexOrThrow("harga"));
                it.total = cursorDetail.getInt(cursorDetail.getColumnIndexOrThrow("total"));
                it.diskon = cursorDetail.getInt(cursorDetail.getColumnIndexOrThrow("diskon"));
                it.subtotal = cursorDetail.getInt(cursorDetail.getColumnIndexOrThrow("subtotal"));

                listItem.add(it);
            } while (cursorDetail.moveToNext());
        }
        cursorDetail.close();

        param.setItems(listItem);

        api.pembelian_simpan(param).enqueue(new Callback<PembelianSimpanResponseJson>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<PembelianSimpanResponseJson> call, Response<PembelianSimpanResponseJson> response) {
                if(response.isSuccessful()) {
                    String res = response.body().getResultcode();
                    if(res.equalsIgnoreCase("00")) {
                        Toast.makeText(PembelianTambahActivity.this,response.body().getPesan(), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(PembelianTambahActivity.this,response.body().getPesan(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<PembelianSimpanResponseJson> call, Throwable t) {

                Toast.makeText(PembelianTambahActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}
