package com.sigma.software.statservice.client;

import java.io.IOException;
import java.util.List;


/**
 * This class is responsible for gather particular statistic information from the
 * performance counters and sending them to the specified output streams
 */
interface Channel {

    /**
     * Writes one bunch measurements to a statistic data. Number of measurements
     * should be same as a number of metrics info.
     * @param hostName the name of the host which measurements are belonged to it
     * @param measurements the measurements to add to statistical data
     * @param nrMeasurements actual number of measurements
     * @param header the metrics description; this field can be <code>null</code>
     * @return <code>true</code> if measurements successfully are added to the statistical data
     */
    boolean write(String hostName, double[] measurements, int nrMeasurements, List<String> header);

    /**
     * Sends statistical data to output streams.
     * @param startTime start time of gathered statistics, milliseconds
     * @param endTime end time of gathered statistics, milliseconds
     * @throws IOException if something went wrong
     */
    void send(long startTime, long endTime) throws IOException;
}
