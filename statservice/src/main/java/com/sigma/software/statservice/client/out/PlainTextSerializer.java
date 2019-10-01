package com.sigma.software.statservice.client.out;

import java.io.IOException;


/**
 * Serializes statistics into PerfOutput stream as a plain text, see {@link PerfOutput}
 * @param <D> type of serializing data
 */
class PlainTextSerializer<D> implements Serializer<D> {

    PlainTextSerializer() {
    }

    @Override
    public void serialize(String key, D data, PerfOutput output) throws IOException {
        output.write(key, data.toString());
    }
}
