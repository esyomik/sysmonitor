#pragma once

#include <pdh.h>
#include <string>
#include <vector>

#include "message-payload.hpp"


/**
 * Wrapper class for grabbing performance metrics.
 */
class SysCounters {

public:
    SysCounters();
    ~SysCounters();

    /**
     * Adds new metrics. Call collect() after adding all necessary metrics
     * @param id the identifier of metrics; it is used for sending to a broker
     * @param name the printable name of metrics; it is used to show user friendly name of the metrics
     * @param counter the Windows' counter name, it matches with appropriate metrics in Windows Performance Monitor
     * @return \code true if metrics successfully added
     */
    bool addMetrics(const std::string& id, const std::string& name, const std::string& counter);
    /**
     * Collects metrics provided by the Windows. You have to call this method periodically
     * to grab metrics values for the passed time.
     */
    void collect() const;
    /**
     * Returns number of tracking metrics.
     * @return number of metrics
     */
    int count() const;
    /**
     * Returns name of metrics. This name is exactly same as is passed to addMetrics() method.
     * @param n the number of metrics; pay attentintion it may not match with addMetrics() ordering
     * @return pointer to name of metrics
     */
    const char* getMetricsPrintName(int n) const;
    /**
     * Returns metrics value. Call this method after calling collect()
     * @param n the number of metrics; pay attentintion it may not match with addMetrics() ordering
     * @return metrics value
     */
    double getMetrics(int n) const;

    /**
     * Serializes metrics for sending to a message broker. Call collect() for collecting
     * metrics before calling this method.
     * @param data serialized data
     * @return \code true if metrics are serialized successfully
     */
    bool serialize(MessagePayload& data) const;
    /**
     * Displays metrics to stdout. This method is commonly used for debugging.
     * @param nIterate number of iteration for gathering result
     * @param periodSeconds period for gathering metrics
     */
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

