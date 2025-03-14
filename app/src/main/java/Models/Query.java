package Models;

import com.example.lms.R;

import java.util.Date;
import java.util.UUID;

public class Query {
    private String id;
    private String recipient;
    private String queryText;
    private String response;
    private String submissionDate;
    private String status; // Replace QueryStatus with a String for status


    public Query(String recipient, String queryText, String response, String status, String submissionDate) {
        this.id = UUID.randomUUID().toString();
        this.recipient = recipient;
        this.queryText = queryText;
        this.response = response;
        this.status = status;
        this.submissionDate = submissionDate;
    }


    public Query(String recipient, String queryText) {
        this.id = UUID.randomUUID().toString();
        this.recipient = recipient;
        this.queryText = queryText;
        this.response = null; // No response yet
        this.status = "Pending"; // Default status
        this.submissionDate = new Date().toString();
    }


    public String getId() {
        return id;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getQueryText() {
        return queryText;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
        this.status = "Responded";
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}