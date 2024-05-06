package service;

import entite.formation;
import util.DateSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class formationService {

    public void addFormation(formation formation) throws SQLException {
        try (Connection connection = DateSource.getInstance().getConnection()) {
            String query = "INSERT INTO formation (nom_formation, description_formation, date_formation, formateur, lieu_formation, image_formation,formationuser_id) VALUES (?,?,?,?,?,?,?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, formation.getNomFormation());
                statement.setString(2, formation.getDescriptionFormation());
                statement.setDate(3, java.sql.Date.valueOf(formation.getDateFormation())); // Conversion de LocalDate en java.sql.Date
                statement.setString(4, formation.getFormateur());
                statement.setString(5, formation.getLieuFormation());
                statement.setString(6, formation.getImageFormation());
                statement.setInt(7, formation.getFormationuser_id());

                statement.executeUpdate();
            }
        }
    }

    public static void deleteFormation(formation formation) throws SQLException {
        try (Connection connection = DateSource.getInstance().getConnection()) {
            String query = "DELETE FROM formation WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, formation.getId());
                statement.executeUpdate();
            }
        }
    }

    public List<formation> getAllFormations() throws SQLException {
        List<formation> formationList = new ArrayList<>();
        try (Connection connection = DateSource.getInstance().getConnection()) {
            String query = "SELECT * FROM formation";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String nomFormation = resultSet.getString("nom_formation");
                    String descriptionFormation = resultSet.getString("description_formation");
                    LocalDate dateFormation = resultSet.getDate("date_formation").toLocalDate(); // Conversion de java.sql.Date en LocalDate
                    String formateur = resultSet.getString("formateur");
                    String lieuFormation = resultSet.getString("lieu_formation");
                    String imageFormation = resultSet.getString("image_formation");

                    formation formation = new formation(id, nomFormation, descriptionFormation, dateFormation, formateur, lieuFormation, imageFormation);
                    formationList.add(formation);
                }
            }
        }
        return formationList;
    }
    public boolean checkFormationNameUniqueness(String nomFormation) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean isUnique = true;

        try {
            // Obtenir une connexion à la base de données à partir de la classe DateSource
            connection = DateSource.getConnection();

            // Préparer la requête SQL pour vérifier l'existence de la formation
            String query = "SELECT COUNT(*) FROM formation WHERE nom_formation = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, nomFormation);

            // Exécuter la requête et récupérer le résultat
            resultSet = statement.executeQuery();

            // Si le résultat contient des lignes, cela signifie que la formation existe déjà
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                isUnique = (count == 0); // Si count est 0, le nom de la formation est unique
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Gérer l'exception (afficher un message d'erreur, etc.)
        } finally {
            // Fermer les ressources (ResultSet, PreparedStatement, Connection)
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace(); // Gérer l'exception
            }
        }

        return isUnique;
    }

    public void updateFormation(formation updatedFormation) throws SQLException {
        String query = "UPDATE formation SET nom_formation=?, description_formation=?, date_formation=?, formateur=?, lieu_formation=?, image_formation=? WHERE id=?";
        try (Connection conn = DateSource.getInstance().getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, updatedFormation.getNomFormation());
            statement.setString(2, updatedFormation.getDescriptionFormation());
            statement.setDate(3, java.sql.Date.valueOf(updatedFormation.getDateFormation())); // Conversion de LocalDate en java.sql.Date
            statement.setString(4, updatedFormation.getFormateur());
            statement.setString(5, updatedFormation.getLieuFormation());
            statement.setString(6, updatedFormation.getImageFormation());
            statement.setInt(7, updatedFormation.getId());
            statement.executeUpdate();
        }
    }
}
