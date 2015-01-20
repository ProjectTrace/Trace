package com.uw.hcde.fizzlab.trace.userInterface.drawing;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;

import com.uw.hcde.fizzlab.trace.dataContainer.TracePoint;

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
    private static final int EPSILON = 40;

    /**
     * Transforms raw points to normalized points that separates by SEGMENT_LENGTH_PL on path
     *
     * @param rawPoints
     * @return normalized points
     */
    public static List<Point> normalizePoints(List<Point> rawPoints) {
        Path path = getBezierPath(rawPoints);

        ArrayList<Point> points = new ArrayList<Point>();
        PathMeasure pm = new PathMeasure(path, true);
        float length = pm.getLength();
        float distance = 0f;

        int maxCount = (int) length / SEGMENT_LENGTH_PL;
        int counter = 0;
        float[] aCoordinates = new float[2];
        while ((distance < length) && (counter <= maxCount)) {
            // get point from the path
            pm.getPosTan(distance, aCoordinates, null);
            points.add(new Point((int) aCoordinates[0], (int) aCoordinates[1]));
            counter++;
            distance = distance + SEGMENT_LENGTH_PL;
        }
        return points;
    }

    /**
     * Generates direct path given points in pixel latLng. The path
     * does not close itself.
     *
     * @param points
     * @return path
     */
    public static Path getDirectPath(List<Point> points) {
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
     * Generates Bezier path given points in pixel latLng. The path
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

    /**
     * Converts tracePoints to points.
     * @param tracePoints
     * @return
     */
    public static List<Point> tracePointsToPoints(List<TracePoint> tracePoints) {
        List<Point> points = new ArrayList<Point>();
        for (TracePoint tracePoint : tracePoints) {
            points.add(tracePoint.point);
        }
        return points;
    }

    /**
     * Converts points to TracePoints
     *
     * @param points
     * @return tracePoints
     */
    public static List<TracePoint> pointsToTracePoints(List<Point> points) {
        List<TracePoint> tracePoints = new ArrayList<TracePoint>();
        for (Point p : points) {
            TracePoint tracePoint = new TracePoint();
            tracePoint.point = p;
            tracePoints.add(tracePoint);
        }
        return tracePoints;
    }

    /**
     * Calculates pixel circumference, assuming enclosed drawing.
     *
     * @param tracePoints
     * @return
     */
    public static double getPixelLength(List<TracePoint> tracePoints) {
        double res = 0;
        for (int i = 1; i < tracePoints.size(); i++) {
            Point prev = tracePoints.get(i - 1).point;
            Point curr = tracePoints.get(i).point;
            res += getEuclideanDistance(prev, curr);
        }

        Point first = tracePoints.get(0).point;
        Point last = tracePoints.get(tracePoints.size() - 1).point;
        res += getEuclideanDistance(first, last);
        return res;
    }

    /**
     * Calculates Euclidean distance between two points.
     *
     * @param p1
     * @param p2
     * @return
     */
    public static double getEuclideanDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    /**
     * Checks if a point is on line, given epsilon
     *
     * @param A
     * @param B
     * @param P
     * @return true or false
     */
    public static boolean isPointOnLine(TracePoint A, TracePoint B, TracePoint P) {
        double normalLength = Math.sqrt((B.point.x - A.point.x) * (B.point.x - A.point.x) + (B.point.y - A.point.y) * (B.point.y - A.point.y));
        double distance = Math.abs((P.point.x - A.point.x) * (B.point.y - A.point.y) - (P.point.y - A.point.y) * (B.point.x - A.point.x)) / normalLength;
        return distance <= EPSILON;
    }


    /**
     * Trims list of trace points. list size >= 3
     *
     * @param list
     * @return
     */
    public static List<TracePoint> trimPoints(List<TracePoint> list) {

        List<TracePoint> res = new ArrayList<TracePoint>();

        TracePoint startPoint = list.get(0);
        TracePoint controlPoint = list.get(1);
        TracePoint endPoint = controlPoint;
        res.add(startPoint);

        for (int i = 2; i < list.size(); i++) {
            TracePoint point = list.get(i);
            if (isPointOnLine(startPoint, controlPoint, point)) {
                endPoint = point;
                // An annotation point
                if (endPoint.annotation != null) {
                    res.add(endPoint);
                }
            } else {
                // Avoid adding annotation point again
                if (!res.contains(endPoint)) {
                    res.add(endPoint);
                }

                startPoint = endPoint;
                controlPoint = point;
                endPoint = point;
            }
        }

        TracePoint fistPoint = list.get(0);
        if (!isPointOnLine(startPoint, controlPoint, fistPoint)) {
            res.add(endPoint);
        } else if (endPoint.annotation != null && !res.contains(endPoint)) {
            res.add(endPoint);
        }

        return res;
    }
}
