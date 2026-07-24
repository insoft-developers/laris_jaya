package com.insoft.laris;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;
import com.insoft.laris.model.Produk;
import com.insoft.laris.utils.MyDatabaseHelper;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.SessionManager;
import com.insoft.laris.utils.SessionPelanggan;

import java.util.HashMap;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission_group.CAMERA;

public class BarcodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private RegisterAPI registerAPI;
    private List<Produk> produk;
    MyDatabaseHelper db;
    private SessionManager sessionManager;
    private SessionPelanggan sessionPelanggan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

        db = new MyDatabaseHelper(this);
        sessionManager = new SessionManager(this);
        sessionPelanggan = new SessionPelanggan(this);
        if (ActivityCompat.checkSelfPermission(BarcodeActivity.this, CAMERA ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(BarcodeActivity.this,
                    new String[]{Manifest.permission.CAMERA},1);
        }
    }

    @Override
    public void handleResult(Result result) {
        if (ActivityCompat.checkSelfPermission(BarcodeActivity.this, CAMERA ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(BarcodeActivity.this,
                    new String[]{Manifest.permission.CAMERA},1);
        }

        get_product(result.getText());
    }



//    private void get_product(String barcode) {
//
//        HashMap<String,String> user = sessionManager.getSessionData();
//        String iduser = user.get(sessionManager.ID);
//
//        HashMap<String,String> cust = sessionPelanggan.getSessionPelanggan();
//        String cust_group = cust.get(sessionPelanggan.CGRUP);
//
//        BarcodeRequestJson param = new BarcodeRequestJson();
//        param.setBarcode(barcode);
//        registerAPI = UtilsAPI.getApiService();
//        registerAPI.get_produk(param).enqueue(new Callback<BarcodeResponseJson>() {
//            @Override
//            public void onResponse(Call<BarcodeResponseJson> call, Response<BarcodeResponseJson> response) {
//                if(response.isSuccessful()) {
//                    String res = response.body().getResultcode();
//                    produk = response.body().getData();
//                    if(res.equalsIgnoreCase("00")){
//                        mScannerView.stopCamera();
//
//                        int hargaaktif = 0;
//                        int konversi = produk.get(0).getKonversi();
//                        String kodebarang = produk.get(0).getKd_barang();
//                        Cursor cursor = db.periksadata(kodebarang);
//
//                        if(cursor.getCount() == 0){
//                            if(cust_group.equalsIgnoreCase("Reguler")) {
//                                hargaaktif = produk.get(0).getHarga_jual();
//                            } else if(cust_group.equalsIgnoreCase("Grosir")){
//                                hargaaktif = produk.get(0).getHarga_member();
//                            } else if(cust_group.equalsIgnoreCase("Freelancer")) {
//                                hargaaktif = produk.get(0).getHarga_freelance();
//                            }
//
//                            db.tambahitem(
//                                    produk.get(0).getKd_barang(),
//                                    produk.get(0).getBarcode(),
//                                    produk.get(0).getNm_barang(),
//                                    produk.get(0).getSatuan(),
//                                    1,
//                                    hargaaktif,
//                                    produk.get(0).getHarga_beli(),
//                                    hargaaktif,
//                                    iduser,
//                                    0,
//                                    produk.get(0).getDiskon(),
//                                    produk.get(0).getKonversi()
//                            );
//                        } else {
//                            cursor.moveToFirst();
//
//
//                            int jumlah = Integer.parseInt(cursor.getString(5));
//                            int jumlahbaru = jumlah + 1;
//                            if(jumlahbaru >= konversi) {
//                                if(cust_group.equalsIgnoreCase("Reguler")) {
//                                    hargaaktif = produk.get(0).getHj();
//                                } else if(cust_group.equalsIgnoreCase("Grosir")){
//                                    hargaaktif = produk.get(0).getDiskon_member();
//                                } else if(cust_group.equalsIgnoreCase("Freelancer")) {
//                                    hargaaktif = produk.get(0).getHarga_karton_freelance();
//                                }
//                            } else {
//                                if(cust_group.equalsIgnoreCase("Reguler")) {
//                                    hargaaktif = produk.get(0).getHarga_jual();
//                                } else if(cust_group.equalsIgnoreCase("Grosir")){
//                                    hargaaktif = produk.get(0).getHarga_member();
//                                } else if(cust_group.equalsIgnoreCase("Freelancer")) {
//                                    hargaaktif = produk.get(0).getHarga_freelance();
//                                }
//                            }
//
//                            int totalbaru = jumlahbaru * hargaaktif;
//                            db.updateitem(produk.get(0).getKd_barang(), jumlahbaru, hargaaktif, totalbaru);
//                        }
//
//                        Intent intent = new Intent(BarcodeActivity.this, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                    } else {
//                        mScannerView.stopCamera();
//                        String pesan = response.body().getPesan();
//                        Toast.makeText(BarcodeActivity.this,  pesan, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BarcodeResponseJson> call, Throwable t) {
//                Toast.makeText(BarcodeActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }



    private void get_product(String barcode) {
        HashMap<String,String> user = sessionManager.getSessionData();
        String iduser = user.get(sessionManager.ID);

        HashMap<String,String> cust = sessionPelanggan.getSessionPelanggan();
        String cust_group = cust.get(sessionPelanggan.CGRUP);

        // 🔹 Ambil produk dari SQLite
        Produk p = db.findProdukByBarcode(barcode);

        if (p != null) {
            mScannerView.stopCamera();
            int hargaaktif = 0;
            int konversi = p.getKonversi();
            String kodebarang = p.getKd_barang();

            Cursor cursor = db.periksadata(kodebarang);
            if (cursor.getCount() == 0) {
                // tentukan harga aktif sesuai grup
                if(cust_group.equalsIgnoreCase("Reguler")) {
                    hargaaktif = p.getHarga_jual();
                } else if(cust_group.equalsIgnoreCase("Grosir")) {
                    hargaaktif = p.getHarga_member();
                } else if(cust_group.equalsIgnoreCase("Freelancer")) {
                    hargaaktif = p.getHarga_freelance();
                }

                db.tambahitem(
                        p.getKd_barang(),
                        p.getBarcode(),
                        p.getNm_barang(),
                        p.getSatuan(),
                        1,
                        hargaaktif,
                        p.getHarga_beli(),
                        hargaaktif,
                        iduser,
                        0,
                        p.getDiskon(),
                        hargaaktif,
                        p.getKonversi()
                );
            } else {
                cursor.moveToFirst();
                int jumlah = cursor.getInt(5);
                int jumlahbaru = jumlah + 1;

                if(jumlahbaru >= konversi) {
                    if(cust_group.equalsIgnoreCase("Reguler")) {
                        hargaaktif = p.getHj();
                    } else if(cust_group.equalsIgnoreCase("Grosir")) {
                        hargaaktif = p.getDiskon_member();
                    } else if(cust_group.equalsIgnoreCase("Freelancer")) {
                        hargaaktif = p.getHarga_karton_freelance();
                    }
                } else {
                    if(cust_group.equalsIgnoreCase("Reguler")) {
                        hargaaktif = p.getHarga_jual();
                    } else if(cust_group.equalsIgnoreCase("Grosir")) {
                        hargaaktif = p.getHarga_member();
                    } else if(cust_group.equalsIgnoreCase("Freelancer")) {
                        hargaaktif = p.getHarga_freelance();
                    }
                }

                int totalbaru = jumlahbaru * hargaaktif;



                db.updateitem(p.getKd_barang(), jumlahbaru, hargaaktif, totalbaru, 0, totalbaru);
            }
            cursor.close();

            // balik ke MainActivity
            Intent intent = new Intent(BarcodeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else {
            mScannerView.stopCamera();
            Toast.makeText(BarcodeActivity.this, "Produk tidak ditemukan di SQLite", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }



    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }
}