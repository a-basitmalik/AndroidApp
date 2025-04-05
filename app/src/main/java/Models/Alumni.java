package Models;

public class Alumni {
    private String name;
    private String email;
    private String phone;
    private String photoUrl;
    private String university;
    private String degree;
    private String graduationYear;
    private String gpa;
    private String standing;
    private String thesis;

    public Alumni(String name, String email, String phone, String photoUrl,
                         String university, String degree, String graduationYear,
                         String gpa, String standing, String thesis) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.photoUrl = photoUrl;
        this.university = university;
        this.degree = degree;
        this.graduationYear = graduationYear;
        this.gpa = gpa;
        this.standing = standing;
        this.thesis = thesis;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getUniversity() {
        return university;
    }

    public String getDegree() {
        return degree;
    }

    public String getGraduationYear() {
        return graduationYear;
    }

    public String getGpa() {
        return gpa;
    }

    public String getStanding() {
        return standing;
    }

    public String getThesis() {
        return thesis;
    }
}