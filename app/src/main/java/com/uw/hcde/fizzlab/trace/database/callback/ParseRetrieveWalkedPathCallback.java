package com.uw.hcde.fizzlab.trace.database.callback;

import com.uw.hcde.fizzlab.trace.database.ParseDrawing;
import com.uw.hcde.fizzlab.trace.database.ParseWalkInfo;

import java.util.List;

/**
 * Created by yellowleaf on 5/1/15.
 */
public interface ParseRetrieveWalkedPathCallback {

    public void retrieveWalkedPathCallBack(int returnCode, List<ParseWalkInfo> drawings);
}
