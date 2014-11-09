package com.uw.hcde.fizzlab.trace.model.object;

import android.graphics.Point;

/**
 * Represents a trace points drew on screen.
 *
 * @author tianchi
 */
public class TracePoint {

    // Pixel coordinate
    public Point point;

    // If annotation == null, annotation is invalid.
    public TraceAnnotation annotation;

    public TracePoint() {
        point = null;
        annotation = null;
    }
}
