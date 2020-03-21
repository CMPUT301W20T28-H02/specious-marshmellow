/*
RequestAdapter
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class for request
 * @author Michael Antifaoff
 */
public class RequestAdapter extends ArrayAdapter<Request> {
    private Context context;
    private List<Request> requestList = new ArrayList<>();
    public RequestAdapter(@NonNull Context context,ArrayList<Request> list) {
        super(context,0, list);
        this.requestList = list;
        this.context = context;
    }

    /**
     * gets all the necessary views to display a request
     * @param {@code int}position
     * @param {@code View}convertView
     * @param {@code ViewGroup}parent
     * @return {@code View} listItem Request view
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if(listItem == null){
            listItem = LayoutInflater.from(context).inflate(R.layout.content, parent, false);
        }
        Request currentRequest = requestList.get(position);

        TextView riderUsername =  listItem.findViewById(R.id.rider_username_TextView);
        riderUsername.setText(currentRequest.getRider().getUsername());

        TextView riderDistance =  listItem.findViewById(R.id.rider_distance_TextView);
        double distance = Request.getDistance(currentRequest.getStartLocation(), currentRequest.getEndLocation());
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        riderDistance.setText(numberFormat.format(distance)+"km");

        TextView fare=  listItem.findViewById(R.id.rider_fare_TextView);
        double fareAmount = currentRequest.getFare();
        fare.setText("$" + numberFormat.format(fareAmount));

        return listItem;

        //return super.getView(position, convertView, parent);

    }



}