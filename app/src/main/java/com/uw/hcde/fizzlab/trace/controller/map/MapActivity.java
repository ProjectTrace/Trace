package com.uw.hcde.fizzlab.trace.controller.map;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.controller.TraceUtil;
import com.uw.hcde.fizzlab.trace.controller.main.MainActivity;
import com.uw.hcde.fizzlab.trace.model.TraceDataFactory;
import com.uw.hcde.fizzlab.trace.model.object.TraceAnnotation;
import com.uw.hcde.fizzlab.trace.model.object.TraceDataContainer;
import com.uw.hcde.fizzlab.trace.model.object.TraceLocation;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Represents map direction screen.
 *
 * @author sonagrigoryan, tianchi
 */
public class MapActivity extends Activity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MapActivity";
    private static final int INTERVAL_MILLI = 5000;
    private static final int MAP_ZOOM_LEVEL = 16;
    private static final int MAP_ANIMATE_DURATION = 1000;
    private static final int SENSITIVITY_METER = 5;
    private static final int SEGMENT_UPDATE_SENSITIVITY_METER = 20;
    private static final int ANNOTATION_SENSITIVITY_METER = 10;

    private static final int MARKER_TYPE_WAY_POINT = 1;
    private static final int MARKER_TYPE_ROUTING_POINT = 2;
    private static final int MARKER_TYPE_ANNOTATION = 3;

    private static final double METER_TO_MILE = 0.000621371192;

    private TextView mButtonNavigation;
    private View mButtonEndingEarly;
    private View mButtonShowDrawing;
    private View mButtonShowTrace;
    private View mTextMiles;
    private TextView mTextDistance;
    private ProgressDialog mProgressDialog;

    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderApi mProviderApi = LocationServices.FusedLocationApi;
    private LatLng mCurrentLatLng;
    LocationRequest mLocationRequest;

    private List<TraceLocation> mTraceLocations;
    private Map<LatLng, TraceAnnotation> mLatLngToAnnotation;
    private Map<Marker, TraceAnnotation> mMarkerToAnnotation;
    private boolean mIsInitializing;
    private boolean mIsTraceSuccess;
    private boolean mIsEndingEarly;
    private int mTraceDistanceMeters;

    private int mRawSegmentsCount;
    // Raw segments defined by trace locations -> transformed segments defined by way point distance
    private List<List<LatLng>> mHiddenSegments;
    private List<List<LatLng>> mDisplayedSegments;
    private List<LatLng> mWalkedPoints;
    private Marker mDirectionMarker;
    private float mDirectionBearing;

    private Polyline mDisplayedPolyline;
    private Polyline mWalkedPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Sets navigation title
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(R.string.map));

        // Buttons
        mButtonEndingEarly = findViewById(R.id.button_ending_early);
        mTextDistance = (TextView) findViewById(R.id.text_distance);
        mTextMiles = findViewById(R.id.text_miles);
        mButtonNavigation = (TextView) findViewById(R.id.navigation_button);
        mButtonShowDrawing = findViewById(R.id.button_show_drawing);
        mButtonShowTrace = findViewById(R.id.button_show_trace);


        // Map description
        TextView description = (TextView) findViewById(R.id.description_text);
        description.setText(TraceDataContainer.description);

        // Creating an instance for being able to interact with Google Map
        MapFragment fm = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mGoogleMap = fm.getMap();
        mGoogleMap.setMyLocationEnabled(true);

        // Location client and latLng request object
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(INTERVAL_MILLI);
        mLocationRequest.setSmallestDisplacement(SENSITIVITY_METER);

        mIsInitializing = true;
        mIsTraceSuccess = false;
        mIsEndingEarly = false;
        mRawSegmentsCount = 0;
        mTraceDistanceMeters = 0;
        mHiddenSegments = new LinkedList<List<LatLng>>();
        mDisplayedSegments = new LinkedList<List<LatLng>>();
        mWalkedPoints = new LinkedList<LatLng>();
        mLatLngToAnnotation = new HashMap<LatLng, TraceAnnotation>();
        mMarkerToAnnotation = new HashMap<Marker, TraceAnnotation>();
        setupListeners();

        PolylineOptions line = new PolylineOptions();
        line.color(getResources().getColor(R.color.transparent_klein_blue1));
        mDisplayedPolyline = mGoogleMap.addPolyline(line);

        PolylineOptions line2 = new PolylineOptions();
        line2.color(getResources().getColor(R.color.main_red1));
        mWalkedPolyline = mGoogleMap.addPolyline(line2);
        mWalkedPolyline.setVisible(false);

        // Sets up progress dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.show();

        // Camera change listener for direction arrow
        mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (mDirectionMarker != null) {
                    float heading = mDirectionMarker.getRotation();
                    mDirectionMarker.setRotation(mDirectionBearing - cameraPosition.bearing);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        if (!mIsTraceSuccess) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

        if (mIsTraceSuccess) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            getFragmentManager().findFragmentById(R.id.map).getView().setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mProviderApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onLocationChanged(Location location) {
        // First time latLng
        LatLng latLng = MapUtil.toLatLng(location);
        if (mCurrentLatLng == null) {
            Log.d(TAG, "first time latLng");
            mCurrentLatLng = latLng;
            mWalkedPoints.add(mCurrentLatLng);
            setCamera(mCurrentLatLng, MAP_ZOOM_LEVEL);

            MarkerOptions markerOption = new MarkerOptions()
                    .position(latLng)
                    .alpha(0.8f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.direction_arrow))
                    .anchor(0.5f, 0.5f)
                    .visible(false);
            mDirectionMarker = mGoogleMap.addMarker(markerOption);

            mTraceLocations = TraceDataFactory.getLocations(mCurrentLatLng);
            for (TraceLocation traceLocation : mTraceLocations) {
                if (traceLocation.annotation != null) {
                    mLatLngToAnnotation.put(traceLocation.latLng, traceLocation.annotation);
                }
                mHiddenSegments.add(null);
            }
            fetchAllRoutingData();


        } else if (!mIsInitializing && !mIsTraceSuccess && !mIsEndingEarly &&
                SphericalUtil.computeDistanceBetween(mCurrentLatLng, latLng) > SENSITIVITY_METER) {
            Log.d(TAG, "latLng update");

            mTraceDistanceMeters += SphericalUtil.computeDistanceBetween(mCurrentLatLng, latLng);
            updateDisplayedDistance(mTraceDistanceMeters);

            mCurrentLatLng = latLng;
            mWalkedPoints.add(latLng);
            setCamera(mCurrentLatLng, -1);

            // Detect annotation
            LatLng annotationLatLng = null;
            for (LatLng key : mLatLngToAnnotation.keySet()) {
                if (SphericalUtil.computeDistanceBetween(mCurrentLatLng, key) < ANNOTATION_SENSITIVITY_METER) {
                    annotationLatLng = key;
                    break;
                }
            }
            if (annotationLatLng != null) {
                showAnnotationDialog(mLatLngToAnnotation.get(annotationLatLng));
                mLatLngToAnnotation.remove(annotationLatLng);
            }

            // Move segment forward
            List<LatLng> lastSegment = mDisplayedSegments.get(mDisplayedSegments.size() - 1);
            LatLng target = lastSegment.get(lastSegment.size() - 1);
            if (SphericalUtil.computeDistanceBetween(mCurrentLatLng, target) < SEGMENT_UPDATE_SENSITIVITY_METER) {
                if (mHiddenSegments.isEmpty()) {
                    traceSuccess();
                } else {
                    mDisplayedSegments.add(mHiddenSegments.get(0));
                    mHiddenSegments.remove(0);
                    updateDisplayedPolyLine(mDisplayedSegments);
                }
            }
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        // Do nothing.
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        TraceUtil.showToast(this, "google play service connection failed");
        finish();
    }

    /**
     * Returns from ending early state.
     */
    private void disableStateEndingEarly() {
        mButtonEndingEarly.setVisibility(View.VISIBLE);
        mTextDistance.setVisibility(View.VISIBLE);
        mTextMiles.setVisibility(View.VISIBLE);
        mButtonShowDrawing.setVisibility(View.INVISIBLE);
        mButtonShowTrace.setVisibility(View.INVISIBLE);
        mButtonNavigation.setText(getString(R.string.home));
        mDirectionMarker.setVisible(true);

        // Clear annotation markers.
        for (Marker m : mMarkerToAnnotation.keySet()) {
            m.remove();
        }
        mMarkerToAnnotation.clear();
        mWalkedPolyline.setVisible(false);
        updateDisplayedPolyLine(mDisplayedSegments);
    }

    /**
     * Sets state to ending early.
     */
    private void setStateEndingEarly() {
        mButtonEndingEarly.setVisibility(View.INVISIBLE);
        mTextDistance.setVisibility(View.INVISIBLE);
        mTextMiles.setVisibility(View.INVISIBLE);
        mButtonShowDrawing.setVisibility(View.VISIBLE);
        mButtonShowTrace.setVisibility(View.VISIBLE);
        mButtonNavigation.setText(getString(R.string.resume));
        mDirectionMarker.setVisible(false);

        // Show all future path
        List<List<LatLng>> allPath = new LinkedList<List<LatLng>>();
        allPath.addAll(mDisplayedSegments);
        allPath.addAll(mHiddenSegments);
        updateDisplayedPolyLine(allPath);

        // displayWayPoints();
        setAllAnnotationMarkers();
    }

    /**
     * Sets listeners after connection success.
     */
    private void setupListeners() {
        mButtonEndingEarly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsEndingEarly = true;
                setStateEndingEarly();
            }
        });

        mButtonShowDrawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ShowDrawingFragment();
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .add(R.id.showing_drawing_content, fragment)
                        .addToBackStack(null).commit();
                getFragmentManager().findFragmentById(R.id.map).getView().setVisibility(View.INVISIBLE);
            }
        });

        mButtonNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsEndingEarly) {
                    mIsEndingEarly = false;
                    disableStateEndingEarly();
                } else {
                    Intent intent = new Intent(MapActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        mButtonShowTrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWalkedPoints.size() <= 2) {
                    TraceUtil.showToast(MapActivity.this, getString(R.string.no_trace));
                } else {
                    mWalkedPolyline.setPoints(mWalkedPoints);
                    mWalkedPolyline.setVisible(true);
                }
            }
        });
    }

    /**
     * Sets camera position.
     *
     * @param latLng
     */
    private void setCamera(LatLng latLng, int zoomLevel) {

        CameraUpdate cameraUpdate;
        if (zoomLevel != -1) {
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
        } else {
            cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
        }

        mGoogleMap.animateCamera(cameraUpdate, MAP_ANIMATE_DURATION, null);
    }

    private void updateDisplayedDistance(int meters) {
        double miles = meters * METER_TO_MILE;
        DecimalFormat df = new DecimalFormat("#.##");
        mTextDistance.setText(df.format(miles));
    }

    /**
     * Trace success.
     */
    private void traceSuccess() {
        mIsTraceSuccess = true;
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setTitle(R.string.trace_success);
        dialog.setMessage(R.string.trace_success_message);
        dialog.setCanceledOnTouchOutside(false);

        // Positive button
        dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

        mProviderApi.removeLocationUpdates(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }


    /**
     * Gets routing data
     */
    private void fetchAllRoutingData() {
        String url = MapUtil.getMapsApiDirectionsUrl(
                mCurrentLatLng, mTraceLocations.get(0).latLng);
        new FetchDirectionTask(0).execute(url);
        for (int i = 0; i < mTraceLocations.size() - 1; i++) {
            url = MapUtil.getMapsApiDirectionsUrl(mTraceLocations.get(i).latLng,
                    mTraceLocations.get(i + 1).latLng);
            new FetchDirectionTask(i + 1).execute(url);
        }
    }


    /**
     * Displays way points on screen.
     */
    private void displayWayPoints() {
        for (List<LatLng> segment : mDisplayedSegments) {
            LatLng point = segment.get(segment.size() - 1);
            displayMarker(point, MARKER_TYPE_WAY_POINT);
        }

        for (List<LatLng> segment : mHiddenSegments) {
            LatLng point = segment.get(segment.size() - 1);
            displayMarker(point, MARKER_TYPE_WAY_POINT);
        }
    }

    /**
     * Displays a marker.
     *
     * @param latLng
     * @param type
     * @return
     */
    private Marker displayMarker(LatLng latLng, int type) {
        MarkerOptions markerOption = new MarkerOptions()
                .position(latLng)
                .alpha(0.8f);

        switch (type) {
            case MARKER_TYPE_WAY_POINT:
                markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                break;
            case MARKER_TYPE_ROUTING_POINT:
                markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                break;
            case MARKER_TYPE_ANNOTATION:
                markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                break;
            default:
                break;
        }
        return mGoogleMap.addMarker(markerOption);
    }

    /**
     * Displays annotation dialog.
     *
     * @param annotation
     */
    private void showAnnotationDialog(TraceAnnotation annotation) {
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setTitle(R.string.message);
        dialog.setCanceledOnTouchOutside(true);

        // Sets up message window
        TextView text = new TextView(this);
        text.setText(annotation.msg);
        text.setLines(3);
        text.setSingleLine(false);
        text.setGravity(Gravity.CENTER);
        dialog.setContentView(text);

        // Positive button
        dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Shows all markers.
     * Blue maker represents trace point. Red marker represents annotation point.
     */
    private void setAllAnnotationMarkers() {
        for (TraceLocation traceLocation : mTraceLocations) {
            if (traceLocation.annotation != null) {
                Marker marker = displayMarker(traceLocation.latLng, MARKER_TYPE_ANNOTATION);
                mMarkerToAnnotation.put(marker, traceLocation.annotation);
            }
        }

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (!mMarkerToAnnotation.containsKey(marker)) {
                    return true;
                }
                TraceAnnotation annotation = mMarkerToAnnotation.get(marker);
                showAnnotationDialog(annotation);
                return true;
            }
        });
    }

    /**
     * Updates displayed lines.
     */
    private void updateDisplayedPolyLine(List<List<LatLng>> segments) {
        List<LatLng> points = new ArrayList<LatLng>();
        for (List<LatLng> segment : segments) {
            points.addAll(segment);
        }
        mDisplayedPolyline.setPoints(points);

        LatLng p1 = points.get(points.size() - 2);
        LatLng p2 = points.get(points.size() - 1);
        updateDirectionArrow(p1, p2);
    }

    /**
     * Updates direction arrow position and orientation.
     * @param p1
     * @param p2
     */
    private void updateDirectionArrow(LatLng p1, LatLng p2) {
        mDirectionMarker.setPosition(p2);
        mDirectionBearing = (float) SphericalUtil.computeHeading(p1, p2);
        float cameraBearing = mGoogleMap.getCameraPosition().bearing;
        mDirectionMarker.setRotation(mDirectionBearing - cameraBearing);
    }

    public class FetchDirectionTask extends AsyncTask<String, Void, String> {

        private int mRawSegmentIndex;

        public FetchDirectionTask(int index) {
            mRawSegmentIndex = index;
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(result);
                routes = PathJSONParser.parse(jObject);
            } catch (Exception e) {
                TraceUtil.showToast(MapActivity.this, "Serializing error");
                finish();
            }

            List<LatLng> points = new LinkedList<LatLng>();
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
            mHiddenSegments.set(mRawSegmentIndex, points);
            mRawSegmentsCount++;

            if (mRawSegmentsCount == mTraceLocations.size()) {
                mHiddenSegments = MapUtil.normalizeWayPoints(mHiddenSegments);
                mProgressDialog.dismiss();
                mIsInitializing = false;

                mDisplayedSegments.add(mHiddenSegments.get(0));
                mHiddenSegments.remove(0);
                updateDisplayedPolyLine(mDisplayedSegments);
                mDirectionMarker.setVisible(true);
            }
        }

        @Override
        protected String doInBackground(String... urls) {
            Log.d(TAG, "FetchDirectionTask doInBackground");
            try {
                HttpConnection http = new HttpConnection();
                return http.readUrl(urls[0]);
            } catch (Exception e) {
                TraceUtil.showToast(MapActivity.this, "Network error");
                finish();
            }
            return null;
        }
    }
}
