package com.sigma.software.statservice.client;

import com.sigma.software.statservice.client.preferences.RecorderChannelPreferences;

import java.util.List;


class HistogramRecorderChannel implements Channel {

    HistogramRecorderChannel(RecorderChannelPreferences preferences) {
        throw new UnsupportedOperationException("The class HistogramRecorderChannel isn't implemented.");
    }

    @Override
    public boolean write(String hostName, double[] measurements, int nrMeasurements, List<String> header) {
        return false;
    }

    @Override
    public void send(long startTime, long endTime) {

    }
}
