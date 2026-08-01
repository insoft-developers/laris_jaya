package com.insoft.laris.admin.pembelian;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.insoft.laris.R;
import com.insoft.laris.utils.BluetoothPrinter58mm;
import com.insoft.laris.utils.BluetoothPrinter58mm2;
import com.insoft.laris.utils.Fungsi;
import com.insoft.laris.utils.ReceiptPembelianUtils;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PembelianDetailActivity extends AppCompatActivity {

    private MaterialToolbar toolbarDetailPembelian;

    private View viewStatusPembelian;

    private MaterialCardView cardStatus;
    private MaterialCardView cardKeterangan;

    private Pembelian dataPembelian;
    private List<PembelianDetail> daftarDetail = new ArrayList<>();

    private String namaSupplier = "";
    private String namaPengguna = "";

    private BluetoothDevice printerTerpilih;

    private TextView tvNota;
    private TextView tvStatus;
    private TextView tvTanggal;
    private TextView tvSupplier;
    private TextView tvNamaSupplier;
    private TextView tvPengguna;
    private TextView tvJumlahProduk;
    private TextView tvSubtotal;
    private TextView tvTotalDiskon;
    private TextView tvTotalPembelian;
    private TextView tvKeterangan;

    private LinearLayout layoutNamaSupplier;
    private LinearLayout layoutProdukKosong;
    private LinearLayout layoutDiskon;

    private RecyclerView rvDetailPembelian;

    private ProgressBar loadingDetailPembelian;

    private MaterialButton btnHapus;
    private MaterialButton btnCetak;
    private RegisterAPI api;

    private BluetoothAdapter bluetoothAdapter;
    private ActivityResultLauncher<String> izinBluetoothLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembelian_detail);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        izinBluetoothLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        diizinkan -> {
                            if (diizinkan) {
                                tampilkanDialogPilihPrinter();
                            } else {
                                Toast.makeText(
                                        PembelianDetailActivity.this,
                                        "Izin Bluetooth diperlukan untuk mencetak",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );

        api = UtilsAPI.getApiService();
        toolbarDetailPembelian =
                findViewById(R.id.toolbarDetailPembelian);

        viewStatusPembelian =
                findViewById(R.id.viewStatusPembelian);

        tvNota =
                findViewById(R.id.tvNota);

        cardStatus =
                findViewById(R.id.cardStatus);

        tvStatus =
                findViewById(R.id.tvStatus);

        tvTanggal =
                findViewById(R.id.tvTanggal);

        tvSupplier =
                findViewById(R.id.tvSupplier);

        layoutNamaSupplier =
                findViewById(R.id.layoutNamaSupplier);

        tvNamaSupplier =
                findViewById(R.id.tvNamaSupplier);

        tvPengguna =
                findViewById(R.id.tvPengguna);

        tvJumlahProduk =
                findViewById(R.id.tvJumlahProduk);

        rvDetailPembelian =
                findViewById(R.id.rvDetailPembelian);

        loadingDetailPembelian =
                findViewById(R.id.loadingDetailPembelian);

        layoutProdukKosong =
                findViewById(R.id.layoutProdukKosong);

        tvSubtotal =
                findViewById(R.id.tvSubtotal);

        layoutDiskon =
                findViewById(R.id.layoutDiskon);

        tvTotalDiskon =
                findViewById(R.id.tvTotalDiskon);

        tvTotalPembelian =
                findViewById(R.id.tvTotalPembelian);

        cardKeterangan =
                findViewById(R.id.cardKeterangan);

        tvKeterangan =
                findViewById(R.id.tvKeterangan);

        btnHapus =
                findViewById(R.id.btnHapus);

        btnCetak =
                findViewById(R.id.btnCetak);

        String json = getIntent().getStringExtra("json");
        Pembelian pembelianIntent = new Gson().fromJson(json, Pembelian.class);

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        dataPembelian = pembelianIntent;
        daftarDetail = pembelianIntent.getItems();

        tvNota.setText(pembelianIntent.getNota());
        tvTanggal.setText(Fungsi.formatTanggal(pembelianIntent.getTanggal()));
        tvSupplier.setText(pembelianIntent.getKd_supplier());
        tvPengguna.setText(pembelianIntent.getNama());
        tvNamaSupplier.setText(pembelianIntent.getNm_supplier());
        tvSubtotal.setText(formatRupiah.format(pembelianIntent.getSubtotal()));
        tvTotalDiskon.setText(formatRupiah.format(pembelianIntent.getTotal_discount()));
        tvTotalPembelian.setText(formatRupiah.format(pembelianIntent.getTotal_pembelian()));
        tvKeterangan.setText(pembelianIntent.getKeterangan());

        namaSupplier = pembelianIntent.getNm_supplier();
        namaPengguna = pembelianIntent.getNama();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvDetailPembelian.setLayoutManager(llm);

        fetch_data(pembelianIntent.getItems());

        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tampilkanDialogHapus(
                        pembelianIntent.getNota(),
                  pembelianIntent.getKd_supplier(),
                        pembelianIntent.getNm_supplier()

                );
            }
        });

        btnCetak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pilihPrinterDanCetak();

            }
        });


    }

    private void fetch_data(List<PembelianDetail> items) {
        PembelianDetailItem adapter = new PembelianDetailItem(PembelianDetailActivity.this, items);
        adapter.notifyDataSetChanged();
        rvDetailPembelian.setAdapter(adapter);
        tvJumlahProduk.setText(items.size()+" Produk");

    }

    private void tampilkanDialogHapus(
           String nota, String kd_supplier,String nm_supplier

    ) {
        String namaBarang = nm_supplier;

        if (namaBarang == null || namaBarang.trim().isEmpty()) {
            namaBarang = "supplier ini";
        }


        AlertDialog dialog = new MaterialAlertDialogBuilder(PembelianDetailActivity.this)
                .setTitle("Hapus Pembelian")
                .setMessage(
                        "Apakah Anda yakin ingin menghapus "
                                + namaBarang
                                + " dari daftar pembelian?"
                )
                .setNegativeButton("Batal", null)
                .setPositiveButton("Hapus", null)
                .create();

        String finalId = kd_supplier;

        dialog.setOnShowListener(dialogInterface -> {

            // Tombol batal
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setOnClickListener(view -> dialog.dismiss());

            // Tombol hapus
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(view -> {
                        pembelianHapus(nota);
                        dialog.dismiss();
                    });
        });

        dialog.show();
    }


    private void pembelianHapus(String nota) {

        PembelianHapusRequestJson param = new PembelianHapusRequestJson();
        param.setNota(nota);

        api.pembelian_hapus(param).enqueue(new Callback<PembelianHapusReponseJson>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<PembelianHapusReponseJson> call, Response<PembelianHapusReponseJson> response) {

                if(response.isSuccessful()) {
                    String res = response.body().getResultcode();
                    if(res.equalsIgnoreCase("00")) {
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<PembelianHapusReponseJson> call, Throwable t) {

                Toast.makeText(PembelianDetailActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cetakStrukPembelian() {
        if (dataPembelian == null) {
            Toast.makeText(
                    this,
                    "Data pembelian tidak tersedia",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (daftarDetail == null
                || daftarDetail.isEmpty()) {

            Toast.makeText(
                    this,
                    "Detail barang belum tersedia",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (printerTerpilih == null) {
            Toast.makeText(
                    this,
                    "Pilih printer Bluetooth terlebih dahulu",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String teksStruk =
                ReceiptPembelianUtils.buatStruk(
                        dataPembelian,
                        daftarDetail,
                        namaSupplier,
                        namaPengguna
                );

        btnCetak.setEnabled(false);
        btnCetak.setText("Mencetak...");

        BluetoothPrinter58mm2.printText(
                printerTerpilih,
                teksStruk,
                new BluetoothPrinter58mm2.PrintCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            btnCetak.setEnabled(true);
                            btnCetak.setText(
                                    "Cetak Pembelian"
                            );

                            Toast.makeText(
                                    PembelianDetailActivity.this,
                                    "Struk berhasil dicetak",
                                    Toast.LENGTH_SHORT
                            ).show();
                        });
                    }

                    @Override
                    public void onError(String pesan) {
                        runOnUiThread(() -> {
                            btnCetak.setEnabled(true);
                            btnCetak.setText(
                                    "Cetak Pembelian"
                            );

                            Toast.makeText(
                                    PembelianDetailActivity.this,
                                    pesan == null
                                            ? "Gagal mencetak struk"
                                            : pesan,
                                    Toast.LENGTH_LONG
                            ).show();
                        });
                    }
                }
        );
    }

    private void pilihPrinterDanCetak() {
        if (bluetoothAdapter == null) {
            Toast.makeText(
                    this,
                    "Perangkat ini tidak mendukung Bluetooth",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(
                    this,
                    "Aktifkan Bluetooth terlebih dahulu",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED) {

            izinBluetoothLauncher.launch(
                    Manifest.permission.BLUETOOTH_CONNECT
            );

            return;
        }

        /*
         * Jika printer sebelumnya sudah dipilih,
         * langsung gunakan printer tersebut.
         */
        if (printerTerpilih != null) {
            cetakStrukPembelian();
            return;
        }

        tampilkanDialogPilihPrinter();
    }

    @SuppressLint("MissingPermission")
    private void tampilkanDialogPilihPrinter() {
        if (bluetoothAdapter == null) {
            Toast.makeText(
                    this,
                    "Bluetooth tidak tersedia",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Set<BluetoothDevice> perangkatTerpasang =
                bluetoothAdapter.getBondedDevices();

        if (perangkatTerpasang == null
                || perangkatTerpasang.isEmpty()) {

            Toast.makeText(
                    this,
                    "Belum ada printer Bluetooth yang dipasangkan",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        List<BluetoothDevice> daftarPrinter =
                new ArrayList<>(perangkatTerpasang);

        String[] namaPerangkat =
                new String[daftarPrinter.size()];

        for (int i = 0; i < daftarPrinter.size(); i++) {
            BluetoothDevice perangkat =
                    daftarPrinter.get(i);

            String nama = perangkat.getName();

            if (nama == null || nama.trim().isEmpty()) {
                nama = "Perangkat Bluetooth";
            }

            namaPerangkat[i] =
                    nama + "\n" + perangkat.getAddress();
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Pilih Printer Bluetooth")
                .setItems(
                        namaPerangkat,
                        (dialog, posisi) -> {
                            printerTerpilih =
                                    daftarPrinter.get(posisi);

                            Toast.makeText(
                                    PembelianDetailActivity.this,
                                    "Printer dipilih: "
                                            + namaPerangkat[posisi],
                                    Toast.LENGTH_SHORT
                            ).show();

                            /*
                             * Setelah printer dipilih,
                             * langsung cetak struk.
                             */
                            cetakStrukPembelian();
                        }
                )
                .setNegativeButton("Batal", null)
                .show();
    }
}
