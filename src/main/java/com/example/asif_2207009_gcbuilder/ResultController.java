package com.example.asif_2207009_gcbuilder;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

public class ResultController {

    @FXML private VBox coursesBox;
    @FXML private Label totalCreditsLabel;
    @FXML private Label gpaLabel;
    @FXML private Button exportButton;

    private ObservableList<Course> courses;
    private double totalCredits;
    private double gpa;
    private Map<String, Double> gradePoints;

public void setData(ObservableList<Course> courses, double totalCredits, double gpa, Map<String, Double> gradePoints) {
    this.courses = courses;
    this.totalCredits = totalCredits;
    this.gpa = gpa;
    this.gradePoints = gradePoints;

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
    private String getGradeLetter(double gpa) {
        if (gpa >= 4.00) return "A+";
        if (gpa >= 3.75) return "A";
        if (gpa >= 3.50) return "A-";
        if (gpa >= 3.25) return "B+";
        if (gpa >= 3.00) return "B";
        if (gpa >= 2.75) return "B-";
        if (gpa >= 2.50) return "C+";
        if (gpa >= 2.25) return "C";
        if (gpa >= 2.00) return "D";
        return "F";
    }

    @FXML
    private void onExport(javafx.event.ActionEvent event) {
        if (courses == null || courses.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Export", "No courses to export.");
            return;
        }

        try {
            String report = generateReport();
            String timestamp = System.currentTimeMillis() + "";
            String filename = "GPA_Report_" + timestamp + ".txt";
            Files.write(Paths.get(filename), report.getBytes());
            showAlert(Alert.AlertType.INFORMATION, "Export", "Report exported to " + filename);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Export Error", "Failed to export: " + e.getMessage());
        }
    }

    private String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("GPA CALCULATOR REPORT\n");
        sb.append("Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("=".repeat(60)).append("\n\n");

        double totalPoints = courses.stream().mapToDouble(c -> c.getCredit() * gradePoints.getOrDefault(c.getGrade(), 0.0)).sum();

        sb.append("COURSES\n");
        sb.append("-".repeat(60)).append("\n");
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            double gradePoint = gradePoints.getOrDefault(c.getGrade(), 0.0);
            double weightedPoints = c.getCredit() * gradePoint;
            sb.append(String.format("%d. %s (%s)\n", i+1, c.getName(), c.getCode()));
            sb.append(String.format("   Credit: %.2f | Grade: %s (%.1f points) | Weighted: %.2f\n",
                    c.getCredit(), c.getGrade(), gradePoint, weightedPoints));
            sb.append(String.format("   Teachers: %s, %s\n\n", c.getTeacher1(), c.getTeacher2()));
        }

        sb.append("-".repeat(60)).append("\n");
        sb.append("SUMMARY\n");
        sb.append("-".repeat(60)).append("\n");
        sb.append(String.format("Total Courses: %d\n", courses.size()));
        sb.append(String.format("Total Credits: %.2f\n", totalCredits));
        sb.append(String.format("Total Weighted Points: %.2f\n", totalPoints));
        sb.append(String.format("GPA (Weighted Average): %.2f\n", gpa));
        sb.append("=".repeat(60)).append("\n");

        return sb.toString();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
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
