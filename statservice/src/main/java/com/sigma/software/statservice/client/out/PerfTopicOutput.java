package com.sigma.software.statservice.client.out;

import com.sigma.software.statservice.client.preferences.SerializerType;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.util.Properties;


/**
 * Writes records to a specified Kafka topic.
 */
class PerfTopicOutput implements PerfOutput {

    private final String topicName;
    private KafkaProducer<String, String> stringProducer;
    private KafkaProducer<String, byte[]> byteArrayProducer;


    PerfTopicOutput(Properties properties, SerializerType serializerType) {
        topicName = properties.getProperty("topic.name");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        if (serializerType == SerializerType.BINARY) {
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
            byteArrayProducer = new KafkaProducer<>(properties);
        } else {
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            stringProducer = new KafkaProducer<>(properties);
        }
    }

    @Override
    public void write(String key, byte[] data) throws IOException {
        byteArrayProducer.send(new ProducerRecord<>(topicName, key, data));
    }

    @Override
    public void write(String key, String data) throws IOException {
        stringProducer.send(new ProducerRecord<>(topicName, key, data));
    }
}
