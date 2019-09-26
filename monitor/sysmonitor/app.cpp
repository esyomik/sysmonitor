#include <thread>
#include <stdexcept>

#include "app.hpp"


App::App()
    : producer_(nullptr) {}


App::~App() {
    destroy();
}


static void deliveryCallback(rd_kafka_t *rk, const rd_kafka_message_t *rkmessage, void *opaque) {
    if (rkmessage->err) {
        fprintf(stderr, "ERROR: Message delivery failed: %s\n", rd_kafka_err2str(rkmessage->err));
    }
}


static void errorCallback(rd_kafka_t *rk, int error, const char* reason, void *opaque) {
    fprintf(stderr, "ERROR %d: %s\n", error, reason);
}


void App::create(const nlohmann::json& config, const char* topicName) {
    destroy();

    rd_kafka_conf_t* configuration = rd_kafka_conf_new();
    rd_kafka_conf_set_dr_msg_cb(configuration, deliveryCallback);
    rd_kafka_conf_set_error_cb(configuration, errorCallback);

    char errstr[512];
    for (const auto& [key, value] : config.items()) {
        if (!value.is_string()) {
            continue;
        }
        if (rd_kafka_conf_set(configuration, key.c_str(), value.get<std::string>().c_str(), errstr, sizeof(errstr)) != RD_KAFKA_CONF_OK) {
            fprintf(stderr, "Can't apply configuration parameter: %s. Error: %s\n", key.c_str(), errstr);
        }
    }

    producer_ = rd_kafka_new(RD_KAFKA_PRODUCER, configuration, errstr, sizeof(errstr));
    if (!producer_) {
        throw std::logic_error("Can't create producer object");
    }

    topicName_ = topicName;
}


void App::destroy() {
    if (producer_) {
        rd_kafka_destroy(producer_);
    }
    producer_ = nullptr;
}


void App::flush(int timeoutMsec) const {
    rd_kafka_flush(producer_, timeoutMsec);
}


void App::run(const SysCounters& counters, int sendPeriod) const {
    unsigned long sendHeader = 0;
    MessagePayload data;
    counters.collect();
    while (true) {
        std::this_thread::sleep_for(std::chrono::seconds(sendPeriod));
        counters.collect();
        data.clear();
        counters.serializeValues(data);
        auto header = ((sendHeader & 15ul) == 0ul) ? createHeaders(counters) : nullptr;
        send(data.data(), data.length(), header);
        ++sendHeader;
    }
}


const char* App::getLastError() const {
    return rd_kafka_err2str(rd_kafka_last_error());
}


bool App::send(const void* buffer, size_t length, rd_kafka_headers_t* headers) const {
    char computerName[256];
    DWORD nameLength = sizeof(computerName);
    GetComputerNameA(computerName, &nameLength);
    rd_kafka_resp_err_t errorCode;

    if (headers) {
        errorCode = rd_kafka_producev(producer_,
            RD_KAFKA_V_TOPIC(topicName_.c_str()),
            RD_KAFKA_V_HEADERS(headers),
            RD_KAFKA_V_KEY(computerName, nameLength),
            RD_KAFKA_V_VALUE(buffer, length),
            RD_KAFKA_V_END);
    } else {
        errorCode = rd_kafka_producev(producer_,
            RD_KAFKA_V_TOPIC(topicName_.c_str()),
            RD_KAFKA_V_KEY(computerName, nameLength),
            RD_KAFKA_V_VALUE(buffer, length),
            RD_KAFKA_V_END);
    }
    
#ifdef _DEBUG
    if (RD_KAFKA_RESP_ERR_NO_ERROR != errorCode) {
        fprintf(stderr, "ERROR: Failed to produce to topic %s: %s\n", topicName_.c_str(), getLastError());
    }
#endif
    rd_kafka_poll(producer_, 1000);
    return true;
}


rd_kafka_headers_t* App::createHeaders(const SysCounters& counters) const {
    rd_kafka_headers_t* headers = rd_kafka_headers_new(counters.count());
    unsigned char data[512];
    for (int i = 0; i < counters.count(); ++i) {
        const auto& metrics = counters.getMetrics(i);
        const auto& name = metrics.getName();
        const auto& id = metrics.getId();
        data[0] = (unsigned char)metrics.getKind();
        memcpy_s(data + 1, sizeof(data) - 1, name.c_str(), name.length());
        rd_kafka_header_add(headers, id.c_str(), id.length(), data, std::min<size_t>(name.length() + 1, sizeof(data)));
    }
    return headers;
}
