package com.sigma.software.rmonitor.ui;

import com.sigma.software.rmonitor.data.ObservableHosts;
import com.sigma.software.rmonitor.data.PerfMetrics;
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


public class ComputerList {

    private static final Image COMPUTER_ACTIVE = new Image("image/Devices-computer.png");
    private static final Image COMPUTER_INACTIVE = new Image("image/Devices-computer-gray.png");
    private static final long INACTIVE_TIME = TimeUnit.SECONDS.toMillis(16);

    private ObservableHosts hosts;
    private ObservableList<String> computers;
    private ListView<String> view;

    public ComputerList(ObservableHosts hosts) {
        this.hosts = hosts;
    }

    public Node create() {
        computers = FXCollections.observableArrayList();
        view = new ListView<String>(computers);
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
                PerfMetrics metrics = hosts.getMetrics(name);
                Image image = metrics == null || System.currentTimeMillis() - metrics.getLastTimeStamp() > INACTIVE_TIME ?
                        COMPUTER_INACTIVE : COMPUTER_ACTIVE;
                imageView.setImage(image);
                setGraphic(imageView);
            }
        });
        return view;
    }

    public void update() {
        Set<String> hostNames = hosts.getObservedHostNames();

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

        view.refresh();
    }
}
