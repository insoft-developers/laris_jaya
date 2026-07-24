package com.insoft.laris.admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.insoft.laris.R;
import com.insoft.laris.adapter.SpinnerAdapter;
import com.insoft.laris.adapter.SpinnerSatuanAdapter;
import com.insoft.laris.json.BarcodeCekRequestJson;
import com.insoft.laris.json.BarcodeCekResponseJson;
import com.insoft.laris.json.KategoriResponseJson;
import com.insoft.laris.json.SatuanResponseJson;
import com.insoft.laris.json.SimpanProdukRequestJson;
import com.insoft.laris.json.SimpanProdukResponseJson;
import com.insoft.laris.model.Kategori;
import com.insoft.laris.model.Satuan;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.UtilsAPI;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("deprecation")
public class BarangAddActivity extends AppCompatActivity {
    private EditText etBarcode, etNamaBarang, etHargaBeli;
    private EditText etKonversi, etHargaJual, etHargaJualKarton;
    private EditText etHargaMember, etHargaMemberKarton;
    private EditText etHargaFreelance, etHargaFreelanceKarton;

    private EditText etHargaReseller;
    private EditText etStok;
    private Button btnSimpan;
    private Spinner spnKategori, spnSatuan;
    private SpinnerAdapter adapter;
    ArrayList<Kategori> kategories;
    ArrayList<Satuan> satuan;
    private ProgressBar loading;
    private RegisterAPI registerAPI;
    private String kategoriPilih = "";
    private String satuanPilih = "";
    private ImageButton btnbarcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang_add);
        registerAPI = UtilsAPI.getApiService();
        etBarcode = findViewById(R.id.et_barcode);
        etNamaBarang = findViewById(R.id.et_nama_barang);
        etHargaBeli = findViewById(R.id.et_harga_beli);
        etKonversi = findViewById(R.id.et_konversi);
        etHargaJual = findViewById(R.id.et_harga_jual);
        etHargaJualKarton = findViewById(R.id.et_harga_jual_karton);
        etHargaMember = findViewById(R.id.et_harga_member);
        etHargaMemberKarton = findViewById(R.id.et_harga_member_karton);
        etHargaFreelance = findViewById(R.id.et_harga_freelance);
        etHargaFreelanceKarton = findViewById(R.id.et_harga_freelance_karton);
        etHargaReseller = findViewById(R.id.et_harga_reseller);
        etStok = findViewById(R.id.et_stok);
        btnSimpan = findViewById(R.id.btn_simpan_barang);
        spnKategori = findViewById(R.id.spn_kategori);
        spnSatuan = findViewById(R.id.spn_satuan);
        btnbarcode = findViewById(R.id.btnbarcode);
        loading = findViewById(R.id.loading);

        initList();

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etBarcode.getText().toString().isEmpty()) {
                    etBarcode.setError("Barcode Tidak Boleh Kosong...!");
                } else if(etNamaBarang.getText().toString().isEmpty()) {
                    etNamaBarang.setError("Nama Barang Tidak Boleh Kosong...!");
                } else if(etHargaBeli.getText().toString().isEmpty()) {
                    etHargaBeli.setError("Harga Beli Tidak Boleh Kosong...!");
                } else if(etKonversi.getText().toString().isEmpty()) {
                    etKonversi.setError("Konversi Tidak Boleh Kosong...!");
                } else if(etHargaJual.getText().toString().isEmpty()) {
                    etHargaJual.setError("Harga Jual Tidak Boleh Kosong...!");
                } else if(etHargaJualKarton.getText().toString().isEmpty()) {
                    etHargaJualKarton.setError("Harga Jual Karton Tidak Boleh Kosong...!");
                } else {
                    submitNewProduct();
                }

            }
        });

        etHargaJual.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(etKonversi.getText().toString().isEmpty()) {
                    etHargaJual.setError("Konversi Tidak Boleh Kosong...!");
                } else {
                    int _hargaJual = 0;
                    if(s.toString().isEmpty()) {
                        _hargaJual = 0;
                    } else {
                        _hargaJual = Integer.parseInt(s.toString());
                    }

                    int _konversi = Integer.parseInt(etKonversi.getText().toString());

                    int _hargaJualKarton = _hargaJual * _konversi;
                    etHargaJualKarton.setText(String.valueOf(_hargaJualKarton));


                }
            }
        });


        etHargaMember.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(etKonversi.getText().toString().isEmpty()) {
                    etHargaMember.setError("Konversi Tidak Boleh Kosong...!");
                } else {
                    int _konversi = Integer.parseInt(etKonversi.getText().toString());
                    int _hargaMember = 0;
                    if(s.toString().isEmpty()) {
                        _hargaMember = 0;
                    } else {
                        _hargaMember = Integer.parseInt(s.toString());
                    }

                    int _hargaMemberKarton = _hargaMember * _konversi;
                    etHargaMemberKarton.setText(String.valueOf(_hargaMemberKarton));

                }
            }
        });


        etHargaFreelance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(etKonversi.getText().toString().isEmpty()) {
                    etHargaFreelance.setError("Konversi Tidak Boleh Kosong...!");
                } else {
                    int _konversi = Integer.parseInt(etKonversi.getText().toString());
                    int _hargaFreelance = 0;
                    if(s.toString().isEmpty()) {
                        _hargaFreelance = 0;
                    } else {
                        _hargaFreelance = Integer.parseInt(s.toString());
                    }

                    int _hargaFreelanceKarton = _hargaFreelance * _konversi;
                    etHargaFreelanceKarton.setText(String.valueOf(_hargaFreelanceKarton));

                }
            }
        });

        btnbarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BarangAddActivity.this, BarcodeActivity.class );
                startActivityForResult(intent, 200);
            }
        });

        etBarcode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkBarcode(etBarcode.getText().toString());
                }
            }
        });

    }

    private void checkBarcode(String barcode) {
        loading.setVisibility(View.VISIBLE);
        BarcodeCekRequestJson param = new BarcodeCekRequestJson();
        param.setBarcode(barcode);
        registerAPI.cek_barcode(param).enqueue(new Callback<BarcodeCekResponseJson>() {
            @Override
            public void onResponse(Call<BarcodeCekResponseJson> call, Response<BarcodeCekResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    if(resultcode.equalsIgnoreCase("01")) {
                        etBarcode.setError("Barcode Sudah Ada...!");
                        etBarcode.setText("");
                    }
                }
            }

            @Override
            public void onFailure(Call<BarcodeCekResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
            }
        });

    }

    private void submitNewProduct() {
        loading.setVisibility(View.VISIBLE);
        SimpanProdukRequestJson param = new SimpanProdukRequestJson();
        param.setBarcode(etBarcode.getText().toString());
        param.setHarga_beli(Integer.parseInt(etHargaBeli.getText().toString()));
        param.setHarga_jual(Integer.parseInt(etHargaJual.getText().toString()));
        param.setHarga_jual_karton(Integer.parseInt(etHargaJualKarton.getText().toString()));
        if(etHargaMember.getText().toString().isEmpty()) {
            param.setHarga_member(Integer.parseInt(etHargaJual.getText().toString()));
        } else {
            param.setHarga_member(Integer.parseInt(etHargaMember.getText().toString()));
        }

        if(etHargaMemberKarton.getText().toString().isEmpty()) {
            param.setHarga_member_karton(Integer.parseInt(etHargaJualKarton.getText().toString()));
        } else {
            param.setHarga_member_karton(Integer.parseInt(etHargaMemberKarton.getText().toString()));
        }


        if(etHargaReseller.getText().toString().isEmpty()) {
            param.setHarga_reseller(Integer.parseInt(etHargaJual.getText().toString()));
        } else {
            param.setHarga_reseller(Integer.parseInt(etHargaReseller.getText().toString()));
        }


        if(etStok.getText().toString().isEmpty()) {
            param.setStok(0);
        } else {
            param.setStok(Integer.parseInt(etStok.getText().toString()));
        }


        param.setKategori(kategoriPilih);
        param.setKonversi(Integer.parseInt(etKonversi.getText().toString()));
        param.setNama_barang(etNamaBarang.getText().toString());
        param.setSatuan(satuanPilih);

        registerAPI.simpan_produk(param).enqueue(new Callback<SimpanProdukResponseJson>() {
            @Override
            public void onResponse(Call<SimpanProdukResponseJson> call, Response<SimpanProdukResponseJson> response) {
                loading.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    if(resultcode.equalsIgnoreCase("00")) {
                        Intent intent = new Intent(BarangAddActivity.this, MasterBarangActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<SimpanProdukResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(BarangAddActivity.this, t.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void setupSpinnerKategori() {
        adapter = new SpinnerAdapter(this, kategories);
        spnKategori.setAdapter(adapter);

        spnKategori.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        Kategori clickedItem = (Kategori)
                                parent.getItemAtPosition(position);
                        kategoriPilih = clickedItem.getNm_kategori();

                            ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                            ((TextView) parent.getChildAt(0)).setTextSize(18);




                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {

                    }
                });
    }

    private void initList()
    {
        loading.setVisibility(View.VISIBLE);
        registerAPI.list_kategori().enqueue(new Callback<KategoriResponseJson>() {
            @Override
            public void onResponse(Call<KategoriResponseJson> call, Response<KategoriResponseJson> response) {
                loading.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    if(resultcode.equalsIgnoreCase("00")) {
                        kategories = response.body().getData();
                        setupSpinnerKategori();
                        initListSatuan();
                    }
                }
            }

            @Override
            public void onFailure(Call<KategoriResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void initListSatuan()
    {
        loading.setVisibility(View.VISIBLE);
        registerAPI.list_satuan().enqueue(new Callback<SatuanResponseJson>() {
            @Override
            public void onResponse(Call<SatuanResponseJson> call, Response<SatuanResponseJson> response) {
                loading.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    String resultcode = response.body().getResultcode();
                    if(resultcode.equalsIgnoreCase("00")) {
                        satuan = response.body().getData();
                        setupSpinnerSatuan();
                    }
                }
            }

            @Override
            public void onFailure(Call<SatuanResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void setupSpinnerSatuan() {
        SpinnerSatuanAdapter adapter = new SpinnerSatuanAdapter(this, satuan);
        spnSatuan.setAdapter(adapter);

        spnSatuan.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        Satuan clickedItem = (Satuan)
                                parent.getItemAtPosition(position);
                        satuanPilih = clickedItem.getNm_satuan();

                        ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                        ((TextView) parent.getChildAt(0)).setTextSize(18);

                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("hasil_scan");
                etBarcode.setText(result);
            }

        }
    }
}