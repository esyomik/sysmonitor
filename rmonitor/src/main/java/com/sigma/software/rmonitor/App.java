package com.sigma.software.rmonitor;

import com.sigma.software.rmonitor.perf.RingPerfRecorder;
import com.sigma.software.rmonitor.client.PerformanceMonitor;
import com.sigma.software.rmonitor.resource.Labels;
import com.sigma.software.rmonitor.resource.Messages;
import com.sigma.software.rmonitor.resource.Resources;
import com.sigma.software.rmonitor.ui.MonitorScene;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class App extends Application {

    private static final int PERF_BUFFER_SIZE = 200;

    private static PerformanceMonitor<String> monitor;
    private static MonitorScene scene;


    public static void main(String[] args) throws IOException {
        Resources.init("ru", "RU");
        if (args.length < 1) {
            System.out.println(Messages.ERR_CONFIG_NOT_FOUND.get());
            return;
        }

        RingPerfRecorder<String> recorder = new RingPerfRecorder<>(PERF_BUFFER_SIZE);
        Properties configuration = new Properties();
        configuration.load(new FileInputStream(args[0]));
        long seekPosition = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(PERF_BUFFER_SIZE + 32);
        monitor = new PerformanceMonitor<>(configuration, recorder, StringDeserializer.class.getName(), seekPosition);
        scene = new MonitorScene(configuration, recorder);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(Labels.APP_TITLE.get());
        primaryStage.setScene(scene.create(1024.0, 768.0));
        primaryStage.show();
        monitor.startMonitor();
        scene.startMonitor();
    }

    @Override
    public void stop() {
        scene.stopMonitor();
        monitor.stopMonitor();
    }
}
