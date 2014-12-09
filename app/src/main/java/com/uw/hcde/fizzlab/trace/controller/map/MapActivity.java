package com.uw.hcde.fizzlab.trace.controller.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.model.TraceDataFactory;
import com.uw.hcde.fizzlab.trace.model.object.TraceAnnotation;
import com.uw.hcde.fizzlab.trace.model.object.TraceDataContainer;
import com.uw.hcde.fizzlab.trace.model.object.TraceLocation;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapActivity extends Activity implements
        LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String TAG = "MapActivity";
    private static final int GOOGLE_PLAY_FAILURE_RESOLUTION_REQUEST = 9001;
    private static final int MAP_ZOOM_LEVEL = 15;
    private static final int MAP_ANIMATE_DURATION = 1000;
    private static final int MAP_UPDATE_INTERVAL_MILLISECONDS = 5000;
    private static final int MAP_UPDATE_SENSITIVITY_METERS = 3;
    private static final int MAP_URL_FETCH_NUM = 2;

    private View mButtonBack;
    private View mButtonEndingEarly;
    private View mButtonShowDrawing;
    private TextView mTextDistanceMiles;

    private GoogleMap mGoogleMap;
    private LocationClient mLocationClient;
    private Location mCurrentLocation;
    LocationRequest mLocationRequest;

    private List<TraceLocation> mTraceLocations;
    private List<LatLng> mTraceLatLngs;
    private Map<LatLng, TraceAnnotation> mLatLngToAnnotation;
    private int mTraceLatLngIndex;
    private boolean mIsEndingEarly;
    private Polyline mDirectionSegment;

//    public static List<LatLng> suggestedPathPoints = new ArrayList<LatLng>(); // points returned
//
//
//    private List<String> urlList = new ArrayList<String>(); //urlList for a request to
//    // GoogleMapsAPIWeb services
//
//
//    private int segmentStart = 0;
//    private int segmentEnd = 0;
//    private int segmentSize = 1;
//    private int tracedIndex = 0; // the index that points to the coordinate
//    // till which the user walked
//    public static Location target = null;      // the target segmentEnd coordinate location


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Sets navigation title
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(R.string.map));

        // Buttons
        mButtonEndingEarly = findViewById(R.id.button_ending_early);
        mTextDistanceMiles = (TextView) findViewById(R.id.text_distance);
        mButtonBack = findViewById(R.id.button_back);
        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mButtonShowDrawing = findViewById(R.id.button_show_drawing);
        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (!isGooglePlayAvailable()) {
            Log.d(TAG, "google play not available");
            return;
        }

        // Map description
        TextView description = (TextView) findViewById(R.id.description_text);
        description.setText(TraceDataContainer.description);

        // Creating an instance for being able to interact with Google Map
        MapFragment fm = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mGoogleMap = fm.getMap();
        mGoogleMap.setMyLocationEnabled(true);

        // Location client and location request object
        mLocationClient = new LocationClient(this, this, this);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(MAP_UPDATE_INTERVAL_MILLISECONDS);

        mTraceLatLngIndex = 0;
        mIsEndingEarly = false;
        mDirectionSegment = null;

        //Drawing the path

        // String url = "";
//        for (int i = 0; i < mTraceLocations.size(); i += MAP_URL_FETCH_NUM) {
//           // url = new MapsUtil(crdList, mGoogleMap).getMapsApiDirectionsUrl(currentIndex);
//            urlList.add(url);
//        }
//

