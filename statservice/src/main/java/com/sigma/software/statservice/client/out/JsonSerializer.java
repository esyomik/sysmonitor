package com.sigma.software.statservice.client.out;

import com.google.gson.Gson;

import java.io.IOException;


/**
 * Serializes statistics into PerfOutput stream as Json string, see {@link PerfOutput}
 * @param <D> type of serializing data
 */
class JsonSerializer<D> implements Serializer<D> {

    private final Gson gson;


    JsonSerializer() {
        gson = new Gson();
    }

    @Override
    public void serialize(String key, D data, PerfOutput output) throws IOException {
        String stringData = gson.toJson(data);
        output.write(key, stringData);
    }
}
