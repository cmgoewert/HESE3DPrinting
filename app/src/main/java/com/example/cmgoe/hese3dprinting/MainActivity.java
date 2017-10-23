package com.example.cmgoe.hese3dprinting;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    String fileInfoString;
    StorageMetadata theMetadata = null;
    static final int REQUEST_BT_ENABLE = 1;
    BluetoothAdapter mBluetoothAdapter;
    ArrayList<BluetoothDevice> deviceList;
    Button connectButton;
    Button printButton;
    BluetoothDevice selectedDevice;
    ConnectedThread mConnectedThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference();
        deviceList = new ArrayList<BluetoothDevice>();

        StorageReference model = ref.child("/jigsaw-curved_phallic.STL");

//        model.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
//            @Override
//            public void onSuccess(StorageMetadata storageMetadata) {
//                fileInfoString = storageMetadata.getPath();
//                System.out.println(storageMetadata.getSizeBytes());
//            }
//        });




        //Enable bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null){
            Toast.makeText(this, "This device does not support Bluetooth", Toast.LENGTH_LONG).show();
        }else{
            //Checks if bluetooth is enabled and enables it if it's not enabled
            if(!mBluetoothAdapter.isEnabled()){
                Intent bluetoothStartIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(bluetoothStartIntent, REQUEST_BT_ENABLE);
            }
        }


        //Finds list of connected devices, adds devices to ArrayList
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
         if(pairedDevices.size()> 0){
             for(BluetoothDevice device : pairedDevices){
                 deviceList.add(device);
                 Log.i("Device Found: ", device.getName());
             }
         }



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectButton = (Button) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDevice();
            }
        });

        printButton = (Button) findViewById(R.id.print_button);
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                print();
            }
        });

        TextView fileInfo = (TextView)findViewById(R.id.fileInfo);
        fileInfo.setText(fileInfoString);
    }


    private void selectDevice(){
        final Dialog dlg = new Dialog(this);
        ListView listView = new ListView(this);
        final String [] deviceNames = new String[deviceList.size()];
        for (int i =0; i<deviceList.size(); i++){
            deviceNames[i] = deviceList.get(i).getName();
        }
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceNames));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            Log.i("Device Selected: ", deviceNames[position]);
            selectedDevice = deviceList.get(position);
            ConnectThread mConnectThread = new ConnectThread(selectedDevice);
            mConnectThread.start();
            dlg.hide();

        }});
        dlg.setContentView(listView);
        dlg.show();


    }

    private void print() {
        System.out.println("Printing...");
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream in;
        private final OutputStream out;

        public ConnectedThread(BluetoothSocket socket){
            mmSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;
            try {
                tempIn = socket.getInputStream();
                tempOut = socket.getOutputStream();
                System.out.println("it worked?");
            } catch (IOException e){
                e.printStackTrace();
                System.out.println("caught something");
            }

            in = tempIn;
            out = tempOut;
        }

    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        public ConnectThread(BluetoothDevice device) {
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
            mBluetoothAdapter.cancelDiscovery();
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

            mConnectedThread = new ConnectedThread(mmSocket);
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                System.out.println("caught something");
            }
        }
    }

}
