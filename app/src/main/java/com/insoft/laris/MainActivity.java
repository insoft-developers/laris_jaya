package com.insoft.laris;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.insoft.laris.Interface.itemInterface;
import com.insoft.laris.adapter.ItemAdapter;
import com.insoft.laris.json.BarangResponseJson;
import com.insoft.laris.json.CustomerRequestJson;
import com.insoft.laris.json.CustomerResponseJson;
import com.insoft.laris.model.Pelanggan;
import com.insoft.laris.model.Produk;
import com.insoft.laris.model.RingkasanTransaksi;
import com.insoft.laris.utils.MyDatabaseHelper;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.SessionManager;
import com.insoft.laris.utils.SessionPelanggan;
import com.insoft.laris.utils.UtilsAPI;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements itemInterface {
    private TextView txtharga, txtitem, txtkeluar;
    private FloatingActionButton fab_barcode, fabitem;
    private RecyclerView rvproduk;
    ArrayList<HashMap<String, String>> list_data;
    private ItemAdapter itemAdapter;
    MyDatabaseHelper db;
    private SessionManager sessionManager;
    private ImageView foto;
    private TextView namapelanggan, alamatpelanggan, gruppelanggan;
    private SessionPelanggan sessionPelanggan;
    private LinearLayout cartcontainer;
    private ProgressBar loading;
    private RegisterAPI registerAPI;
    private TextView txttransaksi;
    private List<Produk> dataProduk;
    private List<Pelanggan> dataPelanggan;
    private EditText totalHidden;

    private long totalPembayaran = 0;
    private long totalKembalian = 0;
    private long totalKekurangan = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionPelanggan = new SessionPelanggan(this);
        registerAPI = UtilsAPI.getApiService();

        check_permission();
        if(Build.VERSION.SDK_INT >= 30){
            check_permission_android11();
        }
        db = new MyDatabaseHelper(this);
        sessionManager  = new SessionManager(this);
        fab_barcode = findViewById(R.id.fab_barcode);
        fabitem = findViewById(R.id.fab_item);
        rvproduk = findViewById(R.id.rvproduk);
        txtharga = findViewById(R.id.txtharga);
        txtitem = findViewById(R.id.txtitem);
        txtkeluar = findViewById(R.id.txtkeluar);
        foto = findViewById(R.id.foto);
        namapelanggan = findViewById(R.id.namapelanggan);
        alamatpelanggan = findViewById(R.id.alamatpelanggan);
        gruppelanggan = findViewById(R.id.gruppelanggan);
        cartcontainer = findViewById(R.id.cartcontainer);
        loading = findViewById(R.id.loading);
        txttransaksi = findViewById(R.id.txttransaksi);
        totalHidden = findViewById(R.id.hidden_total);
        if(db.is_master_barang_empty()) {
            Toast.makeText(MainActivity.this, "Master Barang Kosong", Toast.LENGTH_SHORT).show();
            get_product_list();
        } else {
            Toast.makeText(MainActivity.this, "Master Barang Berisi", Toast.LENGTH_SHORT).show();
        }

        if(db.is_master_pelanggan_empty()) {
            Toast.makeText(MainActivity.this, "Master Pelanggan Kosong", Toast.LENGTH_SHORT).show();
            get_pelanggan_list();
        } else {
            Toast.makeText(MainActivity.this, "Master Pelanggan Berisi", Toast.LENGTH_SHORT).show();
        }

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvproduk.setLayoutManager(llm);
        displayData();
        fab_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(namapelanggan.getText().toString().isEmpty()) {
                    show_information_dialog("Warning","Harap isi data pelanggan dahulu!");
                } else {
                    Intent intent = new Intent(MainActivity.this, BarcodeActivity.class);
                    startActivity(intent);
                }

            }
        });

        fabitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(namapelanggan.getText().toString().isEmpty()) {
                    show_information_dialog("Warning","Harap isi data pelanggan dahulu!");
                } else {
                    Intent intent = new Intent(MainActivity.this, BarangActivity.class);
                    startActivityForResult(intent, 800);
                }

            }
        });

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CustomerActivity.class);
                startActivityForResult(intent, 300);
            }
        });

        txttransaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TransaksiActivity.class);
