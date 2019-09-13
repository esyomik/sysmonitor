#include <thread>
#include <stdexcept>

#include "app.hpp"


App::App()
    : producer_(nullptr)
    , topic_(nullptr) {}


App::~App() {
    if (topic_) {
        rd_kafka_topic_destroy(topic_);
    }
    if (producer_) {
        rd_kafka_destroy(producer_);
    }
}


static void delivery_callback(rd_kafka_t *rk, const rd_kafka_message_t *rkmessage, void *opaque) {
    if (rkmessage->err) {
        fprintf(stderr, "%% Message delivery failed: %s\n", rd_kafka_err2str(rkmessage->err));
    }
}


void App::create(const char* brokers, const char* topicName) {
    char errstr[512];
    rd_kafka_conf_t* configuration = rd_kafka_conf_new();
    if (rd_kafka_conf_set(configuration, "bootstrap.servers", brokers, errstr, sizeof(errstr)) != RD_KAFKA_CONF_OK) {
        throw std::logic_error("Can't initialize kafka configuration");
    }
    rd_kafka_conf_set_dr_msg_cb(configuration, delivery_callback);

    producer_ = rd_kafka_new(RD_KAFKA_PRODUCER, configuration, errstr, sizeof(errstr));
    if (!producer_) {
        throw std::logic_error("Can't create producer object");
    }

    topic_ = rd_kafka_topic_new(producer_, topicName, nullptr);
    if (topic_ == nullptr) {
        throw std::logic_error("Can't create topic");
    }
}


void App::flush(int timeoutMsec) const {
    rd_kafka_flush(producer_, timeoutMsec);
}


bool App::send(const MessagePayload& data) const {
    return send(data.buffer_.c_str(), data.buffer_.length());
}


void App::run(const SysCounters& counters, int sendPeriod) const {
    counters.collect();
    while (true) {
        std::this_thread::sleep_for(std::chrono::seconds(sendPeriod));
        counters.collect();

        MessagePayload data;
        data.add("T", std::to_string(time(nullptr)).c_str());
        addComputerName(data);
        counters.serialize(data);
        send(data);
    }
}


const char* App::getLastError() const {
    return rd_kafka_err2str(rd_kafka_last_error());
}


void App::addComputerName(MessagePayload& data) {
    char computerName[256];
    DWORD length = sizeof(computerName);
    GetComputerNameA(computerName, &length);
    data.add("Name", computerName);
}


bool App::send(const void* buffer, size_t length) const {
    while (rd_kafka_produce(topic_, RD_KAFKA_PARTITION_UA,
        RD_KAFKA_MSG_F_COPY, (void*)buffer, length, NULL, 0, NULL) != 0) {
#ifdef _DEBUG
        fprintf(stderr, "%% Failed to produce to topic %s: %s\n", rd_kafka_topic_name(topic_), getLastError());
#endif
        if (rd_kafka_last_error() != RD_KAFKA_RESP_ERR__QUEUE_FULL) {
            return false;
        }
        rd_kafka_poll(producer_, 1000);
    }
    return true;
}
