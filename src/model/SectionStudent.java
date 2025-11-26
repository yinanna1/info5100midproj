package model;

public class SectionStudent {

    private int id;
    private int sectionId;
    private int studentId;

    public SectionStudent() {}

    public SectionStudent(int id, int sectionId, int studentId) {
        this.id = id;
        this.sectionId = sectionId;
        this.studentId = studentId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSectionId() { return sectionId; }
    public void setSectionId(int sectionId) { this.sectionId = sectionId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    @Override
    public String toString() {
        return "SectionStudent { id=" + id +
                ", sectionId=" + sectionId +
                ", studentId=" + studentId +
                " }";
    }
}