//                Intent intent = new Intent(MainActivity.this, ListPenjualanActivity.class);
                startActivity(intent);
            }
        });



        HashMap<String,String> ses_pelanggan = sessionPelanggan.getSessionPelanggan();
        namapelanggan.setText(ses_pelanggan.get(sessionPelanggan.CNAME));
        alamatpelanggan.setText(ses_pelanggan.get(sessionPelanggan.CADDRESS));
        gruppelanggan.setText(ses_pelanggan.get(sessionPelanggan.CGRUP));


        cartcontainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdialog();
            }
        });

        txtkeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        });
    }

    private void get_product_list() {
        loading.setVisibility(View.VISIBLE);
        CustomerRequestJson param = new CustomerRequestJson();
        param.setKata_cari("");
        registerAPI.product_list(param).enqueue(new Callback<BarangResponseJson>() {
            @Override
            public void onResponse(Call<BarangResponseJson> call, Response<BarangResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    if(resultcode.equalsIgnoreCase("00")) {
                        dataProduk = response.body().getData();
                        if (dataProduk != null && !dataProduk.isEmpty()) {

                            db.clear_master_barang();

                            for (Produk p : dataProduk) {
                                db.insert_master_barang(
                                        p.getKd_barang(),
                                        p.getBarcode(),
                                        p.getNm_barang(),
                                        p.getKd_kategori(),
                                        p.getHarga_beli(),
                                        p.getHarga_jual(),
                                        p.getSatuan(),
                                        p.getStok(),
                                        p.getKonversi(),
                                        p.getHj(),
                                        p.getHarga_member(),
                                        p.getDiskon_member(),
                                        p.getKd_supplier(),
                                        p.getDiskon(),
                                        p.getHarga_reseller()
                                );
                            }

                            db.close();
                            Log.d("SYNC", "DataProduk berhasil disimpan ke SQLite: " + dataProduk.size());
                        } else {
                            Log.e("SYNC", "DataProduk kosong / null");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<BarangResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
            }
        });
    }


    private void get_pelanggan_list() {
        loading.setVisibility(View.VISIBLE);
        CustomerRequestJson param = new CustomerRequestJson();
        param.setKata_cari("");
        registerAPI.get_customer(param).enqueue(new Callback<CustomerResponseJson>() {
            @Override
            public void onResponse(Call<CustomerResponseJson> call, Response<CustomerResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    if(resultcode.equalsIgnoreCase("00")) {
                        dataPelanggan = response.body().getData();
                        if (dataPelanggan != null && !dataPelanggan.isEmpty()) {

                            db.clear_master_pelanggan();

                            for (Pelanggan p : dataPelanggan) {
                               db.insert_master_pelanggan(
                                       p.getKd_pelanggan(),
                                       p.getNm_pelanggan(),
                                       p.getAlamat(),
                                       p.getKontak(),
                                       p.getGrup(),
                                       p.getTelepon()
                               );
                            }

                            db.close();
                            Log.d("SYNC", "DataPelanggan berhasil disimpan ke SQLite: " + dataPelanggan.size());
                        } else {
                            Log.e("SYNC", "DataPelanggan kosong / null");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
            }
        });
    }



    private String formatRupiah(long nominal) {
        NumberFormat format =
                NumberFormat.getNumberInstance(
                        new Locale("id", "ID")
                );

        format.setMaximumFractionDigits(0);
        format.setMinimumFractionDigits(0);

        return "Rp " + format.format(nominal);
    }
    private void showdialog() {
        View view = getLayoutInflater().inflate(
                R.layout.dialog_pembayaran,
                null
        );

        TextView tvSubtotal = view.findViewById(R.id.tvSubtotal);
        TextView tvTotalDiskon = view.findViewById(R.id.tvTotalDiskon);
        TextView tvTotalBelanja = view.findViewById(R.id.tvTotalBelanja);
        TextView tvKembalian = view.findViewById(R.id.tvKembalian);
        TextView tvPeringatan = view.findViewById(R.id.tvPeringatan);

        TextInputLayout layoutPembayaran =
                view.findViewById(R.id.layoutPembayaran);

        TextInputEditText etPembayaran =
                view.findViewById(R.id.etPembayaran);

        TextView tvLabelKembalian =
                view.findViewById(R.id.tvLabelKembalian);

        MaterialCardView cardKembalian =
                view.findViewById(R.id.cardKembalian);

        MaterialButton btn10000 =
                view.findViewById(R.id.btn10000);

        MaterialButton btn20000 =
                view.findViewById(R.id.btn20000);

        MaterialButton btn50000 =
                view.findViewById(R.id.btn50000);

        MaterialButton btn100000 =
                view.findViewById(R.id.btn100000);

        MaterialButton btnUangPas =
                view.findViewById(R.id.btnUangPas);

        MaterialButton btnBatal = view.findViewById(R.id.btnBatal);
        MaterialButton btnSimpan = view.findViewById(R.id.btnSimpan);
        MaterialButton btnResetPembayaran =
                view.findViewById(R.id.btnResetPembayaran);

        /*
         * Sesuaikan dengan variabel total transaksi milik Anda.
         */
        RingkasanTransaksi ringkasan =
                db.getRingkasanTransaksi();

        final long subtotal = ringkasan.getSubtotal();
        final long diskon = ringkasan.getDiskon();
        final long totalBelanja = ringkasan.getTotal();


        tvSubtotal.setText(formatRupiah(subtotal));
        tvTotalDiskon.setText("- " + formatRupiah(diskon));
        tvTotalBelanja.setText(formatRupiah(totalBelanja));


        tvKembalian.setText(formatRupiah(0));

        AlertDialog alertDialog =
                new MaterialAlertDialogBuilder(this)
                        .setView(view)
                        .setCancelable(false)
                        .create();

        etPembayaran.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence charSequence,
                    int start,
                    int count,
                    int after
            ) {
            }

            @Override
            public void onTextChanged(
                    CharSequence charSequence,
                    int start,
                    int before,
                    int count
            ) {
                String input = charSequence.toString()
                        .replaceAll("[^0-9]", "")
                        .trim();

                /*
                 * Tombol simpan hanya dinonaktifkan jika input kosong.
                 * Pembayaran kurang tetap diperbolehkan disimpan.
                 */
                if (input.isEmpty()) {
                    tvLabelKembalian.setText("Kembalian");
                    tvKembalian.setText(formatRupiah(0));

                    tvLabelKembalian.setTextColor(
                            Color.parseColor("#047857")
                    );

                    tvKembalian.setTextColor(
                            Color.parseColor("#065F46")
                    );

                    cardKembalian.setCardBackgroundColor(
                            Color.parseColor("#ECFDF5")
                    );

                    cardKembalian.setStrokeColor(
                            Color.parseColor("#A7F3D0")
                    );

                    tvPeringatan.setVisibility(View.GONE);
                    layoutPembayaran.setError(null);
                    btnSimpan.setEnabled(false);
                    return;
                }

                long pembayaran;

                try {
                    pembayaran = Long.parseLong(input);
                } catch (NumberFormatException e) {
                    pembayaran = 0;
                }

                btnSimpan.setEnabled(true);
                layoutPembayaran.setError(null);

                if (pembayaran >= totalBelanja) {
                    long kembalian = pembayaran - totalBelanja;

                    tvLabelKembalian.setText("Kembalian");
                    tvKembalian.setText(formatRupiah(kembalian));

                    tvLabelKembalian.setTextColor(
                            Color.parseColor("#047857")
                    );

                    tvKembalian.setTextColor(
                            Color.parseColor("#065F46")
                    );

                    cardKembalian.setCardBackgroundColor(
                            Color.parseColor("#ECFDF5")
                    );

                    cardKembalian.setStrokeColor(
                            Color.parseColor("#A7F3D0")
                    );

                    tvPeringatan.setVisibility(View.GONE);

                } else {
                    long kekurangan = totalBelanja - pembayaran;

                    tvLabelKembalian.setText("Kekurangan");
                    tvKembalian.setText(formatRupiah(kekurangan));

                    tvLabelKembalian.setTextColor(
                            Color.parseColor("#B45309")
                    );

                    tvKembalian.setTextColor(
                            Color.parseColor("#92400E")
                    );

                    cardKembalian.setCardBackgroundColor(
                            Color.parseColor("#FFFBEB")
                    );

                    cardKembalian.setStrokeColor(
                            Color.parseColor("#FCD34D")
                    );

                    tvPeringatan.setText(
                            "Pembayaran kurang "
                                    + formatRupiah(kekurangan)
                                    + ". Transaksi tetap dapat disimpan."
                    );

                    tvPeringatan.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        btn10000.setOnClickListener(v ->
                tambahNominalPembayaran(etPembayaran, 10000)
        );

        btn20000.setOnClickListener(v ->
                tambahNominalPembayaran(etPembayaran, 20000)
        );

        btn50000.setOnClickListener(v ->
                tambahNominalPembayaran(etPembayaran, 50000)
        );

        btn100000.setOnClickListener(v ->
                tambahNominalPembayaran(etPembayaran, 100000)
        );

        btnUangPas.setOnClickListener(v ->
                setPembayaranPas(
                        etPembayaran,
                        totalBelanja
                )
        );

        btnResetPembayaran.setOnClickListener(v -> {
            etPembayaran.setText("");
            etPembayaran.requestFocus();
        });

        btnBatal.setOnClickListener(v -> alertDialog.dismiss());

        btnSimpan.setOnClickListener(v -> {
            String inputPembayaran =
                    etPembayaran.getText() == null
                            ? ""
                            : etPembayaran.getText()
                            .toString()
                            .replaceAll("[^0-9]", "")
                            .trim();

            if (inputPembayaran.isEmpty()) {
                layoutPembayaran.setError(
                        "Jumlah pembayaran harus diisi"
                );

                etPembayaran.requestFocus();
                return;
            }

            long pembayaran;

            try {
                pembayaran = Long.parseLong(inputPembayaran);
            } catch (NumberFormatException e) {
                layoutPembayaran.setError(
                        "Nominal pembayaran tidak valid"
                );

                etPembayaran.requestFocus();
                return;
            }

            /*
             * Simpan pembayaran.
             */
            totalPembayaran = pembayaran;

            /*
             * Jika pembayaran lebih, hasilnya menjadi kembalian.
             * Jika kurang, kembalian dibuat 0.
             */
            totalKembalian = Math.max(
                    0,
                    pembayaran - totalBelanja
            );

            /*
             * Jika pembayaran kurang, simpan nilai kekurangannya.
             * Jika lunas atau lebih, kekurangan dibuat 0.
             */
            totalKekurangan = Math.max(
                    0,
                    totalBelanja - pembayaran
            );



            alertDialog.dismiss();

            long totalSisa;
            if(totalPembayaran < totalBelanja) {
                totalSisa = totalKekurangan;
            } else {
                totalSisa = totalKembalian;
            }
            submit(Integer.parseInt(String.valueOf(totalBelanja)), Integer.parseInt(String.valueOf(totalPembayaran)), Integer.parseInt(String.valueOf(totalSisa)), Integer.parseInt(String.valueOf(diskon)), Integer.parseInt(String.valueOf(subtotal)));
        });

        alertDialog.setOnShowListener(dialog -> {
            Window window = alertDialog.getWindow();

            if (window != null) {
                window.setBackgroundDrawable(
                        new ColorDrawable(Color.LTGRAY)
                );

                DisplayMetrics displayMetrics =
                        getResources().getDisplayMetrics();

                int lebarDialog =
                        (int) (displayMetrics.widthPixels * 0.92);

                window.setLayout(
                        lebarDialog,
                        WindowManager.LayoutParams.WRAP_CONTENT
                );

                window.setSoftInputMode(
                        WindowManager.LayoutParams
                                .SOFT_INPUT_ADJUST_RESIZE
                );
            }

            etPembayaran.requestFocus();
        });

        alertDialog.show();
    }


    private void setPembayaranPas(
            TextInputEditText etPembayaran,
            long totalBelanja
    ) {
        etPembayaran.setText(
                String.valueOf(totalBelanja)
        );

        etPembayaran.setSelection(
                etPembayaran.getText().length()
        );
    }
    private void tambahNominalPembayaran(
            TextInputEditText etPembayaran,
            long nominalTambahan
    ) {
        long pembayaranSaatIni = 0;

        if (etPembayaran.getText() != null) {
            String input = etPembayaran.getText()
                    .toString()
                    .replaceAll("[^0-9]", "")
                    .trim();

            if (!input.isEmpty()) {
                try {
                    pembayaranSaatIni = Long.parseLong(input);
                } catch (NumberFormatException e) {
                    pembayaranSaatIni = 0;
                }
            }
        }

        long totalPembayaranBaru =
                pembayaranSaatIni + nominalTambahan;

        etPembayaran.setText(
                String.valueOf(totalPembayaranBaru)
        );

        etPembayaran.setSelection(
                etPembayaran.getText().length()
        );
    }


    private void show_information_dialog(String title, String message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null) // Hanya tombol OK
                .create();

        dialog.show();

    }

    private void submit(int belanja,int bayar, int kembali, int discount, int subtotal  ) {
        HashMap<String,String> user = sessionManager.getSessionData();
        String userkode = user.get(sessionManager.ID);

        HashMap<String, String> customer = sessionPelanggan.getSessionPelanggan();
        String cust_code = customer.get(sessionPelanggan.CCODE);
        String cust_address = customer.get(sessionPelanggan.CADDRESS);
        String cust_name = customer.get(sessionPelanggan.CNAME);
        String cust_group = customer.get(sessionPelanggan.CGRUP);

        String sekarang = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        loading.setVisibility(View.VISIBLE);


        String nota = db.generateNota(userkode);
        db.tambah_penjualan(
                nota,
                cust_code,
                "Penjualan",
                sekarang,
                belanja,
                bayar,
                0,
                kembali,
                userkode,
                0,
                0,
                discount,subtotal
        );


        Cursor cursor = db.tampilkandata();
        while(cursor.moveToNext()) {
            db.tambah_penjualan_item(
                    nota,
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5),
                    cursor.getInt(6),
                    cursor.getInt(7),
                    cursor.getInt(8),
                    cursor.getInt(10),
                    cursor.getInt(11),
                    cursor.getInt(12),
                    cursor.getInt(13),
                    cursor.getInt(14)

            );
        }

        loading.setVisibility(View.GONE);

        Toast.makeText(MainActivity.this, "Sukses Simpan Penjualan", Toast.LENGTH_SHORT).show();
        dialog_hapus();

    }

    private void dialog_hapus() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Hapus Item")
                .setMessage("Hapus Item Transaksi Ini...? ")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.hapussemua();
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                })
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == 300){
            String intent_nama_pelanggan = data.getStringExtra("customer_name");
            String intent_alamat_pelanggan = data.getStringExtra("customer_address");
            String intent_grup_pelanggan = data.getStringExtra("customer_group");
            String intent_kode_pelanggan = data.getStringExtra("customer_code");
            namapelanggan.setText(intent_nama_pelanggan);
            alamatpelanggan.setText(intent_alamat_pelanggan);
            gruppelanggan.setText(intent_grup_pelanggan);
            sessionPelanggan.logout_pelanggan();
            sessionPelanggan.createSession(intent_kode_pelanggan, intent_nama_pelanggan, intent_alamat_pelanggan, intent_grup_pelanggan);
        } else if(resultCode == Activity.RESULT_OK && requestCode == 600){
            displayData();
        } else if(resultCode == Activity.RESULT_OK && requestCode == 800){
            int hargaaktif = 0;
            int konversi = data.getIntExtra("intent_konversi", 0);
            String kodebarang = data.getStringExtra("intent_kodebarang");
            Cursor cursor = db.periksadata(kodebarang);
            HashMap<String,String> cust = sessionPelanggan.getSessionPelanggan();
            String cust_group = cust.get(sessionPelanggan.CGRUP);

            HashMap<String,String> pengguna = sessionManager.getSessionData();
            String kodepengguna = pengguna.get(sessionManager.ID);

            if(cursor.getCount() == 0){
                if(cust_group.equalsIgnoreCase("Reguler")) {
                    hargaaktif = data.getIntExtra("intent_harga_jual", 0);
                } else if(cust_group.equalsIgnoreCase("Grosir")){
                    hargaaktif = data.getIntExtra("intent_harga_grosir", 0);
                } else if(cust_group.equalsIgnoreCase("Freelancer")) {
                    hargaaktif = data.getIntExtra("intent_harga_freelance", 0);
                }


                db.tambahitem(
                        kodebarang,
                        data.getStringExtra("intent_barcode"),
                        data.getStringExtra("intent_nama_barang"),
                        data.getStringExtra("intent_satuan"),
                        1,
                        hargaaktif,
                        data.getIntExtra("intent_harga_beli", 0),
                        hargaaktif,
                        kodepengguna,
                        0,
                        data.getIntExtra("intent_diskon", 0),
                        hargaaktif,
                        konversi,
                        0
                );
            } else {
                cursor.moveToFirst();
                int jumlah = Integer.parseInt(cursor.getString(5));
                int price_type = Integer.parseInt(cursor.getString(14));
                int jumlahbaru = jumlah + 1;
                if(jumlahbaru >= konversi) {
                    if(cust_group.equalsIgnoreCase("Reguler")) {
                        hargaaktif = data.getIntExtra("intent_harga_karton", 0);
                    } else if(cust_group.equalsIgnoreCase("Grosir")){
                        hargaaktif = data.getIntExtra("intent_grosir_karton", 0);
                    } else if(cust_group.equalsIgnoreCase("Freelancer")) {
                        hargaaktif = data.getIntExtra("intent_freelance_karton", 0);
                    }
                } else {
                    if(cust_group.equalsIgnoreCase("Reguler")) {
                        hargaaktif = data.getIntExtra("intent_harga_jual", 0);
                    } else if(cust_group.equalsIgnoreCase("Grosir")){
                        hargaaktif = data.getIntExtra("intent_harga_grosir", 0);
                    } else if(cust_group.equalsIgnoreCase("Freelancer")) {
                        hargaaktif = data.getIntExtra("intent_harga_freelance", 0);
                    }
                }

                int totalbaru = jumlahbaru * hargaaktif;
                if(price_type == 2) {
                    hargaaktif = data.getIntExtra("intent_harga_reseller", 0);
                    totalbaru = jumlahbaru * hargaaktif;

                }
                db.updateitem(kodebarang, jumlahbaru, hargaaktif, totalbaru, 0, totalbaru, price_type);
            }

            displayData();

        }
    }



    private void displayData() {
        list_data = new ArrayList<HashMap<String, String>>();
        Cursor cursor = db.tampilkandata();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }else {
            while(cursor.moveToNext()) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", cursor.getString(0));
                map.put("kd_barang", cursor.getString(1));
                map.put("barcode", cursor.getString(2));
                map.put("nm_barang", cursor.getString(3));
                map.put("satuan", cursor.getString(4));
                map.put("jumlah", cursor.getString(5));
                map.put("harga", cursor.getString(6));
                map.put("modal", cursor.getString(7));
                map.put("total", cursor.getString(8));
                map.put("kd_user", cursor.getString(9));
                map.put("status", cursor.getString(10));
                map.put("disk", cursor.getString(11));
                map.put("subtotal", cursor.getString(12));
                map.put("konversi", cursor.getString(13));

                list_data.add(map);
                itemAdapter = new ItemAdapter(MainActivity.this, list_data, MainActivity.this);
                itemAdapter.notifyDataSetChanged();
                rvproduk.setAdapter(itemAdapter);

            }
        }
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        int totalpenjualan = db.totalpenjualan();
        txtharga.setText(formatRupiah.format(totalpenjualan));
        totalHidden.setText(String.valueOf(totalpenjualan));

        int totalitem = db.totalitem();
        txtitem.setText(String.valueOf(totalitem)+" item");
    }



    private void check_permission() {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) +
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) +
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) +
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) +
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.READ_PHONE_STATE)||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.CAMERA)||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Grant Those Permission");
                builder.setMessage("Access Fine Location, Coarse Location, Camera, Read and Write External Storage");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.READ_PHONE_STATE,
                                        Manifest.permission.CAMERA,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                },
                                123
                        );
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }else{
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        123
                );
            }
        }else{
            Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }


    private void check_permission_android11() {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_PHONE_NUMBERS)){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Grant Those Permission");
                builder.setMessage("Read Phone Numbers");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{
                                        Manifest.permission.READ_PHONE_NUMBERS
                                },
                                125
                        );
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }else{
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{
                                Manifest.permission.READ_PHONE_NUMBERS
                        },
                        125
                );
            }
        }else{
            Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void keluarAplikasi() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("Apakah Anda Ingin Keluar Dari Aplikasi..?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "YA",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db.hapussemua();
                        finish();
                        sessionManager.logout();
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


    private void dialogbataltransaksi() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("Apakah Anda Ingin Membatalkan Transaksi..?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "YA",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db.hapussemua();
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);

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
    public void editItem(int position, String kdbarang) {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra("item_code", kdbarang);
        intent.putExtra("item_name", list_data.get(position).get("nm_barang"));
        intent.putExtra("item_satuan", list_data.get(position).get("satuan"));
        intent.putExtra("item_konversi", list_data.get(position).get("konversi"));
        intent.putExtra("item_jumlah", list_data.get(position).get("jumlah"));
        intent.putExtra("item_discount", list_data.get(position).get("disk"));


        startActivityForResult(intent, 600);
    }

    @Override
    public void bataltransaksi() {
        dialogbataltransaksi();
    }
}