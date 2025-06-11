package iskonek;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteConnector {

    Connection conn = null;
    public static Connection gConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:iskonek.db");
            System.out.println("Connection to database established successfully.");
            createTables(conn);
            
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found.");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("Connection to database failed.");
            e.printStackTrace();
            return null;
        }
    }
    private static void createTables(Connection connection) {
        try {
            Statement stmt = connection.createStatement();
            String createStudentsTable =
                "CREATE TABLE IF NOT EXISTS students (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "student_id TEXT UNIQUE NOT NULL," +
                "first_name TEXT NOT NULL," +
                "middle_name TEXT," +
                "last_name TEXT NOT NULL," +
                "date_of_birth DATE NOT NULL," +
                "gender TEXT NOT NULL," +
                "civil_status TEXT NOT NULL," +
                "nationality TEXT NOT NULL," +
                "contact_number TEXT NOT NULL," +
                "address TEXT NOT NULL," +
                "guardian_name TEXT," +
                "guardian_contact TEXT," +
                "password TEXT NOT NULL," +
                "enrollment_date DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "student_email TEXT UNIQUE NOT NULL," +
                "student_course TEXT NOT NULL," +
                "student_schedule TEXT" +
                ")";
            stmt.execute(createStudentsTable);

            try {
                stmt.execute("ALTER TABLE students ADD COLUMN student_schedule TEXT");
            } catch (SQLException e) {
                System.out.println("Note: student_schedule column might already exist");
            }

            String createStudentCoursesTable =
                "CREATE TABLE IF NOT EXISTS student_courses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "student_id TEXT NOT NULL," +
                "course_code TEXT NOT NULL," +
                "schedule TEXT NOT NULL," +
                "enrollment_date DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE," +
                "UNIQUE(student_id, course_code)" +
                ")";
            stmt.execute(createStudentCoursesTable);
            stmt.close();

            System.out.println("Students and student_courses tables created or already exist.");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }
}