package com.sigma.software.statservice.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;


/**
 * Wrapper class to make momentary snapshot from the statistic see {@link Statistic}.
 * It is intended for serializing gathered statistics to a string representation
 * (Json, XML, etc.) or to a binary data.
 */
@Getter
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StatisticData implements Serializable {

    private static final long serialVersionUID = 1L;

    String id;
    int count;
    double min;
    double max;
    double variance;
    double sampleVariance;
    double average;
    double rootMeanSquare;
    double standardDeviation;
    double standardSampleDeviation;


    public StatisticData(String id, Statistic statistic) {
        this.id = id;
        count = statistic.getCount();
        min = statistic.getMin();
        max = statistic.getMax();
        variance = statistic.variance();
        sampleVariance = statistic.sampleVariance();
        average = statistic.average();
        rootMeanSquare = statistic.rootMeanSquare();
        standardDeviation = statistic.standardDeviation();
        standardSampleDeviation = statistic.standardSampleDeviation();
    }

}
