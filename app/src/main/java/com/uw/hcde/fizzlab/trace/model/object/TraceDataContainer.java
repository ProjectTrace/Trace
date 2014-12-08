package com.uw.hcde.fizzlab.trace.model.object;

import java.util.List;

/**
 * Singleton class for trace points/location list storage
 */
public class TraceDataContainer {

    // Distance in meter
    public static int distance;

    public static List<TracePoint> rawTracePoints;
    public static List<TracePoint> tracePoints;
    public static String description;
}
