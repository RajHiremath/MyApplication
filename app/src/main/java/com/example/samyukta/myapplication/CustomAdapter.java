package com.example.samyukta.myapplication;

import android.content.Context;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by fankave on 2/2/16.
 */
public class CustomAdapter extends BaseAdapter{
Context context;
    private static final String TAG = "CustomAdapter ";
    ArrayList myList = new ArrayList<>();
    public CustomAdapter(Context ctx, ArrayList<MapsActivity.MyGeofenceData>list){
        context = ctx;
        myList = list;
        Log.d(TAG , "Size : "+myList.size());
        }

    public void setListData(ArrayList<MapsActivity.MyGeofenceData> data){
        myList = data;
        notifyDataSetChanged();

    }
    public void addItem(MapsActivity.MyGeofenceData item) {
        myList.add(item);
        notifyDataSetChanged();
    }
    public int getCount() {
        if(myList != null) return myList.size();
        else return 0;
    }

    @Override
    public Object getItem(int position) {
        if(myList != null && myList.size()>=position)
            return myList.get(position);
            return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
        LayoutInflater inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
        rowView = inflater.inflate(R.layout.list_helper, null);
        holder.first = (TextView) rowView.findViewById(R.id.firstLine);
        holder.second = (TextView) rowView.findViewById(R.id.secondLine);
        holder.imgRemoveGeofence = (ImageView)rowView.findViewById(R.id.iconRemove);
        MapsActivity.MyGeofenceData data = (MapsActivity.MyGeofenceData) myList.get(position);
        Log.d(TAG, "data.address : "+ data.address +" & data.message : "+ data.message);
        holder.first.setText(data.address);
        holder.second.setText(data.message);
        holder.imgRemoveGeofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MapsActivity)context).removeGeofenceById();
            }
        });
        return rowView;

    }

    public class Holder
    {
        TextView first;
        TextView second;
        ImageView imgRemoveGeofence;
    }
}
