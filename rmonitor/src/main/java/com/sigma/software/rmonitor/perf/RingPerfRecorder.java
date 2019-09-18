package com.sigma.software.rmonitor.perf;

import com.sigma.software.rmonitor.client.PerfRecorder;
import com.sigma.software.rmonitor.data.ObservableHosts;
import com.sigma.software.rmonitor.data.PerfMetrics;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class RingPerfRecorder implements PerfRecorder, ObservableHosts {

    private Map<String, RingPerfMetrics> perfData;
    private int dataSize;


    public RingPerfRecorder(int size) {
        perfData = new HashMap<>();
        dataSize = size;
    }

    @Override
    public void write(ConsumerRecord<String, String> record) {
        System.out.format("#%s. Partition: %s, offset: %s, timestamp: %s, value: %s. Headers: %s\n",
                record.key(), record.partition(), record.offset(),
                record.timestamp(), record.value(), record.headers());

        RingPerfMetrics metrics = getOrAddMetrics(record.key());
        Headers headers = record.headers();
        if (headers != null) {
            metrics.setHeaders(headers.toArray());
        }
        metrics.add(record.value(), record.timestamp());
    }

    @Override
    public Set<String> getObservedHostNames() {
        return perfData.keySet();
    }

    @Override
    public PerfMetrics getMetrics(String hostName) {
        return perfData.get(hostName);
    }


    private RingPerfMetrics getOrAddMetrics(String hostName) {
        RingPerfMetrics metrics = perfData.get(hostName);
        if (metrics != null) {
            return metrics;
        }

        RingPerfMetrics newMetrics = new RingPerfMetrics(dataSize);
        perfData.put(hostName, newMetrics);
        return newMetrics;
    }
}
