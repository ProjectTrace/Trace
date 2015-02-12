package com.uw.hcde.fizzlab.trace.database;

import android.graphics.Point;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.uw.hcde.fizzlab.trace.dataContainer.TraceAnnotation;
import com.uw.hcde.fizzlab.trace.dataContainer.TracePoint;
import com.uw.hcde.fizzlab.trace.database.callback.ParseAddFriendCallback;
import com.uw.hcde.fizzlab.trace.database.callback.ParseRetrieveDrawingCallback;
import com.uw.hcde.fizzlab.trace.database.callback.ParseRetrieveFriendsCallback;
import com.uw.hcde.fizzlab.trace.database.callback.ParseSendDrawingCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains factory for parse database.
 *
 * @author tianchi
 */
public class ParseDataFactory {
    public static final String TAG = "ParseDataFactory";

    public static void retrieveMyDrawings(ParseUser user, final ParseRetrieveDrawingCallback callback) {
        // Set up query
        ParseQuery<ParseDrawing> query = ParseDrawing.getQuery();
        query.whereEqualTo(ParseDrawing.KEY_CREATOR, user);
        query.include(ParseDrawing.KEY_RECEIVER_LIST);
        query.include(ParseDrawing.KEY_ANNOTATION_LIST);

        // Start query
        query.findInBackground(new FindCallback<ParseDrawing>() {
            public void done(List<ParseDrawing> drawingList, ParseException e) {
                if (e == null) {
                    callback.retrieveDrawingsCallback(ParseConstant.SUCCESS, drawingList);
                } else {
                    Log.e(TAG, "Retrieve drawings failed " + e.getMessage());
                    callback.retrieveDrawingsCallback(ParseConstant.FAILED, null);
                }
            }
        });
    }

    /**
     * Gets list of parse drawings or empty list given user.
     *
     * @param user
     * @return list of parse drawings or empty list
     */
    public static void retrieveDrawings(ParseUser user, final ParseRetrieveDrawingCallback callback) {
        // Set up query
        ParseQuery<ParseDrawing> query = ParseDrawing.getQuery();
        query.whereEqualTo(ParseDrawing.KEY_RECEIVER_LIST, user);
        query.include(ParseDrawing.KEY_CREATOR);
        query.include(ParseDrawing.KEY_ANNOTATION_LIST);

        // Start query
        query.findInBackground(new FindCallback<ParseDrawing>() {
            public void done(List<ParseDrawing> drawingList, ParseException e) {
                if (e == null) {
                    callback.retrieveDrawingsCallback(ParseConstant.SUCCESS, drawingList);
                } else {
                    Log.e(TAG, "Retrieve drawings failed " + e.getMessage());
                    callback.retrieveDrawingsCallback(ParseConstant.FAILED, null);
                }
            }
        });
    }

    /**
     * Adds friend for target user, see ParseAddFriendCallback for details.
     *
     * @param currentUser
     * @param friendEmail
     * @param func
     */
    public static void addFriend(final ParseUser currentUser, String friendEmail, final ParseAddFriendCallback func) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", friendEmail);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null && parseUsers.size() > 0) {
                    currentUser.addUnique(ParseConstant.KEY_FRIEND_LIST, parseUsers.get(0));
                    currentUser.saveInBackground();
                    func.parseAddFriendCallback(ParseConstant.SUCCESS);
                } else {
                    func.parseAddFriendCallback(ParseConstant.FAILED);
                }
            }
        });
    }

    /**
     * Retrieves friends for target user, see ParseRetrieveFriendsCallback for details.
     *
     * @param currentUser
     * @param func
     */
    public static void retrieveFriends(ParseUser currentUser, final ParseRetrieveFriendsCallback func) {
        List<ParseUser> friendsList = currentUser.getList(ParseConstant.KEY_FRIEND_LIST);
        if (friendsList == null || friendsList.size() == 0) {
            func.parseRetrieveFriendsCallback(ParseConstant.SUCCESS, new ArrayList<ParseUser>());
            return;
        }

        boolean isDataAvaiable = true;
        for (ParseUser user : friendsList) {
            if (!user.isDataAvailable()) {
                isDataAvaiable = false;
                break;
            }
        }
        if (isDataAvaiable) {
            func.parseRetrieveFriendsCallback(ParseConstant.SUCCESS, friendsList);
            return;
        }

        ParseObject.fetchAllInBackground(friendsList, new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null) {
                    func.parseRetrieveFriendsCallback(ParseConstant.SUCCESS, parseUsers);
                } else {
                    Log.e(TAG, "Retrieve friend list failed " + e.getMessage());
                    func.parseRetrieveFriendsCallback(ParseConstant.FAILED, parseUsers);
                }
            }
        });
    }

    /**
     * Sends annotation points to database.
     *
     * @param tracePoints
     * @param callback
     */
    public static void sendAnnotation(List<TracePoint> tracePoints, final ParseSendDrawingCallback callback) {

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
                                   final ParseSendDrawingCallback callback) {

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

    /**
     * Convert parse drawings to trace points.
     *
     * @param drawing
     * @return trace points
     */
    public static List<TracePoint> convertToTracePoints(ParseDrawing drawing) {
        List<TracePoint> tracePoints = new ArrayList<TracePoint>();

        List<Integer> xList = drawing.getXList();
        List<Integer> yList = drawing.getYList();
        List<ParseAnnotation> annotations = drawing.getAnnotationList();

        for (int i = 0; i < xList.size(); i++) {
            TracePoint tracePoint = new TracePoint();
            tracePoint.point = new Point(xList.get(i), yList.get(i));
            tracePoints.add(tracePoint);
        }

        int i = 0;
        for (ParseAnnotation annotation : annotations) {
            while (i < tracePoints.size()) {
                TracePoint tracePoint = tracePoints.get(i);
                Point point = tracePoint.point;
                if (annotation.getX() == point.x && annotation.getY() == point.y) {
                    TraceAnnotation traceAnnotation = new TraceAnnotation();
                    traceAnnotation.msg = annotation.getText();
                    tracePoint.annotation = traceAnnotation;
                    break;
                }
                i++;
            }
        }
        return tracePoints;
    }
}
