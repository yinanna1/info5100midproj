package model;

public class Student {

    private int studentId;
    private int userId;
    private String userName; // optional – can be null if not joined

    // Constructor for student JOIN users (studentId, userId, userName)
    public Student(int studentId, int userId, String userName) {
        this.studentId = studentId;
        this.userId = userId;
        this.userName = userName;
    }

    // Constructor for old code (studentId, userId only)
    public Student(int studentId, int userId) {
        this.studentId = studentId;
        this.userId = userId;
        this.userName = null; // name not fetched
    }

    public int getStudentId() { return studentId; }
    public int getUserId() { return userId; }

    public String getUserName() {
        return userName != null ? userName : "(No Name)";
    }

    @Override
    public String toString() {
        return getUserName();
    }
}
