package com.example.cmgoe.hese3dprinting;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Jack Quinn on 10/18/2017.
 * This class is designed to handle the bluetooth connection aspect of the application.
 * See the documentation at: https://developer.android.com/guide/topics/connectivity/bluetooth.html#ManagingAConnection.
 */

public class BluetoothService extends Thread {
    private final BluetoothServerSocket mBluetoothServerSocket;
    private BluetoothAdapter mBluetoothAdapter;
    private UUID ourUUID;

    public BluetoothService(){
        BluetoothServerSocket temp = null;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ourUUID = UUID.fromString("8a08b644-b457-11e7-abc4-cec278b6b50a");

        try{
            temp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("3D Printing Client", ourUUID);
        }catch (IOException e){
            Log.i("BluetoothService: ","Socket's listen() method failed");
            System.out.println("caught here");
            e.printStackTrace();
        }

        mBluetoothServerSocket = temp;
    }

    public void run(){
        BluetoothSocket socket = null;

        while(true) {
            try {
                socket = mBluetoothServerSocket.accept();
            } catch (IOException e) {
                Log.i("BluetoothService: ", "Socket's accept() method failed");
                break;
            }


            if (socket != null) {
                //The connection has been accepted
                //TODO: Manage connection in another thread

                try {
                    mBluetoothServerSocket.close();
                } catch (IOException e) {
                    Log.i("BluetoothService: ", "close() method failed in run() method");
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void cancel(){
        try{
            mBluetoothServerSocket.close();
        }catch (IOException e){
            Log.i("BluetoothService: ", "Failed to close socket in cancel() method");
            e.printStackTrace();
        }
    }
}
