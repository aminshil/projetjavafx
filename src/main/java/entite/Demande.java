package entite;

import java.time.LocalDate;

public class Demande {

    private Integer id;

    private String demande;

    private String nameofobj;

    private String stateofdem;

    private LocalDate dateofdem;

    private Category category;

    private user demandeuser;

    public Demande(String demande, Integer id, String nameofobj, String stateofdem, LocalDate dateofdem, Category category, user demandeuser) {
        this.demande = demande;
        this.id = id;
        this.nameofobj = nameofobj;
        this.stateofdem = stateofdem;
        this.dateofdem = dateofdem;
        this.category = category;
        this.demandeuser = demandeuser;
    }

    public Demande(int id, String demandeText, String nameofobj, String stateofdem, LocalDate dateOfDem, Category category, user user) {
        this.id = id;
        this.demande = demandeText;
        this.nameofobj = nameofobj;
        this.stateofdem = stateofdem;
        this.dateofdem = dateOfDem;
        this.category = category;
        this.demandeuser = user;
    }


//private Collection<Comment> comments;


    public Demande() {
    }

    public Demande(String demande, String nameofobj, String stateofdem, LocalDate dateofdem, Category category, user demandeuser) {
        this.demande = demande;
        this.nameofobj = nameofobj;
        this.stateofdem = stateofdem;
        this.dateofdem = dateofdem;
        this.category = category;
        this.demandeuser = demandeuser;
    }

    public Demande(String demande, String nameofobj, String stateofdem, String categoryId) {
        this.demande = demande;
        this.nameofobj = nameofobj;
        this.stateofdem = stateofdem;


    }
    public Demande(String demande, String nameofobj, String stateofdem, Category category) {
        this.demande = demande;
        this.nameofobj = nameofobj;
        this.stateofdem = stateofdem;
        this.category = category;

    }

    public Demande(String text, String text1) {
        this.demande = text;
        this.nameofobj = text1;

    }

    public Demande(String text, String text1, String string) {
        this.demande = text;
        this.nameofobj = text1;
        this.stateofdem = string;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDemande() {
        return demande;
    }

    public void setDemande(String demande) {
        this.demande = demande;
    }

    public String getNameofobj() {
        return nameofobj;
    }

    public void setNameofobj(String nameofobj) {
        this.nameofobj = nameofobj;
    }

    public String getStateofdem() {
        return stateofdem;
    }

    public void setStateofdem(String stateofdem) {
        this.stateofdem = stateofdem;
    }

    public LocalDate getDateofdem() {
        return dateofdem;
    }

    public void setDateofdem(LocalDate dateofdem) {
        this.dateofdem = dateofdem;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    /* public Collection<Comment> getComments() {
        return comments;
    }

    public void setComments(Collection<Comment> comments) {
        this.comments = comments;
    }*/

    public user getDemandeuser() {
        return demandeuser;
    }

    public void setDemandeuser(user demandeuser) {
        this.demandeuser = demandeuser;
    }

   /* public int getNumberOfComments() {
        return this.comments.size();
    }*/

    @Override
    public String toString() {
        return "Demande{" +
                "id=" + id +
                ", demande='" + demande + '\'' +
                ", nameofobj='" + nameofobj + '\'' +
                ", stateofdem='" + stateofdem + '\'' +
                ", dateofdem=" + dateofdem +
                ", category=" + category +
                //", comments=" + comments +
                ", demandeuser=" + demandeuser +
                '}';
    }
/*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Demande that)) return false;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getDemande(), that.getDemande()) && Objects.equals(getNameofobj(), that.getNameofobj()) && Objects.equals(getStateofdem(), that.getStateofdem()) && Objects.equals(getDateofdem(), that.getDateofdem()) && Objects.equals(getCategory(), that.getCategory()) && Objects.equals(getComments(), that.getComments()) && Objects.equals(getDemandeuser(), that.getDemandeuser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDemande(), getNameofobj(), getStateofdem(), getDateofdem(), getCategory(), getComments(), getDemandeuser());
    }*/
}
