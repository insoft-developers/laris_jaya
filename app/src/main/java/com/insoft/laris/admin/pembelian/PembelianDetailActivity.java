package com.insoft.laris.admin.pembelian;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.insoft.laris.R;
import com.insoft.laris.utils.Fungsi;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PembelianDetailActivity extends AppCompatActivity {

    private MaterialToolbar toolbarDetailPembelian;

    private View viewStatusPembelian;

    private MaterialCardView cardStatus;
    private MaterialCardView cardKeterangan;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembelian_detail);
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

        tvNota.setText(pembelianIntent.getNota());
        tvTanggal.setText(Fungsi.formatTanggal(pembelianIntent.getTanggal()));
        tvSupplier.setText(pembelianIntent.getKd_supplier());
        tvPengguna.setText(pembelianIntent.getNama());
        tvNamaSupplier.setText(pembelianIntent.getNm_supplier());
        tvSubtotal.setText(formatRupiah.format(pembelianIntent.getSubtotal()));
        tvTotalDiskon.setText(formatRupiah.format(pembelianIntent.getTotal_discount()));
        tvTotalPembelian.setText(formatRupiah.format(pembelianIntent.getTotal_pembelian()));
        tvKeterangan.setText(pembelianIntent.getKeterangan());

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvDetailPembelian.setLayoutManager(llm);

        fetch_data(pembelianIntent.getItems());

        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tampilkanDialogHapus(
                  pembelianIntent.getKd_supplier(),
                        pembelianIntent.getNm_supplier()

                );
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
           String kd_supplier,String nm_supplier

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

                        dialog.dismiss();
                    });
        });

        dialog.show();
    }
}
