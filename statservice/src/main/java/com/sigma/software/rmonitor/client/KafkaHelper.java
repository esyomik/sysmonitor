package com.sigma.software.rmonitor.client;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;

import java.util.List;


public final class KafkaHelper {

    private KafkaHelper() {}

    /**
     * Reads records from all partitions and write them into the recorder.
     * @param records the records
     * @param recorder the recorder, see {@link PerfRecorder}
     * @param <T> the type of the topic data
     */
    public static <T> void readFromAllPartitions(ConsumerRecords<String, T> records, PerfRecorder<T> recorder) {
        for (TopicPartition partition : records.partitions()) {
            List<ConsumerRecord<String, T>> partitionRecords = records.records(partition);
            for (ConsumerRecord<String, T> record : partitionRecords) {
                recorder.write(record);
            }
        }
    }

    /**
     * Reads records from the default partition and write them into the recorder.
     * @param records the records
     * @param recorder the recorder, see {@link PerfRecorder}
     * @param <T> the type of the topic data
     */
    public static <T> void readFromDefaultPartition(ConsumerRecords<String, T> records, PerfRecorder<T> recorder) {
        for (ConsumerRecord<String, T> record : records) {
            recorder.write(record);
        }
    }
}
