package model;

public class Student {
    private int studentId;
    private int userId;

    // Add these:
    private String userName;
    private String email;

    public Student() {}

    public Student(int studentId, int userId, String userName, String email) {
        this.studentId = studentId;
        this.userId = userId;
        this.userName = userName;
        this.email = email;
    }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // So your UI can keep calling these:
    public String getName() {
        return userName;
    }

    // already same meaning but keep for consistency
    public String getStudentEmail() {
        return email;
    }
    @Override
    public String toString() {
        if (userName != null && !userName.isBlank()) {
            return userName;
        }
        // fallback if name not loaded yet
        return "Student ID: " + studentId;
    }



}
