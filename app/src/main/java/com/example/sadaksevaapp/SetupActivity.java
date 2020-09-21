package com.example.sadaksevaapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class SetupActivity extends AppCompatActivity {

    //private Uri mainImageURI = null;

    private String user_id;

    private boolean isChanged = false;

    private EditText setupName;
    private EditText setupMobile;
    private EditText setupAddress;
    private Button setupBtn;
    private ProgressBar setupProgress;
    LocationManager lm;
    Geocoder geocoder;
    double longitude;
    double latitude;

    //private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Toolbar setupToolbar = findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Setup");

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        final Location current_location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitude = current_location.getLongitude();
        latitude = current_location.getLatitude();

        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();

                //Toast.makeText(getApplicationContext(),Long,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

        final String current_address=ShowLocationInfo(latitude,longitude);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        //storageReference = FirebaseStorage.getInstance().getReference();


        setupName = findViewById(R.id.setup_name);
        setupMobile = findViewById(R.id.setup_mobile);
        setupAddress = findViewById(R.id.setup_address);
        setupBtn = findViewById(R.id.setup_btn);
        setupProgress = findViewById(R.id.setup_progress);

        setupProgress.setVisibility(View.VISIBLE);
        setupBtn.setEnabled(false);

        setupAddress.setText(current_address);
        Toast.makeText(getApplicationContext(),"Address is auto-detected..",Toast.LENGTH_SHORT).show();

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    if(task.getResult().exists()){

                        String name = task.getResult().getString("name");
                        String mobile = task.getResult().getString("mobile");
                        String address = task.getResult().getString("address");

                        setupName.setText(name);
                        setupMobile.setText(mobile);

                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyMobile", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("mobb", setupMobile.getText().toString());
                        editor.commit();


                        setupAddress.setText(address);
                    }

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                }

                setupProgress.setVisibility(View.INVISIBLE);
                setupBtn.setEnabled(true);

            }
        });


        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user_name = setupName.getText().toString();
                final String user_mobile = setupMobile.getText().toString();
                final String user_address = setupAddress.getText().toString();

                if (!TextUtils.isEmpty(user_name) && !TextUtils.isEmpty(user_mobile) && !TextUtils.isEmpty(user_address)) {

                    setupProgress.setVisibility(View.VISIBLE);

                    if (!isChanged) {


                        storeFirestore(null, user_name, user_mobile, user_address);


                    } else {

                        user_id = firebaseAuth.getCurrentUser().getUid();

                    }

                }

            }

        });

    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String user_name, String user_mobile, String user_address) {

        Uri download_uri;

        if (task != null) {

            //download_uri = task.getResult().getUploadSessionUri();
            storeFirestore(task, user_name, user_mobile, user_address);

        } else {

            //download_uri = mainImageURI;

        }

        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", user_name);
        userMap.put("mobile", user_mobile);
        userMap.put("address", user_address);

        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(SetupActivity.this, "The user Settings are updated.", Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(SetupActivity.this, HomeActivity.class);
                    startActivity(mainIntent);
                    finish();

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "(FIRESTORE Error LEoooo) : " + error, Toast.LENGTH_LONG).show();

                }

                setupProgress.setVisibility(View.INVISIBLE);

            }
        });

    }
        public String ShowLocationInfo(double Lat, double Long)
        {
            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses;
            String fulladdress="";
            try {

                //addresses = geocoder.getFromLocation(22.7541519,75.8873393,1);
                addresses = geocoder.getFromLocation(Lat,Long,1);
                String address = addresses.get(0).getAddressLine(0);

                fulladdress = address; //+ ", " + area + ", " + city + ", " + country + ", " + postalcode;

            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return fulladdress;

        }

    }

