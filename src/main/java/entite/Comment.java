package entite;

import java.time.LocalDateTime;

public class Comment {
    private Integer id;
    private String comment;
    private LocalDateTime dateofcom;
    private Publication publication; // Existing field for linking to a publication
    private Demande demande; // New field for linking to a demande
    private user user;

    // Constructor with all fields including Demande
    public Comment(Integer id, String comment, LocalDateTime dateofcom, Publication publication, Demande demande, user user) {
        this.id = id;
        this.comment = comment;
        this.dateofcom = dateofcom;
        this.publication = publication;
        this.demande = demande;
        this.user = user;
    }

    // Constructor for creating a new comment associated with a Publication
    public Comment(String comment, Publication publication, user user) {
        this.comment = comment;
        this.dateofcom = LocalDateTime.now(); // Set current date and time
        this.publication = publication;
        this.demande = null; // No associated demande
        this.user = user;
    }

    // Constructor for creating a new comment associated with a Demande
    public Comment(String comment, Demande demande, user user) {
        this.comment = comment;
        this.dateofcom = LocalDateTime.now(); // Set current date and time
        this.demande = demande;
        this.publication = null; // No associated publication
        this.user = user;
    }

    // Default constructor
    public Comment() {
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getDateofcom() {
        return dateofcom;
    }

    public void setDateofcom(LocalDateTime dateofcom) {
        this.dateofcom = dateofcom;
    }

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public Demande getDemande() {
        return demande;
    }

    public void setDemande(Demande demande) {
        this.demande = demande;
    }

    public user getUser() {
        return user;
    }

    public void setUser(user user) {
        this.user = user;
    }
}
