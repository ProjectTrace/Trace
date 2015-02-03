package com.uw.hcde.fizzlab.trace.dataContainer;

import java.util.List;

/**
 * Singleton class for trace points/latLng list storage
 */
public class TraceDataContainer {

    // Distance in meter
    public static int distance;

    public static List<TracePoint> rawTracePoints;
    public static List<TracePoint> trimmedTracePoints;
    public static String description;
}
