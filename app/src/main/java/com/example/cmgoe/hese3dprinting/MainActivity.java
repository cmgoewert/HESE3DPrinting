package com.example.cmgoe.hese3dprinting;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    String fileInfoString = "fff";
    StorageMetadata theMetadata = null;
    static final int REQUEST_BT_ENABLE = 1;
    BluetoothAdapter mBluetoothAdapter;
    ArrayList<BluetoothDevice> deviceList;
    Button connectButton;
    BluetoothDevice selectedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference();
        deviceList = new ArrayList<BluetoothDevice>();

        StorageReference model = ref.child("/jigsaw-curved_phallic.STL");

        model.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                fileInfoString = storageMetadata.getPath();
                System.out.println(storageMetadata.getSizeBytes());
            }
        });

        System.out.println();


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

        TextView fileInfo = (TextView)findViewById(R.id.fileInfo);
        fileInfo.setText(fileInfoString);
    }


    private void selectDevice(){
        Dialog dlg = new Dialog(this);
        ListView listView = new ListView(this);
        final String [] deviceNames = new String[deviceList.size()];
        for (int i =0; i<deviceList.size(); i++){
            deviceNames[i] = deviceList.get(i).getName();
        }
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceNames));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            Log.i("Device Selected: ", deviceNames[position]);
            selectedDevice = deviceList.get(position);

        }});
        dlg.setContentView(listView);
        dlg.show();
    }
}
