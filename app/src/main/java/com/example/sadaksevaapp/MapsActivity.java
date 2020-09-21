package com.example.sadaksevaapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FirebaseDatabase database;
    DatabaseReference myRef;
        View maps_view;
        ImageButton close;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        maps_view=findViewById(R.id.map_line);
        setUpToolbar(maps_view);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("2_Pothole_Detail");
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent i=new Intent(MapsActivity.this,HomeActivity.class);
        startActivity(i);
        return true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMinZoomPreference(11);

        //Reading Data from Firebase
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final Const_Pothole_Detail MapDataObject = postSnapshot.getValue(Const_Pothole_Detail.class);
                    //MapData.add(MapDataObject);

                    LatLng Plot = new LatLng(MapDataObject.latitude, MapDataObject.longitude);
                    mMap.addMarker(new MarkerOptions().position(Plot)
                            .title(MapDataObject.severity)
                            .snippet(MapDataObject.location)).setTag(MapDataObject);

                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            View view =getLayoutInflater().inflate(R.layout.infowindow, null);

                            final TextView TitleInfo = view.findViewById(R.id.title);
                            final TextView Pothole_desc=view.findViewById(R.id.pothole_desc);
                            final TextView addressInfo = view.findViewById(R.id.address);
                            final ImageView potholeimg= view.findViewById(R.id.potholeimage);
                            final TextView pothole_reporter=view.findViewById(R.id.reporter);

                            Const_Pothole_Detail Mapobj=(Const_Pothole_Detail) marker.getTag();
                            TitleInfo.setText("Pothole Severity : "+marker.getTitle());
                            Pothole_desc.setText(Mapobj.description);
                            pothole_reporter.setText("Reported By : "+Mapobj.namee);
                            addressInfo.setText(marker.getSnippet());
                            Glide.with(getApplicationContext())
                                    .load(Mapobj.urlimage)
                                    .into(potholeimg);

                            return view;

                        }
                    });

                    mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
                        @Override
                        public void onInfoWindowLongClick(Marker marker) {
                            try {
                                Const_Pothole_Detail potholeinfo=(Const_Pothole_Detail) marker.getTag();
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_SUBJECT, "Sadak Seva App");
                                String sAux = "\nWelcome to Sadak Seva App \nA pothole is reported on your way..\n\n";
                                sAux = sAux + "Pothole is at : "+potholeinfo.location+"\n";
                                i.putExtra(Intent.EXTRA_TEXT, sAux);
                                startActivity(Intent.createChooser(i, "choose one"));
                            } catch(Exception e) {
                                //e.toString();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                // Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // Add a marker in Indore and move the camera
        LatLng indore = new LatLng(22.7196, 75.8577);

        //mMap.addMarker(new MarkerOptions().position(indore).title("Welcome to Indore"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(indore));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(indore, 10.0f));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.Appbar);
        AppCompatActivity activity = (AppCompatActivity) (this);
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            ActionBar actionbar = activity.getSupportActionBar();
            assert actionbar != null;
            actionbar.setDisplayHomeAsUpEnabled(true);
            //actionbar.setHomeAsUpIndicator(R.drawable.navigation_drawer_icon);
        }

    }

}
