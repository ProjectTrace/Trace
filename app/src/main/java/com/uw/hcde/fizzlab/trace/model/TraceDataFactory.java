package com.uw.hcde.fizzlab.trace.model;

import android.location.Location;
import android.util.Log;

import com.uw.hcde.fizzlab.trace.controller.draw.DrawUtil;
import com.uw.hcde.fizzlab.trace.model.object.TraceDataContainer;
import com.uw.hcde.fizzlab.trace.model.object.TraceLocation;
import com.uw.hcde.fizzlab.trace.model.object.TracePoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory class to generate data points for map routing.
 *
 * @author tianchi
 */
public class TraceDataFactory {
    private static final String TAG = "TraceDataFactory";
    private static final double WGS84_RADIUS_METER = 6371200;
    private static final double EARTH_CIRCUMFERENCE_METER = 2 * WGS84_RADIUS_METER * Math.PI;
    private static final String PROVIDER = "Trace_app";

    /**
     * Generates trace geo location data given selected drawing and current location.
     *
     * @param currentLocation current geo location
     * @return list of trace locations scaled from drawing to real world map. If anything goes wrong,
     * null is returned.
     * <p/>
     * The list structure is B -> C -> D -> E -> F -> A
     * B is the first geo point to go, A is the last geo point to go. In our current design,
     * the drawing must be a closing loop, so A is the current location given in the method
     * parameter.
     */
    public static List<TraceLocation> getLocations(Location currentLocation) {
        Log.d(TAG, "current lat: " + currentLocation.getLatitude());
        Log.d(TAG, "current long: " + currentLocation.getLongitude());

        // Gets conversion constants
        double degreesPerMeterLat = 360.0 / EARTH_CIRCUMFERENCE_METER;
        double shrinkFactor = Math.cos(Math.toRadians(currentLocation.getLatitude()));
        double degreesPerMeterLong = degreesPerMeterLat / shrinkFactor;
        Log.d(TAG, "degrees per meter lat: " + degreesPerMeterLat);
        Log.d(TAG, "degrees per meter long: " + degreesPerMeterLong);

        // Points data
        List<TracePoint> tracePoints = TraceDataContainer.sTracePoints;
        TracePoint startingPoint = tracePoints.get(0);

        // Gets scale factor
        int distanceMeter = TraceDataContainer.sDistance;
        double distancePixel = DrawUtil.getPixelLength(tracePoints);
        double scaleFactor = distanceMeter / distancePixel;
        Log.d(TAG, "distanceMeter: " + distanceMeter);
        Log.d(TAG, "distancePixel: " + distancePixel);
        Log.d(TAG, "scaleFactor: " + scaleFactor);

        List<TraceLocation> traceLocations = new ArrayList<TraceLocation>();
        for (int i = 1; i < tracePoints.size(); i++) {

            // Gets vector displacement in pixel, (0, 0) is on top left corner.
            // North -> positive increment
            // East -> positive increment
            TracePoint tracePoint = tracePoints.get(i);
            int pixelDx = tracePoint.point.x - startingPoint.point.x;
            int pixelDy = -(tracePoint.point.y - startingPoint.point.y);

            // Gets vector displacement in meter
            double meterDx = pixelDx * scaleFactor;
            double meterDy = pixelDy * scaleFactor;

            // Generates geo location
            double latitude = generateLat(currentLocation.getLatitude(), degreesPerMeterLat, meterDy);
            double longitude = generateLong(currentLocation.getLongitude(), degreesPerMeterLong, meterDx);
            Location location = new Location(PROVIDER);
            location.setLatitude(latitude);
            location.setLongitude(longitude);

            // Adds to list
            TraceLocation traceLocation = new TraceLocation();
            traceLocation.location = location;
            traceLocation.annotation = tracePoint.annotation;
            traceLocations.add(traceLocation);
        }

        // Last location
        TraceLocation traceCurrentLocation = new TraceLocation();
        traceCurrentLocation.location = currentLocation;
        traceLocations.add(traceCurrentLocation);

        printTraceLocations(traceLocations);
        return traceLocations;
    }

    /**
     * Generates new latitude given start latitude, conversion and displacement of latitude (dy)
     *
     * @param startLat
     * @param degreesPerMeterLat
     * @param displacement
     * @return latitude
     */
    private static double generateLat(double startLat, double degreesPerMeterLat, double displacement) {
        return startLat + degreesPerMeterLat * displacement;
    }

    /**
     * Generates new longitude given start longitude, conversion and displacement of longitude (dx)
     *
     * @param startLong
     * @param degreesPerMeterLong
     * @param displacement
     * @return longitude
     */
    private static double generateLong(double startLong, double degreesPerMeterLong, double displacement) {
        return startLong + degreesPerMeterLong * displacement;
    }

    /**
     * Debug function
     *
     * @param list
     */
    private static void printTraceLocations(List<TraceLocation> list) {
        Log.d(TAG, "start debug list size: " + list.size());
        for (TraceLocation traceLocation : list) {

            Log.d(TAG, "lat: " + traceLocation.location.getLatitude());
            Log.d(TAG, "long: " + traceLocation.location.getLongitude());
        }
    }


}
