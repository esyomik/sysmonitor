package com.sigma.software.rmonitor.data;


import lombok.Value;

/**
 * Metrics information: printable name and kind of usage.
 */
@Value
public class MetricsInfo {

    String id;
    String name;
    MetricsKind kind;
}
