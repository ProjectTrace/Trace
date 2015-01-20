package com.uw.hcde.fizzlab.trace.database.callback;

import com.parse.ParseUser;
import com.uw.hcde.fizzlab.trace.database.ParseAnnotation;

import java.util.List;

/**
 * Callback interface for send parse data.
 *
 * @author tianchi
 */
public interface ParseSendCallback {
    /**
     * Finished sending annotation to database.
     *
     * @param returnCode
     * @param annotations
     */
    public void sendAnnotationCallback(int returnCode, List<ParseAnnotation> annotations);


    /**
     * Finished sending drawing to database.
     *
     * @param returnCode
     */
    public void sendDrawingCallback(int returnCode);

    /**
     * Converts name list to user list
     * @param returnCode
     * @param users
     */
    public void convertNameToUserCallback(int returnCode, List<ParseUser> users);
}
