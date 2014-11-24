package com.uw.hcde.fizzlab.trace.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.controller.walk.PathJSONParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sonagrigoryan on 14-11-22.
 */
public class MapsUtil {
    ArrayList<LatLng> crdList;
    GoogleMap googleMap;
    private Bitmap arrowIcon = null;
    Polyline polylineFuture;
    Polyline polyLineTraced;
    Marker arrowMarker;

    public MapsUtil(List<LatLng> list, GoogleMap gm) {
        crdList = (ArrayList<LatLng>)list;
        googleMap = gm;
    }

    public void drawPathSegment(Context context, int start, int end) {
        if (crdList == null || crdList.size() == 0) return;
        if (polylineFuture != null) {
            polylineFuture.remove();
        }
        PolylineOptions temp = new PolylineOptions();
        temp.color(Color.BLUE);
        for(int i = start; i < end; i++) {
            temp.add(crdList.get(i));

        }
        polylineFuture = googleMap.addPolyline(temp);
        drawDirectionArrow(context, crdList.get(end - 1), crdList.get(end));
    }

    public void drawTracedPathSegment(Context context, List<LatLng> tracedList, int end) {
        if (tracedList == null || tracedList.size() == 0) return;
        if (polyLineTraced != null) {
            polyLineTraced.remove();

        }
        PolylineOptions temp = new PolylineOptions();
        temp.color(Color.RED);
        for(int i = 0; i < end; i++) {
            temp.add(tracedList.get(i));

        }
        polyLineTraced = googleMap.addPolyline(temp);
    }



    private void drawDirectionArrow(Context context, LatLng origin, LatLng destination) {
        float rotationDegrees = (float) Math.toDegrees(Math.atan2(origin.latitude - destination.latitude,
                origin.longitude - destination.longitude));
        if (arrowMarker != null) {
            arrowMarker.remove();
            arrowMarker = null;
        }

        //Rotate the bitmap of the arrowhead somewhere in your code
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegrees);

        if(arrowIcon == null) {
            Drawable myDrawable = context.getResources().getDrawable(R.drawable.arrowdir);
            arrowIcon = ((BitmapDrawable) myDrawable).getBitmap();
        }

        // Create the rotated arrowhead bitmap
        Bitmap arrowheadBitmap = Bitmap.createBitmap(arrowIcon, 0, 0,
                arrowIcon.getWidth(), arrowIcon.getHeight(), matrix, true);
        // Now we are going to add a marker

        arrowMarker = googleMap.addMarker(new MarkerOptions().position(destination)
                .icon(BitmapDescriptorFactory.fromBitmap(arrowheadBitmap)));
    }

    public  String getMapsApiDirectionsUrl(int index) {
        StringBuilder waypoints = new StringBuilder();
        waypoints.append("waypoints=optimize:true");
        for (int i=index; (i < crdList.size() && i <= index+7 ); i++) {
            LatLng ll = crdList.get(i);
            waypoints.append("|");
            waypoints.append(ll.latitude);
            waypoints.append(",");
            waypoints.append(ll.longitude);
        }

        StringBuilder sensor = new StringBuilder("sensor=false");
        StringBuilder params = new StringBuilder(waypoints + "&" + sensor);
        StringBuilder output = new StringBuilder("json");
        StringBuilder mode = new StringBuilder("walking");
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params + "&mode" + mode);
        return url.toString();
    }

    public static final List<LatLng> parseLatLongJSON(String json) {
        JSONObject jObject;
        List<LatLng> points = new ArrayList<LatLng>();
        //Log.d(TAG, String.format("doInBackground: %s", json));
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(json);
            PathJSONParser parser = new PathJSONParser();
            routes = parser.parse(jObject);
            //Log.d(TAG, String.format("Routes: %s", routes.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Traversing through routes
        for (int i = 0; i < routes.size(); i++) {
            points = new ArrayList<LatLng>();
            List<HashMap<String, String>> path = routes.get(i);

            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }
        }
        return points;
    }
}
