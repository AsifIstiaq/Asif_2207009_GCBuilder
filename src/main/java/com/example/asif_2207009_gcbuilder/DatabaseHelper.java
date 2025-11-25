package com.example.asif_2207009_gcbuilder;

import com.example.asif_2207009_gcbuilder.Course;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class DatabaseHelper {

    private static final String DB_URL = "jdbc:sqlite:gpa.db";
    private static Connection connection = null;

    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS courses (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "code TEXT NOT NULL, " +
                    "credit REAL NOT NULL, " +
                    "teacher1 TEXT, " +
                    "teacher2 TEXT, " +
                    "grade TEXT NOT NULL" +
                    ")";

    private static final String INSERT_COURSE =
            "INSERT INTO courses (name, code, credit, teacher1, teacher2, grade) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_COURSE =
            "UPDATE courses SET name = ?, code = ?, credit = ?, teacher1 = ?, teacher2 = ?, grade = ? WHERE id = ?";

    private static final String DELETE_COURSE =
            "DELETE FROM courses WHERE id = ?";

    private static final String SELECT_ALL_COURSES =
            "SELECT id, name, code, credit, teacher1, teacher2, grade FROM courses ORDER BY id";

    private static final String SELECT_COURSE_BY_ID =
            "SELECT id, name, code, credit, teacher1, teacher2, grade FROM courses WHERE id = ?";

    public static void initializeDatabase() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                createTables();
                System.out.println("✓ Database initialized successfully: " + DB_URL);
            }
        } catch (SQLException e) {
            System.err.println("✗ Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE);
            System.out.println("✓ Courses table ready");
        }
    }

    private static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            initializeDatabase();
        }
        return connection;
    }

    public static int insertCourse(Course course) {
        try (PreparedStatement pstmt = getConnection().prepareStatement(INSERT_COURSE, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, course.getName());
            pstmt.setString(2, course.getCode());
            pstmt.setDouble(3, course.getCredit());
            pstmt.setString(4, course.getTeacher1());
            pstmt.setString(5, course.getTeacher2());
            pstmt.setString(6, course.getGrade());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        course.setId(generatedId); // Update the course object with generated ID
                        System.out.println("✓ Course inserted with ID: " + generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Insert failed: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean updateCourse(Course course) {
        try (PreparedStatement pstmt = getConnection().prepareStatement(UPDATE_COURSE)) {
            pstmt.setString(1, course.getName());
            pstmt.setString(2, course.getCode());
            pstmt.setDouble(3, course.getCredit());
            pstmt.setString(4, course.getTeacher1());
            pstmt.setString(5, course.getTeacher2());
            pstmt.setString(6, course.getGrade());
            pstmt.setInt(7, course.getId());

            int affectedRows = pstmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                System.out.println("✓ Course updated: ID " + course.getId());
            } else {
                System.err.println("✗ Update failed: Course ID " + course.getId() + " not found");
            }

            return success;
        } catch (SQLException e) {
            System.err.println("✗ Update failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteCourse(int id) {
        try (PreparedStatement pstmt = getConnection().prepareStatement(DELETE_COURSE)) {
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                System.out.println("✓ Course deleted: ID " + id);
            } else {
                System.err.println("✗ Delete failed: Course ID " + id + " not found");
            }

            return success;
        } catch (SQLException e) {
            System.err.println("✗ Delete failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static ObservableList<Course> getAllCourses() {
        ObservableList<Course> courses = FXCollections.observableArrayList();

        try (PreparedStatement pstmt = getConnection().prepareStatement(SELECT_ALL_COURSES);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Course course = new Course(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("code"),
                        rs.getDouble("credit"),
                        rs.getString("teacher1"),
                        rs.getString("teacher2"),
                        rs.getString("grade")
                );
                courses.add(course);
            }

            System.out.println("✓ Loaded " + courses.size() + " courses from database");

        } catch (SQLException e) {
            System.err.println("✗ Failed to load courses: " + e.getMessage());
            e.printStackTrace();
        }

        return courses;
    }

    public static Course getCourseById(int id) {
        try (PreparedStatement pstmt = getConnection().prepareStatement(SELECT_COURSE_BY_ID)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Course(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("code"),
                            rs.getDouble("credit"),
                            rs.getString("teacher1"),
                            rs.getString("teacher2"),
                            rs.getString("grade")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Failed to retrieve course: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("✗ Failed to close connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean deleteAllCourses() {
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute("DELETE FROM courses");
            System.out.println("✓ All courses deleted from database");
            return true;
        } catch (SQLException e) {
            System.err.println("✗ Failed to delete all courses: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

