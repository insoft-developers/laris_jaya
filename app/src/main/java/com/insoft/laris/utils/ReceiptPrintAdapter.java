package com.insoft.laris.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;

import java.io.FileOutputStream;
import java.io.IOException;

public class ReceiptPrintAdapter extends PrintDocumentAdapter {
    private Context context;
    private String header, detail, footer;
    private PrintAttributes printAttributes;

    public ReceiptPrintAdapter(Context context, String header, String detail, String footer) {
        this.context = context;
        this.header = header;
        this.detail = detail;
        this.footer = footer;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback callback, Bundle extras) {
        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }
        PrintDocumentInfo info = new PrintDocumentInfo.Builder("receipt.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .build();
        callback.onLayoutFinished(info, true);
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal,
                        WriteResultCallback callback) {

        // --- Hitung tinggi yang dibutuhkan ---
        int y = 40;
        y += 40;
        Paint paintTitle = new Paint();
        paintTitle.setTextSize(52);
        paintTitle.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));

        Paint paintHeader = new Paint();
        paintHeader.setTextSize(44);
        paintHeader.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));

        Paint paintNamaBarang = new Paint();
        paintNamaBarang.setTextSize(40);
        paintNamaBarang.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));

        Paint paintDetail = new Paint();
        paintDetail.setTextSize(39);
        paintDetail.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));



        // Simulasi perhitungan tinggi (tanpa gambar dulu)
        y += 40 + (int)(paintTitle.descent() - paintTitle.ascent() + 20);

        for (String line : header.split("\n")) {
            y += (int)(paintHeader.descent() - paintHeader.ascent());
        }

        for (String line : detail.split("\n")) {
            if (line.split("\\|").length == 3) {
                y += (int)(paintNamaBarang.descent() - paintNamaBarang.ascent());
                y += (int)(paintDetail.descent() - paintDetail.ascent() + 10);
            } else {
                y += (int)(paintDetail.descent() - paintDetail.ascent());
            }
        }

        y += 30;

        for (String line : footer.split("\n")) {
            if (!line.trim().isEmpty()) {
                y += (int)(paintTitle.descent() - paintTitle.ascent());
            }
        }

        y += 150;

        int contentHeight = y;   // total tinggi konten
        int pageWidth = 700;     // sesuaikan lebar (px)

        // --- Buat PdfDocument dengan tinggi sesuai isi ---
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, contentHeight, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Reset posisi Y untuk menggambar beneran
        y = 40;

        // Cetak nama toko
        y += 40;
        String namaToko = "TOKO LARIS JAYA";
        float textWidth = paintTitle.measureText(namaToko);
        canvas.drawText(namaToko, 70, y, paintTitle);
        y += paintTitle.descent() - paintTitle.ascent() + 2;

        y += 1;
        String alamatToko = "Sei Kalam Dusun 6";
        canvas.drawText(alamatToko, 70, y, paintNamaBarang);
        y += paintNamaBarang.descent() - paintNamaBarang.ascent() + 2;

        y += 1;
        String hp = "No HP. 0822 7375 7110";
        canvas.drawText(hp, 70, y, paintNamaBarang);
        y += paintNamaBarang.descent() - paintNamaBarang.ascent() + 20;

        y += 10;

        // Header
        for (String line : header.split("\n")) {
            canvas.drawText(line, 10, y, paintHeader);
            y += paintHeader.descent() - paintHeader.ascent();
        }

        // Detail
        for (String line : detail.split("\n")) {
            String[] parts = line.split("\\|");
            if (parts.length == 3) {
                String namaBarang = parts[0];
                String qtyHarga   = parts[1];
                String total      = parts[2];

                canvas.drawText(namaBarang, 10, y, paintNamaBarang);
                y += paintNamaBarang.descent() - paintNamaBarang.ascent();

                canvas.drawText(qtyHarga, 10, y, paintDetail);
                float textWidthRight = paintDetail.measureText(total);
                canvas.drawText(total, pageWidth - textWidthRight - 10, y, paintDetail);

                y += paintDetail.descent() - paintDetail.ascent() + 10;
            } else {
                canvas.drawText(line, 10, y, paintDetail);
                y += paintDetail.descent() - paintDetail.ascent();
            }
        }

        y += 30;

        for (String line : footer.split("\n")) {
            if (!line.trim().isEmpty()) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    // rata kiri dan kanan
                    drawTextAligned(canvas, paintHeader, parts[0], parts[1], y);
                } else {
                    // default rata kiri
                    canvas.drawText(line, 10, y, paintHeader);
                }
                y += paintHeader.descent() - paintHeader.ascent();
            }
        }


        y += 150;

        pdfDocument.finishPage(page);

        try {
            pdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pdfDocument.close();
        }

        callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
    }


    private void drawTextAligned(Canvas canvas, Paint paint, String left, String right, int y) {
        int pageWidth = canvas.getWidth();
        float textWidthRight = paint.measureText(right);

        // kiri
        canvas.drawText(left, 10, y, paint);

        // kanan
        canvas.drawText(right, pageWidth - textWidthRight - 10, y, paint);
    }



}

