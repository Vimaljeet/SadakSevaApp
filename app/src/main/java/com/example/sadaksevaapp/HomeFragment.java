package com.example.sadaksevaapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment
        {
    private DrawerLayout mDrawerLayout;
    private android.widget.ShareActionProvider mShareActionProvider;
            private FirebaseAuth mAuth;
            private FirebaseFirestore firebaseFirestore;
            private String emailid_currentuser;
            FirebaseDatabase database;
            DatabaseReference myRef;
            private int count;
            //recycler view
            private RecyclerView recyclerView;
            private RecyclerView.LayoutManager mLayoutManager;
            private ImageButton imageButton;
            private ArrayList<Const_Pothole_Detail> MapData;
            private TextView spotted;
            private TextView spotted_indore;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        setUpToolbar(view);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        if(mAuth.getCurrentUser()!=null) {
            emailid_currentuser = mAuth.getCurrentUser().getEmail();
        }
        spotted=view.findViewById(R.id.spotted);
        spotted_indore=view.findViewById(R.id.spottedhere);

        imageButton=view.findViewById(R.id.horizontal_view);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),PhotoActivity.class);
                startActivity(i);
            }
        });

        MapData=new ArrayList<Const_Pothole_Detail>();

        recyclerView = (RecyclerView) view.findViewById(R.id.pothole_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        // Optionally customize the position you want to default scroll to
        layoutManager.scrollToPosition(0);
        // Attach layout manager to the RecyclerView
        recyclerView.setLayoutManager(layoutManager);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("2_Pothole_Detail");


//Reading Data from Firebase
        myRef.orderByChild("email").equalTo(emailid_currentuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final Const_Pothole_Detail MapDataObject = postSnapshot.getValue(Const_Pothole_Detail.class);
                    MapData.add(MapDataObject);
                   //Toast.makeText(getContext(),MapDataObject.namee,Toast.LENGTH_LONG).show();

                    //creating adapter
                    PotholeListAdapter adapter = new PotholeListAdapter(getContext(), MapData);

                    //adding adapter to recyclerview
                    recyclerView.setAdapter(adapter);

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                // Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    long count = dataSnapshot.getChildrenCount();

                    spotted.setText(String.valueOf(count));
                    spotted_indore.setText(String.valueOf(count));
                }
            }

            @Override
            public void onCancelled (@NonNull DatabaseError databaseError){

            }

        });



        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

            private void setUpToolbar(View view) {
                Toolbar toolbar = view.findViewById(R.id.Appbar);
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                if (activity != null) {
                    activity.setSupportActionBar(toolbar);
                    ActionBar actionbar = activity.getSupportActionBar();
                    assert actionbar != null;
                    actionbar.setDisplayHomeAsUpEnabled(true);
                    actionbar.setHomeAsUpIndicator(R.drawable.navigation_drawer_icon);

                }
            }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDrawerLayout = getView().findViewById(R.id.drawer_layout);


        NavigationView navigationView = getView().findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        switch (menuItem.getItemId()) {
                            case R.id.nav_home: {
                                HomeFragment home = new HomeFragment();
                                (Objects.requireNonNull(getActivity())).getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.main_frame, home).commit();
                                break;
                            }
                            case R.id.nav_profile: {
                                (Objects.requireNonNull(getActivity())).getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.main_frame, new Profile()).commit();
                                break;
                            }
                            case R.id.nav_share: {
                                // Fetch and store ShareActionProvider
                                if(menuItem!=null){

                                    try {
                                        Intent i = new Intent(Intent.ACTION_SEND);
                                        i.setType("text/plain");
                                        i.putExtra(Intent.EXTRA_SUBJECT, "Sadak Seva");
                                        String sAux = "\nLet me recommend you this application\n\n";
                                        sAux = sAux + "https://play.google.com/store/apps/details?id=the.package.id \n\n";
                                        i.putExtra(Intent.EXTRA_TEXT, sAux);
                                        startActivity(Intent.createChooser(i, "choose one"));
                                    } catch(Exception e) {
                                        //e.toString();
                                    }
                                }


                                // Return true to display menu
                                return true;

                            }
                            case R.id.nav_map: {

                                Intent i=new Intent(getActivity(),MapsActivity.class);
                                startActivity(i);
                                getActivity().finish();
                                break;
                            }
                            case R.id.nav_policy: {

                                (Objects.requireNonNull(getActivity())).getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.main_frame, new Privacy_policy()).commit();
                                break;

                            }
                            case R.id.nav_about:{
                                (getActivity()).getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.main_frame, new about_us()).commit();
                                break;
                            }
                            case R.id.nav_logout:
                            {
                                logOut();
                            }
                        }

                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            startActivity(Intent.createChooser(shareIntent, "Share via"));
            mShareActionProvider.setShareIntent(shareIntent);

        }
    }

            private void logOut() {

                mAuth.signOut();
                sendToLogin();
            }

            private void sendToLogin() {

                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginIntent);
                getActivity().finish();
            }
}
