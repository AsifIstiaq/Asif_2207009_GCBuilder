package com.example.asif_2207009_gcbuilder;

import com.example.asif_2207009_gcbuilder.DatabaseHelper;
import com.example.asif_2207009_gcbuilder.Course;
import javafx.concurrent.Task;

public class CourseUpdate extends Task<Boolean> {

    private final Course course;

    public CourseUpdate (Course course) {
        this.course = course;
    }

    @Override
    protected Boolean call() throws Exception {
        updateMessage("Updating course: " + course.getName() + " (ID: " + course.getId() + ")");
        updateProgress(0, 1);

        boolean success = DatabaseHelper.updateCourse(course);

        updateProgress(1, 1);

        if (success) {
            updateMessage("Course updated successfully");
        } else {
            updateMessage("Failed to update course");
            throw new Exception("Database update failed");
        }

        return success;
    }
}

