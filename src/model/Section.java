package model;

public class Section {

    private int sectionId;
    private String sectionName;
    private int lessonId;
    private int instructorId;
    private int room;

    public Section(int sectionId, String sectionName, int lessonId, int instructorId, int room) {
        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.lessonId = lessonId;
        this.instructorId = instructorId;
        this.room = room;
    }

    public int getSectionId() { return sectionId; }
    public String getSectionName() { return sectionName; }
    public int getLessonId() { return lessonId; }
    public int getInstructorId() { return instructorId; }
    public int getRoom() { return room; }

    @Override
    public String toString() {
        return sectionId + " — " + sectionName + " (lesson " + lessonId + ")";
    }
}
