package com.sigma.software.rmonitor.perf;

import com.sigma.software.rmonitor.client.PerfRecorder;
import com.sigma.software.rmonitor.data.ObservableHosts;
import com.sigma.software.rmonitor.data.PerfCounters;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Implementation of the metrics recorder. This class implements two interfaces:
 * {@link PerfRecorder} and {@link ObservableHosts}. Result of measurements are
 * written to a cyclic buffer.
 * @param <D> type of recording data
 */
public class RingPerfRecorder<D> implements PerfRecorder<D>, ObservableHosts<D> {

    private final Map<String, RingPerfMetrics<D>> perfData;
    private final int dataSize;


    public RingPerfRecorder(int size) {
        perfData = new ConcurrentHashMap<>();
        dataSize = size;
    }

    @Override
    public void write(ConsumerRecord<String, D> record) {
        RingPerfMetrics<D> metrics = getOrAddMetrics(record.key());
        Headers headers = record.headers();
        if (headers != null) {
            Header[] headerArray = headers.toArray();
            if (headerArray.length > 0) {
                metrics.setHeaders(headerArray);
            }
        }
        metrics.add(record.value(), record.timestamp());
    }

    @Override
    public Set<String> getObservedHostNames() {
        return perfData.keySet();
    }

    @Override
    public PerfCounters<D> getMetrics(String hostName) {
        return perfData.get(hostName);
    }


    private RingPerfMetrics<D> getOrAddMetrics(String hostName) {
        RingPerfMetrics<D> metrics = perfData.get(hostName);
        if (metrics != null) {
            return metrics;
        }

        RingPerfMetrics<D> newMetrics = new RingPerfMetrics<>(dataSize);
        perfData.put(hostName, newMetrics);
        return newMetrics;
    }
}
