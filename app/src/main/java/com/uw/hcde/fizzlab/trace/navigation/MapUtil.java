package com.uw.hcde.fizzlab.trace.navigation;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a map util class.
 *
 * @author sonagrigoryan, tianchi
 */
public class MapUtil {
    private static final String TAG = "MapUtil";
    private static final int WAY_POINT_DISTANCE_METER = 100;
    private static final int WAY_POINT_TOLERANCE_METER = 10;


    /**
     * A pipeline to break list of latLng into way points. Each end of
     * sub list will contain a way point.
     *
     * @param list
     * @return
     */
    public static List<List<LatLng>> normalizeWayPoints(List<List<LatLng>> list) {
        List<List<LatLng>> res = new LinkedList<List<LatLng>>();
        List<LatLng> points = new ArrayList<LatLng>();
        for (List<LatLng> subList : list) {
            points.addAll(subList);
        }

        res.add(new LinkedList<LatLng>());
        res.get(0).add(points.get(0));
        int segmentIndex = 0;
        LatLng prev = points.get(0);

        int i = 1;
        double meters = 0;
        while (i < points.size()) {
            double distance = SphericalUtil.computeDistanceBetween(prev, points.get(i));
            if (meters + distance < WAY_POINT_DISTANCE_METER) {
                meters += distance;
                res.get(segmentIndex).add(points.get(i));
                prev = points.get(i);
                i++;

                if (WAY_POINT_DISTANCE_METER - meters < WAY_POINT_TOLERANCE_METER && i < points.size() - 1) {
                    meters = 0;
                    res.add(new LinkedList<LatLng>());
                    segmentIndex++;
                }

            } else {
                double heading = SphericalUtil.computeHeading(prev, points.get(i));
                LatLng wayPoint = SphericalUtil.computeOffset(prev, WAY_POINT_DISTANCE_METER - meters, heading);
                res.get(segmentIndex).add(wayPoint);
                res.add(new LinkedList<LatLng>());
                segmentIndex++;
                prev = wayPoint;
                meters = 0;
            }
        }

        return res;
    }

    /**
     * Generates Google Map API request URL.
     *
     * @param start
     * @param end
     * @return url
     */
    public static String getMapsApiDirectionsUrl(LatLng start, LatLng end) {
        StringBuilder url = new StringBuilder();
        url.append("http://maps.googleapis.com/maps/api/directions/json?");
        url.append("origin=").append(start.latitude).append(",").append(start.longitude);
        url.append("&destination=").append(end.latitude).append(",").append(end.longitude);
        url.append("&sensor=false&units=metric&mode=walking");
        return url.toString();
    }

    /**
     * Converts Location to LagLng
     *
     * @param location
     * @return
     */
    public static LatLng toLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }
}
