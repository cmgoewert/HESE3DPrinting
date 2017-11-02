package com.example.cmgoe.hese3dprinting.Interface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.cmgoe.hese3dprinting.R;

/**
 * Created by cmgoe on 11/2/2017.
 */

public class DesignDetailActivity extends AppCompatActivity {
    private TextView posText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listitem);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);

        posText = (TextView) findViewById(R.id.my_textview);

        posText.setText(Integer.toString(position));

    }
}
