package com.codersofblvkn.criminaltagging.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.codersofblvkn.criminaltagging.Fragments.AboutFragment;
import com.codersofblvkn.criminaltagging.Fragments.DetectFragment;
import com.codersofblvkn.criminaltagging.Fragments.ManualEntryFragment;
import com.codersofblvkn.criminaltagging.R;
import com.codersofblvkn.criminaltagging.Utils.ServerKey;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    private static final String TAG = "ALERT";

    public final static String AUTH_KEY_FCM = new ServerKey().getSERVER_KEY();
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE =1002;
    final private String contentType = "application/json";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        alert = findViewById(R.id.alert);
//        alert.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new FCMTask().execute();
//            }
//        });

        bottomNavigation = findViewById(R.id.bottom_navigation);




        BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_detect:
                                openFragment(DetectFragment.newInstance());
                                return true;
                            case R.id.navigation_me:
                                openFragment(ManualEntryFragment.newInstance("", ""));
                                return true;
                            case R.id.navigation_about:
                                openFragment(AboutFragment.newInstance());
                                return true;
                        }
                        return false;
                    }
                };

        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(DetectFragment.newInstance());
    }


    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    class FCMTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                String result = "";
                URL url = new URL(API_URL_FCM);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject json = new JSONObject();

                json.put("to", "/topics/alert");
                JSONObject info = new JSONObject();
                info.put("title", "notification title"); // Notification title
                info.put("body", "message body"); // Notification
                // body
                json.put("notification", info);
                try {
                    OutputStreamWriter wr = new OutputStreamWriter(
                            conn.getOutputStream());
                    wr.write(json.toString());
                    wr.flush();

                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));

                    String output;
                    System.out.println("Output from Server .... \n");
                    while ((output = br.readLine()) != null) {
                        System.out.println(output);
                    }
                    result = "Success";
                } catch (Exception e) {
                    e.printStackTrace();
                    result = "Failure";
                }
                System.out.println("GCM Notification is sent successfully");
            } catch (Exception e) {

            }
            return null;
        }
    }
}