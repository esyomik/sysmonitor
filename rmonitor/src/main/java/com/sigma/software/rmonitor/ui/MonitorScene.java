package com.sigma.software.rmonitor.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class MonitorScene {

    public Scene create() {
        BorderPane root = new BorderPane();
        ComputerList computers = new ComputerList();
        Node computerNode = computers.create();
        root.setLeft(computerNode);
        BorderPane.setAlignment(computerNode, Pos.TOP_LEFT);
        BorderPane.setMargin(computerNode, new Insets(8, 8, 8, 8));

        return new Scene(root, 1024, 768);
    }
}
