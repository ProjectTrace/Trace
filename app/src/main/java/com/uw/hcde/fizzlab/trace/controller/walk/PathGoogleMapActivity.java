package com.uw.hcde.fizzlab.trace.controller.walk;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.uw.hcde.fizzlab.trace.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PathGoogleMapActivity extends FragmentActivity {
    //private static final String TAG = "MapActivity";
    //TextView title = (TextView) findViewById(R.id.navigation_title);
    //title.setText(getString(R.string.map));

    //private static final LatLng LOWER_MANHATTAN = new LatLng(40.722543, -73.998585);
    //private static final LatLng BROOKLYN_BRIDGE = new LatLng(40.7057, -73.9964);
    //private static final LatLng WALL_STREET = new LatLng(40.7064, -74.0094);
    private static List<LatLng> crdList = new ArrayList<LatLng>();

    GoogleMap googleMap;
    final String TAG = "PathGoogleMapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_google_map);
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        googleMap = fm.getMap();


        crdList.add(new LatLng(40.722543, -73.998585));
        crdList.add(new LatLng(40.7057, -73.9964));
        crdList.add(new LatLng(40.7064, -74.0094));
        crdList.add(new LatLng(40.7164, -74.0094));
        crdList.add(new LatLng(40.6964, -74.0094));
        crdList.add(new LatLng(40.6764, -74.0094));
        crdList.add(new LatLng(40.6864, -74.0094));
        crdList.add(new LatLng(40.7064, -74.0094));
        crdList.add(new LatLng(40.7164, -74.0094));
        crdList.add(new LatLng(40.7264, -74.0094));
        crdList.add(new LatLng(40.7364, -74.0094));
        crdList.add(new LatLng(40.7464, -74.0094));
        crdList.add(new LatLng(40.7444, -74.0094));
        crdList.add(new LatLng(40.7364, -74.0094));
        crdList.add(new LatLng(40.7364, -74.0094));

        MarkerOptions options = new MarkerOptions();
        for (LatLng ll: crdList) {
            options.position(ll);
        }
        googleMap.addMarker(options);

        int currentIndex = 0; // index in the crdlist after taking 8x points
        int crdListSize = crdList.size();

        String url = "";
        for(int i = 0; i < crdListSize; i+=6) {
            url = getMapsApiDirectionsUrl(currentIndex);
            currentIndex = i;
            ReadTask downloadTask = new ReadTask();
            downloadTask.execute(url);
        }
        if (crdListSize != currentIndex) {
            url = getMapsApiDirectionsUrl(currentIndex);
            ReadTask downloadTask = new ReadTask();
            downloadTask.execute(url);
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(crdList.get(0), 13));
        addMarkers();

    }

    private String getMapsApiDirectionsUrl(int index) {
        StringBuilder waypoints = new StringBuilder();
        waypoints.append("waypoints=optimize:true");
        for (int i=index; (i < index+6 && i < crdList.size()); i++) {
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

    private void addMarkers() {
        if (googleMap != null) {
            int i = 0;
            for(LatLng ll: crdList) {
                googleMap.addMarker(new MarkerOptions().position(ll)
                        .title("Point Number #" + i));
                i++;
            }
            //  googleMap.addMarker(new MarkerOptions().position(BROOKLYN_BRIDGE)
            //        .title("First Point"));
            //googleMap.addMarker(new MarkerOptions().position(LOWER_MANHATTAN)
            //        .title("Second Point"));
            //googleMap.addMarker(new MarkerOptions().position(WALL_STREET)
            //        .title("Third Point"));
        }
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(4);
                polyLineOptions.color(Color.BLUE);
            }

            googleMap.addPolyline(polyLineOptions);
        }
    }
}
