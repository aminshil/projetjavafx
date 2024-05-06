package entite;

import java.util.ArrayList;
import java.util.Collection;

public class Category {

    private Integer id;

    private String typeofcat;

    private Collection<Demande> Demandes;

    public Category() {
    }

    public Category(String typeofcat) {
        this.typeofcat = typeofcat;
    }

    public Category(int categoryId, String typeofcat) {
        this.typeofcat = typeofcat;
        this.id = categoryId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeofcat() {
        return typeofcat;
    }

    public void setTypeofcat(String typeofcat) {
        this.typeofcat = typeofcat;
    }

    public Collection<Demande> getDemandes() {
        return Demandes;
    }

    public void setDemandes(Collection<Demande> Demandes) {
        this.Demandes = Demandes;
    }

    public void addDemande(Demande demande) {
        if (this.Demandes == null) {
            this.Demandes = new ArrayList<>();
        }
        this.Demandes.add(demande);
        demande.setCategory(this);
    }

    public void removeDemande(Demande demande) {
        if (this.Demandes != null) {
            this.Demandes.remove(demande);
            demande.setCategory(null);
        }
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", typeofcat='" + typeofcat + '\'' +
                '}';
    }
}


