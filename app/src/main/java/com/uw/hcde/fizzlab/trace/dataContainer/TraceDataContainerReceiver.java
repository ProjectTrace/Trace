package com.uw.hcde.fizzlab.trace.dataContainer;

import java.util.List;

/**
 * Singleton class for trace points/latLng list storage
 */
public class TraceDataContainerReceiver {

    // Distance in meter
    public static int distance;
    public static String description;

    public static List<TracePoint> rawTracePoints;
    public static List<TracePoint> trimmedTracePoints;
}
