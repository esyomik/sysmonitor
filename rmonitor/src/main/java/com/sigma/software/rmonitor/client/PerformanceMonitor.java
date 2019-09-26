package com.sigma.software.rmonitor.client;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Monitor to trace performance metrics. It reads messages from the all partitions.
 */
public class PerformanceMonitor {

    private final String topic;
    private final PerfRecorder<String> recorder;
    private final KafkaConsumer<String, String> consumer;
    private final ExecutorService executor;
    private final long seekPosition;


    /**
     * Constructs monitor and seeks to specific timestamp position.
     * @param configuration the configuration to initialize monitor, see {@link Properties}
     * @param recorder the performance metrics recorder, see {@link PerfRecorder}
     * @param timestamp unix timestamp in milliseconds to seek to
     */
    public PerformanceMonitor(Properties configuration, PerfRecorder<String> recorder, long timestamp) {
        topic = configuration.getProperty("topic.name");
        this.recorder = recorder;
        executor = Executors.newFixedThreadPool(1);
        seekPosition = timestamp;

        configuration.put("key.deserializer", StringDeserializer.class.getName());
        configuration.put("value.deserializer", StringDeserializer.class.getName());
        consumer = new KafkaConsumer<>(configuration);
    }

    /**
     * Runs monitoring metrics. It reads messages from the all partitions. This
     * method doesn't commit offsets because it assumes that the application
     * tries to seek to particular timestamp when it runs.
     */
    public void startMonitor() {
        executor.submit(() -> {
            System.out.println("Running consumer...");
            try {
                seekToTimestamp(seekPosition);
                while (true) {
                    readFromAllPartitions(consumer.poll(Duration.ofDays(7)));
                }
            } catch (WakeupException exception) {
                /*SUPPRESSED*/
            } catch (Exception exception) {
                System.out.println("Can't start monitoring.");
                exception.printStackTrace();
            } finally {
                consumer.close();
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(this::stopMonitor));
    }

    /**
     * Stops monitoring performance metrics.
     */
    public void stopMonitor() {
        if (executor == null) {
            return;
        }

        System.out.println("Stopping consumer...");
        consumer.wakeup();
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {/*SUPPRESSED*/}
    }

    private void readFromAllPartitions(ConsumerRecords<String, String> records) {
        for (TopicPartition partition : records.partitions()) {
            List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
            for (ConsumerRecord<String, String> record : partitionRecords) {
                recorder.write(record);
            }
        }
    }

    private void readFromDefaultPartition(ConsumerRecords<String, String> records) {
        for (ConsumerRecord<String, String> record : records) {
            recorder.write(record);
        }
    }

    // Seeks reading cursor to a particular position. This method also assigns
    // partitions to the consumer, so don't call method KafkaConsumer.subscribe().
    private void seekToTimestamp(long timestamp) {
        List<PartitionInfo> partitions = consumer.partitionsFor(topic);
        if (partitions == null || partitions.size() == 0) {
            return;
        }

        Map<TopicPartition, Long> timestamps = new HashMap<>();
        for (int i = 0; i < partitions.size(); ++i) {
            timestamps.put(new TopicPartition(topic, i), timestamp);
        }
        consumer.assign(timestamps.keySet());

        Map<TopicPartition, OffsetAndTimestamp> offsets = consumer.offsetsForTimes(timestamps);
        for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry : offsets.entrySet()) {
            if (entry.getValue() != null) {
                consumer.seek(entry.getKey(), entry.getValue().offset());
            }
        }
    }
}
