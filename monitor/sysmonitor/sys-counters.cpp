#include <chrono>
#include <thread>

#include "sys-counters.hpp"


SysCounters::SysCounters() {
    query_ = INVALID_HANDLE_VALUE;
    PdhOpenQueryA(nullptr, 0, &query_);
}


SysCounters::~SysCounters() {
    if (query_ != INVALID_HANDLE_VALUE) {
        PdhCloseQuery(query_);
    }
}


bool SysCounters::addMetrics(const std::string& id, const std::string& name, const std::string& counter) {
    PDH_HCOUNTER hCounter = INVALID_HANDLE_VALUE;
    auto status = PdhAddEnglishCounterA(query_, counter.c_str(), NULL, &hCounter);
    if (ERROR_SUCCESS != status) {
        return false;
    }
    counters_.emplace_back(METRICS(id, name, hCounter));
    return true;
}


void SysCounters::collect() const {
    PdhCollectQueryData(query_);
}


int SysCounters::count() const {
    return counters_.size();
}


const char* SysCounters::getMetricsPrintName(int n) const {
    return counters_[n].name_.c_str();
}


double SysCounters::getMetrics(int n) const {
    return counters_[n].getMetrics();
}


bool SysCounters::serialize(MessagePayload& data) const {
    if (query_ == INVALID_HANDLE_VALUE) {
        return false;
    }

    data.reserve(count() * 16);
    for (const auto& m : counters_) {
        data.add(m.id_.c_str(), m.getMetrics());
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
            printf("\t%s = %g\n", m.name_.c_str(), m.getMetrics());
        }
    }
}


SysCounters::METRICS::METRICS(const std::string& id, const std::string& name, PDH_HCOUNTER counter)
    : id_(id)
    , name_(name)
    , counter_(counter) {}

double SysCounters::METRICS::getMetrics() const {
    PDH_FMT_COUNTERVALUE value;
    PdhGetFormattedCounterValue(counter_, PDH_FMT_DOUBLE, nullptr, &value);
    return value.doubleValue;
}
