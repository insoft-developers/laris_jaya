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

    private int priceType = 0;

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
                    priceType = 1;
                } else if(cust_group.equalsIgnoreCase("Grosir")) {
                    hargaaktif = p.getHarga_member();
                    priceType = 2;
                } else if(cust_group.equalsIgnoreCase("Freelancer")) {
                    hargaaktif = p.getHarga_freelance();
                    priceType = 3;
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
                        p.getKonversi(),
                        priceType
                );
            } else {
                cursor.moveToFirst();
                int jumlah = cursor.getInt(5);
                int jumlahbaru = jumlah + 1;
                int price_type = cursor.getInt(14);


                    if(cust_group.equalsIgnoreCase("Reguler")) {
                        hargaaktif = p.getHarga_jual();
                        priceType = 1;
                    } else if(cust_group.equalsIgnoreCase("Grosir")) {
                        hargaaktif = p.getHarga_member();
                        priceType = 2;
                    } else if(cust_group.equalsIgnoreCase("Freelancer")) {
                        hargaaktif = p.getHarga_freelance();
                        priceType = 3;
                    }


                int totalbaru = jumlahbaru * hargaaktif;
                if(priceType == 2) {
                    hargaaktif = p.getHarga_member();
                    totalbaru = jumlahbaru * hargaaktif;
                }
                else if(priceType == 3) {
                    hargaaktif = p.getHarga_freelance();
                    totalbaru = jumlahbaru * hargaaktif;
                }



                db.updateitem(p.getKd_barang(), jumlahbaru, hargaaktif, totalbaru, 0, totalbaru, priceType);
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