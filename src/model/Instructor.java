package model;

public class Instructor {

    private int instructorId;
    private int userId;
    private String userName;   // <-- ADD THIS FIELD!!

    public Instructor() {}

    public Instructor(int instructorId, int userId) {
        this.instructorId = instructorId;
        this.userId = userId;
    }

    // NEW constructor required by DAO
    public Instructor(int instructorId, int userId, String userName) {
        this.instructorId = instructorId;
        this.userId = userId;
        this.userName = userName;
    }

    public int getInstructorId() {
        return instructorId;
    }
    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
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
        return "Instructor { " +
                "instructorId=" + instructorId +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                " }";
    }
}
