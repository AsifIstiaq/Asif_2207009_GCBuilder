package com.example.asif_2207009_gcbuilder;

import javafx.concurrent.Task;

public class CourseDelete extends Task<Boolean> {

    private final int courseId;
    private final String courseName;

    public CourseDelete(int courseId, String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
    }

    @Override
    protected Boolean call() throws Exception {
        updateMessage("Deleting course: " + courseName + " (ID: " + courseId + ")");
        updateProgress(0, 1);

        boolean success = DatabaseHelper.deleteCourse(courseId);

        updateProgress(1, 1);

        if (success) {
            updateMessage("Course deleted successfully");
        } else {
            updateMessage("Failed to delete course");
            throw new Exception("Database delete failed");
        }

        return success;
    }
}
