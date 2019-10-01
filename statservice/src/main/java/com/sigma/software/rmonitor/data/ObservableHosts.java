package com.sigma.software.rmonitor.data;

import java.util.Set;


/**
 * Interface for accessing to observing host names and their metrics.
 * @param <D> the type of the storing raw data
 */
public interface ObservableHosts<D> {

    /**
     * Returns set of names observing hosts
     * @return the set of names of the observing hosts
     */
    Set<String> getObservedHostNames();

    /**
     * Returns array of metrics got from the appropriate host
     * @param hostName the host which metrics need to get
     * @return {@link PerfCounters} interface
     */
    PerfCounters<D> getMetrics(String hostName);
}
