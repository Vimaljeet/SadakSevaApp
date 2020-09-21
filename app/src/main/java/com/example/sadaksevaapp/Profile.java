package com.example.sadaksevaapp;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sadaksevaapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends android.support.v4.app.Fragment {
    TextView textName;
    TextView textEmail;
    TextView textEnrollment;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    TextView TextName,Textaddress,Textphone,Textemail;

    SharedPreferences msharedpreferences;

    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_profile, container, false);
        textName=(TextView)v.findViewById(R.id.TextName);
        textEmail=(TextView)v.findViewById(R.id.TextEmail);
        TextName=v.findViewById(R.id.TextName);
        Textaddress=v.findViewById(R.id.TextAddress);
        Textemail=v.findViewById(R.id.TextEmail);
        Textphone=v.findViewById(R.id.TextPhone);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        String user_id=mAuth.getCurrentUser().getUid();

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    if(task.getResult().exists()){

                        String name = task.getResult().getString("name");
                        String mobile = task.getResult().getString("mobile");
                        String address = task.getResult().getString("address");

                        TextName.setText(name);
                        Textphone.setText(mobile);
                        Textemail.setText(mAuth.getCurrentUser().getEmail());

                        SharedPreferences pref = getContext().getSharedPreferences("MyMobile", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();
                       // editor.putString("mobb", setupMobile.getText().toString());
                        editor.commit();


                       Textaddress .setText(address);
                    }

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(getContext(), "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                }

                //setupProgress.setVisibility(View.INVISIBLE);
                //setupBtn.setEnabled(true);

            }
        });


        // msharedpreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        /*String email=msharedpreferences.getString("EmailKey","");
        String enroll=msharedpreferences.getString("EnrollKey","");
        String name=msharedpreferences.getString("NameKey","");*/



        //textEmail.setText(email);
        //textEnrollment.setText(enroll);
       // textName.setText(name);

        return v;

    }

}
