package com.sigma.software.statservice.client.out;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;


/**
 * Serializes statistics into PerfOutput stream as Java binaries, see {@link PerfOutput}
 * @param <D> type of serializing data
 */
class BinarySerializer<D> implements Serializer<D> {

    private final ByteArrayOutputStream byteStream;
    private ObjectOutput objectOutput;


    BinarySerializer() throws IOException {
        byteStream = new ByteArrayOutputStream();
        objectOutput = new ObjectOutputStream(byteStream);
        byteStream.reset();
    }

    @Override
    public void serialize(String key, D data, PerfOutput output) throws IOException {
        if (objectOutput == null) {
            return;
        }

        objectOutput.writeObject(data);
        objectOutput.flush();
        output.write(key, byteStream.toByteArray());
        byteStream.reset();
    }
}
