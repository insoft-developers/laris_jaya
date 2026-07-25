package com.insoft.laris;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.insoft.laris.adapter.ItemDetail;
import com.insoft.laris.utils.MyDatabaseHelper;
import com.insoft.laris.utils.ReceiptPrintAdapter;
import com.insoft.laris.utils.SessionPelanggan;

import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private RecyclerView rv_detail;
    private ProgressBar loading;
    ArrayList<HashMap<String,String>> list_data;
    private MyDatabaseHelper db;
    ItemDetail itemDetail;
    private Button btn_wa, btn_print;
    private SessionPelanggan sessionPelanggan;
    Locale localeID = new Locale("id", "ID");
    NumberFormat formatRupiah = NumberFormat.getInstance(localeID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        db = new MyDatabaseHelper(this);
        rv_detail = findViewById(R.id.rv_detail);
        loading = findViewById(R.id.loading);
        btn_wa = findViewById(R.id.wa);
        btn_print = findViewById(R.id.print);
        sessionPelanggan = new SessionPelanggan(this);



        String nota = getIntent().getStringExtra("id_hold_intent");
        displayData(nota);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        rv_detail.setLayoutManager(llm);

        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                print_receipt(nota);
            }
        });

        btn_wa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whatsapp(nota);
            }
        });

    }

    private void hapus_transaksi(String id_hold) {
        db.hapustransaksibyhold(id_hold);
        Intent intent = new Intent(DetailActivity.this, TransaksiActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void resend_item(String idhold) {
        db.hapussemua();

        Cursor cursor = db.displayItem(idhold);
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }else {
            while(cursor.moveToNext()) {
                db.tambahitem(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        Integer.parseInt(cursor.getString(5)),
                        Integer.parseInt(cursor.getString(6)),
                        Integer.parseInt(cursor.getString(7)),
                        Integer.parseInt(cursor.getString(8)),
                        cursor.getString(9),
                        Integer.parseInt(cursor.getString(10)),
                        Integer.parseInt(cursor.getString(11)),
                        Integer.parseInt(cursor.getString(12)),
                        1,0

                );

                sessionPelanggan.createSession(
                        cursor.getString(12),
                        cursor.getString(13),
                        cursor.getString(14),
                        cursor.getString(15)

                );
            }

            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void displayData(String id_hold) {

        list_data = new ArrayList<HashMap<String, String>>();
        Cursor cursor = db.tampilkan_penjualan_item(id_hold);
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }else {
            while(cursor.moveToNext()) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", cursor.getString(0));
                map.put("nm_barang", cursor.getString(1));
                map.put("total", cursor.getString(2));
                map.put("jumlah", cursor.getString(3));
                map.put("harga", cursor.getString(4));

                list_data.add(map);
                itemDetail = new ItemDetail(DetailActivity.this, list_data);
                itemDetail.notifyDataSetChanged();
                rv_detail.setAdapter(itemDetail);

            }
        }
    }


    private void print_receipt(String nota) {
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursorHeader = database.rawQuery("SELECT p.nota, p.tanggal, p.belanja, p.bayar, p.kembali, c.nm_pelanggan FROM penjualan p LEFT JOIN master_pelanggan c ON c.kd_pelanggan = p.kd_pelanggan WHERE p.nota = ?", new String[]{nota});
        Cursor cursorDetail = database.rawQuery("SELECT nm_barang, jumlah, harga, total FROM penjualan_item WHERE nota = ?", new String[]{nota});
        StringBuilder struk = new StringBuilder();


        String header = "";
        String detail = "";
        String footer = "";

        if (cursorHeader.moveToFirst()) {
            header += "Pelanggan : " + cursorHeader.getString(5) + "\n";
            header += "Nota : " + cursorHeader.getString(0) + "\n";
            header += "Tanggal : " + cursorHeader.getString(1) + "\n";
            header += "-------------------------------------------------\n";

            while (cursorDetail.moveToNext()) {
                detail += cursorDetail.getString(0) + "\n" +
                        cursorDetail.getInt(1) + " x " +
                        formatRupiah.format(cursorDetail.getInt(2)) + " = " +
                        formatRupiah.format(cursorDetail.getInt(3)) + "\n\n";
            }

            footer += "-------------------------------------------------\n";
            footer += "Total : " + formatRupiah.format(cursorHeader.getInt(2)) + "\n";
            footer += "Bayar : " + formatRupiah.format(cursorHeader.getInt(3)) + "\n";
            footer += "Kembali: " + cursorHeader.getInt(4) + "\n";
            footer += "Terima Kasih\n";
        }

        cursorHeader.close();
        cursorDetail.close();
        database.close();

        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setMediaSize(PrintAttributes.MediaSize.NA_INDEX_4X6); // bisa diubah sesuai printer

        printManager.print(
                "Struk Penjualan",
                new ReceiptPrintAdapter(this, header, detail, footer),
                builder.build()
        );

    }

    private void whatsapp(String nota) {
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursorHeader = database.rawQuery(
                "SELECT p.nota, p.tanggal, p.belanja, p.bayar, p.kembali, c.nm_pelanggan, c.telepon " +
                        "FROM penjualan p " +
                        "LEFT JOIN master_pelanggan c ON c.kd_pelanggan = p.kd_pelanggan " +
                        "WHERE p.nota = ?", new String[]{nota});

        Cursor cursorDetail = database.rawQuery(
                "SELECT nm_barang, jumlah, harga, total " +
                        "FROM penjualan_item WHERE nota = ?", new String[]{nota});




        StringBuilder struk = new StringBuilder();

        if (cursorHeader.moveToFirst()) {
            struk.append("Pelanggan : ").append(cursorHeader.getString(5)).append("\n");
            struk.append("Nota      : ").append(cursorHeader.getString(0)).append("\n");
            struk.append("Tanggal   : ").append(cursorHeader.getString(1)).append("\n");
            struk.append("-------------------------------------------------\n");

            while (cursorDetail.moveToNext()) {
                struk.append(cursorDetail.getString(0)).append("\n")
                        .append(cursorDetail.getInt(1)).append(" x ")
                        .append(formatRupiah.format(cursorDetail.getInt(2))).append(" = ")
                        .append(formatRupiah.format(cursorDetail.getInt(3))).append("\n\n");
            }

            struk.append("-------------------------------------------------\n");
            struk.append("Total   : ").append(formatRupiah.format(cursorHeader.getInt(2))).append("\n");
            struk.append("Bayar   : ").append(formatRupiah.format(cursorHeader.getInt(3))).append("\n");
            struk.append("Kembali : ").append(cursorHeader.getInt(4)).append("\n");
            struk.append("Terima Kasih\n");
        }

        String nomorPelanggan = formatNomor(cursorHeader.getString(6));

        cursorHeader.close();
        cursorDetail.close();
        database.close();

        String pesan = struk.toString();
        kirimWhatsapp(nomorPelanggan, pesan);
    }

    private void kirimWhatsapp(String nomorDb, String pesan) {
        String nomor = formatNomor(nomorDb);

        if (nomor == null || nomor.isEmpty()) {
            // Kalau kosong, munculkan dialog input
            showNomorDialog(pesan);
        } else if (!isNomorValid(nomor)) {
            // Kalau format tidak sesuai, munculkan dialog input
            showNomorDialog(pesan);
        } else {
            // Kalau valid → langsung kirim ke WA
            bukaWhatsapp(nomor, pesan);
        }
    }

    private void bukaWhatsapp(String nomor, String pesan) {
        try {
            String url = "https://wa.me/" + nomor + "?text=" + URLEncoder.encode(pesan, "UTF-8");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal membuka WhatsApp", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNomorDialog(String pesan) {
        AlertDialog
                .Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Masukkan Nomor WhatsApp");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        builder.setView(input);

        builder.setPositiveButton("Kirim", (dialog, which) -> {
            String nomorBaru = input.getText().toString().trim();
            String nomorFormat = formatNomor(nomorBaru);

            if (!isNomorValid(nomorFormat)) {
                Toast.makeText(this, "Nomor tidak valid", Toast.LENGTH_SHORT).show();
            } else {
                bukaWhatsapp(nomorFormat, pesan);
            }
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private boolean isNomorValid(String nomor) {
        return nomor.matches("^(62|0)[0-9]{8,15}$");
    }

    private String formatNomor(String nomor) {
        // buang spasi atau tanda - kalau ada
        nomor = nomor.replaceAll("[^0-9]", "");

        if (nomor.startsWith("0")) {
            return "62" + nomor.substring(1); // ganti 0 awal jadi 62
        }
        return nomor; // kalau sudah format internasional langsung return
    }


}