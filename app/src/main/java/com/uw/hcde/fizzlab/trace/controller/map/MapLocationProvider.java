package com.uw.hcde.fizzlab.trace.controller.map;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Represents a singleton map mLocation provider using FusedLocationProviderApi.
 *
 * @author tianchi
 */
public class MapLocationProvider implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static MapLocationProvider sInstance = null;
    private static final long INTERVAL = 5000;
    private static final long FASTEST_INTERVAL = 5000;
    private static final long ONE_MIN = 1000 * 60;
    private static final long REFRESH_TIME = ONE_MIN * 5;
    private static final float MINIMUM_ACCURACY = 50.0f;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private FusedLocationProviderApi mProviderApi = LocationServices.FusedLocationApi;

    /**
     * Get instance.
     * @param context
     * @return
     */
    public static MapLocationProvider getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MapLocationProvider(context);
        }
        return sInstance;
    }

    /**
     * Constructor.
     * @param locationActivity
     */
    private MapLocationProvider(Context locationActivity) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        mGoogleApiClient = new GoogleApiClient.Builder(locationActivity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Location currentLocation = mProviderApi.getLastLocation(mGoogleApiClient);
        if (currentLocation != null && currentLocation.getTime() > REFRESH_TIME) {
            mLocation = currentLocation;
        } else {
            mProviderApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            // Schedule a Thread to unregister mLocation listeners
            Executors.newScheduledThreadPool(1).schedule(new Runnable() {
                @Override
                public void run() {
                    mProviderApi.removeLocationUpdates(mGoogleApiClient,
                            MapLocationProvider.this);
                }
            }, ONE_MIN, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //if the existing mLocation is empty or
        //the current mLocation accuracy is greater than existing accuracy
        //then store the current mLocation
        if (null == this.mLocation || location.getAccuracy() < this.mLocation.getAccuracy()) {
            this.mLocation = location;
            //if the accuracy is not better, remove all mLocation updates for this listener
            if (this.mLocation.getAccuracy() < MINIMUM_ACCURACY) {
                mProviderApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        }
    }

    public Location getLocation() {
        return this.mLocation;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}