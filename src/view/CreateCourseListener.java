package view;

@FunctionalInterface
public interface CreateCourseListener {
    void onCreate(String title, String instrument, String start, String end, String desc);
}

