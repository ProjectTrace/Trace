package com.uw.hcde.fizzlab.trace.model.parse.callback;

import com.uw.hcde.fizzlab.trace.model.parse.ParseDrawing;

import java.util.List;

/**
 * Callback interface for retrieving parse data.
 *
 * @author tianchi
 */
public interface ParseRetrieveCallback {
    /**
     * Retrieves drawings
     * @param returnCode
     * @param drawings
     */
    public void retrieveCallback(int returnCode, List<ParseDrawing> drawings);
}
