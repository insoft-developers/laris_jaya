package com.insoft.laris.admin.piutang;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.insoft.laris.R;
import com.insoft.laris.admin.pembayaran.PembayaranActivity;
import com.insoft.laris.admin.pengguna.PenggunaActivity;
import com.insoft.laris.admin.pengguna.PenggunaItem;
import com.insoft.laris.admin.pengguna.PenggunaRequestJson;
import com.insoft.laris.admin.pengguna.PenggunaResponseJson;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.SessionManager;
import com.insoft.laris.utils.UtilsAPI;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class PiutangActivity  extends AppCompatActivity {
    private SearchView searchPiutang;
    private RadioGroup radioFilterPiutang;
    private RecyclerView rvPiutang;
    private ProgressBar loadingPiutang;
    private LinearLayout layoutDataKosong;
    private TextView tvPesanKosong;
    private TextView tvTotalSisaHutang;

    private String kataPencarian = "";
    private String filterDipilih = "outstanding";

    private RegisterAPI api;
    private List<Piutang> piutangList;

    private SessionManager session;

    String kodepengguna = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piutang);
        api = UtilsAPI.getApiService();
        session = new SessionManager(this);

        HashMap<String,String> pengguna = session.getSessionData();
        kodepengguna = pengguna.get(session.ID);

        searchPiutang =
                findViewById(R.id.searchPiutang);

        radioFilterPiutang =
                findViewById(R.id.radioFilterPiutang);

        rvPiutang =
                findViewById(R.id.rvPiutang);

        loadingPiutang =
                findViewById(R.id.loadingPiutang);

        layoutDataKosong =
                findViewById(R.id.layoutDataKosong);

        tvPesanKosong =
                findViewById(R.id.tvPesanKosong);

        tvTotalSisaHutang = findViewById(R.id.tvTotalSisaHutang);


        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvPiutang.setLayoutManager(llm);



        fetch_data("", filterDipilih);

        searchPiutang.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetch_data(query.trim(), filterDipilih);
                searchPiutang.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetch_data(newText.trim(), filterDipilih);
                return true;
            }
        });

        radioFilterPiutang.setOnCheckedChangeListener(
                (group, checkedId) -> {

                    if (checkedId == R.id.radioOutstanding) {
                        filterDipilih = "outstanding";

                    }
                    else if (checkedId == R.id.radioTempo) {
                        filterDipilih = "tempo";
                    }
                    else if (checkedId == R.id.radioSemua) {
                        filterDipilih = "all";
                    }

                    fetch_data(kataPencarian, filterDipilih);
                }
        );

    }

    private void hitungTotalSisaHutang(List<Piutang> daftarPiutang) {
        double total = 0;

        if (daftarPiutang != null) {
            for (Piutang item : daftarPiutang) {
                if (item == null) {
                    continue;
                }

                String nilaiSisa = String.valueOf(item.getSisa());

                if (nilaiSisa != null && !nilaiSisa.trim().isEmpty()) {
                    try {
                        total += Double.parseDouble(nilaiSisa);
                    } catch (NumberFormatException ignored) {
                        // Abaikan nilai yang bukan angka
                    }
                }
            }
        }

        NumberFormat formatRupiah =
                NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        formatRupiah.setMaximumFractionDigits(0);
        formatRupiah.setMinimumFractionDigits(0);

        tvTotalSisaHutang.setText(formatRupiah.format(total));
    }
    private void fetch_data(String s, String filter) {
        loadingPiutang.setVisibility(View.VISIBLE);
        PiutangRequestJson param = new PiutangRequestJson();
        param.setKata_cari(s);
        param.setFilter(filter);

        api.piutangList(param).enqueue(new Callback<PiutangResponseJson>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<PiutangResponseJson> call, Response<PiutangResponseJson> response) {
                loadingPiutang.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String res = response.body().getResultcode();
                    if(res.equalsIgnoreCase("00")) {
                        piutangList = response.body().getData();
                        hitungTotalSisaHutang(piutangList);

                        PiutangItem adapter = new PiutangItem(PiutangActivity.this, piutangList, item -> tampilkanModalPembayaran(item));
                        adapter.notifyDataSetChanged();
                        rvPiutang.setAdapter(adapter);

                    }
                }
            }

            @Override
            public void onFailure(Call<PiutangResponseJson> call, Throwable t) {
                loadingPiutang.setVisibility(View.GONE);
                Toast.makeText(PiutangActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void tampilkanModalPembayaran(Piutang piutang) {
        Dialog dialog = new Dialog(this);

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_pembayaran_piutang, null, false);

        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        Window window = dialog.getWindow();

        if (window != null) {
            window.setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );

            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.55f;

            window.setAttributes(params);
            window.addFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND
            );
        }

        ImageButton btnTutupDialog =
                view.findViewById(R.id.btnTutupDialog);

        MaterialButton btnBatal =
                view.findViewById(R.id.btnBatal);

        MaterialButton btnSimpan =
                view.findViewById(R.id.btnSimpanPembayaran);

        TextView tvNamaPelanggan =
                view.findViewById(R.id.tvDialogNamaPelanggan);

        TextView tvNota =
                view.findViewById(R.id.tvDialogNota);

        TextView tvTotalBelanja =
                view.findViewById(R.id.tvDialogTotalBelanja);

        TextView tvSudahDibayar =
                view.findViewById(R.id.tvDialogSudahDibayar);

        TextView tvSisaPiutang =
                view.findViewById(R.id.tvDialogSisaPiutang);

        TextInputLayout layoutJumlahPembayaran =
                view.findViewById(R.id.layoutJumlahPembayaran);

        TextInputEditText etJumlahPembayaran =
                view.findViewById(R.id.etJumlahPembayaran);

        TextInputEditText etKeterangan =
                view.findViewById(R.id.etKeterangan);

        /*
         * Sesuaikan nama getter dengan model Piutang Anda.
         */
        long totalBelanja = piutang.getBelanja();
        long sudahDibayar = piutang.getBayar();
        long sisaPiutang = piutang.getSisa();

        tvNamaPelanggan.setText(
                aman(piutang.getNm_pelanggan())
        );

        tvNota.setText(
                "Nota: " + aman(piutang.getNota())
        );

        tvTotalBelanja.setText(
                formatRupiah(totalBelanja)
        );

        tvSudahDibayar.setText(
                formatRupiah(sudahDibayar)
        );

        tvSisaPiutang.setText(
                formatRupiah(sisaPiutang)
        );

        btnTutupDialog.setOnClickListener(v -> dialog.dismiss());
        btnBatal.setOnClickListener(v -> dialog.dismiss());

        btnSimpan.setOnClickListener(v -> {
            layoutJumlahPembayaran.setError(null);

            String pembayaranText =
                    ambilText(etJumlahPembayaran)
                            .replaceAll("[^0-9]", "");

            String keterangan = ambilText(etKeterangan);

            if (pembayaranText.isEmpty()) {
                layoutJumlahPembayaran.setError(
                        "Jumlah pembayaran wajib diisi"
                );

                etJumlahPembayaran.requestFocus();
                return;
            }

            long jumlahPembayaran;

            try {
                jumlahPembayaran =
                        Long.parseLong(pembayaranText);
            } catch (NumberFormatException e) {
                layoutJumlahPembayaran.setError(
                        "Jumlah pembayaran tidak valid"
                );
                return;
            }

            if (jumlahPembayaran <= 0) {
                layoutJumlahPembayaran.setError(
                        "Pembayaran harus lebih dari Rp 0"
                );
                return;
            }

            if (jumlahPembayaran > sisaPiutang) {
                layoutJumlahPembayaran.setError(
                        "Pembayaran melebihi sisa piutang "
                                + formatRupiah(sisaPiutang)
                );
                return;
            }

            btnSimpan.setEnabled(false);
            btnSimpan.setText("Menyimpan...");
            simpanPembayaran(piutang.getNota(), piutang.getKd_pelanggan(), piutang.getSisa(), Integer.parseInt(String.valueOf(jumlahPembayaran)), keterangan );



        });

        dialog.setOnShowListener(dialogInterface -> {
            Window dialogWindow = dialog.getWindow();

            if (dialogWindow != null) {
                int lebarLayar =
                        getResources()
                                .getDisplayMetrics()
                                .widthPixels;

                int margin = (int) (
                        32 * getResources()
                                .getDisplayMetrics()
                                .density
                );

                dialogWindow.setLayout(
                        lebarLayar - margin,
                        WindowManager.LayoutParams.WRAP_CONTENT
                );

                dialogWindow.setGravity(Gravity.CENTER);
            }
        });

        dialog.show();
    }

    private void simpanPembayaran(String nota, String pelanggan, int nilai, int pembayaran, String keterangan) {


        PembayaranSimpanRequestJson param = new PembayaranSimpanRequestJson();
        param.setNota(nota);
        param.setPelanggan(pelanggan);
        param.setNilai_nota(nilai);
        param.setPembayaran(pembayaran);
        param.setKeterangan(keterangan);
        param.setKd_user(kodepengguna);

        api.tambah_pembayaran(param).enqueue(new Callback<PembayaranSimpanResponseJson>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<PembayaranSimpanResponseJson> call, Response<PembayaranSimpanResponseJson> response) {
                if(response.isSuccessful()) {
                    String res = response.body().getResultcode();
                    if(res.equalsIgnoreCase("00")) {
                        Toast.makeText(PiutangActivity.this, response.body().getMessage().toString(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(PiutangActivity.this, PembayaranActivity.class);
                        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(PiutangActivity.this, response.body().getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<PembayaranSimpanResponseJson> call, Throwable t) {
                loadingPiutang.setVisibility(View.GONE);
                Toast.makeText(PiutangActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private String aman(String nilai) {
        if (nilai == null || nilai.trim().isEmpty()) {
            return "-";
        }

        return nilai.trim();
    }

    private String ambilText(TextInputEditText editText) {
        if (editText == null || editText.getText() == null) {
            return "";
        }

        return editText.getText().toString().trim();
    }

    private String formatRupiah(long nilai) {
        NumberFormat formatter =
                NumberFormat.getCurrencyInstance(
                        new Locale("id", "ID")
                );

        formatter.setMaximumFractionDigits(0);
        formatter.setMinimumFractionDigits(0);

        return formatter.format(nilai);
    }
}
