package entite;

import java.sql.Timestamp;

public class commentaireformation {
    private int id;
    private int idFormation;
    private String contenu;
    private Timestamp dateCommentaire;
private int commentaireuserformation_id;

    public int getCommentaireuserformation_id() {
        return commentaireuserformation_id;
    }

    public void setCommentaireuserformation_id(int commentaireuserformation_id) {
        this.commentaireuserformation_id = commentaireuserformation_id;
    }

    public commentaireformation(int id, int idFormation, String contenu, Timestamp dateCommentaire,int commentaireuserformation_id) {
        this.id = id;
        this.idFormation = idFormation;
        this.contenu = contenu;
        this.dateCommentaire = dateCommentaire;
        this.commentaireuserformation_id=commentaireuserformation_id;
    }

    // Getters et setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdFormation() {
        return idFormation;
    }

    public void setIdFormation(int idFormation) {
        this.idFormation = idFormation;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Timestamp getDateCommentaire() {
        return dateCommentaire;
    }

    public void setDateCommentaire(Timestamp dateCommentaire) {
        this.dateCommentaire = dateCommentaire;
    }

    // Méthode toString pour l'affichage

    @Override
    public String toString() {
        return "CommentaireFormation{" +
                "id=" + id +
                ", idFormation=" + idFormation +
                ", contenu='" + contenu + '\'' +
                ", dateCommentaire=" + dateCommentaire +
                '}';
    }
}
