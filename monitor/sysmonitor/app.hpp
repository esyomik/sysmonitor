#pragma once

#include <librdkafka/rdkafka.h>

#include "message-payload.hpp"
#include "sys-counters.hpp"


class App
{
public:
    App();
    ~App();

    void create(const char* brokers, const char* topicName);
    void flush(int timeoutMsec) const;
    bool send(const MessagePayload& data) const;
    void run(const SysCounters& counters, int sendPeriod) const;

    const char* getLastError() const;

private:
    bool send(const void* buffer, size_t length) const;
    static void addComputerName(MessagePayload& data);

private:
    rd_kafka_t* producer_;
    rd_kafka_topic_t* topic_;

};

