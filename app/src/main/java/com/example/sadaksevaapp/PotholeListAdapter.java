package com.example.sadaksevaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.view.LayoutInflater;

import com.bumptech.glide.Glide;
import com.example.sadaksevaapp.Const_Pothole_Detail;

class PotholeListAdapter extends RecyclerView.Adapter<PotholeListHolder>
{  private List<Const_Pothole_Detail> PotholeList;
    private Context context;



    public PotholeListAdapter(Context mcontext,List<Const_Pothole_Detail> PotholeList) {
        super();
        this.context=mcontext;
        this.PotholeList=PotholeList;

    }

    @NonNull
    @Override
    public PotholeListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        return new PotholeListHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull PotholeListHolder ViewHolder, int i) {
        final Const_Pothole_Detail Mapobj=PotholeList.get(i);
        if(PotholeList!=null && i<PotholeList.size())
        {
            ViewHolder.pothole_severity.setText(Mapobj.getSeverity());
            ViewHolder.pothole_location.setText(Mapobj.getLocation());

            Glide.with(context).load(Mapobj.getUrlimage()).into(ViewHolder.pothole_image);
        }

        /*bookCardViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context,DetailActivity.class);
                i.putExtra("booknm",bi.getName());
                context.startActivity(i);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return PotholeList.size();
    }
}