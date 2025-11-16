package com.example.asif_2207009_gcbuilder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.HashMap;
import java.util.Map;

public class CourseEntryController {

    @FXML private TextField totalCreditsField;
    @FXML private Label creditProgressLabel;

    @FXML private TextField nameField;
    @FXML private TextField codeField;
    @FXML private TextField creditField;
    @FXML private TextField teacher1Field;
    @FXML private TextField teacher2Field;
    @FXML private ComboBox<String> gradeCombo;

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
            } else {
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
        calculateButton.setDisable(!(required > 0 && Math.abs(sum - required) < 1e-6));
    }

    private void updateCreditProgress() {
        if (creditProgressLabel == null) return;
        double required = parseDoubleOrZero(totalCreditsField.getText());
        double sum = courses.stream().mapToDouble(Course::getCredit).sum();
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

        if (name.isEmpty() || code.isEmpty() || creditStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Please fill in Course Name, Code and Credit.");
            return;
        }

        double credit;
        try {
            credit = Double.parseDouble(creditStr);
            if (credit <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Validation", "Credit must be a positive number (ex: 3.5).");
            return;
        }

        Course c = new Course(name, code, credit, t1, t2, grade);
        courses.add(c);
        clearInputs();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Course added successfully!");
    }

    @FXML
    private void onCalculate(ActionEvent event) {
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


