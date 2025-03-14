package Models;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Assignment implements Serializable {
    private String id;
    private String title;
    private String description;
    private Date dueDate;
    private String instructions;
    private List<String> attachmentUrls;
    private Date createdAt;
    private Date updatedAt;
    private String createdBy;
    private AssignmentStatus status;


    public enum AssignmentStatus {
        DRAFT,
        PUBLISHED,
        CLOSED,
        ARCHIVED
    }


    public Assignment(String title, String description, Date dueDate, String instructions) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.instructions = instructions;
        this.attachmentUrls = new ArrayList<>();
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.status = AssignmentStatus.DRAFT;
    }

    // Full constructor
    public Assignment(String title, String description, Date dueDate, String instructions,
                      List<String> attachmentUrls, String createdBy) {
        this(title, description, dueDate, instructions);
        this.attachmentUrls = attachmentUrls;
        this.createdBy = createdBy;
    }


    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public String getInstructions() {
        return instructions;
    }

    public List<String> getAttachmentUrls() {
        return new ArrayList<>(attachmentUrls);
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public AssignmentStatus getStatus() {
        return status;
    }


    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = new Date();
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = new Date();
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
        this.updatedAt = new Date();
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
        this.updatedAt = new Date();
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
        this.updatedAt = new Date();
    }

    // Methods to manage attachments
    public void addAttachment(String attachmentUrl) {
        this.attachmentUrls.add(attachmentUrl);
        this.updatedAt = new Date();
    }

    public void removeAttachment(String attachmentUrl) {
        this.attachmentUrls.remove(attachmentUrl);
        this.updatedAt = new Date();
    }

    public void clearAttachments() {
        this.attachmentUrls.clear();
        this.updatedAt = new Date();
    }


    public boolean isOverdue() {
        return new Date().after(dueDate);
    }

    public boolean isPublished() {
        return status == AssignmentStatus.PUBLISHED;
    }

    public long getRemainingTimeInMillis() {
        return dueDate.getTime() - System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assignment that = (Assignment) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}