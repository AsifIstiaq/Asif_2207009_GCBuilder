package com.example.asif_2207009_gcbuilder;

import javafx.concurrent.Task;

public class CourseReset extends Task<Boolean> {

    @Override
    protected Boolean call() throws Exception {
        updateMessage("Deleting all courses from database...");
        updateProgress(0, 1);

        boolean success = DatabaseHelper.deleteAllCourses();

        updateProgress(1, 1);

        if (success) {
            updateMessage("All courses deleted successfully");
        } else {
            updateMessage("Failed to delete all courses");
            throw new Exception("Database reset failed");
        }

        return success;
    }
}
