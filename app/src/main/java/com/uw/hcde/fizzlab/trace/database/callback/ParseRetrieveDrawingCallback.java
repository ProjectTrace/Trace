package com.uw.hcde.fizzlab.trace.database.callback;

import com.uw.hcde.fizzlab.trace.database.ParseDrawing;

import java.util.List;

/**
 * Callback interface for retrieving parse data.
 *
 * @author tianchi
 */
public interface ParseRetrieveDrawingCallback {
    /**
     * Retrieves drawings
     *
     * @param returnCode
     * @param drawings
     */
    public void retrieveDrawingsCallback(int returnCode, List<ParseDrawing> drawings);
}
