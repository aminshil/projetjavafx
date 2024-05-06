package service;

import entite.commentaireformation;
import util.DateSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class commentaireformationService {

    public static void addCommentaire(commentaireformation commentaire) throws SQLException {
        try (Connection connection = DateSource.getInstance().getConnection()) {
            String query = "INSERT INTO commentaireformation (formation_id, contenu, datecreation,commentaireuserformation_id\t) VALUES (?, ?, ?,?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, commentaire.getIdFormation());
                statement.setString(2, commentaire.getContenu());
                statement.setTimestamp(3, commentaire.getDateCommentaire());
                statement.setInt(4, commentaire.getCommentaireuserformation_id());
                statement.executeUpdate();
            }
        }
    }

    public void deleteCommentaire(commentaireformation commentaire) throws SQLException {
        try (Connection connection = DateSource.getInstance().getConnection()) {
            String query = "DELETE FROM commentaireformation WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, commentaire.getId());
                statement.executeUpdate();
            }
        }

    }

    public List<commentaireformation> getCommentairesForFormation(int idFormation) throws SQLException {
        List<commentaireformation> commentaireList = new ArrayList<>();
        try (Connection connection = DateSource.getInstance().getConnection()) {
            String query = "SELECT * FROM commentaireformation WHERE formation_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idFormation);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String contenu = resultSet.getString("contenu");
                        Timestamp dateCommentaire = resultSet.getTimestamp("datecreation");
                        int commentaireuserformation_id = resultSet.getInt("commentaireuserformation_id");

                         commentaireformation commentaire = new commentaireformation(id, idFormation, contenu, dateCommentaire,commentaireuserformation_id);
                        commentaireList.add(commentaire);
                    }
                }
            }
        }
        return commentaireList;
    }
    public void deleteCommentairesByFormationId(int formationId) throws SQLException {
        try (Connection connection = DateSource.getInstance().getConnection()) {
            String query = "DELETE FROM commentaireformation WHERE formation_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, formationId);
                int affectedRows = statement.executeUpdate();
                System.out.println(affectedRows + " comments were deleted.");
            }
        }
    }

}
