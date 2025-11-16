package com.example.asif_2207009_gcbuilder;

import javafx.beans.property.*;

public class Course {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty code = new SimpleStringProperty();
    private final DoubleProperty credit = new SimpleDoubleProperty();
    private final StringProperty teacher1 = new SimpleStringProperty();
    private final StringProperty teacher2 = new SimpleStringProperty();
    private final StringProperty grade = new SimpleStringProperty();

    public Course() {}

    public Course(String name, String code, double credit, String teacher1, String teacher2, String grade) {
        setName(name);
        setCode(code);
        setCredit(credit);
        setTeacher1(teacher1);
        setTeacher2(teacher2);
        setGrade(grade);
    }

    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }

    public String getCode() { return code.get(); }
    public void setCode(String value) { code.set(value); }
    public StringProperty codeProperty() { return code; }

    public double getCredit() { return credit.get(); }
    public void setCredit(double value) { credit.set(value); }
    public DoubleProperty creditProperty() { return credit; }

    public String getTeacher1() { return teacher1.get(); }
    public void setTeacher1(String value) { teacher1.set(value); }
    public StringProperty teacher1Property() { return teacher1; }

    public String getTeacher2() { return teacher2.get(); }
    public void setTeacher2(String value) { teacher2.set(value); }
    public StringProperty teacher2Property() { return teacher2; }

    public String getGrade() { return grade.get(); }
    public void setGrade(String value) { grade.set(value); }
    public StringProperty gradeProperty() { return grade; }
}
