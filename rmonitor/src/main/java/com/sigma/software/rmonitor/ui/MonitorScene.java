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


public class MonitorScene {

    private long updatePeriod;
    private Timer timer;
    private ComputerList computersView;


    public MonitorScene(Configuration configuration, ObservableHosts hosts) {
        updatePeriod = TimeUnit.SECONDS.toMillis(configuration.updatePeriod());
        computersView = new ComputerList(hosts);
    }

    public Scene create() {
        BorderPane root = new BorderPane();
        Node computerNode = computersView.create();
        root.setLeft(computerNode);
        BorderPane.setAlignment(computerNode, Pos.TOP_LEFT);
        BorderPane.setMargin(computerNode, new Insets(8, 8, 8, 8));

        return new Scene(root, 1024, 768);
    }

    public void startMonitor() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> computersView.update());
            }
        }, TimeUnit.SECONDS.toMillis(1L), updatePeriod);
    }

    public void stopMonitor() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
