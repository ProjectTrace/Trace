package com.uw.hcde.fizzlab.trace.model;

import android.graphics.Point;

import java.util.ArrayList;

/**
 * Model for drawing data. It is client's responsibility to
 * initialize fields in this class.
 */
public class DrawingData {

    // Points in pixel
    public ArrayList<Point> points;

    // Euclidean length in pixel
    public int length;
}
