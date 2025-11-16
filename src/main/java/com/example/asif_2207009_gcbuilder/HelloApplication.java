package com.example.asif_2207009_gcbuilder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        stage.setTitle("GPA Calculator");
        stage.setScene(scene);
        stage.show();
    }
}
