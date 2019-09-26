package com.sigma.software.rmonitor.data;


/**
 * Metrics information: printable name and kind of usage.
 */
public class MetricsInfo {

    private final String id;
    private final String name;
    private final MetricsKind kind;


    /**
     * Constructs an object.
     * @param id the unique identifier of metrics
     * @param name the displayed name of the metrics
     * @param kind the kind of metrics, see {@link MetricsKind}
     */
    public MetricsInfo(String id, String name, MetricsKind kind) {
        this.id = id;
        this.name = name;
        this.kind = kind;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public MetricsKind getKind() {
        return kind;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MetricsInfo{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", kind=").append(kind);
        sb.append('}');
        return sb.toString();
    }
}
