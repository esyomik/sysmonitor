package com.sigma.software.statservice.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Helper class for gathering statistic data from the named parameters. The
 * parameter structure may be changed.
 */
public class NamedStatistics {

    private final Map<String, Integer> metricsMap;
    private final List<Statistic> statistics;


    public NamedStatistics() {
        metricsMap = new HashMap<>();
        statistics = new ArrayList<>();
    }

    /**
     * Creates array for mapping input data to according statistic. This method
     * also creates new statistics if it is necessary.
     * @param headers the name of metrics
     * @return map represented as array of integer values
     */
    public int[] createMapper(List<String> headers) {
        int[] map = new int[headers.size()];
        for (int i = 0; i < map.length; i++) {
            String name = headers.get(i);
            Integer num = metricsMap.get(name);
            if (num == null) {
                num = statistics.size();
                metricsMap.put(name, num);
                statistics.add(new Statistic());
            }
            map[i] = num;
        }
        return map;
    }

    /**
     * Puts bunch of measurements into statistics. This method should be used if
     * your data structure can be changed.
     * @param metricsValues the measurements
     * @param nrValues actual number of measurements
     * @param mapper the array intended to map measurements into statistic counter,
     *               see {@link #createMapper(List)}
     * @return <code>true</code> if measurements is successfully added to statistics
     */
    public boolean putValues(double[] metricsValues, int nrValues, int[] mapper) {
        if (mapper == null || statistics.isEmpty() || statistics.size() != mapper.length) {
            return false;
        }

        for (int i = 0; i < nrValues; ++i) {
            statistics.get(mapper[i]).put(metricsValues[i]);
        }
        return true;
    }

    /**
     * Returns list of statistics data.
     * @param threshold minimal statistical data sample size to add statistic
     * @return list of {@link StatisticData}
     */
    public List<StatisticData> getStatisticsData(int threshold) {
        List<StatisticData> statisticData = new ArrayList<>(statistics.size());
        for (Map.Entry<String, Integer> entry : metricsMap.entrySet()) {
            Statistic statEntry = statistics.get(entry.getValue());
            if (statEntry.getCount() >= threshold) {
                statisticData.add(new StatisticData(entry.getKey(), statEntry));
            }
        }
        return statisticData;
    }

    /**
     * Removes all of the statistics from this class. The statistics will be
     * empty after this call returns.
     */
    public void clear() {
        metricsMap.clear();
        statistics.clear();
    }
}
