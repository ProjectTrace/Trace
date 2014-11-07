package com.uw.hcde.fizzlab.trace.model;

import android.location.Location;

/**
 * Data model that represents a trace location.
 * May change later.
 *
 * @author tianchi
 */
public class TraceLocation {

    // Geo location
    public Location location;

    // Weather this point contains an annotation
    public boolean hasAnnotation;

    // If hasAnnotation == true, annotation is valid.
    // Otherwise annotation is null.
    public TraceAnnotation annotation;
}
