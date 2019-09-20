package com.sigma.software.rmonitor.ui;

import com.sigma.software.rmonitor.data.MetricsKind;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;


class CounterChart {

    private static final double X_TICK_NUMBER = 10.0;
    private static final double Y_TICK_NUMBER = 5.0;

    private LineChart<Number, Number> chart;
    private XYChart.Series<Number, Number> series;
    private MetricsKind metricsKind;


    CounterChart() {
        chart = null;
        metricsKind = null;
    }

    Node create(MetricsKind kind, int duration) {
        destroy();
        metricsKind = kind;

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("T, sec");
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(-duration);
        xAxis.setUpperBound(0.0);
        xAxis.setTickUnit(duration / X_TICK_NUMBER);
        xAxis.setAnimated(false);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setAnimated(false);
        if (kind == MetricsKind.PERCENT) {
            yAxis.setAutoRanging(false);
            yAxis.setUpperBound(100.0);
            yAxis.setLowerBound(0.0);
            yAxis.setTickUnit(100.0 / Y_TICK_NUMBER);
        }

        chart = new LineChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        series = new XYChart.Series<>();
        chart.getData().add(series);
        chart.setCreateSymbols(false);
        chart.setAnimated(false);
        return chart;
    }

    void startUpdate(String title, MetricsKind kind, int duration) {
        if (chart == null || kind != metricsKind) {
            create(kind, duration);
        }
        chart.setTitle(title);
        series.getData().clear();
    }

    void addMeasure(long relativeTime, double value) {
        series.getData().add(new XYChart.Data<>(relativeTime, value));
    }

    private void destroy() {
        chart = null;
        metricsKind = null;
        series = null;
    }
}
