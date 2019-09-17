package com.sigma.software.rmonitor.client;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;


public class Configuration {

    public String brokers;
    public String topic;
    public String groupId;
    public int period;


    private Configuration() {
        brokers = "";
        topic = "";
        groupId = "";
        period = 2;
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
}
