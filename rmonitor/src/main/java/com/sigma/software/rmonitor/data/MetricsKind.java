package com.sigma.software.rmonitor.data;


/**
 * Types of metrics.
 */
public enum MetricsKind {
    PERCENT,
    VALUE,
    ACCUMULATED;

    public static MetricsKind create(byte type) {
        switch(type) {
            case 0: return PERCENT;
            case 1: return VALUE;
            case 2: return ACCUMULATED;
        }
        return VALUE;
    }
}
