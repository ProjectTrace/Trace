package com.uw.hcde.fizzlab.trace.model.object;

import com.google.android.gms.maps.model.LatLng;

/**
 * Data model that represents a trace latLng.
 *
 * @author tianchi
 */
public class TraceLocation {

    // Geo latLng
    public LatLng latLng;

    // If annotation == null, annotation is invalid.
    public TraceAnnotation annotation;

    public TraceLocation() {
        latLng = null;
        annotation = null;
    }
}
