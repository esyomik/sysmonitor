package com.sigma.software.rmonitor.client;

import org.apache.kafka.clients.consumer.ConsumerRecord;


public interface PerfRecorder {

    void write(ConsumerRecord<String, String> record);
}
