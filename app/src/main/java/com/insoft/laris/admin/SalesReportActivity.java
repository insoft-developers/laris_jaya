package com.insoft.laris.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.card.MaterialCardView;
import com.insoft.laris.R;
import com.insoft.laris.report.SalesByDateActivity;
import com.insoft.laris.report.SalesTodayActivity;

public class SalesReportActivity extends AppCompatActivity {
    private MaterialCardView sales_tday;
    private  MaterialCardView penjualantanggal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_report);

        sales_tday = findViewById(R.id.penjualantoday);
        penjualantanggal =findViewById(R.id.penjualantanggal);

        sales_tday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(SalesReportActivity.this, SalesTodayActivity.class);
                Intent intent = new Intent(SalesReportActivity.this, SalesTodayActivity.class);
                startActivity(intent);
            }
        });

        penjualantanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SalesReportActivity.this, SalesByDateActivity.class);
                startActivity(intent);
            }
        });
    }
}