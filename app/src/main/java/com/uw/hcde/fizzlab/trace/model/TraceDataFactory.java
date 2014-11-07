package com.uw.hcde.fizzlab.trace.model;

import android.content.Context;
import android.location.Location;

import java.util.List;

/**
 * Factory class to generate data points for map routing.
 *
 * @author tianchi
 */
public class TraceDataFactory {

    /**
     * Generates trace geo location data given selected drawing and current location.
     *
     * @param drawingIdentifier identifier for selected drawing
     * @param currentLocation   current geo location
     * @param context           application context
     * @return list of trace locations scaled from drawing to real world map. If anything goes wrong,
     * null is returned.
     * <p/>
     * The list structure is B -> C -> D -> E -> F -> A
     * B is the first geo point to go, A is the last geo point to go. In our current design,
     * the drawing must be a closing loop, so A is the current location given in the method
     * parameter.
     * <p/>
     * <p/>
     * To use this method, do the following in MapActivity:
     * Intent intent = getIntent();
     * int drawingIdentifier = intent.getIntExtra(ChooseDrawingActivity.EXTRA_INT_DRAWING_IDENTIFIER, -1);
     * List<TraceLocation> locations = TraceDataFactory.getLocations(drawingIdentifier, currentLocation, this);
     */
    public static List<TraceLocation> getLocations(int drawingIdentifier,
                                                   Location currentLocation, Context context) {

        // TODO: this method will be implemented later. Add your test locations here.
        return null;
    }
}
