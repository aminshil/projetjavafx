package entite;

import java.time.LocalDate;


public class Publication {


    private Integer id;

    private String publication;

    private LocalDate dateofpub;

    private String topicofpub;

    /*@OneToMany(mappedBy = "publication", cascade = CascadeType.REMOVE)
    private Collection<Comment> comments;*/

    private String imagepub;
    private user user;

    public Publication(String publication, String topicofpub, String imagepub) {
        this.publication = publication;
        this.topicofpub = topicofpub;
        this.imagepub = imagepub;
    }

    public String getImagepub() {
        return imagepub;
    }

    public void setImagepub(String imagepub) {
        this.imagepub = imagepub;
    }

    private user publicationuser;

    public Publication() {
    }
    public Publication(String publication, LocalDate dateofpub, String topicofpub, user publicationuser) {
        this.publication = publication;
        this.dateofpub = dateofpub;
        this.topicofpub = topicofpub;
        this.publicationuser = publicationuser;
    }

    public Publication(String publication, String topicofpub) {
        this.publication = publication;
        this.topicofpub = topicofpub;
    }

    public Publication(int id, String publication, LocalDate dateOfPublication, String topicOfPublication,String imagepub, user user) {
        this.publication = publication;
        this.topicofpub = topicOfPublication;
        this.id = id;
        this.dateofpub = dateOfPublication;
        this.imagepub=imagepub;
this.user=user;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "publication{" +
                "id=" + id +
                ", publication='" + publication + '\'' +
                ", dateofpub=" + dateofpub +
                ", topicofpub='" + topicofpub + '\'' +
                ", publicationuser=" + publicationuser +
                '}';
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public LocalDate getDateofpub() {
        return dateofpub;
    }

    public void setDateofpub(LocalDate dateofpub) {
        this.dateofpub = dateofpub;
    }

    public String getTopicofpub() {
        return topicofpub;
    }

    public void setTopicofpub(String topicofpub) {
        this.topicofpub = topicofpub;
    }

    /*public Collection<Comment> getComments() {
        return comments;
    }

    public void setComments(Collection<Comment> comments) {
        this.comments = comments;
    }

    public String getImagepub() {
        return imagepub;
    }

    public void setImagepub(String imagepub) {
        this.imagepub = imagepub;
    }
*/
    public user getPublicationuser() {
        return publicationuser;
    }

    public void setPublicationuser(user publicationuser) {
        this.publicationuser = publicationuser;
    }


}
