package com.codersofblvkn.criminaltagging.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codersofblvkn.criminaltagging.R;
import com.codersofblvkn.criminaltagging.Utils.Detection;
import com.codersofblvkn.criminaltagging.Utils.FCMTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ProfileActivity extends AppCompatActivity implements Serializable {

    CircleImageView imgView;
    TextView cid,time,coordinates;
    MapView mapView;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    List<Detection> detections=new ArrayList<Detection>();
    Detection detection;
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


    @SuppressLint("CheckResult")
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

        detection=(Detection)getIntent().getSerializableExtra("detection");
        Observable.fromCallable(() -> {
            Request request = new Request.Builder()
                    .url("http://coders-of-blaviken-api.herokuapp.com/api/detections")
                    .build();
            try {
                OkHttpClient sHttpClient=new OkHttpClient();
                Response response = sHttpClient.newCall(request).execute();
                if(response.isSuccessful())
                {
                    return response.body().string();
                }
                else
                {
                    return null;
                }
            } catch (IOException e) {
                Log.e("Network request", "Failure", e);
            }

            return null;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {

                    if(result!=null)
                    {

                        JSONObject jsonObject=new JSONObject(result);
                        JSONArray jsonArray=jsonObject.getJSONArray("detections");

                        for(int i=0;i<jsonArray.length();i++)
                        {
                            double lat=0,lon=0;
                            JSONObject tDetect=jsonArray.getJSONObject(i);
                            int id=tDetect.getInt("id");
                            int cid_=tDetect.getInt("cid");
                            if(cid_==detection.getCid())
                            {
                                String[] location =tDetect.getString("location").replace("dot",".").split(",");
                                if(location.length!=2)
                                {
                                    lat=0;
                                    lon=0;
                                }
                                else {
                                    lat=Double.parseDouble(location[0].substring(0,7));
                                    lon=Double.parseDouble(location[1].substring(1,8));
                                }
                                String img=tDetect.getString("rsrc");
                                String myDate = tDetect.getString("time_stamp");
                                Date date = sdf.parse(myDate);
                                long time=date.getTime();
                                Detection detection_=new Detection(lat,lon,time,img,id,cid_);
                                detections.add(detection_);
                            }

                        }
                        Collections.sort(detections, new Comparator<Detection>() {
                            @Override
                            public int compare(Detection t1, Detection t2) {
                                return (int)(t2.getTimestamp()-t1.getTimestamp());
                            }
                        });

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
                        coordinates.setText("Latest Location:"+detections.get(0).getLatitude()+ (char)0x00B0 + "N ,"+detections.get(0).getLongitude()+ (char)0x00B0 +"E");

                        Date ld=new Date(detections.get(0).getTimestamp());
                        String ldText=sdf.format(ld);
                        time.setText("Last Detected:"+ldText+" ");

                        mapView.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {

                                for(Detection i:detections)
                                {
                                    Date date=new Date(i.getTimestamp());
                                    String dateText=sdf.format(date);
                                    googleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(i.getLatitude(),i.getLongitude()))
                                            .title("Timestamp:"+dateText));
                                }

                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(detection.getLatitude(),detection.getLongitude()), 5.0f));

                                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15.0f));
                                        marker.showInfoWindow();
                                        return true;
                                    }
                                });

                                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                    @Override
                                    public void onInfoWindowClick(Marker marker) {
                                        double lat=marker.getPosition().latitude;
                                        double lon=marker.getPosition().longitude;
                                        String toParse="geo:"+lat+","+lon;
                                        Uri gmmIntentUri = Uri.parse(toParse);
                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                        mapIntent.setPackage("com.google.android.apps.maps");
                                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                            startActivity(mapIntent);
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(),"Maps Application not installed",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });

                    }
                });


        Log.d("Detection",detection.toString());



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.alert_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        new FCMTask(getApplicationContext()).execute("Criminal Detected, CID:"+ detection.getCid());
        return super.onOptionsItemSelected(item);

    }
}
