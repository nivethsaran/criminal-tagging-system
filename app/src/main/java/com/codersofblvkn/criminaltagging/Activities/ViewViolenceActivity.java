package com.codersofblvkn.criminaltagging.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.codersofblvkn.criminaltagging.R;
import com.codersofblvkn.criminaltagging.Utils.Violence;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewViolenceActivity extends AppCompatActivity {
    TextView textView,textView4;
    private ProgressDialog bar;
    private MediaController ctlr;
    MapView mapView;
    private VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_violence);

        textView=findViewById(R.id.textView);
        textView4=findViewById(R.id.textView4);
        Intent intent=getIntent();
        Violence violence= (Violence) intent.getSerializableExtra("violence");
        String url=violence.getVideoPath();
        mapView=findViewById(R.id.mapView2);
        textView.setText(Integer.toString(violence.getId()));
        textView.setText(violence.getTimestamp().substring(0,12));
        bar=new ProgressDialog(ViewViolenceActivity.this);
        bar.setTitle("Connecting server");
        bar.setMessage("Please Wait... ");
        bar.setCancelable(false);
        bar.show();
        if(bar.isShowing()) {
            videoView = findViewById(R.id.videoView2);
            Uri uri = Uri.parse(url);
            videoView.setVideoURI(uri);
            videoView.start();
            ctlr = new MediaController(this);
            ctlr.setMediaPlayer(videoView);
            videoView.setMediaController(ctlr);
            videoView.requestFocus();
        }
        bar.dismiss();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng coord=new LatLng(violence.getLatitude(),violence.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(coord).title("Violence Detected Area"));
            }
        });

    }
}