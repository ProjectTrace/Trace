package com.uw.hcde.fizzlab.trace.model.object;

import android.graphics.Point;

/**
 * Represents an annotation point.
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

    /**
     * Gets annotation point.
     * @return
     */
    public Point getPoint() {
        return mPoint;
    }

    /**
     * Sets annotation point.
     * @param p
     */
    public void setPoint(Point p) {
        mPoint = p;
    }

    /**
     * Gets annotation message.
     * @return
     */
    public String getMsg() {
        return mMsg;
    }

    /**
     * Sets annotation message.
     * @param msg
     */
    public void setMsg(String msg) {
        mMsg = msg;
    }
}
