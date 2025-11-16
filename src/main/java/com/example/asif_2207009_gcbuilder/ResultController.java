package com.example.asif_2207009_gcbuilder;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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

            Label courseLabel = new Label(
                    String.format("%d. %s (%s) - %.2f credits - Grade: %s (%.1f pts) - Weighted GPA: %.2f - Teachers: %s, %s",
                            i + 1, c.getName(), c.getCode(), c.getCredit(), c.getGrade(), gradePoint, weightedPoints, c.getTeacher1(), c.getTeacher2())
            );
            courseLabel.setWrapText(true);
            courseLabel.getStyleClass().add("course-entry");
            coursesBox.getChildren().add(courseLabel);
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
