package com.sigma.software.statservice.client.out;

import java.io.IOException;


/**
 * Output stream for writing serialized statistic or performance data.
 * Serialized data can has two forms: binary, represented as byte array and string.
 */
interface PerfOutput {

    /**
     * Writes the specified byte array to this output stream. The general contract
     * is that the order of bytes should be kept.
     * @param key the message key; might be ignored
     * @param data the data
     * @throws IOException if an I/O operation has been failed
     */
    void write(String key, byte[] data) throws IOException;

    /**
     * Writes the specified string to this output stream.
     * @param key the message key; might be ignored
     * @param data the string representation of string
     * @throws IOException if an I/O operation has been failed
     */
    void write(String key, String data) throws IOException;
}
