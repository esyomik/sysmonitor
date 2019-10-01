package com.sigma.software.rmonitor.perf;

import com.sigma.software.rmonitor.data.MetricsInfo;
import com.sigma.software.rmonitor.data.PerfCounters;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.kafka.common.header.Header;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Collection of metrics got from the particular host.
 * @param <T> the type of data
 */
public class RingPerfMetrics<T> implements PerfCounters<T> {

    private final List<MetricsInfo> headers;
    private final CircularFifoQueue<Metrics<T>> perfMetrics;
    private long lastTimeStamp;


    RingPerfMetrics(int size) {
        headers = new ArrayList<>();
        perfMetrics = new CircularFifoQueue<>(size);
        lastTimeStamp = 0L;
    }

    synchronized void add(T data, long timeStamp) {
        perfMetrics.add(new Metrics<>(data, timeStamp));
        lastTimeStamp = timeStamp;
    }

    void setHeaders(Header[] newHeaders) {
        if (PerfMetricsHelper.isEqual(headers, newHeaders)) {
            return;
        }

        synchronized (this) {
            perfMetrics.clear();
            PerfMetricsHelper.createMetricsInfo(headers, newHeaders);
        }
    }

    @Override
    public long getLastTimeStamp() {
        return lastTimeStamp;
    }

    @Override
    synchronized public int size() {
        return perfMetrics.size();
    }

    @Override
    public int maxSize() {
        return perfMetrics.maxSize();
    }

    @Override
    synchronized public long getTimestamp(int index) {
        return perfMetrics.get(index).timeStamp;
    }

    @Override
    synchronized public T getRaw(int index) {
        return perfMetrics.get(index).data;
    }

    @Override
    synchronized public List<MetricsInfo> getMetricsInfo() {
        return Collections.unmodifiableList(headers);
    }

    private static class Metrics<T> {
        private final T data;
        final long timeStamp;

        Metrics(T data, long timeStamp) {
            this.data = data;
            this.timeStamp = timeStamp;
        }
    }
}
