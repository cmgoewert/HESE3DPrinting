package com.example.cmgoe.hese3dprinting;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by cmgoe on 10/22/2017.
 */

public class BluetoothThread extends Thread {
    private final BluetoothDevice mmDevice;
    private final BluetoothSocket mmSocket;
    private InputStream in = null;
    private File localFile;
    private OutputStream out = null;
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    public BluetoothThread(BluetoothDevice device, File localFile) {
        mmDevice = device;
        this.localFile = localFile;
        BluetoothSocket tmp = null;
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
                System.out.println("closed socket?");
            } catch (IOException closeException) {
                System.out.println("caught something");
            }
            return;
        }

        //mConnectedThread = new MainActivity.ConnectedThread(mmSocket);
        InputStream tempIn = null;
        OutputStream tempOut = null;
        try {
            tempIn = mmSocket.getInputStream();
            tempOut = mmSocket.getOutputStream();
            System.out.println("it worked?");
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("caught something");
        }

        in = tempIn;
        out = tempOut;

        sendFile(localFile);

//        byte[] buffer = new byte[1024];
//        int begin = 0;
//        int bytes = 0;
//        while (true) {
//            try {
//                bytes += in.read(buffer, bytes, buffer.length - bytes);
//                for(int i = begin; i < bytes; i++) {
//                    if(buffer[i] == "#".getBytes()[0]) {
//                        begin = i + 1;
//                        if(i == bytes - 1) {
//                            bytes = 0;
//                            begin = 0;
//                        }
//                    }
//                }
//                System.out.println(bytes + " || response");
//            } catch (IOException e) {
//                break;
//            }
//        }

    }

    public void cancel() {
        try {
            mmSocket.close();
            System.out.println("file send worked?");
        } catch (IOException e) {
            System.out.println("caught something");
        }
    }

    public void sendFile(File fileToSend){
        byte[] convertedFile = FileToByteConverter.convertFile(fileToSend);
        System.out.println("Sending...");

        try{
            out.write(convertedFile);
            System.out.println("sent file");
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("something failed");
        }
    }

}
