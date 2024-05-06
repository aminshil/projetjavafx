package entite;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class user {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String role;
    private LocalDate dateNaissance;
    private String imagePath;
    private boolean isBlocked;
    private LocalDateTime blockEndTime;

    // Constructor with all fields
    public user(int id, String nom, String prenom, String email, String password, String role, LocalDate dateNaissance, String imagePath, boolean isBlocked) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.role = role;
        this.dateNaissance = dateNaissance;
        this.imagePath = imagePath;
        this.isBlocked = isBlocked;
    }

    // Constructor without date of birth and image path, including isBlocked
    public user(int id, String nom, String prenom, String email, String password, String role, boolean isBlocked) {
        this(id, nom, prenom, email, password, role, null, null, isBlocked);
    }

    // Overloaded constructor for easier object creation without blocking information
    public user(int id, String nom, String prenom, String email, String password, String role, LocalDate dateNaissance, String imagePath) {
        this(id, nom, prenom, email, password, role, dateNaissance, imagePath, false);
    }
    public user() {
    }

    // Overloaded constructor for user creation without date of birth, image path, or block status
    public user(int id, String nom, String prenom, String email, String password, String role) {
        this(id, nom, prenom, email, password, role, null, null, false);
    }

    // Getters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public String getImagePath() { return imagePath; }
    public boolean isBlocked() { return isBlocked; } // Getter for isBlocked

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setBlocked(boolean isBlocked) { this.isBlocked = isBlocked; } // Setter for isBlocked

    @Override
    public String toString() {
        return "user{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", dateNaissance=" + dateNaissance +
                ", imagePath='" + imagePath + '\'' +
                ", isBlocked=" + isBlocked +
                '}';
    }
    public LocalDateTime getBlockEndTime() {
        return blockEndTime;
    }
    public void setBlockEndTime(LocalDateTime blockEndTime) {
        this.blockEndTime = blockEndTime;
    }
}
