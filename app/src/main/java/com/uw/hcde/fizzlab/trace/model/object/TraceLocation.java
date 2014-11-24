package com.uw.hcde.fizzlab.trace.model.object;

import android.location.Location;

/**
 * Data model that represents a trace location.
 *
 * @author tianchi
 */
public class TraceLocation {

    // Geo location
    public Location location;

    // If annotation == null, annotation is invalid.
    public TraceAnnotation annotation;

    public TraceLocation() {
        location = null;
        annotation = null;
    }
    public Location getLocation() {
        return location;
    }
}
