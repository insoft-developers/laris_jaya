package com.insoft.laris.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.insoft.laris.Interface.penjualanInterface;
import com.insoft.laris.MainActivity;
import com.insoft.laris.R;
import com.insoft.laris.adapter.ItemPenjualan;
import com.insoft.laris.json.HapusItemRequestJson;
import com.insoft.laris.json.HapusItemResponseJson;
import com.insoft.laris.json.PenjualanRequestJson;
import com.insoft.laris.json.PenjualanResponseJson;
import com.insoft.laris.json.TransferPenjualanRequestJson;
import com.insoft.laris.json.TransferPenjualanResponseJson;
import com.insoft.laris.json.UpadateItemResponseJson;
import com.insoft.laris.json.UpdateItemRequestJson;
import com.insoft.laris.model.Penjualan;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PenjualanActivity extends AppCompatActivity implements penjualanInterface {
    private RegisterAPI registerAPI;
    private List<Penjualan> dataPenjualan;
    private ProgressBar loading;
    private RecyclerView rv;
    private TextView nmPelanggan, totalPenjualan;
    private FloatingActionButton fabSimpan;
    private int idHold;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penjualan);
        registerAPI = UtilsAPI.getApiService();
        loading = findViewById(R.id.loading);
        rv = findViewById(R.id.rv);
        nmPelanggan = findViewById(R.id.namaPelanggan);
        totalPenjualan = findViewById(R.id.totalPenjualan);
        fabSimpan = findViewById(R.id.fabSimpan);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        rv.setLayoutManager(llm);
        idHold = getIntent().getIntExtra("id_hold", 0);
        String np = getIntent().getStringExtra("cust_name");
        nmPelanggan.setText(np);





        fabSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSubmit();
            }
        });
        fetchdata(idHold);
    }

    private void fetchdata(int idHold) {
        loading.setVisibility(View.VISIBLE);
        PenjualanRequestJson param = new PenjualanRequestJson();
        param.setId_hold(idHold);
        registerAPI.penjualan(param).enqueue(new Callback<PenjualanResponseJson>() {
            @Override
            public void onResponse(Call<PenjualanResponseJson> call, Response<PenjualanResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    if(resultcode.equalsIgnoreCase("00")) {
                        dataPenjualan = response.body().getData();
                        ItemPenjualan itemPenjualan = new ItemPenjualan(PenjualanActivity.this, dataPenjualan, PenjualanActivity.this);
                        itemPenjualan.notifyDataSetChanged();
                        rv.setAdapter(itemPenjualan);

                        Locale localeID = new Locale("in", "ID");
                        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                        int totalpenjualan = response.body().getTotal();
                        totalPenjualan.setText(formatRupiah.format(totalpenjualan));
                    }
                }
            }

            @Override
            public void onFailure(Call<PenjualanResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void showDialogSubmit() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(PenjualanActivity.this);
        builder1.setMessage("Proses Penjualan..?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "YA",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        transferSubmit();

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

    private void transferSubmit() {
        loading.setVisibility(View.VISIBLE);
        TransferPenjualanRequestJson param = new TransferPenjualanRequestJson();
        param.setId_hold(idHold);
        registerAPI.transfer(param).enqueue(new Callback<TransferPenjualanResponseJson>() {
            @Override
            public void onResponse(Call<TransferPenjualanResponseJson> call, Response<TransferPenjualanResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    if(resultcode.equalsIgnoreCase("00")) {
                        String nota = response.body().getNota();
                        showDialogPrint(nota);
                  }
                }
            }

            @Override
            public void onFailure(Call<TransferPenjualanResponseJson> call, Throwable t) {

            }
        });

    }

    private void showDialogPrint(String nota) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(PenjualanActivity.this);
        builder1.setMessage("Penjualan Berhasil Disimpan. Cetak atau Share Struk No "+nota+"..?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "YA",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        printStruk(nota);
                    }
                });

        builder1.setNegativeButton(
                "TIDAK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(PenjualanActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    private void showDialogHapus(int posisi) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(PenjualanActivity.this);
        builder1.setMessage("Hapus Item Ini..?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "YA",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        conHapusItem(posisi);
                        dialog.cancel();
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

    private void conHapusItem(int posisi) {
        loading.setVisibility(View.VISIBLE);
        HapusItemRequestJson param = new HapusItemRequestJson();
        param.setId(dataPenjualan.get(posisi).getId());
        param.setType(1);
        registerAPI.hapus_item(param).enqueue(new Callback<HapusItemResponseJson>() {
            @Override
            public void onResponse(Call<HapusItemResponseJson> call, Response<HapusItemResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    if(resultcode.equalsIgnoreCase("00")) {
                        fetchdata(idHold);
                    }
                }
            }

            @Override
            public void onFailure(Call<HapusItemResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void printStruk(String nota) {
        Intent intent = new Intent(PenjualanActivity.this, ReceiptActivity.class);
        intent.putExtra("_nota", nota);
        startActivity(intent);
    }

    @Override
    public void pilihItem(int posisi) {

        showCustomDialog(posisi);
    }

    @Override
    public void hapusItem(int posisi) {
        showDialogHapus(posisi);
    }

    private void showCustomDialog(int posisi) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_modal);
        dialog.setCancelable(false);
        EditText namaproduk = dialog.findViewById(R.id.namaproduk);
        EditText hargasatuan = dialog.findViewById(R.id.hargasatuan);
        EditText jumlahproduk = dialog.findViewById(R.id.jumlahproduk);
        namaproduk.setText(dataPenjualan.get(posisi).getNm_barang());
        hargasatuan.setText(String.valueOf(dataPenjualan.get(posisi).getHarga()));
        jumlahproduk.setText(String.valueOf(dataPenjualan.get(posisi).getJumlah()));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((Button) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int _harga = Integer.parseInt(hargasatuan.getText().toString());
                int _jumlah = Integer.parseInt(jumlahproduk.getText().toString());
                updateItem(dataPenjualan.get(posisi).getId(), _jumlah, _harga );
                dialog.dismiss();
            }
        });

        ((Button) dialog.findViewById(R.id.bt_restart)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }

    private void updateItem(int id, int jumlah, int harga) {
        loading.setVisibility(View.VISIBLE);
        UpdateItemRequestJson param = new UpdateItemRequestJson();
        param.setId(id);
        param.setJumlah(jumlah);
        param.setHarga(harga);
        registerAPI.update_item(param).enqueue(new Callback<UpadateItemResponseJson>() {
            @Override
            public void onResponse(Call<UpadateItemResponseJson> call, Response<UpadateItemResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    if(resultcode.equalsIgnoreCase("00")) {
                        fetchdata(idHold);
                    }
                }
            }

            @Override
            public void onFailure(Call<UpadateItemResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);

            }
        });
    }
}