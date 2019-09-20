package com.sigma.software.rmonitor.client;

import org.apache.kafka.clients.consumer.ConsumerRecord;


/**
 * Parametrized interface for storing records from the Kafka consumer.
 * @param <D> type of recorded data
 */
public interface PerfRecorder<D> {

    /**
     * Writes records.
     * @param record the record, {@link org.apache.kafka.clients.consumer.ConsumerRecord ConsumerRecord}
     */
    void write(ConsumerRecord<String, D> record);
}
