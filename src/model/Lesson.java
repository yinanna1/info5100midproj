package model;

public class Lesson {

    private int lessonId;
    private int instructorId;
    private String title;
    private String instrument;
    private String startTime;
    private String endTime;
    private String description;

    public Lesson() {}

    public Lesson(int lessonId, int instructorId, String title, String instrument,
                  String startTime, String endTime, String description) {
        this.lessonId = lessonId;
        this.instructorId = instructorId;
        this.title = title;
        this.instrument = instrument;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }

    // ---------------------------
    // Getters / Setters
    // ---------------------------
    public int getLessonId() { return lessonId; }
    public void setLessonId(int lessonId) { this.lessonId = lessonId; }

    public int getInstructorId() { return instructorId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getInstrument() { return instrument; }
    public void setInstrument(String instrument) { this.instrument = instrument; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // ---------------------------
    // Show Course ID in UI lists
    // ---------------------------
    @Override
    public String toString() {
        // This is what appears inside all JLists
        return lessonId + " — " + title + " (" + instrument + ")";
    }
}
