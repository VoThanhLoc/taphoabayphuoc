package com.example.taphoabayphuoc.printer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.OutputStream;
import java.util.UUID;

public class BluetoothPrinter {

    private BluetoothSocket socket;
    private OutputStream outputStream;

    public boolean connect(String macAddress) {

        try {

            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

            if (adapter == null)
                return false;

            BluetoothDevice device =
                    adapter.getRemoteDevice(macAddress);

            socket = device.createRfcommSocketToServiceRecord(
                    UUID.fromString(
                            "00001101-0000-1000-8000-00805F9B34FB"));

            adapter.cancelDiscovery();

            socket.connect();

            outputStream = socket.getOutputStream();

            return true;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    public void print(String text) {

        try {

            outputStream.write(text.getBytes("GBK"));

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void disconnect() {

        try {

            if (outputStream != null)
                outputStream.close();

            if (socket != null)
                socket.close();

        } catch (Exception ignored) {

        }

    }

}