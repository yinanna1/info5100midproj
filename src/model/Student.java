package model;

public class Student {
    private int studentId;
    private int userId;
    private String userName;

    public Student(int studentId, int userId, String userName) {
        this.studentId = studentId;
        this.userId = userId;
        this.userName = userName;
    }

    public int getStudentId() { return studentId; }
    public int getUserId() { return userId; }
    public String getUserName() { return userName; }

    @Override
    public String toString() {
        return userName + " (ID: " + studentId + ")";
    }
}
