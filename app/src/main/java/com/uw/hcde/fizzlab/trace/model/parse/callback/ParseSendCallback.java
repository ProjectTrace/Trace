package com.uw.hcde.fizzlab.trace.model.parse.callback;

import com.uw.hcde.fizzlab.trace.model.parse.ParseAnnotation;

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
}
