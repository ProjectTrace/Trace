package com.uw.hcde.fizzlab.trace.util;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for drawing and annotation.
 *
 * @author tianchi
 */
public class DrawUtil {

    private static final String TAG = "DrawUtil";
    private static final int SEGMENT_LENGTH_PL = 50;

    /**
     * Transform Bezier points to Direct points
     *
     * @param bezierPoints
     * @return points
     */
    public static ArrayList<Point> transformPointsBezierToDirect(List<Point> bezierPoints) {
        Path path = getBezierPath(bezierPoints);

        ArrayList<Point> points = new ArrayList<Point>();
        PathMeasure pm = new PathMeasure(path, true);
        float length = pm.getLength();
        float distance = 0f;

        int maxCount = (int) length / SEGMENT_LENGTH_PL;
        int counter = 0;
        float[] aCoordinates = new float[2];
        while ((distance < length) && (counter < maxCount)) {
            // get point from the path
            pm.getPosTan(distance, aCoordinates, null);
            points.add(new Point((int) aCoordinates[0], (int) aCoordinates[1]));
            counter++;
            distance = distance + SEGMENT_LENGTH_PL;
        }
        return points;
    }

    /**
     * Generates direct path given points in pixel location. The path
     * does not close itself.
     *
     * @param points
     * @return path
     */
    public static Path getDirectPath(ArrayList<Point> points) {
        Path path = new Path();
        Point start = points.get(0);
        path.moveTo(start.x, start.y);
        for (int i = 1; i < points.size(); i++) {
            Point p = points.get(i);
            path.lineTo(p.x, p.y);
        }
        return path;
    }

    /**
     * Generates Bezier path given points in pixel location. The path
     * does not close itself.
     *
     * @param points
     * @return path
     */
    public static Path getBezierPath(List<Point> points) {
        Path path = new Path();
        if (points.size() > 1) {
            Point prevPoint = null;
            for (int i = 0; i < points.size(); i++) {
                Point point = points.get(i);

                if (i == 0) {
                    path.moveTo(point.x, point.y);
                } else {
                    float midX = (prevPoint.x + point.x) / 2;
                    float midY = (prevPoint.y + point.y) / 2;

                    if (i == 1) {
                        path.lineTo(midX, midY);
                    } else {
                        path.quadTo(prevPoint.x, prevPoint.y, midX, midY);
                    }
                }
                prevPoint = point;
            }
            path.lineTo(prevPoint.x, prevPoint.y);
        }
        return path;
    }
}
