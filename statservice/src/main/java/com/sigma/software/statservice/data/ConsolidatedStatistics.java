package com.sigma.software.statservice.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;


/**
 * Wrapper class to make momentary snapshot from the bunch of statistics see
 * {@link Statistic}. It is intended for serializing gathered statistics array
 * to a string representation(Json, XML, etc.) or to a binary data.
 */
@Getter
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConsolidatedStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    List<String> sourceNames;
    long startTimestamp;
    long endTimestamp;
    List<StatisticData> statistics;


    public ConsolidatedStatistics(List<String> sources, long startTime, long endTime, List<StatisticData> stat) {
        sourceNames = sources;
        startTimestamp = startTime;
        endTimestamp = endTime;
        statistics = stat;
    }

    public ConsolidatedStatistics(String source, long startTime, long endTime, List<StatisticData> stat) {
        sourceNames = Collections.singletonList(source);
        startTimestamp = startTime;
        endTimestamp = endTime;
        statistics = stat;
    }
}
