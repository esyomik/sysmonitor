package com.sigma.software.statservice.client;

import com.sigma.software.statservice.client.out.Writer;
import com.sigma.software.statservice.client.preferences.RecorderChannelPreferences;
import com.sigma.software.statservice.data.ConsolidatedStatistics;
import com.sigma.software.statservice.data.NamedStatistics;
import com.sigma.software.statservice.data.StatisticData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Gathers and sends consolidated statistics. Pay attention, the methods in this
 * class aren't synchronized. Consolidated statistics means that the measurements
 * from the different hosts will are added to the same statistics.
 */
class ConsolidatedStatisticRecorderChannel implements Channel {

    private final Writer<ConsolidatedStatistics> writer;
    private final Map<String, Mapper> hostsHeaderMap;
    private final NamedStatistics statistics;


    ConsolidatedStatisticRecorderChannel(RecorderChannelPreferences preferences) throws Exception {
        writer = new Writer<>(preferences.getOutput(), true);
        hostsHeaderMap = new HashMap<>();
        statistics = new NamedStatistics();
    }

    @Override
    public boolean write(String hostName, double[] measurements, int nrMeasurements, List<String> header) {
        Mapper mapper = getOrCreateMapper(hostName, header);
        if (mapper == null) {
            return false;
        }

        return statistics.putValues(measurements, nrMeasurements, mapper.map);
    }

    @Override
    public void send(long startTime, long endTime) {
        List<String> names = new ArrayList<>(hostsHeaderMap.keySet());
        List<StatisticData> statData = statistics.getStatisticsData(8);
        hostsHeaderMap.clear();
        statistics.clear();
        writer.write("ConsolidatedStatistics", new ConsolidatedStatistics(names, startTime, endTime, statData));
    }

    private Mapper getOrCreateMapper(String hostName, List<String> headers) {
        if (headers == null || headers.isEmpty()) {
            return hostsHeaderMap.get(hostName);
        }

        Mapper mapper = new Mapper(statistics.createMapper(headers));
        hostsHeaderMap.put(hostName, mapper);
        return mapper;
    }

    private static class Mapper {
        final int[] map;

        Mapper(int[] map) {
            this.map = map;
        }
    }
}
