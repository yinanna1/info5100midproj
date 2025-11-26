package model;

public class Section {

    private int sectionId;
    private String sectionName;
    private int lessonId;
    private int instructorId;

    public Section() {}

    public Section(int sectionId, String sectionName, int lessonId, int instructorId) {
        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.lessonId = lessonId;
        this.instructorId = instructorId;
    }

    public int getSectionId() { return sectionId; }
    public String getSectionName() { return sectionName; }
    public int getLessonId() { return lessonId; }
    public int getInstructorId() { return instructorId; }

    @Override
    public String toString() {
        return sectionName + " (ID " + sectionId + ")";
    }
}
