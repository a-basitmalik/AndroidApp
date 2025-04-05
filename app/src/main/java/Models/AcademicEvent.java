package Models;

public class AcademicEvent {
    private String date;
    private String title;
    private String description;
    private int colorResId;

    public AcademicEvent(String date, String title, String description, int colorResId) {
        this.date = date;
        this.title = title;
        this.description = description;
        this.colorResId = colorResId;
    }


    public String getDate() { return date; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getColorResId() { return colorResId; }
}