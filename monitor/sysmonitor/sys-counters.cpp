#include <chrono>
#include <thread>

#include "sys-counters.hpp"


Metrics::Metrics(const std::string& id, const std::string& kind, const std::string& name, PDH_HCOUNTER counter)
    : id_(id)
    , kind_(kindFromString(kind))
    , name_(name)
    , counter_(counter) {}


double Metrics::getMetrics() const {
    PDH_FMT_COUNTERVALUE value;
    PdhGetFormattedCounterValue(counter_, PDH_FMT_DOUBLE, nullptr, &value);
    return value.doubleValue;
}


Metrics::Kind Metrics::kindFromString(const std::string& kind) {
    if (kind == "VALUE") {
        return Kind::VALUE;
    }
    if (kind == "ACCUMULATED") {
        return Kind::ACCUMULATED;
    }
    return Kind::PERCENT;
}

SysCounters::SysCounters() {
    query_ = INVALID_HANDLE_VALUE;
    PdhOpenQueryA(nullptr, 0, &query_);
}


SysCounters::~SysCounters() {
    if (query_ != INVALID_HANDLE_VALUE) {
        PdhCloseQuery(query_);
    }
}


bool SysCounters::addMetrics(const std::string& name, const std::string& counter, const std::string& kind) {
    PDH_HCOUNTER hCounter = INVALID_HANDLE_VALUE;
    auto status = PdhAddEnglishCounterA(query_, counter.c_str(), NULL, &hCounter);
    if (ERROR_SUCCESS != status) {
        return false;
    }
    counters_.emplace_back(Metrics(counter, kind, name, hCounter));
    return true;
}


void SysCounters::collect() const {
    PdhCollectQueryData(query_);
}


int SysCounters::count() const {
    return (int) counters_.size();
}


const Metrics& SysCounters::getMetrics(int n) const {
    return counters_[n];
}


bool SysCounters::serializeValues(MessagePayload& data) const {
    if (query_ == INVALID_HANDLE_VALUE) {
        return false;
    }

    data.reserve(count() * 12);
    for (const auto& m : counters_) {
        data.add(m.getMetrics());
    }

    return true;
}


void SysCounters::traceMetrics(int nIterate, int periodSeconds) const {
    collect();
    for (int i = 0; i < nIterate; ++i) {
        std::this_thread::sleep_for(std::chrono::seconds(periodSeconds));
        collect();
        printf("%d.\n", i);
        for (const auto& m : counters_) {
            printf("\t%s = %g\n", m.getName().c_str(), m.getMetrics());
        }
    }
}

