package com.example.sadaksevaapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

class PotholeListHolder extends RecyclerView.ViewHolder {
    protected ImageView pothole_image;
    protected TextView pothole_severity;
    protected TextView pothole_location;


    public PotholeListHolder(@NonNull View itemView) {
        super(itemView);

        pothole_image=(ImageView)itemView.findViewById(R.id.pothole_image);
       pothole_severity=(TextView)itemView.findViewById(R.id.pothole_severity);
        pothole_location=(TextView)itemView.findViewById(R.id.pothole_location);
    }

}
