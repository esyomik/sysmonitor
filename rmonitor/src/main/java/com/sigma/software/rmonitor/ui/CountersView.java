package com.sigma.software.rmonitor.ui;

import com.sigma.software.rmonitor.data.MetricsInfo;
import com.sigma.software.rmonitor.data.PerfCounters;
import javafx.scene.Node;
import javafx.scene.layout.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Counters view. It reorders children controls {@link CounterChart} into a grid.
 * Currently it isn't intended to show a lot of metrics, because of it always
 * tries to fit children controls to a single no scrollable page.
 */
class CountersView {

    private PerfCounters<String> counters;
    private GridPane view;
    private CounterChart[] charts;


    /**
     * Constructs an empty object.
     */
    CountersView(){
        counters = null;
        view = null;
        charts = null;
    }

    /**
     * Creates view. This method doesn't create children widgets. They are created
     * dynamically in {@link #update() update()} function
     * @return created view node, see {@link javafx.scene.Node}
     */
    Node create() {
        view = new GridPane();
        view.setGridLinesVisible(false);
        view.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        view.setHgap(12.0);
        view.setVgap(8.0);
        return view;
    }

    /**
     * Updates widgets for the current observable machine. This method also
     * creates children widgets dynamically if it is necessary.
     */
    void update() {
        if (counters == null) {
            destroyWidgets();
            return;
        }

        List<MetricsInfo> metricsInfo = counters.getMetricsInfo();
        int numMetrics = metricsInfo.size();
        if (charts == null || charts.length != numMetrics) {
            createWidgets();
        }

        for (int iMetrics = 0; iMetrics < numMetrics; ++iMetrics) {
            MetricsInfo info = metricsInfo.get(iMetrics);
            charts[iMetrics].startUpdate(info.getName(), info.getKind(), counters.maxSize());
        }

        long curTime = System.currentTimeMillis();
        int numData = counters.size();
        for (int iData = 0; iData < numData; ++iData) {
            long dTime = (counters.getTimestamp(iData) - curTime) / TimeUnit.SECONDS.toMillis(1);
            if (dTime < -counters.maxSize()) {
                continue;
            }
            String data = counters.getRaw(iData);
            String[] parsedData = data.split(";");
            int nData = Math.min(numMetrics, parsedData.length);
            for (int iMetrics = 0; iMetrics < nData; ++iMetrics) {
                charts[iMetrics].addMeasure(dTime, Double.parseDouble(parsedData[iMetrics]));
            }
        }
    }

    /**
     * Sets metrics to display and updates view, see {@link #update() update()}
     * function
     * @param counters the counters to show, see {@link PerfCounters}
     */
    void setCounters(PerfCounters<String> counters) {
        this.counters = counters;
        update();
    }

    private void createWidgets() {
        destroyWidgets();
        List<MetricsInfo> metricsInfo = counters.getMetricsInfo();
        int numCharts = metricsInfo.size();
        int numColumn = (int) Math.sqrt(numCharts);

        charts = new CounterChart[numCharts];
        int row = 0;
        int column = 0;
        for (int i = 0; i < numCharts; ++i) {
            charts[i] = new CounterChart();
            Node node = charts[i].create(metricsInfo.get(i).getKind(), counters.maxSize());
            view.add(node, column, row);
            ++column;
            if (column >= numColumn) {
                column = 0;
                ++row;
            }
        }

        ColumnConstraints constraints = new ColumnConstraints();
        constraints.setFillWidth(true);
        constraints.setHgrow(Priority.ALWAYS);
        for (int i = 0; i < numColumn; ++i) {
            view.getColumnConstraints().add(constraints);
        }

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setFillHeight(true);
        rowConstraints.setVgrow(Priority.ALWAYS);
        for (int i = 0; i <= row; ++i) {
            view.getRowConstraints().add(rowConstraints);
        }

        view.getParent().layout();
    }

    private void destroyWidgets() {
        view.getChildren().clear();
        while(view.getRowConstraints().size() > 0){
            view.getRowConstraints().remove(0);
        }

        while(view.getColumnConstraints().size() > 0){
            view.getColumnConstraints().remove(0);
        }

        if (charts != null) {
            Arrays.fill(charts, null);
            charts = null;
        }
    }
}
