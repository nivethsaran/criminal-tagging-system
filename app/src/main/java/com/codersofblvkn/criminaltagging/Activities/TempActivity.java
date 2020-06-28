package com.codersofblvkn.criminaltagging.Activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.codersofblvkn.criminaltagging.R;
import com.codersofblvkn.criminaltagging.Utils.Detection;

import java.io.Serializable;

public class TempActivity extends AppCompatActivity implements Serializable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        Detection detection=(Detection)getIntent().getSerializableExtra("detection");
        Log.d("Detection",detection.toString());
    }
}
