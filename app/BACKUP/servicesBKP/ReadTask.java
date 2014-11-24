package com.uw.hcde.fizzlab.trace.services;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.uw.hcde.fizzlab.trace.controller.walk.HttpConnection;
import com.uw.hcde.fizzlab.trace.controller.walk.PathGoogleMapActivity;
import com.uw.hcde.fizzlab.trace.controller.walk.PathJSONParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by sonagrigoryan on 14-11-21.
 */
public class ReadTask extends AsyncTask<String, Void, String> {
    private GoogleMap googleMap;
    private ArrayList<LatLng> points = null;
    public ReadTask(GoogleMap gMap) {
        googleMap = gMap;
    }
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
        final ParserTask parserTask = new ParserTask(googleMap);
        try {
            parserTask.execute(result).get();
            points = parserTask.getPoints();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<LatLng> getPoints() {
        return points;
    }


}
