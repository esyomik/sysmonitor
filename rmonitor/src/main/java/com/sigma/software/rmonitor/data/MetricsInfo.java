package com.sigma.software.rmonitor.data;

import java.util.StringJoiner;


public class MetricsInfo {

    private String name;
    private MetricsKind kind;


    public MetricsInfo(String name, MetricsKind kind) {
        this.name = name;
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public MetricsKind getKind() {
        return kind;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MetricsInfo.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("kind=" + kind)
                .toString();
    }
}