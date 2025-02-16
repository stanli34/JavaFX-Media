package com.kensoftph.javafxmedia;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("media-player.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("JavaFX MediaPlayer!");
        stage.setScene(scene);
        stage.show();
        stage.setAlwaysOnTop(true);
    }

    public static void main(String[] args) {
        launch();
    }
}