package com.sigma.software.rmonitor.ui;

import com.sigma.software.rmonitor.client.Configuration;
import com.sigma.software.rmonitor.data.ObservableHosts;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


/**
 * The scene object of the remote performance monitor. The scene consists of two
 * widgets: {@link ComputerList} and {@link CountersView}.
 */
public class MonitorScene {

    private final long updatePeriod;
    private Timer timer;
    private final ComputerList computersView;
    private final CountersView metricsView;


    /**
     * Constructs scene object.
     * @param configuration the app configuration, see {@link Configuration}
     * @param hosts the object which provides measured data from the observed
     *              host, see {@link ObservableHosts}
     */
    public MonitorScene(Configuration configuration, ObservableHosts<String> hosts) {
        updatePeriod = TimeUnit.SECONDS.toMillis(configuration.updatePeriod());
        metricsView = new CountersView();
        computersView = new ComputerList(metricsView, hosts);
    }

    /**
     * Creates GUI widgets.
     * @param width width of created scene in pixels
     * @param height height of created scene in pixels
     * @return created {@link javafx.scene.Scene Scene} object.
     */
    public Scene create(double width, double height) {
        BorderPane root = new BorderPane();
        Node computerNode = computersView.create();
        root.setLeft(computerNode);
        BorderPane.setAlignment(computerNode, Pos.TOP_LEFT);
        BorderPane.setMargin(computerNode, new Insets(8, 8, 8, 8));

        Node metricsNode = metricsView.create();
        root.setCenter(metricsNode);
        BorderPane.setMargin(metricsNode, new Insets(8, 8, 8, 8));

        return new Scene(root, width, height);
    }

    /**
     * Runs task to update views. Call it when scene successfully created.
     */
    public void startMonitor() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    computersView.update();
                    metricsView.update();
                });
            }
        }, TimeUnit.SECONDS.toMillis(1L), updatePeriod);
    }

    /**
     * Stops task to update views.
     */
    public void stopMonitor() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
