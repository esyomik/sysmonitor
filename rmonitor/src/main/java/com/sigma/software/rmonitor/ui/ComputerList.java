package com.sigma.software.rmonitor.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class ComputerList {

    private static final Image COMPUTER_ACTIVE = new Image("image/Devices-computer-icon.png");

    private ObservableList<String> computers;
    private ListView<String> view;


    public Node create() {
        computers = FXCollections.observableArrayList("First", "Second");
        view = new ListView<String>(computers);
        view.setMinSize(64.0, 128.0);
        view.setPrefSize(128.0, 300.0);
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
                imageView.setImage(COMPUTER_ACTIVE);
                setGraphic(imageView);
            }
        });
        return view;
    }

    public Node getView() {
        return view;
    }
}
