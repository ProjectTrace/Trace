package com.uw.hcde.fizzlab.trace.draw;

import android.graphics.Point;

/**
 * Represents an annotation point
 *
 * @author tianchi
 */
public class AnnotationPoint {
    private Point mPoint;
    private String mMsg;

    public AnnotationPoint(Point point, String msg) {
        mPoint = point;
        mMsg = msg;
    }

    public Point getPoint() {
        return mPoint;
    }

    public int getX() {
        return mPoint.x;
    }

    public int getY() {
        return mPoint.y;
    }

    public String getMsg() {
        return mMsg;
    }

    public void setMsg(String msg) {
        mMsg = msg;
    }
}
