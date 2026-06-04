package com.insoft.laris.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.zxing.Result;
import com.insoft.laris.R;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission_group.CAMERA;

public class BarcodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

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

        String hasilScan = result.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("hasil_scan", hasilScan);
        setResult(Activity.RESULT_OK, intent);
        finish();
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