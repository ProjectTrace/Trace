package com.uw.hcde.fizzlab.trace.controller.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.uw.hcde.fizzlab.trace.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sonagrigoryan on 14-11-22.
 */
public class MapUtil {
    private static final String TAG = "MapUtil";

    ArrayList<LatLng> crdList;
    GoogleMap googleMap;
    private Bitmap arrowIcon = null;
    Polyline polylineFuture;
    Polyline polyLineTraced;
    Marker arrowMarker;

//
//    public void drawSegment(Context context, int start, int end) {
//        if (crdList == null || crdList.size() == 0) return;
//        if (polylineFuture != null) {
//            polylineFuture.remove();
//        }
//        PolylineOptions temp = new PolylineOptions();
//        temp.color(Color.BLUE);
//        for (int i = start; i < end; i++) {
//            temp.add(crdList.get(i));
//
//        }
//        polylineFuture = googleMap.addPolyline(temp);
//        drawDirectionArrow(context, crdList.get(end - 1), crdList.get(end));
//    }

    /**
     * Draws a segment with given color.
     *
     * @param list
     * @param color
     * @param map
     * @param maxLength
     */
    public static Polyline drawSegment(List<LatLng> list, int color, GoogleMap map, int maxLength) {

        PolylineOptions temp = new PolylineOptions();
        temp.color(color);
        int meters = 0;
        temp.add(list.get(0));

        for (int i = 0; i < list.size() - 1; i ++) {
            Location l1 = lagLngToLocation(list.get(i));
            Location l2 = lagLngToLocation(list.get(i + 1));

            if (meters + l1.distanceTo(l2) > maxLength) {
                break;
            }
            temp.add(list.get(i + 1));
            meters += l1.distanceTo(l2);
        }
        return map.addPolyline(temp);
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

        if (arrowIcon == null) {
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
    public static LatLng locationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public static Location lagLngToLocation(LatLng latLng) {
        Location l = new Location("Provider");
        l.setLatitude(latLng.latitude);
        l.setLongitude(latLng.longitude);
        return l;
    }
}
