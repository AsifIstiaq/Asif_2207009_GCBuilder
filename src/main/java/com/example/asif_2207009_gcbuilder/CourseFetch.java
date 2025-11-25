package com.example.asif_2207009_gcbuilder;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class CourseFetch extends Service<ObservableList<Course>> {

    @Override
    protected Task<ObservableList<Course>> createTask() {
        return new Task<ObservableList<Course>>() {
            @Override
            protected ObservableList<Course> call() throws Exception {
                updateMessage("Loading courses from database...");
                updateProgress(0, 1);

                Thread.sleep(100);

                ObservableList<Course> courses = DatabaseHelper.getAllCourses();

                updateProgress(1, 1);
                updateMessage("Loaded " + courses.size() + " courses");

                return courses;
            }
        };
    }
}