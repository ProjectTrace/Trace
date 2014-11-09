package com.uw.hcde.fizzlab.trace.model.parse;

import android.graphics.Point;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.uw.hcde.fizzlab.trace.model.object.TracePoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains factory for parse database.
 *
 * @author tianchi
 */
public class ParseDataFactory {
    public static final String TAG = "ParseDataFactory";

    /**
     * Sends data to parse database, without any error handling at this moment.
     *
     * @param names
     * @param tracePoints
     */
    public static void sendDrawing(final List<String> names, List<TracePoint> tracePoints) {

        // Initialize data
        final ParseDrawing parseDrawing = new ParseDrawing();
        final List<ParseAnnotation> parseAnnotations = new ArrayList<ParseAnnotation>();
        final List<String> parseAnnotationIDs = new ArrayList<String>();
        final List<String> parseReceiverIDs = new ArrayList<String>();

        // Set up drawing coordinates and annotation points
        List<Integer> xList = new ArrayList<Integer>();
        List<Integer> yList = new ArrayList<Integer>();
        for (TracePoint tracePoint : tracePoints) {
            Point p = tracePoint.point;
            xList.add(p.x);
            yList.add(p.y);

            // An annotation point
            if (tracePoint.annotation != null) {
                final ParseAnnotation annotation = new ParseAnnotation();
                annotation.setX(p.x);
                annotation.setY(p.y);
                annotation.setText(tracePoint.annotation.msg);

                // Add to annotation list
                parseAnnotations.add(annotation);
            }
        }

        // Add data entry
        parseDrawing.setCreator(ParseUser.getCurrentUser().getObjectId());
        parseDrawing.setXList(xList);
        parseDrawing.setYList(yList);

        // Save annotation points to cloud and set annotation list
        ParseObject.saveAllInBackground(parseAnnotations, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Success
                    for (ParseAnnotation annotation : parseAnnotations) {
                        parseAnnotationIDs.add(annotation.getObjectId());
                    }
                    parseDrawing.setAnnotationList(parseAnnotationIDs);
                    parseDrawing.saveInBackground();
                } else {
                    // Something went wrong.
                    Log.d(TAG, "save all annotations failed");
                }
            }
        });

        // Set receiver list
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        Log.d(TAG, "Receiver : " + names.toString());
        query.whereContainedIn("username", names);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, com.parse.ParseException e) {
                if (e == null) {
                    // Success
                    for (ParseUser receiver : parseUsers) {
                        parseReceiverIDs.add(receiver.getObjectId());
                    }
                } else {
                    // Something went wrong.
                    Log.d(TAG, "find receiver IDs failed");
                }
                Log.d(TAG, "receiver id list size: " + parseReceiverIDs.size());
                parseDrawing.setReceiverList(parseReceiverIDs);
                parseDrawing.saveInBackground();
            }
        });
    }
}
