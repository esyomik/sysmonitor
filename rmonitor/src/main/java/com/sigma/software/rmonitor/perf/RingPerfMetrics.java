package com.sigma.software.rmonitor.perf;

import com.sigma.software.rmonitor.data.MetricsInfo;
import com.sigma.software.rmonitor.data.MetricsKind;
import com.sigma.software.rmonitor.data.PerfCounters;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.kafka.common.header.Header;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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
        if (isEqual(headers, newHeaders)) {
            return;
        }

        synchronized (this) {
            perfMetrics.clear();
            headers.clear();
            for (Header h : newHeaders) {
                byte[] value = h.value();
                byte[] nameBytes = Arrays.copyOfRange(value, 1, value.length);
                String name = new String(nameBytes, StandardCharsets.UTF_8);
                headers.add(new MetricsInfo(h.key(), name, MetricsKind.create(value[0])));
            }
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

    private static boolean isEqual(List<MetricsInfo> headers, Header[] newHeaders) {
        if (headers == null || newHeaders == null) {
            return false;
        }

        if (headers.size() != newHeaders.length) {
            return false;
        }

        for(int i = 0; i < newHeaders.length; ++i) {
            if (!headers.get(i).getId().equals(newHeaders[i].key())) {
                return false;
            }
        }
        return true;
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
