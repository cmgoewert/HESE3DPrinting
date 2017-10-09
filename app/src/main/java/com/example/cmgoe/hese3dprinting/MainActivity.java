package com.example.cmgoe.hese3dprinting;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {
    String fileInfoString = "";
    StorageMetadata theMetadata = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference();


        StorageReference model = ref.child("/jigsaw-curved_phallic.STL");

        model.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                fileInfoString = storageMetadata.getPath();
                System.out.println(storageMetadata.getSizeBytes());
            }
        });

        System.out.println();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView fileInfo = (TextView)findViewById(R.id.fileInfo);
        fileInfo.setText(fileInfoString);
    }
}
