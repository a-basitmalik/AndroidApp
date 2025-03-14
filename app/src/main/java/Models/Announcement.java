package Models;


import java.util.Date;

public class Announcement {
    private String title;
    private String description;
    private Date date;
    private boolean isRead;
    private String authorName;
    private String authorRole;

    public Announcement(String title, String description, Date date, String authorName, String authorRole) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.authorName = authorName;
        this.authorRole = authorRole;
        this.isRead = false;
    }


    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Date getDate() { return date; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public String getAuthorName() { return authorName; }
    public String getAuthorRole() { return authorRole; }
}