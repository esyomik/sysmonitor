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


public class PerformanceMonitor {

    private String topic;
    private PerfRecorder<String> recorder;
    private KafkaConsumer<String, String> consumer;
    private ExecutorService executor;


    /**
     * Constructs monitor.
     * @param configuration the configuration to initialize monitor, see {@link Configuration}
     * @param recorder the performance metrics recorder, see {@link PerfRecorder}
     */
    public PerformanceMonitor(Configuration configuration, PerfRecorder<String> recorder) {
        this.topic = configuration.topic;
        this.recorder = recorder;
        Properties props = new Properties();
        props.put("bootstrap.servers", configuration.brokers);
        props.put("group.id", configuration.groupId); // TODO add hostname to groupId
        props.setProperty("enable.auto.commit", "true");
        props.setProperty("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        this.consumer = new KafkaConsumer<>(props);
        this.executor = Executors.newFixedThreadPool(1);
    }

    /**
     * Runs monitoring performance metrics.
     */
    public void startMonitor() {
        executor.submit(() -> {
            System.out.println("Running consumer...");
            try {
                consumer.subscribe(Collections.singleton(topic));
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofDays(7));
                    for (ConsumerRecord<String, String> record : records) {
                        recorder.write(record);
                    }
                }
            } catch (WakeupException exception) {
                /*SUPPRESSED*/
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

}
