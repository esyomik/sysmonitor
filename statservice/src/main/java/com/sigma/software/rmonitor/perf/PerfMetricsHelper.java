package com.sigma.software.rmonitor.perf;

import com.sigma.software.rmonitor.data.MetricsInfo;
import com.sigma.software.rmonitor.data.MetricsKind;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public final class PerfMetricsHelper {

    private PerfMetricsHelper() {}


    /**
     * Compares headers list with headers got from the Kafka. The result is <code>true</code>
     * if and only if the argument is not null and if headers is the same as existing.
     * @param headers the existing headers
     * @param newHeaders the headers got from the Kafka
     * @return <code>true</code> if headers are equivalent
     */
    public static boolean isEqual(List<MetricsInfo> headers, Header[] newHeaders) {
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

    /**
     * Creates list of header from the headers array got from the Kafka topic.
     * @param headers the existing headers
     * @param newHeaders the headers got from the Kafka
     */
    public static void createMetricsInfo(List<MetricsInfo> headers, Header[] newHeaders) {
        headers.clear();
        for (Header h : newHeaders) {
            byte[] value = h.value();
            String name = new String(value, 1, value.length - 1, StandardCharsets.UTF_8);
            headers.add(new MetricsInfo(h.key(), name, MetricsKind.create(value[0])));
        }
    }

    /**
     * Creates list of header keys.
     * @param headers the header
     * @return list of string
     */
    public static List<String> getMetricsNames(Headers headers) {
        List<String> names = new ArrayList<>();
        if (headers == null) {
            return names;
        }

        for (Header h : headers) {
            names.add(h.key());
        }
        return names;
    }
}
