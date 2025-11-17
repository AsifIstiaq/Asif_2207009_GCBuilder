package com.example.asif_2207009_gcbuilder;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class ResultController {

    @FXML private VBox coursesBox;
    @FXML private Label totalCreditsLabel;
    @FXML private Label gpaLabel;

public void setData(ObservableList<Course> courses, double totalCredits, double gpa, Map<String, Double> gradePoints) {
    totalCreditsLabel.setText(String.format("Total Credits: %.2f", totalCredits));
    gpaLabel.setText(String.format("GPA: %.2f", gpa));

    coursesBox.getChildren().clear();

    for (int i = 0; i < courses.size(); i++) {
        Course c = courses.get(i);
        double gradePoint = gradePoints.getOrDefault(c.getGrade(), 0.0);
        double weightedPoints = c.getCredit() * gradePoint;

        HBox row = new HBox(15);
        row.setStyle("-fx-padding:5;");
        row.setAlignment(Pos.CENTER);

        Label snLabel = new Label(String.valueOf(i + 1));
        snLabel.setPrefWidth(40); snLabel.setAlignment(Pos.CENTER);

        Label nameLabel = new Label(c.getName());
        nameLabel.setPrefWidth(150); nameLabel.setAlignment(Pos.CENTER);

        Label codeLabel = new Label(c.getCode());
        codeLabel.setPrefWidth(80); codeLabel.setAlignment(Pos.CENTER);

        Label creditLabel = new Label(String.format("%.2f", c.getCredit()));
        creditLabel.setPrefWidth(60); creditLabel.setAlignment(Pos.CENTER);

        Label gpaLabelCol = new Label(String.format("%.2f", gradePoint));
        gpaLabelCol.setPrefWidth(50); gpaLabelCol.setAlignment(Pos.CENTER);

        Label weightedGpaLabel = new Label(String.format("%.2f", weightedPoints));
        weightedGpaLabel.setPrefWidth(80); weightedGpaLabel.setAlignment(Pos.CENTER);

        Label teachersLabel = new Label(c.getTeacher1() + ", " + c.getTeacher2());
        teachersLabel.setPrefWidth(200); teachersLabel.setAlignment(Pos.CENTER);

        row.getChildren().addAll(snLabel, nameLabel, codeLabel, creditLabel, gpaLabelCol, weightedGpaLabel, teachersLabel);
        coursesBox.getChildren().add(row);
    }
}


    @FXML
    private void onBack(javafx.event.ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/asif_2207009_gcbuilder/course_entry.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1000, 750);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style/style.css")).toExternalForm());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }
}
