package service;

import util.DateSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParticipationFormationService {

    public void registerParticipation(int userId, int formationId) throws SQLException {
        try (Connection connection = DateSource.getInstance().getConnection()) {
            String query = "INSERT INTO formation_user (user_id, formation_id) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                statement.setInt(2, formationId);
                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("No rows affected, registration failed.");
                }
            }
        }
    }

    public void cancelParticipation(int userId, int formationId) throws SQLException {
        try (Connection connection = DateSource.getInstance().getConnection()) {
            String query = "DELETE FROM formation_user WHERE user_id = ? AND formation_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                statement.setInt(2, formationId);
                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("No rows affected, cancellation failed.");
                }
            }
        }
    }

    public boolean isUserParticipating(int userId, int formationId) throws SQLException {
        try (Connection connection = DateSource.getInstance().getConnection()) {
            String query = "SELECT COUNT(*) FROM formation_user WHERE user_id = ? AND formation_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                statement.setInt(2, formationId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) > 0;
                    }
                }
            }
        }
        return false;
    }


}
