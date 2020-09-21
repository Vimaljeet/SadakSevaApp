package com.example.sadaksevaapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PhotoDetailActivity extends AppCompatActivity {


    public String x;
    public Button next_button;
    public Button back_btn;

    public String y;
    DatabaseReference rootRef, demoRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    public String location;
    public String tolocation;
    public String severity;
    public String description;
    public String namee;
    public String email;
    LocationManager lm;
    Location current_location;
    Geocoder geocoder;
    double longitude;
    double latitude;
    TextInputEditText editTextLocation, editTextRemark;
    TextInputEditText editTextName, editTextEmail;
    MaterialBetterSpinner Spinner_type_of_location;
    RadioGroup rg;
    View photo_detail_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        final String[] SPINNERLIST = {"Middle of Road", "Sides of Road", "FootPath", "Crosswalks"};

        editTextLocation = (TextInputEditText) findViewById(R.id.editTextLocation);
        Spinner_type_of_location = (MaterialBetterSpinner) findViewById(R.id.Spinner_type_of_location);
        rg = (RadioGroup) findViewById(R.id.rg);
        editTextRemark = (TextInputEditText) findViewById(R.id.editTextRemark);
        editTextName = (TextInputEditText) findViewById(R.id.editTextName);
        photo_detail_view=findViewById(R.id.pothole_detail_view);
        setUpToolbar(photo_detail_view);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        email=mAuth.getCurrentUser().getEmail();

        rootRef = FirebaseDatabase.getInstance().getReference();

        //database reference pointing to demo node
        demoRef = rootRef.child("2_Pothole_Detail");


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


                Toast.makeText(getApplicationContext(),"Location not found",Toast.LENGTH_LONG).show();


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

            final String current_address = ShowLocationInfo(latitude, longitude);

            editTextLocation.setText(current_address);
         // Toast.makeText(getApplicationContext(),"Can not found Location",Toast.LENGTH_SHORT).show();






        MaterialBetterSpinner location_type_spinner = findViewById(R.id.Spinner_type_of_location);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, SPINNERLIST);
// Apply the adapter to the spinner
        location_type_spinner.setAdapter(adapter);

        next_button=findViewById(R.id.next_button);



        Spinner_type_of_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                x=parent.getItemAtPosition(position).toString();

                //    Toast.makeText(getApplicationContext(),x,Toast.LENGTH_LONG).show();
            }
        });
       // editTextEmail.setText("sha@gmail.com");
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                location=editTextLocation.getText().toString();

                tolocation=x;

                int idd=rg.getCheckedRadioButtonId();
                RadioButton button=findViewById(idd);

                severity=button.getText().toString();



                description=editTextRemark.getText().toString();

                namee=editTextName.getText().toString();


                Intent ii=getIntent();
                y=ii.getStringExtra("URI");
                SharedPreferences pref = getApplicationContext().getSharedPreferences("My", 0); // 0 - for private mode
                String ur=pref.getString("URI", null);

                Const_Pothole_Detail const_pothole_detail=new Const_Pothole_Detail(location,tolocation,severity,description,namee,ur,latitude,longitude,email);
                demoRef.push().setValue(const_pothole_detail);
                Toast t=Toast.makeText(getApplicationContext(),"saved successfully",Toast.LENGTH_SHORT);
                t.show();




                Intent i=new Intent(PhotoDetailActivity.this, SummaryActivity.class);

                i.putExtra("location", location);
                i.putExtra("tolocation", tolocation);
                i.putExtra("severity", severity);
                i.putExtra("description", description);
                i.putExtra("namee", namee);
                i.putExtra("img",ur);

                startActivity(i);
            }
        });

        back_btn = findViewById(R.id.back_button);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(PhotoDetailActivity.this,PhotoActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.severity_high:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.severity_medium:
                if (checked)
                    // Ninjas rule
                    break;
            case R.id.severity_low:
                if (checked)
                    // Ninjas rule
                    break;
        }
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

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.Appbar);
        AppCompatActivity activity = (AppCompatActivity)(this);
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            ActionBar actionbar = activity.getSupportActionBar();
            assert actionbar != null;
            actionbar.setDisplayHomeAsUpEnabled(true);
            //actionbar.setHomeAsUpIndicator(R.drawable.navigation_drawer_icon);

        }
}
}
