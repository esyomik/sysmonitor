package com.sigma.software.rmonitor.data;

import java.util.List;


/**
 * Interface for accessing data got from the particular host.
 * @param <D> the type of the stored raw data
 */
public interface PerfCounters<D> {

    /**
     * Returns timestamp of the last record
     * @return timestamp of last message in milliseconds
     */
    long getLastTimeStamp();

    /**
     * Actual number of records.
     * @return number of records
     */
    int size();

    /**
     * Capacity of records
     * @return maximal count of records
     */
    int maxSize();

    /**
     * Returns timestamp of particular message.
     * @param index index of message
     * @return timestamp of particular message
     */
    long getTimestamp(int index);

    /**
     * Returns raw data of the particular message
     * @param index index of message
     * @return raw received data
     */
    D getRaw(int index);

    /**
     * Return description of recorded fields.
     * @return list of description for each field
     */
    List<MetricsInfo> getMetricsInfo();
}
