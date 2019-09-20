package com.sigma.software.rmonitor.ui;

import com.sigma.software.rmonitor.data.ObservableHosts;
import com.sigma.software.rmonitor.data.PerfCounters;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Set;
import java.util.concurrent.TimeUnit;


class ComputerList {

    private static final Image COMPUTER_ACTIVE = new Image("image/Devices-computer.png");
    private static final Image COMPUTER_INACTIVE = new Image("image/Devices-computer-gray.png");
    private static final long INACTIVE_TIME = TimeUnit.SECONDS.toMillis(16);

    private CountersView metricsView;
    private ObservableHosts<String> hosts;
    private ListView<String> view;


    ComputerList(CountersView metricsView, ObservableHosts<String> hosts) {
        this.metricsView = metricsView;
        this.hosts = hosts;
    }

    Node create() {
        view = new ListView<>(FXCollections.observableArrayList());
        view.setMinSize(64.0, 128.0);
        view.setPrefSize(160.0, 300.0);
        view.setCellFactory(param -> new ListCell<String>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                setText(name);
                PerfCounters metrics = hosts.getMetrics(name);
                imageView.setImage(metrics == null ||
                        System.currentTimeMillis() - metrics.getLastTimeStamp() > INACTIVE_TIME ?
                        COMPUTER_INACTIVE : COMPUTER_ACTIVE);
                setGraphic(imageView);
            }
        });
        view.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)
                -> metricsView.setCounters(hosts.getMetrics(newValue)));
        return view;
    }

    void update() {
        Set<String> hostNames = hosts.getObservedHostNames();
        ObservableList<String> computers = view.getItems();

        if (hostNames.size() != computers.size() || !hostNames.containsAll(computers)) {
            MultipleSelectionModel<String> selected = view.getSelectionModel();
            String nameSelected = selected.getSelectedItem();
            int indexSelected = selected.getSelectedIndex();

            computers.clear();
            computers.addAll(hostNames);

            MultipleSelectionModel<String> newSelection = view.getSelectionModel();
            if (hostNames.contains(nameSelected)) {
                newSelection.select(nameSelected);
            } else {
                newSelection.select(Math.min(indexSelected, hostNames.size()));
            }
        }

        if (hostStatusChanged()) {
            view.refresh();
        }
    }

    // TODO implement it to prevent blinking
    private boolean hostStatusChanged() {
        return true;
    }
}
