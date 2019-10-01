package com.sigma.software.statservice.client.preferences;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import lombok.Value;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Properties;


@Value
public class Preferences {
    Properties kafkaPerfProperties;
    RecorderChannelPreferences[] recorders;

    public Preferences() {
        kafkaPerfProperties = new Properties();
        recorders = new RecorderChannelPreferences[0];
    }

    public static Preferences fromJSON(String fileName) {
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader(fileName));
            return gson.fromJson(reader, Preferences.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new Preferences();
    }
}
