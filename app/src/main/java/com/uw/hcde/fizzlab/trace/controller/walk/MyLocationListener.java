package com.uw.hcde.fizzlab.trace.controller.walk;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;


/**
 * Created by sonagrigoryan on 14-11-22.
 */
public class MyLocationListener implements LocationListener {
    private Context context;
    private GoogleMap googleMap;
    private final float DISTANCE = 50;

    public MyLocationListener(Context ctext, GoogleMap gm) {
        context = ctext;
        googleMap = gm;
    }


    @Override
    public void onLocationChanged(Location location) {

        googleMap.setMyLocationEnabled(true);
        if(PathGoogleMapActivity.target != null && location.distanceTo(PathGoogleMapActivity.target) > 3) {
            PathGoogleMapActivity.tracedPoints.add(new LatLng(location.getLatitude(),
                                                                 location.getLongitude()));
            PathGoogleMapActivity.handler.sendEmptyMessage(0);
        }
        String text = "My current Location is: Latitude" + location.getLatitude() +
                ", " + location.getLongitude();
        //Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        //LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(context, "GPS is Enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(context, "GPS is Disabled", Toast.LENGTH_SHORT).show();
    }



}
