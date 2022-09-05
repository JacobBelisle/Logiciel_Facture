package com.gui_java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Window w = new Window("This is the first window.",
                HelloApplication.class.getResource("hello-view.fxml"), 960, 540, Modality.NONE);

        w.showWindow();
    }

    public static void main(String[] args) {
        launch();
    }
}