package entite;

import java.time.LocalDate;

public class formation {
    private int id;
    private String nomFormation;
    private String descriptionFormation;
    private LocalDate dateFormation;
    private String formateur;
    private String lieuFormation;
    private String imageFormation;

    public int getFormationuser_id() {
        return formationuser_id;
    }

    public void setFormationuser_id(int formationuser_id) {
        this.formationuser_id = formationuser_id;
    }

    private int formationuser_id;
    // Constructeur
    public formation(int id, String nomFormation, String descriptionFormation, LocalDate dateFormation, String formateur, String lieuFormation, String imageFormation) {
        this.id = id;
        this.nomFormation = nomFormation;
        this.descriptionFormation = descriptionFormation;
        this.dateFormation = dateFormation;
        this.formateur = formateur;
        this.lieuFormation = lieuFormation;
        this.imageFormation = imageFormation;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNomFormation() {
        return nomFormation;
    }

    public String getDescriptionFormation() {
        return descriptionFormation;
    }

    public LocalDate getDateFormation() {
        return dateFormation;
    }

    public String getFormateur() {
        return formateur;
    }

    public String getLieuFormation() {
        return lieuFormation;
    }

    public String getImageFormation() {
        return imageFormation;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNomFormation(String nomFormation) {
        this.nomFormation = nomFormation;
    }

    public void setDescriptionFormation(String descriptionFormation) {
        this.descriptionFormation = descriptionFormation;
    }

    public void setDateFormation(LocalDate dateFormation) {
        this.dateFormation = dateFormation;
    }

    public void setFormateur(String formateur) {
        this.formateur = formateur;
    }

    public void setLieuFormation(String lieuFormation) {
        this.lieuFormation = lieuFormation;
    }

    public void setImageFormation(String imageFormation) {
        this.imageFormation = imageFormation;
    }

    @Override
    public String toString() {
        return "Formation{" +
                "id=" + id +
                ", nomFormation='" + nomFormation + '\'' +
                ", descriptionFormation='" + descriptionFormation + '\'' +
                ", dateFormation='" + dateFormation + '\'' +
                ", formateur='" + formateur + '\'' +
                ", lieuFormation='" + lieuFormation + '\'' +
                ", imageFormation='" + imageFormation + '\'' +
                '}';
    }
}
