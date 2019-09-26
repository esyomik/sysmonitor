#pragma once

#include <pdh.h>
#include <string>
#include <vector>

#include "message-payload.hpp"


class Metrics
{
public:
    enum class Kind {
        PERCENT,
        VALUE,
        ACCUMULATED
    };

public:
    Metrics(const std::string& id, const std::string& kind, const std::string& name, PDH_HCOUNTER counter);

    /**
     * Returns unique identifier of metrics. Identifier always contains only ASCII characters.
     * @return string representation of metrics identifier
     */
    inline const std::string& getId() const {return id_;}
    /**
    * Returns kind of metrics, @see Kind
    * @return kind of metrics
    */
    inline Kind getKind() const {return kind_;}
    /**
    * Returns name of metrics. This name is exactly same as is passed to addMetrics() method.
    * @return name of metrics
    */
    inline const std::string& getName() const {return name_;}
    /**
    * Returns metrics value.
    * @return metrics the metrics
    */
    double getMetrics() const;

    /**
     * Converts string representation of metrics kind to type.
     */
    static Kind kindFromString(const std::string& kind);

private:
    std::string id_;
    Kind kind_;
    std::string name_;
    PDH_HCOUNTER counter_;

};

/**
 * Wrapper class for grabbing performance metrics.
 */
class SysCounters
{
public:
    SysCounters();
    ~SysCounters();

    /**
     * Adds new metrics. Call collect() after adding all necessary metrics
     * @param name the printable name of metrics; it is used to show user friendly name of the metrics
     * @param counter the Windows' counter name, it matches with appropriate metrics in Windows Performance Monitor
     * @param kind kind of metrics. Possible values: PERCENT, VALUE, ACCUMULATED
     * @return \code true if metrics successfully added
     */
    bool addMetrics(const std::string& name, const std::string& counter, const std::string& kind);
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
     * Returns metrics. Call this method after calling collect()
     * @param n the number of metrics; pay attentintion it may not match with addMetrics() ordering
     * @return metrics the metrics
     */
    const Metrics& getMetrics(int n) const;

    /**
     * Serializes metrics as a binary data for sending to a message broker. Call
     * collect() for collecting metrics before calling this method.
     * @param data serialized data
     * @return \code true if metrics are serialized successfully
     */
    bool serializeValues(MessagePayload& data) const;
    /**
     * Displays metrics to stdout. This method is commonly used for debugging.
     * @param nIterate number of iteration for gathering result
     * @param periodSeconds period for gathering metrics
     */
    void traceMetrics(int nIterate, int periodSeconds) const;

private:
    PDH_HQUERY query_;
    std::vector<Metrics> counters_;

};

