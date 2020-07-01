package com.codersofblvkn.criminaltagging.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.codersofblvkn.criminaltagging.Fragments.DetectFragment;
import com.codersofblvkn.criminaltagging.Fragments.ManualEntryFragment;
import com.codersofblvkn.criminaltagging.Fragments.MapsFragment;
import com.codersofblvkn.criminaltagging.R;
import com.codersofblvkn.criminaltagging.Utils.FCMTask;
import com.codersofblvkn.criminaltagging.Utils.ServerKey;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity{

    BottomNavigationView bottomNavigation;
    private static final String TAG = "ALERT";

    public final static String AUTH_KEY_FCM = new ServerKey().getSERVER_KEY();
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE =1002;
    final private String contentType = "application/json";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
    }

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
                                openFragment(MapsFragment.newInstance());
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





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.taskbar_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logout)
        {
            mAuth.signOut();
            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.criminal_alert)
        {
            LayoutInflater li = LayoutInflater.from(getApplicationContext());
            View promptsView = li.inflate(R.layout.custom_dialog, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    MainActivity.this);

            // set alert_dialog.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = promptsView.findViewById(R.id.dialoged);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // get user input and set it to result
                            // edit text
                            String cid=userInput.getText().toString();
                            if(!cid.equals("")||cid!=null)
                            {
                                new FCMTask(getApplicationContext()).execute("Criminal Detected, CID:"+cid);
                            }
                            Toast.makeText(getApplicationContext(), "Entered: "+userInput.getText().toString(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
        else if(item.getItemId()==R.id.settings)
        {

        }
        return super.onOptionsItemSelected(item);
    }
}