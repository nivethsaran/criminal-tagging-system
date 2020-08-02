package com.codersofblvkn.criminaltagging.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.codersofblvkn.criminaltagging.R;
import com.codersofblvkn.criminaltagging.Utils.Violence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewViolenceActivity extends AppCompatActivity {
    TextView textView,textView4;
    MapView mapView;
    Button button;
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
        setContentView(R.layout.activity_view_violence);

        textView=findViewById(R.id.textView);
        textView4=findViewById(R.id.textView4);
        Intent intent=getIntent();
        Violence violence= (Violence) intent.getSerializableExtra("violence");
        String url=violence.getVideoPath();
        mapView=findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        String ids=violence.getId()+" ";
        textView.setText(ids);
        textView4.setText(violence.getTimestamp().substring(0,12));
        button=findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ViewViolenceActivity.this,VideoActivity.class);
                intent.putExtra("video",violence.getVideoPath());
                startActivity(intent);
            }
        });

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng coord=new LatLng(violence.getLatitude(),violence.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(coord).title("Violence Detected Area"));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coord, 5.0f));

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15.0f));
                        marker.showInfoWindow();
                        return true;
                    }
                });
            }
        });




    }
}