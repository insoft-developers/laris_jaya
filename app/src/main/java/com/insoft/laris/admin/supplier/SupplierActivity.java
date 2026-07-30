package com.insoft.laris.admin.supplier;

import static com.insoft.laris.utils.ReceiptPembayaranUtils.aman;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.insoft.laris.R;
import com.insoft.laris.admin.piutang.PiutangActivity;
import com.insoft.laris.admin.piutang.PiutangItem;
import com.insoft.laris.admin.piutang.PiutangRequestJson;
import com.insoft.laris.admin.piutang.PiutangResponseJson;
import com.insoft.laris.admin.supplier.hapus.SupplierHapusRequestJson;
import com.insoft.laris.admin.supplier.hapus.SupplierHapusResponseJson;
import com.insoft.laris.admin.supplier.tambah.SupplierTambahActivity;
import com.insoft.laris.admin.supplier.tambah.SupplierTambahRequestJson;
import com.insoft.laris.admin.supplier.tambah.SupplierTambahResponseJson;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupplierActivity extends AppCompatActivity implements SupplierInterface {
    private SearchView searchView;
    private MaterialButton btnTambahSupplier;
    private RecyclerView rvSupplier;
    private ProgressBar loading;
    private LinearLayout layoutSupplierKosong;
    private TextView tvPesanSupplierKosong;

    private List<Supplier> supplierList;
    private RegisterAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);
        api = UtilsAPI.getApiService();
        searchView = findViewById(R.id.searchView);
        btnTambahSupplier = findViewById(R.id.btnTambahSupplier);
        rvSupplier = findViewById(R.id.rvSupplier);
        loading = findViewById(R.id.loading);
        layoutSupplierKosong = findViewById(R.id.layoutSupplierKosong);
        tvPesanSupplierKosong = findViewById(R.id.tvPesanSupplierKosong);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvSupplier.setLayoutManager(llm);


        fetch_data("");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetch_data(query.trim());
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetch_data(newText.trim());
                return true;
            }
        });

        btnTambahSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SupplierActivity.this, SupplierTambahActivity.class);
                intent.putExtra("METHOD", "add");
                startActivity(intent);
            }
        });


    }

    private void fetch_data(String s) {
        loading.setVisibility(View.VISIBLE);
        SupplierRequestJson param = new SupplierRequestJson();
        param.setCari(s);

        api.supplier_list(param).enqueue(new Callback<SupplierResponseJson>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<SupplierResponseJson> call, Response<SupplierResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String res = response.body().getResultcode();
                    if(res.equalsIgnoreCase("00")) {
                        supplierList = response.body().getData();

                        SupplierItem adapter = new SupplierItem(SupplierActivity.this, supplierList, SupplierActivity.this);
                        adapter.notifyDataSetChanged();
                        rvSupplier.setAdapter(adapter);

                    }
                }
            }

            @Override
            public void onFailure(Call<SupplierResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(SupplierActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetch_data("");
    }


    @Override
    public void hapusItem(int position) {

        tampilkanDialogHapusSupplier(supplierList.get(position));
    }

    @Override
    public void editItem(int position) {
        Intent intent = new Intent(SupplierActivity.this, SupplierTambahActivity.class);
        intent.putExtra("kd_supplier", supplierList.get(position).getKd_supplier());
        intent.putExtra("nm_supplier", supplierList.get(position).getNm_supplier());
        intent.putExtra("kontak", supplierList.get(position).getKontak());
        intent.putExtra("alamat", supplierList.get(position).getAlamat());
        intent.putExtra("telepon", supplierList.get(position).getTelepon());
        intent.putExtra("METHOD", "edit");
        startActivity(intent);

    }

    private void tampilkanDialogHapusSupplier(Supplier supplier) {
        String namaSupplier = supplier.getNm_supplier();

        if (namaSupplier == null || namaSupplier.trim().isEmpty()) {
            namaSupplier = "Supplier";
        }

        String kodeSupplier = supplier.getKd_supplier();

        if (kodeSupplier == null || kodeSupplier.trim().isEmpty()) {
            kodeSupplier = "-";
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle("Hapus Supplier?")
                .setMessage(
                        "Apakah Anda yakin ingin menghapus supplier:\n\n"
                                + namaSupplier
                                + "\nKode: "
                                + kodeSupplier
                                + "\n\nData yang sudah dihapus tidak dapat dikembalikan."
                )
                .setNegativeButton("Batal", null)
                .setPositiveButton("Hapus", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(
                            ContextCompat.getColor(
                                    SupplierActivity.this,
                                    android.R.color.holo_red_dark
                            )
                    );

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(view -> {
                        dialog.dismiss();

                        hapusSupplier(supplier.getKd_supplier());
                    });
        });

        dialog.show();
    }

    private void hapusSupplier(String code) {

        SupplierHapusRequestJson param = new SupplierHapusRequestJson();
        param.setKd_supplier(code);

        api.supplier_hapus(param).enqueue(new Callback<SupplierHapusResponseJson>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<SupplierHapusResponseJson> call, Response<SupplierHapusResponseJson> response) {

                if(response.isSuccessful()) {
                    String res = response.body().getResultcode();
                    if(res.equalsIgnoreCase("00")) {
                        Toast.makeText(SupplierActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        fetch_data("");
                    }
                }
            }

            @Override
            public void onFailure(Call<SupplierHapusResponseJson> call, Throwable t) {

                Toast.makeText(SupplierActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
