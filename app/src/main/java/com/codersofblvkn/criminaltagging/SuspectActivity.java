package com.codersofblvkn.criminaltagging;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Intent.ACTION_PICK;

public class SuspectActivity extends AppCompatActivity {
    Button button_capture,button_choose;
    ImageView imageView;
    String mCurrentPhotoPath;
    private static final int PIC_ID=1001;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE =1002;
    private static final int PICK_IMAGE_REQUEST=1003;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_capture=findViewById(R.id.button_capture);
        button_choose=findViewById(R.id.button_choose);
        imageView=findViewById(R.id.criminal_image_iv);

        if (ContextCompat.checkSelfPermission(SuspectActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SuspectActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(SuspectActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            }
        } else {

        }

        button_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();}
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }

                if (photoFile != null) {
                    Log.d("mylog", "Photofile not null");
                    Uri photoURI = FileProvider.getUriForFile(SuspectActivity.this,
                            "com.codersofblvkn.criminaltagging.provider",
                            photoFile);
                    Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(camera_intent, PIC_ID);
                }
            }
        });

        button_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(ACTION_PICK);
                intent.setType("image/*");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE_REQUEST);
            }
        });


    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == PIC_ID && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            imageView.setImageBitmap(bitmap);
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            try {
//                Log.d("Blaviken","Inside");
                Uri uri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_CANCELED) {
            Log.d("Blaviken","Error");
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        boolean success = true;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "SUSPECT_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/SuspectImages/");
        if (!storageDir.exists()) {
            //Toast.makeText(MainActivity.this, "Directory Does Not Exist, Create It", Toast.LENGTH_SHORT).show();
            success = storageDir.mkdir();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d("mylog", "Path: " + mCurrentPhotoPath);
        return image;
    }
}