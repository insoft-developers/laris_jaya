package com.insoft.laris.report;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.insoft.laris.R;
import com.insoft.laris.utils.SessionTanggal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SalesByDateActivity extends AppCompatActivity {

    private EditText tanggalawal, tanggalakhir;
    private Button btnsubmit;
    private Calendar myCalendar;
    private SessionTanggal sessionTanggal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_by_date);
        sessionTanggal = new SessionTanggal(this);
        myCalendar = Calendar.getInstance();
        tanggalawal = findViewById(R.id.tanggalawal);
        tanggalakhir = findViewById(R.id.tanggalakhir);
        btnsubmit = findViewById(R.id.btnsubmit);

        DatePickerDialog.OnDateSetListener dateawal = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                isiawal();
            }
        };

        DatePickerDialog.OnDateSetListener dateakhir = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                isiakhir();
            }
        };

        tanggalawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SalesByDateActivity.this, dateawal, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        tanggalakhir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SalesByDateActivity.this, dateakhir, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tawal = tanggalawal.getText().toString();
                String takhir = tanggalakhir.getText().toString();
                sessionTanggal.createSession(tawal, takhir);

                Intent intent = new Intent(SalesByDateActivity.this, SalesDateActivity.class);
                startActivity(intent);
            }
        });

    }

    private void isiawal() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tanggalawal.setText(sdf.format(myCalendar.getTime()));

    }

    private void isiakhir() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tanggalakhir.setText(sdf.format(myCalendar.getTime()));

    }
}