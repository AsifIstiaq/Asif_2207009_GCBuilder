package com.example.asif_2207009_gcbuilder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CourseEntryController {

    @FXML private TextField totalCreditsField;
    @FXML private Label creditProgressLabel;

    @FXML private TextField nameField;
    @FXML private TextField codeField;
    @FXML private TextField creditField;
    @FXML private TextField teacher1Field;
    @FXML private TextField teacher2Field;
    @FXML private ComboBox<String> gradeCombo;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button resetButton;
    @FXML private Button calculateButton;

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> nameCol;
    @FXML private TableColumn<Course, String> codeCol;
    @FXML private TableColumn<Course, Double> creditCol;
    @FXML private TableColumn<Course, String> teacher1Col;
    @FXML private TableColumn<Course, String> teacher2Col;
    @FXML private TableColumn<Course, String> gradeCol;

    private final ObservableList<Course> courses = FXCollections.observableArrayList();
    private Course selectedCourse = null;

    // grade mapping
    private final Map<String, Double> gradePoints = new HashMap<>();

    @FXML
    public void initialize() {
        initializeGradePoints();
        setupUI();
        setupTableListener();
    }

    private void initializeGradePoints() {
        gradePoints.put("A+", 4.00);
        gradePoints.put("A", 3.75);
        gradePoints.put("A-", 3.50);
        gradePoints.put("B+", 3.25);
        gradePoints.put("B", 3.00);
        gradePoints.put("B-", 2.75);
        gradePoints.put("C+", 2.50);
        gradePoints.put("C", 2.25);
        gradePoints.put("D", 2.00);
        gradePoints.put("F", 0.00);
    }

    private void setupUI() {
        gradeCombo.setItems(FXCollections.observableArrayList(gradePoints.keySet()));
        gradeCombo.getSelectionModel().selectFirst();

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        creditCol.setCellValueFactory(new PropertyValueFactory<>("credit"));
        teacher1Col.setCellValueFactory(new PropertyValueFactory<>("teacher1"));
        teacher2Col.setCellValueFactory(new PropertyValueFactory<>("teacher2"));
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));

        courseTable.setItems(courses);
        calculateButton.setDisable(true);
        updateButton.setDisable(true);
        resetButton.setDisable(true);

        if (creditProgressLabel != null) {
            updateCreditProgress();
        }
    }

    private void setupTableListener() {
        courses.addListener((javafx.collections.ListChangeListener.Change<? extends Course> c) -> {
            updateCalculateEnabled();
            updateCreditProgress();
        });

        courseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedCourse = newVal;
            if (selectedCourse != null) {
                populateFieldsFromCourse(selectedCourse);
                updateButton.setDisable(false);
                resetButton.setDisable(false);
            } else {
                updateButton.setDisable(true);
                resetButton.setDisable(true);
                clearInputs();
            }
        });

        totalCreditsField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateCalculateEnabled();
            updateCreditProgress();
        });
    }

    private void updateCalculateEnabled() {
        double required = parseDoubleOrZero(totalCreditsField.getText());
        double sum = courses.stream().mapToDouble(Course::getCredit).sum();
        addButton.setDisable(required == 0 || sum >= required);
        calculateButton.setDisable(!(required > 0 && Math.abs(sum - required) < 1e-6));
    }

    private void updateCreditProgress() {
        if (creditProgressLabel == null) return;
        double required = parseDoubleOrZero(totalCreditsField.getText());
        double sum = courses.stream().mapToDouble(Course::getCredit).sum();
        addButton.setDisable(required == 0 || sum >= required);
        if (required > 0) {
            creditProgressLabel.setText(String.format("Progress: %.2f / %.2f credits", sum, required));
        } else {
            creditProgressLabel.setText("Progress: 0.00 / 0.00 credits");
        }
    }

    private double parseDoubleOrZero(String s) {
        try { return Double.parseDouble(s.trim()); }
        catch (Exception e) { return 0.0; }
    }

    @FXML
    private void onAdd(ActionEvent event) {
        String name = nameField.getText().trim();
        String code = codeField.getText().trim();
        String creditStr = creditField.getText().trim();
        String t1 = teacher1Field.getText().trim();
        String t2 = teacher2Field.getText().trim();
        String grade = gradeCombo.getSelectionModel().getSelectedItem();

        if (name.isEmpty() || code.isEmpty() || creditStr.isEmpty() || t1.isEmpty() || t2.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Please fill in Course Name, Code, Credit and Teachers name.");
            return;
        }

        double credit;
        credit = Double.parseDouble(creditStr);
        try {
            if (credit <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Validation", "Credit must be a positive number (ex: 3.5).");
            return;
        }

        double required = parseDoubleOrZero(totalCreditsField.getText());
        double currentSum = courses.stream().mapToDouble(Course::getCredit).sum();

        if (required > 0 && currentSum + credit > required + 1e-6) {
            showAlert(Alert.AlertType.ERROR, "Credit Limit Exceeded",
                    "Adding this course exceeds the required total credit.");
            return;
        }

        Course c = new Course(name, code, credit, t1, t2, grade);
        courses.add(c);
        clearInputs();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Course added successfully!");
    }

    @FXML
    private void onUpdate(ActionEvent event) {
        if (selectedCourse == null) {
            showAlert(Alert.AlertType.WARNING, "Edit", "Select a course to edit.");
            return;
        }

        String name = nameField.getText().trim();
        String code = codeField.getText().trim();
        String creditStr = creditField.getText().trim();
        String t1 = teacher1Field.getText().trim();
        String t2 = teacher2Field.getText().trim();
        String grade = gradeCombo.getSelectionModel().getSelectedItem();

        if (name.isEmpty() || code.isEmpty() || creditStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Please fill in Course Name, Code and Credit.");
            return;
        }

        double credit;
        credit = Double.parseDouble(creditStr);
        try {
            if (credit <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Validation", "Credit must be a positive number (ex: 3.5).");
            return;
        }

        double required = parseDoubleOrZero(totalCreditsField.getText());
        double currentSum = courses.stream().mapToDouble(Course::getCredit).sum();
        double adjustedSum = currentSum - selectedCourse.getCredit() + credit;

        if (required > 0 && adjustedSum > required + 1e-6) {
            showAlert(Alert.AlertType.ERROR, "Credit Limit Exceeded",
                    "Editing this course exceeds the required total credit.");
            return;
        }

        selectedCourse.setName(name);
        selectedCourse.setCode(code);
        selectedCourse.setCredit(credit);
        selectedCourse.setTeacher1(t1);
        selectedCourse.setTeacher2(t2);
        selectedCourse.setGrade(grade);

        courseTable.refresh();
        courseTable.getSelectionModel().clearSelection();
        clearInputs();
        selectedCourse = null;
        if (courses.isEmpty())
            updateButton.setDisable(true);
        showAlert(Alert.AlertType.INFORMATION, "Success", "Course edited successfully!");
    }

    @FXML
    private void onRemove(ActionEvent event) {
        Course sel = courseTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "Delete", "Select a course to delete.");
            return;
        }
        courseTable.refresh();
        courseTable.getSelectionModel().clearSelection();
        courses.remove(sel);
        clearInputs();
        selectedCourse = null;
        updateButton.setDisable(true);
        showAlert(Alert.AlertType.INFORMATION, "Delete", "Course deleted successfully!");
    }

    @FXML
    private void onReset(ActionEvent event) {
        if (courses.isEmpty()) {
            resetButton.setDisable(true);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reset");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to clear all courses?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            courses.clear();
            clearInputs();
            totalCreditsField.clear();
            selectedCourse = null;
            updateButton.setDisable(true);
            showAlert(Alert.AlertType.INFORMATION, "Reset", "All courses cleared!");
        }
    }

    @FXML
    private void onExport(ActionEvent event) {
        double required = parseDoubleOrZero(totalCreditsField.getText());
        double sum = courses.stream().mapToDouble(Course::getCredit).sum();
        if (courses.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Export", "No courses to export. Add some courses first.");
            return;
        }

        if (required <= 0) {
            showAlert(Alert.AlertType.WARNING, "Export Error",
                    "Please enter the required credit before exporting.");
            return;
        }

        if (Math.abs(sum - required) != 0) {
            showAlert(Alert.AlertType.WARNING, "Export Error",
                    "Total course credits do not match the required credit.");
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

        double totalCredits = courses.stream().mapToDouble(Course::getCredit).sum();
        double totalPoints = courses.stream().mapToDouble(c -> c.getCredit() * gradePoints.getOrDefault(c.getGrade(), 0.0)).sum();
        double gpa = totalCredits > 0 ? totalPoints / totalCredits : 0.0;

        sb.append("COURSES\n");
        sb.append("-".repeat(60)).append("\n");
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            double gradePoint = gradePoints.getOrDefault(c.getGrade(), 0.0);
            double weightedPoints = c.getCredit() * gradePoint;
            sb.append(String.format("%d. %s (%s)\n", i+1, c.getName(), c.getCode()));
            sb.append(String.format("   Credit: %.2f | Grade: %s (%.1f points) | Weighted GPA: %.2f\n",
                    c.getCredit(), c.getGrade(), gradePoint, weightedPoints));
            sb.append(String.format("   Teachers: %s, %s\n\n", c.getTeacher1(), c.getTeacher2()));
        }

        sb.append("-".repeat(60)).append("\n");
        sb.append("SUMMARY\n");
        sb.append("-".repeat(60)).append("\n");
        sb.append(String.format("Total Courses: %d\n", courses.size()));
        sb.append(String.format("Total Credits: %.2f\n", totalCredits));
        sb.append(String.format("Total Weighted GPA: %.2f\n", totalPoints));
        sb.append(String.format("GPA: %.2f\n", gpa));
        sb.append("=".repeat(60)).append("\n");

        return sb.toString();
    }

    @FXML
    private void onCalculate(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/asif_2207009_gcbuilder/result.fxml"));
        Parent root = loader.load();
        ResultController controller = loader.getController();
        double totalCredits = courses.stream().mapToDouble(Course::getCredit).sum();
        double totalPoints = courses.stream().mapToDouble(c -> c.getCredit() * gradePoints.getOrDefault(c.getGrade(), 0.0)).sum();
        double gpa = totalCredits > 0 ? totalPoints / totalCredits : 0.0;

        controller.setData(courses, totalCredits, gpa, gradePoints);

        Scene scene = new Scene(root, 950, 750);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style/style.css")).toExternalForm());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    @FXML
    private void onTotalCreditsChanged() {
        updateCalculateEnabled();
        updateCreditProgress();
    }

    private void populateFieldsFromCourse(Course c) {
        nameField.setText(c.getName());
        codeField.setText(c.getCode());
        creditField.setText(String.valueOf(c.getCredit()));
        teacher1Field.setText(c.getTeacher1());
        teacher2Field.setText(c.getTeacher2());
        gradeCombo.getSelectionModel().select(c.getGrade());
    }

    private void clearInputs() {
        nameField.clear(); codeField.clear(); creditField.clear(); teacher1Field.clear(); teacher2Field.clear();
        gradeCombo.getSelectionModel().selectFirst();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}