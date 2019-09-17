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

