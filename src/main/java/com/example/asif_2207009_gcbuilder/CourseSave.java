package com.example.asif_2207009_gcbuilder;

import javafx.concurrent.Task;

public class CourseSave extends Task<Integer> {

    private final Course course;

    public CourseSave(Course course) {
        this.course = course;
    }

    @Override
    protected Integer call() throws Exception {
        updateMessage("Saving course: " + course.getName());
        updateProgress(0, 1);

        int generatedId = DatabaseHelper.insertCourse(course);

        updateProgress(1, 1);

        if (generatedId > 0) {
            updateMessage("Course saved successfully with ID: " + generatedId);
        } else {
            updateMessage("Failed to save course");
            throw new Exception("Database insert failed");
        }

        return generatedId;
    }
}
