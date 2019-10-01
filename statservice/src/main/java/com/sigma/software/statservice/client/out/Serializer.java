package com.sigma.software.statservice.client.out;

import java.io.IOException;


// TODO add close method

/**
 * Serializes statistics into PerfOutput stream, see {@link PerfOutput}
 * @param <D> type of serializing data
 */
interface Serializer<D> {

    /**
     * Serializes data and writes them into output stream.
     * @param key the key of message; e.g. type name
     * @param data the data to serialize
     * @param output the output stream, see {@link PerfOutput}
     * @throws IOException if an I/O operation has been failed
     */
    void serialize(String key, D data, PerfOutput output) throws IOException;
}
