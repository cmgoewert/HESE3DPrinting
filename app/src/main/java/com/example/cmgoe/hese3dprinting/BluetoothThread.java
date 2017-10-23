package com.example.cmgoe.hese3dprinting;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by cmgoe on 10/22/2017.
 */

public class BluetoothThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    public BluetoothThread(BluetoothDevice device) {
        BluetoothSocket tmp = null;
        mmDevice = device;
        try {
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("caught something");
        }
        mmSocket = tmp;
    }
    public void run() {
        //mBluetoothAdapter.cancelDiscovery();
        try {
            mmSocket.connect();
            System.out.println(mmDevice.getName());
        } catch (IOException connectException) {
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                System.out.println("caught something");
            }
            return;
        }

        //mConnectedThread = new MainActivity.ConnectedThread(mmSocket);
    }
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            System.out.println("caught something");
        }
    }

}
