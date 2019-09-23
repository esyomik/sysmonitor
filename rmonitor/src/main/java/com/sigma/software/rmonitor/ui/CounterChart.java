package com.sigma.software.rmonitor.ui;

import com.sigma.software.rmonitor.data.MetricsKind;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.math.BigDecimal;
import java.math.RoundingMode;


class CounterChart {

    private static final double X_TICK_NUMBER = 10.0;
    private static final double Y_TICK_NUMBER = 5.0;

    private LineChart<Number, Number> chart;
    private XYChart.Series<Number, Number> series;
    private MetricsKind metricsKind;
    private double minRoundValue;
    private double maxRoundValue;


    CounterChart() {
        chart = null;
        metricsKind = null;
        minRoundValue = Double.MAX_VALUE;
        maxRoundValue = Double.MIN_VALUE;
    }

    Node create(MetricsKind kind, int duration) {
        destroy();
        metricsKind = kind;
        minRoundValue = Double.MAX_VALUE;
        maxRoundValue = Double.MIN_VALUE;

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("T, sec");
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(-duration);
        xAxis.setUpperBound(0.0);
        xAxis.setTickUnit(duration / X_TICK_NUMBER);
        xAxis.setAnimated(false);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setAnimated(false);
        yAxis.setAutoRanging(kind == MetricsKind.VALUE);
        if (kind == MetricsKind.PERCENT) {
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
        if (metricsKind == MetricsKind.ACCUMULATED) {
            correctAxisBound(value);
        }
        series.getData().add(new XYChart.Data<>(relativeTime, value));
    }

    private void destroy() {
        chart = null;
        metricsKind = null;
        series = null;
    }

    private void correctAxisBound(double value) {
        ObservableList<XYChart.Data<Number, Number>> data = series.getData();
        int curSz = data.size();
        double minValue = curSz == 0? Math.min(value, minRoundValue)
                : Math.max(data.get(0).getYValue().doubleValue(), minRoundValue);
        double maxValue = curSz < 1? Math.max(value, maxRoundValue)
                : Math.max(data.get(curSz - 1).getYValue().doubleValue(), maxRoundValue);
        int scale = Math.abs(value) < 0.0001? 0 : (int) Math.log10(Math.abs(value)) / 2;
        minValue = new BigDecimal(minValue).setScale(-scale, RoundingMode.DOWN).doubleValue();
        maxValue = new BigDecimal(maxValue).setScale(-scale, RoundingMode.UP).doubleValue();
        if (Double.compare(minValue, minRoundValue) == 0 && Double.compare(maxValue, maxRoundValue) == 0) {
            return;
        }

        minRoundValue = minValue;
        maxRoundValue = maxValue;
        NumberAxis axis = (NumberAxis) chart.getYAxis();
        axis.setLowerBound(minRoundValue);
        axis.setUpperBound(maxRoundValue);
        axis.setTickUnit((maxRoundValue - minRoundValue) / Y_TICK_NUMBER);
    }
}
