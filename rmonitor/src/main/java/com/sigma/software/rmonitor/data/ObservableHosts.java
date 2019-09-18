package com.sigma.software.rmonitor.data;

import java.util.Set;


public interface ObservableHosts {

    Set<String> getObservedHostNames();

    PerfMetrics getMetrics(String hostName);
}
