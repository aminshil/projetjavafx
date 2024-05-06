package service;

import entite.Comment;
import entite.Demande;
import entite.Publication;
import entite.user;
import util.DateSource;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static sample.SessionManager.getCurrentUserId;

public class CommentService {

    private Connection cnx;

    public CommentService() throws SQLException {
        cnx = DateSource.getInstance().getConnection();
    }

    // Method to add a new comment to the database
    public void addComment(Comment comment) {
        String sql = "INSERT INTO Comment (comment, dateofcom, publication_id, demande_id, commentpubdemuser_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, comment.getComment());
            statement.setTimestamp(2, Timestamp.valueOf(comment.getDateofcom()));

            // Set publication_id or demande_id based on which one is present
            if (comment.getPublication() != null) {
                statement.setInt(3, comment.getPublication().getId());
            } else {
                statement.setNull(3, Types.INTEGER);
            }

            if (comment.getDemande() != null) {
                statement.setInt(4, comment.getDemande().getId());
            } else {
                statement.setNull(4, Types.INTEGER);
            }

            statement.setInt(5, getCurrentUserId());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    comment.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating comment failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting comment into the database: " + e.getMessage(), e);
        }
    }

    // Method to retrieve comments for a specific publication or demande
    public List<Comment> getCommentsForEntity(int entityId, boolean isPublication) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM Comment WHERE " + (isPublication ? "publication_id" : "demande_id") + " = ?";
        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            statement.setInt(1, entityId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Comment comment = new Comment();
                comment.setId(resultSet.getInt("id"));
                comment.setComment(resultSet.getString("comment"));
                comment.setDateofcom(resultSet.getTimestamp("dateofcom").toLocalDateTime());

                // Populate related user and entity (Publication or Demande)
                int userId = resultSet.getInt("commentpubdemuser_id");
                userService service = new userService();

                comment.setUser(service.getUserById(userId));

                if (isPublication) {
                    comment.setPublication(fetchPublicationById(entityId));
                } else {
                    comment.setDemande(fetchDemandeById(entityId));
                }

                comments.add(comment);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving comments from the database: " + e.getMessage(), e);
        }
        return comments;
    }

    // Dummy methods to fetch Publication, Demande, and User
    private Publication fetchPublicationById(int id) {
        // Fetch publication logic
        return new Publication(); // Placeholder
    }

    private Demande fetchDemandeById(int id) {
        // Fetch demande logic
        return new Demande(); // Placeholder
    }

    private user fetchUserById(int id) {
        // Fetch user logic
        return new user(); // Placeholder
    }
    public void updateComment(int commentId, String updatedContent) {
        String sql = "UPDATE Comment SET comment = ?, dateofcom = ? WHERE id = ?";
        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            statement.setString(1, updatedContent);
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now())); // Update the timestamp to the current time
            statement.setInt(3, commentId);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating comment failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating comment in the database: " + e.getMessage(), e);
        }
    }
    public void deleteComment(int commentId) {
        String sql = "DELETE FROM Comment WHERE id = ?";
        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            statement.setInt(1, commentId);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting comment failed, no rows affected.");

            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting comment from the database: " + e.getMessage(), e);
        }
    }


    public void deleteCommentsOfPub(int publicationId) {
        String sql = "DELETE FROM Comment WHERE publication_id = ?";
        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            statement.setInt(1, publicationId);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("No comments found for publication ID: " + publicationId);
            } else {
                System.out.println("Deleted " + affectedRows + " comments for publication ID: " + publicationId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting comments from the database: " + e.getMessage(), e);
        }
    }

    public void deleteCommentsOfDemande(int demandeId) {
        String sql = "DELETE FROM Comment WHERE demande_id = ?";
        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            statement.setInt(1, demandeId);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("No comments found for demande ID: " + demandeId);
            } else {
                System.out.println("Deleted " + affectedRows + " comments for demande ID: " + demandeId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting comments from the database: " + e.getMessage(), e);
        }
    }


}
