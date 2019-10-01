package com.sigma.software.statservice.client;

import com.sigma.software.rmonitor.client.PerfRecorder;
import com.sigma.software.rmonitor.perf.PerfMetricsHelper;
import com.sigma.software.statservice.client.preferences.Preferences;
import com.sigma.software.statservice.client.preferences.RecorderChannelPreferences;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


/**
 * Records measurements. Methods aren't thread safe.
 */
public class StatisticRecorder implements PerfRecorder<String> {

    private final List<ChannelData> outputChannels;
    private double[] metricsValues;


    public StatisticRecorder(Preferences preferences) {
        outputChannels = new ArrayList<>(preferences.getRecorders().length);
        metricsValues = new double[4];

        for (RecorderChannelPreferences recorderPreferences : preferences.getRecorders()) {
            try {
                Channel channel = createOutputChannel(recorderPreferences);
                outputChannels.add(new ChannelData(channel, recorderPreferences));
                System.out.format("Recorder channel %s is successfully created.\n", recorderPreferences);
            } catch (Exception exception) {
                System.err.format("ERROR. Can't create recorder channel %s. Error %s.\n", recorderPreferences, exception);
            }
        }
    }

    @Override
    public void write(ConsumerRecord<String, String> record) {
        String hostName = record.key();
        int nrValues = parseValue(record.value());
        long currentTime = record.timestamp();
        List<String> headers = PerfMetricsHelper.getMetricsNames(record.headers());

        for (ChannelData channel : outputChannels) {
            channel.write(hostName, currentTime, metricsValues, nrValues, headers);
        }
    }

    private int parseValue(String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, ";");
        int nrToken = 0;
        while (tokenizer.hasMoreTokens()) {
            if (metricsValues.length < nrToken + 1) {
                metricsValues = Arrays.copyOf(metricsValues, metricsValues.length * 2);
            }
            try {
                metricsValues[nrToken] = Double.parseDouble(tokenizer.nextToken());
            } catch (Exception exception) {
                metricsValues[nrToken] = 0.0;
            }
            ++nrToken;
        }
        return nrToken;
    }

    private static class ChannelData {
        private final long duration;
        private final Channel channel;
        private final String className;
        private final Pattern inputPattern;
        private long startTime;

        ChannelData(Channel channel, RecorderChannelPreferences preferences) {
            this.channel = channel;
            className = channel.getClass().getSimpleName();
            duration = TimeUnit.SECONDS.toMillis(preferences.getDuration());
            inputPattern = Pattern.compile(preferences.getInputName());
            startTime = 0L;
        }

        void write(String hostName, long timestamp, double[] metricsValues, int nrValues, List<String> headers) {
            if (!inputPattern.matcher(hostName).matches()) {
                return;
            }

            try {
                if (!channel.write(hostName, metricsValues, nrValues, headers)) {
                    if (!headers.isEmpty()) {
                        System.out.format("WARNING: Statistical data isn't written. %s\n", className);
                    }
                    return;
                }

                if (startTime == 0L) {
                    System.out.format("Start gathering statistic, timestamp=%s, %s\n", timestamp, className);
                    startTime = timestamp;
                }
                if (timestamp - startTime >= duration) {
                    System.out.format("Sending statistics. %s\n", className);
                    channel.send(startTime, timestamp);
                    startTime = 0L;
                }
            } catch(Exception exception) {
                System.out.printf("ERROR. %s\n", exception);
                exception.printStackTrace();
            }
        }
    }

    private static Channel createOutputChannel(RecorderChannelPreferences preferences) throws Exception {
        switch(preferences.getType()) {
            case STATISTIC:
                return new StatisticRecorderChannel(preferences);
            case CONSOLIDATED_STATISTIC:
                return new ConsolidatedStatisticRecorderChannel(preferences);
            case HISTOGRAM:
                return new HistogramRecorderChannel(preferences);
            case CONSOLIDATED_HISTOGRAM:
                return new ConsolidatedHistogramRecorderChannel(preferences);
        }
        throw new UnsupportedOperationException("Unknown channel type.");
    }
}
