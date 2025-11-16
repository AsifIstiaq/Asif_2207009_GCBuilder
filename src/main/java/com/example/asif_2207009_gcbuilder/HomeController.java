package com.example.asif_2207009_gcbuilder;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HomeController {
    public HomeController() {
    }

    @FXML
    private void onStart(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/asif_2207009_gcbuilder/course_entry.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1000.0, 650.0);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }
}

