package com.sigma.software.statservice;

import com.sigma.software.rmonitor.client.PerformanceMonitor;
import com.sigma.software.statservice.client.out.Writer;
import com.sigma.software.statservice.client.preferences.Preferences;
import com.sigma.software.statservice.client.StatisticRecorder;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Scanner;


public class StatService {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Configuration file is not found. Pass path to configuration file to cmd.");
            return;
        }

        Preferences preferences = Preferences.fromJSON(args[0]);
        StatisticRecorder recorder = new StatisticRecorder(preferences);
        PerformanceMonitor<String> monitor = new PerformanceMonitor<>(
                preferences.getKafkaPerfProperties(), recorder, StringDeserializer.class.getName());

        monitor.startMonitor();
        parseCommands();
        monitor.stopMonitor();
        Writer.stop(30);
    }

    private static void parseCommands() {
        System.out.println("Type 'exit' to stop service and exit...");
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            if (sc.nextLine().equals("exit")) {
                break;
            }
        }
        sc.close();
    }
}
