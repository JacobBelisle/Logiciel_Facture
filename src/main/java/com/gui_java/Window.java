package com.gui_java;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Window {
    Scene _scene;
    Stage _stage;
    FXMLLoader _fxmlLoader;

    DataPassing _dataPassing;

    public Window() {
    }

    public Window(String titleName, java.net.URL url, int dimensionX, int dimensionY, javafx.stage.Modality modality){

        _fxmlLoader = new FXMLLoader(url);

        try {
            _scene = new Scene(_fxmlLoader.load(), dimensionX, dimensionY);
        } catch (IOException e) {
            Log.print("Cannot open the window");
            Log.print(e.getMessage());
        }
        _stage = new Stage();
        _stage.setTitle(titleName);
        _stage.initModality(modality);
        _stage.setScene(_scene);

    }

    public void sendData(Object o) {
        _dataPassing = _fxmlLoader.getController();
        _dataPassing.receiveData(o);
    }

    public void sendArrayData(Object[] o) {
        _dataPassing = _fxmlLoader.getController();
        _dataPassing.receiveArrayData(o);
    }

    public void showWindow() {
        _stage.show();
    }


    public Scene getScene() {
        return _scene;
    }

    public void setScene(Scene _scene) {
        this._scene = _scene;
    }

    public Stage getStage() {
        return _stage;
    }

    public void setStage(Stage _stage) {
        this._stage = _stage;
    }

    public FXMLLoader getFxmlLoader() {
        return _fxmlLoader;
    }

    public void setFxmlLoader(FXMLLoader _fxmlLoader) {
        this._fxmlLoader = _fxmlLoader;
    }
}
