#pragma once

#include <pdh.h>
#include <string>
#include <vector>

#include "message-payload.hpp"


class SysCounters {

public:
    SysCounters();
    ~SysCounters();

    bool addMetrics(const std::string& id, const std::string& name, const std::string& counter);
    void collect() const;
    int getMetricsCount() const;
    const char* getMetricsPrintName(int n) const;
    double getMetrics(int n) const;

    bool serialize(MessagePayload& data) const;
    void traceMetrics(int nIterate, int periodSeconds) const;

private:
    PDH_HQUERY query_;
    struct METRICS {
        std::string id_;
        std::string name_;
        PDH_HCOUNTER counter_;

        METRICS(const std::string& id, const std::string& name, PDH_HCOUNTER counter);
        double getMetrics() const;
    };
    std::vector<METRICS> counters_;

};

