package com.sigma.software.rmonitor.data;

import java.util.List;


public interface PerfMetrics {

    long getLastTimeStamp();

    int size();

    int maxSize();

    long getTimestamp(int index);

    String getRaw(int index);

    List<MetricsInfo> getMetricsInfo();
}
