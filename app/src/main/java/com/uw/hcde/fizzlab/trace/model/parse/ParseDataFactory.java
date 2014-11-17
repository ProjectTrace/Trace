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
import com.uw.hcde.fizzlab.trace.model.parse.callback.ParseNameToUserCallback;
import com.uw.hcde.fizzlab.trace.model.parse.callback.ParseSendCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains factory for parse database.
 *
 * @author tianchi
 */
public class ParseDataFactory {
    public static final String TAG = "ParseDataFactory";

//    /**
//     * Gets list of parse drawings or empty list given user ID.
//     * @param userId
//     * @return list of parse drawings or empty list
//     */
//    public static List<ParseDrawing> getDrawings(String userId) {
//        final List<ParseDrawing> res = new ArrayList<ParseDrawing>();
//        ParseQuery<ParseDrawing> query = ParseDrawing.getQuery();
//        query.whereEqualTo(ParseDrawing.KEY_RECEIVER_LIST, userId);
//
//        query.findInBackground(new FindCallback<ParseDrawing>() {
//            public void done(List<ParseDrawing> drawingList, ParseException e) {
//                if (e == null) {
//                    res.addAll(drawingList);
//
//                    Log.d(TAG, "Retrieved " + drawingList.size() + " drawings");
//                } else {
//                    Log.e(TAG, "Retrieved drawings failed " +  e.getMessage());
//                }
//            }
//        });
//        return res;
//    }


    /**
     * Sends annotation points to database.
     *
     * @param tracePoints
     * @param callback
     */
    public static void sendAnnotation(List<TracePoint> tracePoints, final ParseSendCallback callback) {

        // Initialize annotation list
        final List<ParseAnnotation> parseAnnotations = new ArrayList<ParseAnnotation>();

        // Get all annotation points
        for (TracePoint tracePoint : tracePoints) {
            Point p = tracePoint.point;

            // An annotation point
            if (tracePoint.annotation != null) {
                ParseAnnotation annotation = new ParseAnnotation();
                annotation.setX(p.x);
                annotation.setY(p.y);
                annotation.setText(tracePoint.annotation.msg);

                // Add to annotation list
                parseAnnotations.add(annotation);
            }
        }

        // Save annotation points to cloud
        ParseObject.saveAllInBackground(parseAnnotations, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Success
                    callback.sendAnnotationCallback(ParseConstant.SUCCESS, parseAnnotations);
                } else {
                    // Something went wrong.
                    Log.e(TAG, "save all annotations failed " + e.getMessage());
                    callback.sendAnnotationCallback(ParseConstant.FAILED, null);
                }
            }
        });
    }

    /**
     * Converts name list to ParseUser list
     *
     * @param names
     * @param callback
     */
    public static void convertNameToParseUser(List<String> names, final ParseNameToUserCallback callback) {

        // Get user query
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        Log.d(TAG, "Names : " + names.toString());
        query.whereContainedIn("username", names);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null) {
                    // Success
                    callback.nameToUserCallback(ParseConstant.SUCCESS, parseUsers);
                } else {
                    // Something went wrong.
                    Log.e(TAG, "find users failed " + e.getMessage());
                    callback.nameToUserCallback(ParseConstant.FAILED, null);
                }
            }
        });
    }

    /**
     * Sends drawing to database
     *
     * @param description
     * @param receivers
     * @param tracePoints
     * @param annotations
     * @param callback
     */
    public static void sendDrawing(String description, List<ParseUser> receivers,
                                   List<TracePoint> tracePoints, List<ParseAnnotation> annotations,
                                   final ParseSendCallback callback) {

        // Initialize drawing
        final ParseDrawing parseDrawing = new ParseDrawing();

        // Set up drawing coordinates and annotation points
        List<Integer> xList = new ArrayList<Integer>();
        List<Integer> yList = new ArrayList<Integer>();
        for (TracePoint tracePoint : tracePoints) {
            Point p = tracePoint.point;
            xList.add(p.x);
            yList.add(p.y);
        }

        // Add data entry
        parseDrawing.setDescription(description);
        parseDrawing.setCreator(ParseUser.getCurrentUser());
        parseDrawing.setXList(xList);
        parseDrawing.setYList(yList);
        parseDrawing.setReceiverList(receivers);
        parseDrawing.setAnnotationList(annotations);

        // Save
        parseDrawing.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Success
                    callback.sendDrawingCallback(ParseConstant.SUCCESS);
                } else {
                    // Something went wrong.
                    Log.e(TAG, "send drawing failed " + e.getMessage());
                    callback.sendDrawingCallback(ParseConstant.FAILED);
                }
            }
        });

    }
}
