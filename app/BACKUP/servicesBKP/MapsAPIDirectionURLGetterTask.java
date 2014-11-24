package com.uw.hcde.fizzlab.trace.services;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by sonagrigoryan on 14-11-22.
 */
public class MapsAPIDirectionURLGetterTask {
    ArrayList<LatLng> crdList;

    public MapsAPIDirectionURLGetterTask(ArrayList<LatLng> list) {
        crdList = list;
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
}
