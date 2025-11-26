package model;

public class Student {

    private int studentId;
    private int userId;
    private String userName;

    public Student() {}

    public Student(int studentId, int userId, String userName) {
        this.studentId = studentId;
        this.userId = userId;
        this.userName = userName;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "Student { " +
                "studentId=" + studentId +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                " }";
    }
}
