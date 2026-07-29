package com.insoft.laris.utils;



import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.insoft.laris.admin.pembayaran.Pembayaran;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class BluetoothPrinter58mm {

    private static final UUID UUID_SPP =
            UUID.fromString(
                    "00001101-0000-1000-8000-00805F9B34FB"
            );

    private static final ExecutorService EXECUTOR =
            Executors.newSingleThreadExecutor();

    private BluetoothPrinter58mm() {
    }

    public interface PrintCallback {
        void onSuccess();

        void onError(String pesan);
    }

    @SuppressLint("MissingPermission")
    public static void print(
            BluetoothDevice printer,
            Pembayaran pembayaran,
            PrintCallback callback
    ) {
        EXECUTOR.execute(() -> {
            BluetoothSocket socket = null;
            OutputStream output = null;

            try {
                socket = printer.createRfcommSocketToServiceRecord(
                        UUID_SPP
                );

                socket.connect();

                output = socket.getOutputStream();

                // ESC @ = inisialisasi printer
                output.write(new byte[]{
                        0x1B, 0x40
                });

                // Rata kiri
                output.write(new byte[]{
                        0x1B, 0x61, 0x00
                });

                String struk =
                        ReceiptPembayaranUtils
                                .buatTeksStruk(pembayaran);

                Charset charset;

                try {
                    charset = Charset.forName("CP437");
                } catch (Exception e) {
                    charset = Charset.defaultCharset();
                }

                output.write(struk.getBytes(charset));

                // Feed beberapa baris
                output.write(new byte[]{
                        0x0A, 0x0A, 0x0A
                });

                output.flush();

                callback.onSuccess();

            } catch (Exception e) {
                String pesan = e.getLocalizedMessage();

                callback.onError(
                        pesan == null
                                ? "Tidak dapat mencetak struk"
                                : pesan
                );

            } finally {
                if (output != null) {
                    try {
                        output.close();
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
}