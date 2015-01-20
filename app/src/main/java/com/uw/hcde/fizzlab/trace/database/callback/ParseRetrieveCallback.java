package com.uw.hcde.fizzlab.trace.database.callback;

import com.uw.hcde.fizzlab.trace.database.ParseDrawing;

import java.util.List;

/**
 * Callback interface for retrieving parse data.
 *
 * @author tianchi
 */
public interface ParseRetrieveCallback {
    /**
     * Retrieves drawings
     *
     * @param returnCode
     * @param drawings
     */
    public void retrieveDrawingsCallback(int returnCode, List<ParseDrawing> drawings);

    /**
     * Retrieves annotations
     *
     * @param returnCode
     */
    public void retrieveAnnotationsCallback(int returnCode);

    /**
     * Retrieves creator
     *
     * @param returnCode
     */
    public void retrieveCreatorsCallback(int returnCode);
}
