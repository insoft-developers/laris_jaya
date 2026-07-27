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
import android.print.pdf.PrintedPdfDocument;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MasterBarangPrintAdapter
            extends PrintDocumentAdapter {

    private final Context context;
    private final List<String> barisCetak;

    private PrintAttributes printAttributes;

    /*
     * Jumlah baris per halaman.
     * Sesuaikan jika hasil terpotong.
     */
    private static final int BARIS_PER_HALAMAN = 70;

    public MasterBarangPrintAdapter(
            Context context,
            ArrayList<String> barisCetak
    ) {
        this.context = context;
        this.barisCetak = barisCetak;
    }

    @Override
    public void onLayout(
            PrintAttributes oldAttributes,
            PrintAttributes newAttributes,
            CancellationSignal cancellationSignal,
            LayoutResultCallback callback,
            Bundle extras
    ) {
        printAttributes = newAttributes;

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        int jumlahHalaman = hitungJumlahHalaman();

        PrintDocumentInfo info =
                new PrintDocumentInfo.Builder(
                        "master_barang_58mm.pdf"
                )
                        .setContentType(
                                PrintDocumentInfo.CONTENT_TYPE_DOCUMENT
                        )
                        .setPageCount(jumlahHalaman)
                        .build();

        boolean berubah =
                !newAttributes.equals(oldAttributes);

        callback.onLayoutFinished(
                info,
                berubah
        );
    }

    @Override
    public void onWrite(
            PageRange[] pages,
            ParcelFileDescriptor destination,
            CancellationSignal cancellationSignal,
            WriteResultCallback callback
    ) {
        PrintedPdfDocument document =
                new PrintedPdfDocument(
                        context,
                        printAttributes
                );

        try {
            int jumlahHalaman = hitungJumlahHalaman();

            for (int halaman = 0;
                 halaman < jumlahHalaman;
                 halaman++) {

                if (cancellationSignal.isCanceled()) {
                    callback.onWriteCancelled();
                    document.close();
                    return;
                }

                if (!halamanDipilih(
                        pages,
                        halaman
                )) {
                    continue;
                }

                PdfDocument.Page page =
                        document.startPage(halaman);

                gambarHalaman(
                        page,
                        halaman
                );

                document.finishPage(page);
            }

            document.writeTo(
                    new FileOutputStream(
                            destination.getFileDescriptor()
                    )
            );

            callback.onWriteFinished(pages);

        } catch (IOException e) {
            callback.onWriteFailed(
                    e.getMessage()
            );

        } finally {
            document.close();
        }
    }

    private void gambarHalaman(
            PdfDocument.Page page,
            int nomorHalaman
    ) {
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTypeface(
                Typeface.create(
                        Typeface.MONOSPACE,
                        Typeface.NORMAL
                )
        );

        /*
         * Ukuran yang cocok untuk kertas 58 mm:
         * normal = 7.5f
         * judul  = 9f
         */
        float ukuranNormal = 7.5f;
        float ukuranJudul = 9f;

        float posisiX = 5f;
        float posisiY = 12f;
        float tinggiBaris = 10f;

        int indexAwal = nomorHalaman * BARIS_PER_HALAMAN;

        int indexAkhir = Math.min(
                indexAwal + BARIS_PER_HALAMAN,
                barisCetak.size()
        );

        for (int i = indexAwal; i < indexAkhir; i++) {
            String teks = barisCetak.get(i);

            boolean teksTebal =
                    teks.contains("DAFTAR MASTER BARANG")
                            || teks.contains("TOTAL JENIS")
                            || teks.contains("TOTAL SELURUH")
                            || teks.contains("AKHIR DAFTAR");

            if (teksTebal) {
                paint.setTypeface(
                        Typeface.create(
                                Typeface.MONOSPACE,
                                Typeface.NORMAL
                        )
                );

                paint.setTextSize(ukuranJudul);

            } else {
                paint.setTypeface(
                        Typeface.create(
                                Typeface.MONOSPACE,
                                Typeface.NORMAL
                        )
                );

                paint.setTextSize(ukuranNormal);
            }

            /*
             * Mengecilkan teks otomatis jika melebihi
             * lebar kertas.
             */
            float lebarMaksimal =
                    page.getInfo().getPageWidth() - 10f;

            float ukuranAwal = paint.getTextSize();

            while (
                    paint.measureText(teks) > lebarMaksimal
                            && paint.getTextSize() > 6f
            ) {
                paint.setTextSize(
                        paint.getTextSize() - 0.5f
                );
            }

            canvas.drawText(
                    teks,
                    posisiX,
                    posisiY,
                    paint
            );

            /*
             * Kembalikan ukuran font setelah
             * pengecilan otomatis.
             */
            paint.setTextSize(ukuranAwal);

            posisiY += tinggiBaris;
        }
    }

    private int hitungJumlahHalaman() {
        if (barisCetak == null
                || barisCetak.isEmpty()) {
            return 1;
        }

        return (int) Math.ceil(
                (double) barisCetak.size()
                        / BARIS_PER_HALAMAN
        );
    }

    private boolean halamanDipilih(
            PageRange[] pageRanges,
            int halaman
    ) {
        for (PageRange range : pageRanges) {
            if (
                    halaman >= range.getStart()
                            && halaman <= range.getEnd()
            ) {
                return true;
            }
        }

        return false;
    }
}

