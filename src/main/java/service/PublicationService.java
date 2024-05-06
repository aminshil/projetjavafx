package service;

import entite.Publication;
import entite.user;
import util.DateSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static sample.SessionManager.getCurrentUserId;

public class PublicationService {

    private Connection cnx;

    public PublicationService() throws SQLException {
        cnx = DateSource.getInstance().getConnection();
    }

    public void add(Publication publication) {
        String sql = "INSERT INTO publication (publication, dateofpub, imagepub, topicofpub, publicationuser_id) VALUES (?,?, ?, ?, ?)";

        try (PreparedStatement pstmt = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, publication.getPublication());
            pstmt.setDate(2, Date.valueOf(LocalDate.now()));
            pstmt.setString(3, publication.getImagepub());
            pstmt.setString(4, publication.getTopicofpub());

            pstmt.setInt(5, getCurrentUserId());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Publication> getAll() {
        List<Publication> Publications = new ArrayList<>();
        String sql = "SELECT * FROM publication";

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Publication publication = new Publication();
                publication.setId(rs.getInt("id")); // Set the ID from the ResultSet
                publication.setPublication(rs.getString("publication"));
                publication.setDateofpub(rs.getDate("dateofpub").toLocalDate());
                publication.setTopicofpub(rs.getString("topicofpub"));
                publication.setImagepub(rs.getString("imagepub"));

userService userService = new userService();
                // You need to fetch the associated User object here
                user user = userService.getUserById(rs.getInt("publicationuser_id")); // Dummy user object, replace it with fetching from the database
                publication.setPublicationuser(user);
                Publications.add(publication);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Publications;
    }

    public void update(Publication publication) {
        String sql = "UPDATE publication SET publication = ?, dateofpub = ?, topicofpub = ? ,imagepub=?WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setString(1, publication.getPublication());
            pstmt.setDate(2, Date.valueOf(publication.getDateofpub()));
            pstmt.setString(3, publication.getTopicofpub());
            pstmt.setString(4, publication.getImagepub());
            pstmt.setInt(5, publication.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) throws SQLException {
        CommentService commentService = new CommentService();
        commentService.deleteCommentsOfPub(id);
        String sql = "DELETE FROM publication WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Publication getPublicationById(int id) throws SQLException {
        String sql = "SELECT * FROM publication WHERE id = ?";
        Publication publication = null;
        userService us = new userService();
        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                publication = new Publication();
                publication.setId(rs.getInt("id"));
                publication.setPublication(rs.getString("publication"));
                publication.setDateofpub(rs.getDate("dateofpub").toLocalDate());
                publication.setTopicofpub(rs.getString("topicofpub"));
                publication.setImagepub(rs.getString("imagepub"));


                // You need to fetch the associated User object here
                user user = us.getUserById(rs.getInt("publicationuser_id")); // Dummy user object, replace it with fetching from the database
                publication.setPublicationuser(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return publication;
    }
    public Publication findById(int id) throws SQLException {
        PreparedStatement statement = cnx.prepareStatement("SELECT * FROM type_reclamation WHERE id = ?");
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return new Publication( resultSet.getString("publication"), resultSet.getString("topicofpub"),resultSet.getString("imagepub"));

        } else {
            return null;
        }
    }
    public List<Publication> getAllPublications() throws SQLException {
        List<Publication> publications = new ArrayList<>();
        String sql = "SELECT * FROM Publication";
        try (Statement statement = cnx.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String publication = resultSet.getString("publication");
                LocalDate dateOfPublication = resultSet.getDate("dateofpub").toLocalDate();
                String topicOfPublication = resultSet.getString("topicofpub");
                String imagepub = resultSet.getString("imagepub");


                // Retrieve user_publication_id
                int userPublicationId = resultSet.getInt("publicationuser_id");
                // Retrieve user information from the User table using user_publication_id
                userService us = new userService();
                user user = us.getUserById(userPublicationId);

                Publication pub = new Publication(id, publication, dateOfPublication, topicOfPublication,imagepub, user);
                pub.setPublicationuser(user);
                publications.add(pub);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return publications;
    }

}
