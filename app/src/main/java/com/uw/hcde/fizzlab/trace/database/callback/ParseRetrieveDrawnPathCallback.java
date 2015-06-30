package com.uw.hcde.fizzlab.trace.database.callback;

import com.uw.hcde.fizzlab.trace.database.ParseDrawing;
import com.uw.hcde.fizzlab.trace.database.ParseWalkInfo;

import java.util.List;

/**
 * Callback interface for retrieving parse data.
 *
 * @author Ethan
 */
public interface ParseRetrieveDrawnPathCallback {
    /**
     * Retrieves drawings
     *
     * @param returnCode
     * @param drawings
     */
    public void retrieveDrawingsCallback(int returnCode, List<ParseWalkInfo> drawings);
}
