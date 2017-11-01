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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
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
    private ListView mListView;
    BluetoothDevice selectedDevice;
    File localFile;
    BluetoothThread connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference();
        deviceList = new ArrayList<BluetoothDevice>();

        //StorageReference model = ref.child("/microscope knob.gcode");
        StorageReference model = ref.child("/motortest.gcode");
        //StorageReference model = ref.child("/g28.gcode");
        //StorageReference model = ref.child("PInardenlarged.stl");

        try {
            localFile = File.createTempFile("design-", ".gcode");
            model.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    System.out.println(taskSnapshot.getTotalByteCount() + " HERE IS THE TOTAL BYTE COUNT");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    System.out.println("FAILED");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            localFile = null;
            System.out.println("FAILED");
        }

        System.out.println(localFile.getName() + " FILE NAME");
        System.out.println(localFile.exists());
        System.out.println(localFile.getTotalSpace());
        System.out.println(new File(localFile.toString()).length());


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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Design> designs = new ArrayList<Design>();
        for(int i = 0; i < 50; i++){
            designs.add(new Design("Item Number " + i,"This is a short description for item number "+i,Integer.toString(i)));
        }

        mListView = (ListView) findViewById(R.id.designs_list_view);
        DesignListAdapter adapter = new DesignListAdapter(this, designs);
        mListView.setAdapter(adapter);

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

    }


    private void selectDevice(){

        //Finds list of connected devices, adds devices to ArrayList
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size()> 0){
            for(BluetoothDevice device : pairedDevices){
                deviceList.add(device);
                Log.i("Device Found: ", device.getName());

            }
        }

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
//            ConnectThread mConnectThread = new ConnectThread(selectedDevice);
//            mConnectThread.start();
            connection = new BluetoothThread(selectedDevice, localFile);
            connection.start();
            System.out.println(localFile.exists() + "does it exist");

            dlg.hide();

        }});
        dlg.setContentView(listView);
        dlg.show();


    }

    private void print() {
        System.out.println("Printing...");
        connection.sendFile(localFile);

    }

}
