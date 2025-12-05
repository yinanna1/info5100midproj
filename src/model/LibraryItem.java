package model;

public class LibraryItem {
    private int libraryId;
    private String title;
    private String url;
    private String instrument;

    public LibraryItem(int libraryId, String title, String url, String instrument) {
        this.libraryId = libraryId;
        this.title = title;
        this.url = url;
        this.instrument = instrument;
    }

    public int getLibraryId() {
        return libraryId;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getInstrument() {
        return instrument;
    }

    @Override
    public String toString() {
        // what appears in the list
        if (instrument != null && !instrument.isEmpty()) {
            return libraryId + " — " + title + " (" + instrument + ")";
        }
        return libraryId + " — " + title;
    }
}

