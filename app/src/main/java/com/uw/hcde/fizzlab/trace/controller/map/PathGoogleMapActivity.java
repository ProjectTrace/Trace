package com.uw.hcde.fizzlab.trace.controller.map;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.model.TraceDataFactory;
import com.uw.hcde.fizzlab.trace.model.object.TraceLocation;
import com.uw.hcde.fizzlab.trace.model.object.TracePoint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class PathGoogleMapActivity extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    public static Handler handler;

    private GoogleMap googleMap;

    public static List<LatLng> tracedPoints = new ArrayList<LatLng>(); // the user walked those points already

    public static List<LatLng> suggestedPathPoints = new ArrayList<LatLng>(); // points returned
                                                                                // by parser task
    private List<TraceLocation> originalCoords = null; // get its values from TraceFactory

    private static ArrayList<LatLng> crdList = new ArrayList<LatLng>(); //Coordinates after adding the
                                                                        //current location
    private List<String> urlList = new ArrayList<String>(); //urlList for a request to
                                                                    // GoogleMapsAPIWeb services
    private Location myCurrLocation;

    private int segmentStart = 0;
    private int segmentEnd = 0;
    private int segmentSize = 1;
    private int tracedIndex = 0; // the index that points to the coordinate
                                                            // till which the user walked
    public static Location target = null;      // the target segmentEnd coordinate location

    public LocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_google_map);

        //Mock testing code here
        //Connect to Location Services
        mLocationClient= new LocationClient(this, this, this);
        mLocationClient.connect();





        // Set navigation title
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(R.string.map));

        // Creating an instance for being able to interact with Google Map
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        googleMap = fm.getMap();


        // Getting the current location of the user for further gps tracking and path drawing
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final LocationListener locListener = new MyLocationListener(this, googleMap);
        myCurrLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                10000, 10, locListener); //10 seconds and 10 meters




        // end early functionality (must be improved)
        TextView endEarly = (TextView) findViewById(R.id.map_ending_early);
        endEarly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager.removeUpdates(locListener);

            }
        });

        originalCoords = TraceDataFactory.getLocations(myCurrLocation);
        for (TraceLocation tracePoint : originalCoords) {


            if (tracePoint.annotation != null) {
                Location location = tracePoint.location;
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .alpha(0.5f));
            } else {
                Location location = tracePoint.location;
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude())));
            }
        }



        myCurrLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //take
                                                            // updated gps location
        crdList.add(new LatLng(myCurrLocation.getLatitude(), myCurrLocation.getLongitude()));
        for(TraceLocation tLoc: originalCoords) {
            Location tempLoc = tLoc.getLocation();
            crdList.add(new LatLng(tempLoc.getLatitude(), tempLoc.getLongitude()));
        }


        //Drawing the path
        int currentIndex = 0; // index in the crdlist after taking 8x points
        int crdListSize = crdList.size();


        String url = "";
        for (int i = 0; i < crdListSize; i += 7) {
            currentIndex = i;
            url = new MapsUtil(crdList, googleMap).getMapsApiDirectionsUrl(currentIndex);
            urlList.add(url);
        }

        ReadTask downloadTask = new ReadTask(googleMap);
        try {
            Log.d("onCreate: ", "1");
            downloadTask.execute(urlList.toArray(new String[0])).get();
            Log.d("onCreate: ", "2");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        final MapsUtil mapsUtil = new MapsUtil(suggestedPathPoints, googleMap);

        if(suggestedPathPoints != null) {
            // **** For testing porposes only
            PolylineOptions temp = new PolylineOptions();
            for(int i = 0; i < suggestedPathPoints.size(); i++) {
                temp.add(suggestedPathPoints.get(i));
            }
            temp.color(Color.YELLOW);
            googleMap.addPolyline(temp);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(suggestedPathPoints.get(0), 13));
            //****

            if (myCurrLocation != null) {
                googleMap.setMyLocationEnabled(true);

            }

            if (segmentSize > suggestedPathPoints.size()) {
                segmentSize = suggestedPathPoints.size();
            }
            segmentEnd = segmentSize;
            target = new Location("Test");
            target.setLatitude(suggestedPathPoints.get(segmentEnd).latitude);
            target.setLongitude(suggestedPathPoints.get(segmentEnd).longitude);

            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            //        10000, 10, locListener); //10 seconds and 10 meters
            mapsUtil.drawPathSegment(this, segmentStart, segmentEnd);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(suggestedPathPoints.get(segmentStart), 15));

            handler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 0) {

                        if (segmentEnd == suggestedPathPoints.size() - 1) {
                            mapsUtil.drawTracedPathSegment(PathGoogleMapActivity.this,
                                    tracedPoints, tracedPoints.size());
                            mLocationClient.setMockMode(false);
                            locationManager.removeUpdates(locListener);
                            return;
                        }
                        segmentStart = segmentEnd - 1;
                        tracedIndex = segmentEnd - 1;

                        if (segmentEnd - 1 + segmentSize > suggestedPathPoints.size()) {
                            segmentEnd = suggestedPathPoints.size() - segmentEnd - 1;
                            target.setLatitude(suggestedPathPoints.get(segmentEnd-1).latitude);
                            target.setLongitude(suggestedPathPoints.get(segmentEnd-1).longitude);
                        } else {
                            segmentEnd += segmentSize - 1;
                            target.setLatitude(suggestedPathPoints.get(segmentEnd).latitude);
                            target.setLongitude(suggestedPathPoints.get(segmentEnd).longitude);
                        }
                        mapsUtil.drawPathSegment(PathGoogleMapActivity.this,
                                                             segmentStart, segmentEnd);
                        mapsUtil.drawTracedPathSegment(PathGoogleMapActivity.this, tracedPoints, tracedPoints.size());

                    }

                }
            };
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        //TODO define location services callbacks
        // When the location client is connected, set mock mode
       // mLocationClient.setMockMode(true);
    }

    @Override
    public void onDisconnected() {
        //TODO define location services callbacks

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //TODO define location services callbacks
    }

    /**
     * Created by sonagrigoryan on 14-11-21.
     */
    public class ReadTask extends AsyncTask<String, Void, Void> {
        private GoogleMap googleMap;
        public ReadTask(GoogleMap gMap) {
            Log.d("ReadTask", "init");
            googleMap = gMap;
        }

        @Override
        protected Void doInBackground(String... urlList) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                for(String s: urlList) {
                    data = http.readUrl(s);
                    suggestedPathPoints.addAll(MapsUtil.parseLatLongJSON(data));
                }
            } catch (Exception e) {
                Log.d("Background Task: ", e.toString());
            }

            return null;
        }
    }

}