//
//        final MapsUtil mapsUtil = new MapsUtil(suggestedPathPoints, mGoogleMap);
//
//        if (suggestedPathPoints != null) {
//            // **** For testing porposes only
//            PolylineOptions temp = new PolylineOptions();
//            for (int i = 0; i < suggestedPathPoints.size(); i++) {
//                temp.add(suggestedPathPoints.get(i));
//            }
//            temp.color(Color.YELLOW);
//            mGoogleMap.addPolyline(temp);
//            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(suggestedPathPoints.get(0), 13));
//            //****
//
//            if (mCurrentLocation != null) {
//                mGoogleMap.setMyLocationEnabled(true);
//
//            }
//
//            if (segmentSize > suggestedPathPoints.size()) {
//                segmentSize = suggestedPathPoints.size();
//            }
//            segmentEnd = segmentSize;
//            target = new Location("Test");
//            target.setLatitude(suggestedPathPoints.get(segmentEnd).latitude);
//            target.setLongitude(suggestedPathPoints.get(segmentEnd).longitude);
//
//            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//            //        10000, 10, locListener); //10 seconds and 10 meters
//            mapsUtil.drawSegment(this, segmentStart, segmentEnd);
//            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(suggestedPathPoints.get(segmentStart), 15));
//
//            Handler handler = new Handler() {
//                @Override
//                public void handleMessage(Message msg) {
//                    if (msg.what == 0) {
//
//                        if (segmentEnd == suggestedPathPoints.size() - 1) {
//                            mapsUtil.drawTracedPathSegment(MapActivity.this,
//                                    tracedPoints, tracedPoints.size());
//                            mLocationClient.setMockMode(false);
//                            //locationManager.removeUpdates(locListener);
//                            return;
//                        }
//                        segmentStart = segmentEnd - 1;
//                        tracedIndex = segmentEnd - 1;
//
//                        if (segmentEnd - 1 + segmentSize > suggestedPathPoints.size()) {
//                            segmentEnd = suggestedPathPoints.size() - segmentEnd - 1;
//                            target.setLatitude(suggestedPathPoints.get(segmentEnd - 1).latitude);
//                            target.setLongitude(suggestedPathPoints.get(segmentEnd - 1).longitude);
//                        } else {
//                            segmentEnd += segmentSize - 1;
//                            target.setLatitude(suggestedPathPoints.get(segmentEnd).latitude);
//                            target.setLongitude(suggestedPathPoints.get(segmentEnd).longitude);
//                        }
//                        mapsUtil.drawSegment(MapActivity.this,
//                                segmentStart, segmentEnd);
//                        mapsUtil.drawTracedPathSegment(MapActivity.this, tracedPoints, tracedPoints.size());
//
//                    }
//
//                }
//            };
//        }
    }

    /**
     * Sets listeners after connection success.
     */
    private void setupListeners() {
        mButtonEndingEarly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsEndingEarly = true;
                mButtonEndingEarly.setVisibility(View.INVISIBLE);
                mTextDistanceMiles.setVisibility(View.INVISIBLE);
                mButtonShowDrawing.setVisibility(View.VISIBLE);

                if (mDirectionSegment != null) {
                    mDirectionSegment.remove();
                }
                showMarkers();
                showAllFuturePath();
                mLocationClient.removeLocationUpdates(MapActivity.this);
            }
        });

        mButtonShowDrawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, ShowDrawingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.connect();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.disconnect();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.distanceTo(mCurrentLocation) > MAP_UPDATE_SENSITIVITY_METERS) {
            List<LatLng> points = new ArrayList<LatLng>();
            points.add(MapUtil.locationToLatLng(mCurrentLocation));
            points.add(MapUtil.locationToLatLng(location));

            mDirectionSegment = MapUtil.drawSegment(points, getResources().getColor(R.color.transparent_klein_blue1), mGoogleMap, 20);
            mCurrentLocation = location;

            Log.d(TAG, "update new location");
        } else {
            Log.d(TAG, "get new location");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");

        // Set and zoom to current location
        mCurrentLocation = mLocationClient.getLastLocation();
        LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, MAP_ZOOM_LEVEL);
        mGoogleMap.animateCamera(cameraUpdate, MAP_ANIMATE_DURATION, null);

        // Add location update
        mLocationClient.requestLocationUpdates(mLocationRequest, this);

        // Fetch locations and do the conversions
        mTraceLocations = TraceDataFactory.getLocations(mCurrentLocation);
        mTraceLatLngs = new ArrayList<LatLng>();
        mLatLngToAnnotation = new HashMap<LatLng, TraceAnnotation>();
        for (TraceLocation traceLocation : mTraceLocations) {
            LatLng latlng = new LatLng(traceLocation.location.getLatitude(),
                    traceLocation.location.getLongitude());
            mTraceLatLngs.add(latlng);
            if (traceLocation.annotation != null) {
                mLatLngToAnnotation.put(latlng, traceLocation.annotation);
            }
        }

        setupListeners();
        showDirectionPath();
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "onDisConnected");

    }

    /**
     * TODO: requires test
     *
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, GOOGLE_PLAY_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            // If no resolution is available, display a dialog to the user with the error
            Log.e(TAG, "error code: " + connectionResult.getErrorCode());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Google play service error: " + connectionResult.getErrorCode());
            builder.setPositiveButton(getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            builder.create().show();
        }
    }

    /**
     * Checks if google play service is available
     *
     * @return
     */
    private boolean isGooglePlayAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d(TAG, "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode, this, GOOGLE_PLAY_FAILURE_RESOLUTION_REQUEST);
            if (errorDialog != null) {
                errorDialog.show();
            }
            return false;
        }
    }

    /**
     * Show next direction path
     */
    private void showDirectionPath() {
        String url = MapUtil.getMapsApiDirectionsUrl(
                MapUtil.locationToLatLng(mCurrentLocation), mTraceLatLngs.get(mTraceLatLngIndex));
        new FetchDirectionTask().execute(url);
    }


    /**
     * Shows all future path.
     */
    private void showAllFuturePath() {
        String url = MapUtil.getMapsApiDirectionsUrl(
                MapUtil.locationToLatLng(mCurrentLocation), mTraceLatLngs.get(0));
        new FetchDirectionTask().execute(url);
        for (int i = mTraceLatLngIndex; i < mTraceLatLngs.size() - 1; i++) {
            url = MapUtil.getMapsApiDirectionsUrl(mTraceLatLngs.get(i), mTraceLatLngs.get(i + 1));
            new FetchDirectionTask().execute(url);
        }
    }

    /**
     * Shows all markers.
     * Blue maker represents trace point. Red marker represents annotation point.
     */
    private void showMarkers() {
        for (LatLng latlng : mTraceLatLngs) {
            MarkerOptions markerOption = new MarkerOptions()
                    .position(latlng)
                    .alpha(0.8f);
            if (mLatLngToAnnotation.containsKey(latlng)) {
                markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            } else {
                markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            }
            mGoogleMap.addMarker(markerOption);
        }

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng latlng = marker.getPosition();
                if (!mLatLngToAnnotation.containsKey(latlng)) {
                    return true;
                }

                TraceAnnotation annotation = mLatLngToAnnotation.get(latlng);
                AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                builder.setTitle(getString(R.string.message));

                // Sets up message window
                TextView text = new TextView(MapActivity.this);
                text.setText(annotation.msg);
                text.setLines(3);
                text.setSingleLine(false);
                text.setGravity(Gravity.CENTER);
                builder.setView(text);

                // Positive button
                builder.setPositiveButton(getString(R.string.ok), null);
                builder.create().show();
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GOOGLE_PLAY_FAILURE_RESOLUTION_REQUEST:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        recreate();
                        break;
                }
        }
    }

    /**
     * Created by sonagrigoryan on 14-11-21.
     */
    public class FetchDirectionTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Log.d(TAG, "FetchDirectionTask onPostExecute null");
                return;
            }
            new ParseDirectionTask().execute(result);
        }

        @Override
        protected String doInBackground(String... urls) {
            Log.d(TAG, "FetchDirectionTask doInBackground");
            try {
                HttpConnection http = new HttpConnection();
                return http.readUrl(urls[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class ParseDirectionTask extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            Log.d(TAG, "ParseDirectionTask doInBackground");
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                routes = PathJSONParser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            if (routes == null) {
                Log.d(TAG, "ParseDirectionTask onPostExecute null");
                return;
            }

            List<LatLng> points = new ArrayList<LatLng>();
            // Traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                List<HashMap<String, String>> path = routes.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
            }

            if (mIsEndingEarly) {
                MapUtil.drawSegment(points, getResources().getColor(R.color.transparent_klein_blue1), mGoogleMap, Integer.MAX_VALUE);
            } else {
                mDirectionSegment = MapUtil.drawSegment(points, getResources().getColor(R.color.transparent_klein_blue1), mGoogleMap, Integer.MAX_VALUE);
            }

        }
    }

}
