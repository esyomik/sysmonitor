package com.sigma.software.rmonitor.client;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class PerformanceMonitor implements Runnable {

    private String topic;
    private KafkaConsumer<String, String> consumer;
    private ExecutorService executor;


    public PerformanceMonitor(Configuration configuration) {
        topic = configuration.topic;
        Properties props = new Properties();
        props.put("bootstrap.servers", configuration.brokers);
        props.put("group.id", configuration.groupId); // TODO add hostname to groupId
        props.setProperty("enable.auto.commit", "true");
        props.setProperty("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        this.consumer = new KafkaConsumer<>(props);
    }

    public void startMonitor() {
        executor = Executors.newFixedThreadPool(1);
        executor.submit(this);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                stopMonitor();
            }
        });
    }

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

    @Override
    public void run() {
        System.out.println("Runs consumer...");
        try {
            consumer.subscribe(Collections.singleton(topic));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofDays(7));
                for (ConsumerRecord<String, String> record : records) {
                    processRecord(record);
                }
            }
        } catch (WakeupException exception) {
            /*SUPPRESSED*/
        } finally {
            consumer.close();
        }
    }

    private void processRecord(ConsumerRecord<String, String> record) {
        System.out.format("#%s. Partition: %s, offset: %s, timestamp: %s, value: %s. Headers: %s\n",
                record.key(), record.partition(), record.offset(),
                record.timestamp(), record.value(), record.headers());
    }
}
