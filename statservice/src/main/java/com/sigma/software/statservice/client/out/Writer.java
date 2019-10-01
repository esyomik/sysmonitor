package com.sigma.software.statservice.client.out;

import com.sigma.software.statservice.client.preferences.OutputChannelPreferences;
import com.sigma.software.statservice.client.preferences.OutputStreamType;
import com.sigma.software.statservice.client.preferences.SerializerType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Writer<D> {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(2);

    private final List<Serializer<D>> serializers;
    private final PerfOutput[] outputs;


    public Writer(OutputChannelPreferences[] preferences, boolean ignoreErrors) throws Exception {
        serializers = new ArrayList<>();
        outputs = new PerfOutput[preferences.length];
        for (OutputChannelPreferences pref : preferences) {
            try {
                PerfOutput out = createOutputStream(pref);
                Serializer<D> serializer = createSerializer(pref.getSerializer());
                outputs[serializers.size()] = out;
                serializers.add(serializer);
            } catch (Exception exception) {
                if (!ignoreErrors) {
                    throw exception;
                }
                System.err.printf("Can't create output channel %s. Error: %s", pref, exception);
                exception.printStackTrace();
            }
        }

        if (serializers.size() == 0) {
            throw new IllegalArgumentException("Can't create Writer. There aren't serializers.");
        }
    }

    public void write(String key, D data) {
        EXECUTOR_SERVICE.submit(() -> {
            for (int i = 0; i < serializers.size(); ++i) {
                try {
                    serializers.get(i).serialize(key, data, outputs[i]);
                } catch (IOException e) {
                    System.out.printf("ERROR. Can't write data, error %s\r", e);
                }
            }
        });
    }

    public static void stop(long timeoutSeconds) {
        System.out.println("Stopping writers...");
        EXECUTOR_SERVICE.shutdown();
        try {
            EXECUTOR_SERVICE.awaitTermination(timeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {/*SUPPRESSED*/}
    }

    private Serializer<D> createSerializer(SerializerType type) throws Exception {
        if (type == null) {
            throw new IllegalArgumentException("SerializerFactory.createSerializer() can't create serializer null");
        }

        switch (type) {
            case XML:
                return new XmlSerializer<>();
            case PLAIN:
                return new PlainTextSerializer<>();
            case JSON:
                return new JsonSerializer<>();
            case BINARY:
                return new BinarySerializer<>();
        }
        throw new UnsupportedOperationException("Can't create serializer " + type.toString());
    }

    private static PerfOutput createOutputStream(OutputChannelPreferences pref) {
        OutputStreamType type = pref.getDestination();
        if (type == null) {
            throw new IllegalArgumentException("SerializerFactory.createWriter() can't create writer null");
        }

        Properties properties = pref.getProperties();
        switch (type) {
            case FILE:
                return new PerfFileOutput(properties);
            case STDOUT:
                return PerfStdOutputHolder.instance;
            case TOPIC:
                return new PerfTopicOutput(properties, pref.getSerializer());
        }
        throw new UnsupportedOperationException("Can't create writer " + type.toString());
    }

    private static class PerfStdOutputHolder {
        private static final PerfStdOutput instance = new PerfStdOutput();
    }

}
