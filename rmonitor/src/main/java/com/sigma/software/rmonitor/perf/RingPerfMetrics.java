package com.sigma.software.rmonitor.perf;

import com.sigma.software.rmonitor.data.MetricsInfo;
import com.sigma.software.rmonitor.data.MetricsKind;
import com.sigma.software.rmonitor.data.PerfMetrics;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.kafka.common.header.Header;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RingPerfMetrics implements PerfMetrics {

    private List<MetricsInfo> headers;
    private CircularFifoQueue<Metrics> perfMetrics;
    private long lastTimeStamp;


    RingPerfMetrics(int size) {
        headers = new ArrayList<>();
        perfMetrics = new CircularFifoQueue<>(size);
        lastTimeStamp = 0L;
    }

    void add(String data, long timeStamp) {
        perfMetrics.add(new Metrics(data, timeStamp));
        lastTimeStamp = timeStamp;
    }

    void setHeaders(Header[] newHeaders) {
        if (isEqual(headers, newHeaders)) {
            return;
        }
        perfMetrics.clear();
        headers.clear();
        for (Header h : newHeaders) {
            headers.add(new MetricsInfo(h.key(), MetricsKind.create(h.value()[0])));
        }
    }

    @Override
    public long getLastTimeStamp() {
        return lastTimeStamp;
    }

    @Override
    public int size() {
        return perfMetrics.size();
    }

    @Override
    public int maxSize() {
        return perfMetrics.maxSize();
    }

    @Override
    public long getTimestamp(int index) {
        return perfMetrics.get(index).timeStamp;
    }

    @Override
    public String getRaw(int index) {
        return perfMetrics.get(index).data;
    }

    @Override
    public List<MetricsInfo> getMetricsInfo() {
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
            if (!headers.get(i).getName().equals(newHeaders[i].key())) {
                return false;
            }
        }
        return true;
    }


    private static class Metrics {
        private String data;
        long timeStamp;

        Metrics(String data, long timeStamp) {
            this.data = data;
            this.timeStamp = timeStamp;
        }
    }
}
