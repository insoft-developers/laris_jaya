package com.insoft.laris.admin.pembayaran;

import static com.insoft.laris.utils.ReceiptPembayaranUtils.aman;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.insoft.laris.R;
import com.insoft.laris.admin.piutang.Piutang;
import com.insoft.laris.admin.piutang.PiutangActivity;
import com.insoft.laris.admin.piutang.PiutangItem;
import com.insoft.laris.admin.piutang.PiutangRequestJson;
import com.insoft.laris.admin.piutang.PiutangResponseJson;
import com.insoft.laris.utils.BluetoothPrinter58mm;
import com.insoft.laris.utils.ReceiptPembayaranUtils;
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

public class PembayaranActivity  extends AppCompatActivity implements ActionButtonInterface {
    private RegisterAPI api;
    private SearchView searchView;
    private EditText etTanggalMulai;
    private EditText etTanggalSelesai;
    private MaterialButton btnResetTanggal, btnTambahPembayaran;
    private RecyclerView rvPembayaran;
    private ProgressBar loading;
    private LinearLayout layoutDataKosong;
    private TextView tvPesanKosong;
    private TextView tvTotalPembayaran;

    private List<Pembayaran> pembayaranList;

    private Pembayaran dataMenungguPrint;

    private ActivityResultLauncher<String>
            bluetoothPermissionLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran);
        api = UtilsAPI.getApiService();

        searchView = findViewById(R.id.searchView);
        etTanggalMulai = findViewById(R.id.etTanggalMulai);
        etTanggalSelesai = findViewById(R.id.etTanggalSelesai);
        btnResetTanggal = findViewById(R.id.btnResetTanggal);
        btnTambahPembayaran = findViewById(R.id.btnTambahPembayaran);
        rvPembayaran = findViewById(R.id.rvPembayaran);

        loading = findViewById(R.id.loading);

        layoutDataKosong = findViewById(R.id.layoutDataKosong);
        tvPesanKosong = findViewById(R.id.tvPesanKosong);
        tvTotalPembayaran = findViewById(R.id.tvTotalPembayaran);

        bluetoothPermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        diberikan -> {
                            if (diberikan && dataMenungguPrint != null) {
                                tampilkanPilihanPrinter(
                                        dataMenungguPrint
                                );
                            } else {
                                Toast.makeText(
                                        this,
                                        "Izin Bluetooth diperlukan untuk mencetak",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvPembayaran.setLayoutManager(llm);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetch_data(query.trim(), "", "", "");
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetch_data(newText.trim(), "","", "");
                return true;
            }
        });

        fetch_data("", "", "", "" );

        btnTambahPembayaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PembayaranActivity.this, PiutangActivity.class);
                startActivity(intent);
            }
        });



    }

    private void hitungTotalPembayaran(List<Pembayaran> daftarPembayaran) {
        double total = 0;

        if (daftarPembayaran != null) {
            for (Pembayaran item : daftarPembayaran) {
                if (item == null) {
                    continue;
                }

                String nilaiSisa = String.valueOf(item.getPembayaran());

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

        tvTotalPembayaran.setText(formatRupiah.format(total));
    }

    private void fetch_data(String s, String awal, String akhir, String nota ) {
        loading.setVisibility(View.VISIBLE);
        PembayaranRequestJson param = new PembayaranRequestJson();
        param.setCari(s);
        param.setAwal(awal);
        param.setAkhir(akhir);
        param.setNota(nota);

        api.pembayaranList(param).enqueue(new Callback<PembayaranResponseJson>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<PembayaranResponseJson> call, Response<PembayaranResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String res = response.body().getResultcode();
                    if(res.equalsIgnoreCase("00")) {
                        pembayaranList = response.body().getData();
                        hitungTotalPembayaran(pembayaranList);

                        PembayaranItem item = new PembayaranItem(PembayaranActivity.this, pembayaranList, PembayaranActivity.this);
                        item.notifyDataSetChanged();
                        rvPembayaran.setAdapter(item);

                    }
                }
            }

            @Override
            public void onFailure(Call<PembayaranResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(PembayaranActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void prosesPrint(Pembayaran data) {
        dataMenungguPrint = data;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED) {

            bluetoothPermissionLauncher.launch(
                    Manifest.permission.BLUETOOTH_CONNECT
            );

            return;
        }

        tampilkanPilihanPrinter(data);
    }

    @SuppressLint("MissingPermission")
    private void tampilkanPilihanPrinter(Pembayaran data) {
        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(
                        Context.BLUETOOTH_SERVICE
                );

        BluetoothAdapter bluetoothAdapter =
                bluetoothManager == null
                        ? null
                        : bluetoothManager.getAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(
                    this,
                    "Perangkat tidak mendukung Bluetooth",
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

        String[] namaPrinter =
                new String[daftarPrinter.size()];

        for (int i = 0; i < daftarPrinter.size(); i++) {
            BluetoothDevice perangkat =
                    daftarPrinter.get(i);

            String nama = perangkat.getName();

            if (nama == null || nama.trim().isEmpty()) {
                nama = "Perangkat Bluetooth";
            }

            namaPrinter[i] =
                    nama + "\n" + perangkat.getAddress();
        }

        new AlertDialog.Builder(this)
                .setTitle("Pilih printer 58 mm")
                .setItems(namaPrinter, (dialog, posisi) -> {
                    BluetoothDevice printer =
                            daftarPrinter.get(posisi);

                    cetakKePrinter(printer, data);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void cetakKePrinter(
            BluetoothDevice printer,
            Pembayaran data
    ) {
        Toast.makeText(
                this,
                "Menghubungkan printer...",
                Toast.LENGTH_SHORT
        ).show();

        BluetoothPrinter58mm.print(
                printer,
                data,
                new BluetoothPrinter58mm.PrintCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() ->
                                Toast.makeText(
                                        PembayaranActivity.this,
                                        "Struk berhasil dicetak",
                                        Toast.LENGTH_SHORT
                                ).show()
                        );
                    }

                    @Override
                    public void onError(String pesan) {
                        runOnUiThread(() ->
                                Toast.makeText(
                                        PembayaranActivity.this,
                                        "Print gagal: " + pesan,
                                        Toast.LENGTH_LONG
                                ).show()
                        );
                    }
                }
        );
    }


    private void kirimStrukWhatsApp(Pembayaran data) {
        try {
            Uri gambarUri =
                    ReceiptPembayaranUtils
                            .simpanGambarStruk(
                                    this,
                                    data
                            );

            Intent kirimIntent =
                    new Intent(Intent.ACTION_SEND);

            kirimIntent.setType("image/png");

            kirimIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    gambarUri
            );

            kirimIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Struk pembayaran "
                            + aman(data.getNo_pembayaran())
                            + "\nPelanggan: "
                            + aman(data.getNm_pelanggan())
                            + "\nPembayaran: "
                            + ReceiptPembayaranUtils.formatRupiah(
                            data.getPembayaran()
                    )
            );

            kirimIntent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            // Mencoba WhatsApp biasa terlebih dahulu
            Intent whatsappIntent =
                    new Intent(kirimIntent);

            whatsappIntent.setPackage("com.whatsapp");

            try {
                startActivity(whatsappIntent);
                return;

            } catch (ActivityNotFoundException ignored) {
            }

            // Mencoba WhatsApp Business
            Intent whatsappBusinessIntent =
                    new Intent(kirimIntent);

            whatsappBusinessIntent.setPackage(
                    "com.whatsapp.w4b"
            );

            try {
                startActivity(whatsappBusinessIntent);
                return;

            } catch (ActivityNotFoundException ignored) {
            }

            // Bila WhatsApp tidak ditemukan, tampilkan menu berbagi
            startActivity(
                    Intent.createChooser(
                            kirimIntent,
                            "Kirim struk pembayaran"
                    )
            );

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Gagal membuat struk: "
                            + e.getLocalizedMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    public void onHapus(Pembayaran data) {
        tampilkanDialogHapusPembayaran(data);
    }

    @Override
    public void onPrint(Pembayaran data) {
        prosesPrint(data);
    }

    @Override
    public void onWhatsApp(Pembayaran data) {
        kirimStrukWhatsApp(data);
    }

    private void tampilkanDialogHapusPembayaran(Pembayaran data) {
        String nomorPembayaran = data.getNo_pembayaran();

        if (nomorPembayaran == null
                || nomorPembayaran.trim().isEmpty()) {
            nomorPembayaran = "-";
        }

        String namaPelanggan = data.getNm_pelanggan();

        if (namaPelanggan == null
                || namaPelanggan.trim().isEmpty()) {
            namaPelanggan = "Pelanggan";
        }

        String pesan =
                "Pembayaran nomor " + nomorPembayaran
                        + " milik " + namaPelanggan
                        + " akan dihapus.\n\n"
                        + "Data yang sudah dihapus tidak dapat dikembalikan.";

        new MaterialAlertDialogBuilder(this)
                .setTitle("Hapus Pembayaran?")
                .setMessage(pesan)
                .setIcon(R.drawable.ic_delete)
                .setNegativeButton(
                        "Batal",
                        (dialog, which) -> dialog.dismiss()
                )
                .setPositiveButton(
                        "Hapus",
                        (dialog, which) -> {
                            dialog.dismiss();
                            hapusPembayaran(data.getId());
                        }
                )
                .show();
    }

    private void hapusPembayaran(int id) {
        PembayaranHapusRequestJson param = new PembayaranHapusRequestJson();
        param.setId(id);
        api.hapus_pembayaran(param).enqueue(new Callback<PembayaranHapusResponseJson>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<PembayaranHapusResponseJson> call, Response<PembayaranHapusResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String res = response.body().getResultcode();
                    if(res.equalsIgnoreCase("00")) {
                        Toast.makeText(PembayaranActivity.this, response.body().getMessage().toString(), Toast.LENGTH_LONG).show();
                        fetch_data("","","","");
                    } else {
                        Toast.makeText(PembayaranActivity.this, response.body().getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<PembayaranHapusResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(PembayaranActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }
}
