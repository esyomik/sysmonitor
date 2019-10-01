package com.sigma.software.statservice.client;

import com.sigma.software.statservice.client.preferences.RecorderChannelPreferences;

import java.util.List;


class ConsolidatedHistogramRecorderChannel implements Channel {

    ConsolidatedHistogramRecorderChannel(RecorderChannelPreferences preferences) {
        throw new UnsupportedOperationException("The class ConsolidatedHistogramRecorderChannel isn't implemented.");
    }

    @Override
    public boolean write(String hostName, double[] measurements, int nrMeasurements, List<String> header) {
        return false;
    }

    @Override
    public void send(long startTime, long endTime) {

    }
}
