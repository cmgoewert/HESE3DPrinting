package com.example.cmgoe.hese3dprinting;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by cmgoe on 11/1/2017.
 */

public class FirebaseInteraction {
    private FirebaseStorage storage;
    private StorageReference ref;

    public FirebaseInteraction() {
        storage = FirebaseStorage.getInstance();
        ref = storage.getReference();

    }

    public File getFile(String path){
        File file = null;
        StorageReference model = ref.child(path);
        try {
            file = File.createTempFile("design-", ".gcode");
            model.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
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
            file = null;
            System.out.println("FAILED");
        }

        return file;
    }

}
