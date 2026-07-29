package com.insoft.laris.utils;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class BluetoothPrinter58mm2 {

    private static final UUID UUID_SPP =
            UUID.fromString(
                    "00001101-0000-1000-8000-00805F9B34FB"
            );

    private static final ExecutorService EXECUTOR =
            Executors.newSingleThreadExecutor();

    private BluetoothPrinter58mm2() {
    }

    public interface PrintCallback {
        void onSuccess();

        void onError(String pesan);
    }

    @SuppressLint("MissingPermission")
    public static void printText(
            BluetoothDevice printer,
            String teks,
            PrintCallback callback
    ) {
        EXECUTOR.execute(() -> {
            BluetoothSocket socket = null;
            OutputStream outputStream = null;

            try {
                socket =
                        printer.createRfcommSocketToServiceRecord(
                                UUID_SPP
                        );

                socket.connect();

                outputStream =
                        socket.getOutputStream();

                /*
                 * ESC @
                 * Reset printer.
                 */
                outputStream.write(
                        new byte[]{
                                0x1B,
                                0x40
                        }
                );

                /*
                 * ESC a 0
                 * Posisi teks rata kiri.
                 */
                outputStream.write(
                        new byte[]{
                                0x1B,
                                0x61,
                                0x00
                        }
                );

                Charset charset;

                try {
                    charset =
                            Charset.forName("CP437");
                } catch (Exception e) {
                    charset =
                            Charset.defaultCharset();
                }

                byte[] dataPrint = teks.getBytes(charset);

                kirimBertahap(
                        outputStream,
                        dataPrint
                );

                outputStream.flush();

                /*
                 * Beri kesempatan printer menyelesaikan
                 * seluruh data sebelum socket ditutup.
                 */
                Thread.sleep(1500);

                callback.onSuccess();

            } catch (Exception e) {
                String pesan =
                        e.getLocalizedMessage();

                callback.onError(
                        pesan == null
                                ? "Tidak dapat mencetak struk"
                                : pesan
                );

            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException ignored) {
                    }
                }

                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        });
    }

    private static void kirimBertahap(
            OutputStream outputStream,
            byte[] data
    ) throws IOException, InterruptedException {

        /*
         * Printer thermal Bluetooth 58 mm umumnya
         * lebih stabil dengan ukuran 128–512 byte.
         */
        final int ukuranPotongan = 256;

        int posisi = 0;

        while (posisi < data.length) {
            int jumlahData = Math.min(
                    ukuranPotongan,
                    data.length - posisi
            );

            outputStream.write(
                    data,
                    posisi,
                    jumlahData
            );

            outputStream.flush();

            posisi += jumlahData;

            /*
             * Beri waktu buffer printer memproses data.
             */
            Thread.sleep(80);
        }
    }
}