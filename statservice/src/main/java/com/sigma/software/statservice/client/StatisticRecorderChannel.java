package com.sigma.software.statservice.client;

import com.sigma.software.statservice.client.out.Writer;
import com.sigma.software.statservice.client.preferences.RecorderChannelPreferences;
import com.sigma.software.statservice.data.ConsolidatedStatistics;
import com.sigma.software.statservice.data.NamedStatistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Gathers and sends statistics per hosts. Pay attention, the methods in this
 * class aren't synchronized.
 */
class StatisticRecorderChannel implements Channel {

    private final Writer<List<ConsolidatedStatistics>> writer;
    private final Map<String, HostStatistics> statistics;


    StatisticRecorderChannel(RecorderChannelPreferences preferences) throws Exception {
        writer = new Writer<>(preferences.getOutput(), true);
        statistics = new HashMap<>();
    }

    @Override
    public boolean write(String hostName, double[] measurements, int nrMeasurements, List<String> header) {
        HostStatistics hostStatistics = statistics.get(hostName);
        if (hostStatistics == null) {
            hostStatistics = new HostStatistics();
            statistics.put(hostName, hostStatistics);
        }

        return hostStatistics.write(measurements, nrMeasurements, header);
    }

    @Override
    public void send(long startTime, long endTime) {
        List<ConsolidatedStatistics> hostsStatistics = new ArrayList<>(statistics.size());
        for (Map.Entry<String, HostStatistics> entry : statistics.entrySet()) {
            hostsStatistics.add(new ConsolidatedStatistics(entry.getKey(), startTime,
                    endTime, entry.getValue().statistics.getStatisticsData(8)));
        }

        statistics.clear();
        writer.write("[]ConsolidatedStatistics", hostsStatistics);
    }

    private static class HostStatistics {
        private int[] mapper;
        private final NamedStatistics statistics;

        HostStatistics() {
            mapper = new int[0];
            statistics = new NamedStatistics();
        }

        boolean write(double[] metricsValues, int nrValues, List<String> header) {
            if (header != null && !header.isEmpty()) {
                mapper = statistics.createMapper(header);
            }

            return statistics.putValues(metricsValues, nrValues, mapper);
        }
    }
}
