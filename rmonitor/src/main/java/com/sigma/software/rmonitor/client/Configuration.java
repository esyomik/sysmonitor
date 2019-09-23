package com.sigma.software.rmonitor.client;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;


public class Configuration {

    final String brokers;
    final String topic;
    final String groupId;
    private final long period;


    private Configuration() {
        brokers = "";
        topic = "";
        groupId = "";
        period = 2L;
    }

    public static Configuration load(String file) {
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(file));
            return gson.fromJson(reader, Configuration.class);
        } catch (Exception exception) {
            return new Configuration();
        }
    }

    public long updatePeriod() {
        return period;
    }
}
