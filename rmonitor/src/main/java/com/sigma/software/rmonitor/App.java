package com.sigma.software.rmonitor;

import com.sigma.software.rmonitor.client.Configuration;
import com.sigma.software.rmonitor.client.PerformanceMonitor;
import com.sigma.software.rmonitor.resource.Labels;
import com.sigma.software.rmonitor.resource.Resources;
import com.sigma.software.rmonitor.ui.MonitorScene;
import javafx.application.Application;
import javafx.stage.Stage;


public class App extends Application {

    private static PerformanceMonitor monitor;

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Path to config file is not found.");
            return;
        }

        monitor = new PerformanceMonitor(Configuration.load(args[0]));
        Resources.init("ru", "RU");
        launch(args);
        monitor.stopMonitor();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            primaryStage.setTitle(Labels.APP_TITLE.get());
            MonitorScene scene = new MonitorScene();
            primaryStage.setScene(scene.create());
            primaryStage.show();
            monitor.startMonitor();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
