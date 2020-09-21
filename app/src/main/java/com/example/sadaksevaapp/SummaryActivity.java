package com.example.sadaksevaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SummaryActivity extends AppCompatActivity {

    public Button back_btn;
    public Button report_pothole;
    private static final int ALERT_DIALOG = 1;
    public static final String MyPREFERENCES = "User" ;
    SharedPreferences msharedpreferences;
    public View detail_view;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    TextView tv1,tv2,tv3,tv4,tv5;
    ImageView image_preview;
    String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        back_btn = findViewById(R.id.back_button);
        tv1=(TextView) findViewById(R.id.tv1);
        tv2=(TextView) findViewById(R.id.tv2);
        tv3=(TextView) findViewById(R.id.tv3);
        tv4=(TextView) findViewById(R.id.tv4);
        tv5=(TextView) findViewById(R.id.tv5);
        detail_view=findViewById(R.id.summary_view);
        setUpToolbar(detail_view);

        image_preview=(ImageView)findViewById(R.id.image_preview);

        final String xx;


        Intent i=getIntent();
        String location= i.getStringExtra("location");
        String tolocation= i.getStringExtra("tolocation");
        String severity= i.getStringExtra("severity");
        String description= i.getStringExtra("description");
        String namee= i.getStringExtra("namee");
        tv1.setText(location);
        tv2.setText(tolocation);
        tv3.setText(severity);
        tv4.setText(description);
        tv5.setText(namee);

        String ur=i.getStringExtra("img");

        new DownloadImageFromInternet((ImageView) findViewById(R.id.image_preview))
                .execute(ur);


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SummaryActivity.this, PhotoDetailActivity.class);
                startActivity(i);
            }
        });

        report_pothole = findViewById(R.id.Report_button);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        final String phone=mAuth.getCurrentUser().getPhoneNumber();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyMobile", 0); // 0 - for private mode
        xx=pref.getString("mobb", null);

        String user_id=mAuth.getCurrentUser().getUid();

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    if(task.getResult().exists()){

                        String name = task.getResult().getString("name");
                         mobile = task.getResult().getString("mobile");
                        String address = task.getResult().getString("address");


                        /*SharedPreferences pref = getContext().getSharedPreferences("MyMobile", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();
                        // editor.putString("mobb", setupMobile.getText().toString());
                        editor.commit();*/

                    }

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(SummaryActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                }

                //setupProgress.setVisibility(View.INVISIBLE);
                //setupBtn.setEnabled(true);

            }
        });




        report_pothole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast t=Toast.makeText(getApplicationContext(),"Spothole Reported Successfully !!",
                        Toast.LENGTH_SHORT);
                t.show();

                try {

                    // Construct data
                    String apiKey = "apikey=" + "oflkwRRJnAc-ZNMzQzeM220ilUdoZ7NHEbdmChXnr2";
                    String message = "&message=" + "Thankyou for reporting pothole";
                    //    String message="Thankyou for reporting pothole";
                    String sender = "&sender=" + "TXTLCL";
                    String numbers = "&numbers=" + mobile;

                    // Send data
                    HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
                    String data = apiKey + numbers + message + sender;
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
                    conn.getOutputStream().write(data.getBytes("UTF-8"));
                    final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    final StringBuffer stringBuffer = new StringBuffer();
                    String line;
                    while ((line = rd.readLine()) != null) {
                        Toast.makeText(SummaryActivity.this, line.toString() , Toast.LENGTH_SHORT).show();
                    }
                    rd.close();

                } catch (Exception e) {
                    Toast.makeText(SummaryActivity.this,e.toString(), Toast.LENGTH_SHORT).show();

                }

                Intent i=new Intent(SummaryActivity.this,HomeActivity.class);
                startActivity(i);
            }

        });


        StrictMode.ThreadPolicy st=new StrictMode.ThreadPolicy.Builder().build();
        StrictMode.setThreadPolicy(st);

    }


    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
            Toast.makeText(getApplicationContext(), "Please wait, it may take a few minute...", Toast.LENGTH_SHORT).show();
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }
    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.Appbar);
        AppCompatActivity activity = (AppCompatActivity) (this);
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            ActionBar actionbar = activity.getSupportActionBar();
            assert actionbar != null;
            actionbar.setDisplayHomeAsUpEnabled(true);
           // actionbar.setHomeAsUpIndicator(R.drawable.navigation_drawer_icon);

        }

    }
}