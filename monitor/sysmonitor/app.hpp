#pragma once

#include <librdkafka/rdkafka.h>

#include "message-payload.hpp"
#include "sys-counters.hpp"
#include "third/json.hpp"


/**
 * Gathers performance data and sends it to specified topic.
 * Each message is a string array of double values separated by a semicolon.
 * Periodically, the description of metrics is sent as a Kafka header.
 * Header is an array of pairs<Key, Value>:<br>
 * <Key> a string representation of unique identifier of metrics<br>
 * <Value> a byte buffer; first item is kind of metrics, other
 * items is a byte representation of metrics name in UTF-8 encoding
 */
class App
{
public:
    App();
    ~App();

    void create(const nlohmann::json& config, const char* topicName);
    void destroy();
    void flush(int timeoutMsec) const;
    void run(const SysCounters& counters, int sendPeriod) const;

    const char* getLastError() const;

private:
    bool send(const void* buffer, size_t length, rd_kafka_headers_t* headers) const;
    rd_kafka_headers_t* createHeaders(const SysCounters& counters) const;

private:
    rd_kafka_t* producer_;
    std::string topicName_;

};

