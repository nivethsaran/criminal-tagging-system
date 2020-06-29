package com.codersofblvkn.criminaltagging.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codersofblvkn.criminaltagging.R;
import com.codersofblvkn.criminaltagging.Utils.Detection;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements Serializable {

    CircleImageView imgView;
    TextView cid,time,coordinates;
    MapView mapView;
    LatLng cam = new LatLng(11, 77);
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgView=findViewById(R.id.profile_image);
        cid=findViewById(R.id.cid);
        time=findViewById(R.id.time);
        coordinates=findViewById(R.id.coordinates);
        mapView=findViewById(R.id.mapView);

        mapView.onCreate(savedInstanceState);




        Detection detection=(Detection)getIntent().getSerializableExtra("detection");
        Log.d("Detection",detection.toString());

        CircularProgressDrawable circularProgressDrawable=new CircularProgressDrawable(ProfileActivity.this);
        circularProgressDrawable.setStrokeWidth(5);
        circularProgressDrawable.setCenterRadius(30);
        circularProgressDrawable.start();
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(circularProgressDrawable)
                .error(R.mipmap.ic_launcher_round);
        Glide.with(ProfileActivity.this).load(detection.getImg()).apply(options).into(imgView);

        cid.setText("Criminal ID:"+detection.getCid());
        coordinates.setText(detection.getLatitude()+" "+detection.getLongitude());
        time.setText(detection.getTimestamp()+" ");

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(detection.getLatitude(),detection.getLongitude()))
                        .title("CID:"+detection.getCid()));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(detection.getLatitude(),detection.getLongitude()), 15.0f));
            }
        });

    }
}
