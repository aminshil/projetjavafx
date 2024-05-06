package service;

import entite.Category;
import entite.Demande;
import entite.user;
import util.DateSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static sample.SessionManager.getCurrentUserId;
import static sample.affichercatController.category;

public class DemandeService {

    private Connection cnx;

    public DemandeService() throws SQLException {
        cnx = DateSource.getInstance().getConnection();
    }

    public void add(Demande demande) throws RuntimeException {
        String sql = "INSERT INTO Demande (demande, nameofobj, stateofdem, dateofdem, Category_id, demandeuser_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, demande.getDemande());
            pstmt.setString(2, demande.getNameofobj());
            pstmt.setString(3, demande.getStateofdem());
            pstmt.setDate(4, Date.valueOf(LocalDate.now())); // Set dateofdem at index 4
            // Add null check for Category object
            if (demande.getCategory() != null) {
                pstmt.setInt(5, demande.getCategory().getId()); // Set Category_id at index 5
            } else {
                // Handle null Category object (you may choose to throw an exception, set a default value, etc.)
                pstmt.setNull(5, Types.INTEGER);
            }
            // Set demandeuser_id at index 6
            pstmt.setInt(6, getCurrentUserId());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                demande.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Demande> getAll() throws SQLException {
        List<Demande> demandes = new ArrayList<>();
        String sql = "SELECT * FROM Demande";
        userService us = new userService();
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Demande demande = new Demande();
                demande.setId(rs.getInt("id"));
                demande.setDemande(rs.getString("demande"));
                demande.setNameofobj(rs.getString("nameofobj"));
                demande.setStateofdem(rs.getString("stateofdem"));
                demande.setDateofdem(rs.getDate("dateofdem").toLocalDate());

                // Fetching associated Category and User objects
                Category category = new Category();
                category.setId(rs.getInt("Category_id")); // Assuming Category_id is the column name in the Demande table
                demande.setCategory(category);

                user user = us.getUserById(rs.getInt("demandeuser_id"));
                demande.setDemandeuser(user);
                demandes.add(demande);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return demandes;
    }

    public void update(Demande demande) {
        String sql = "UPDATE Demande SET demande = ?, nameofobj = ?, stateofdem = ?, dateofdem = ?, Category_id = ?, demandeuser_id = ? WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setString(1, demande.getDemande());
            pstmt.setString(2, demande.getNameofobj());
            pstmt.setString(3, demande.getStateofdem());
            pstmt.setDate(4, Date.valueOf(demande.getDateofdem()));
            if (demande.getCategory() != null) {
                pstmt.setInt(5, demande.getCategory().getId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }

            pstmt.setInt(6, 1);
            pstmt.setInt(7, demande.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) throws SQLException {
        CommentService commentService = new CommentService();
        commentService.deleteCommentsOfDemande(id);
        String sql = "DELETE FROM Demande WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Demande getDemandeById(int id) throws SQLException {
        String sql = "SELECT * FROM Demande WHERE id = ?";
        Demande demande = null;
        userService us = new userService();
        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                demande = new Demande();
                demande.setId(rs.getInt("id"));
                demande.setDemande(rs.getString("demande"));
                demande.setNameofobj(rs.getString("nameofobj"));
                demande.setStateofdem(rs.getString("stateofdem"));
                demande.setDateofdem(rs.getDate("dateofdem").toLocalDate());
                // Fetching associated Category and User objects
                Category category = new Category();
                category.setId(rs.getInt("Category_id")); // Assuming Category_id is the column name in the Demande table
                demande.setCategory(category);

                user user = us.getUserById(rs.getInt("demandeuser_id"));
                demande.setDemandeuser(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return demande;
    }


    public Demande findById(int id) throws SQLException {
        PreparedStatement statement = cnx.prepareStatement("SELECT * FROM type_reclamation WHERE id = ?");
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();
        int categoryId = resultSet.getInt("Category_id");

        String getCategorySql = "SELECT typeofcat FROM Category WHERE id = ?";
        try (PreparedStatement getCategoryStatement = cnx.prepareStatement(getCategorySql)) {
            getCategoryStatement.setInt(1, categoryId);
            try (ResultSet categoryResultSet = getCategoryStatement.executeQuery()) {
                if (categoryResultSet.next()) {
                    String typeofcat = categoryResultSet.getString("typeofcat");
                    Category category = new Category(categoryId, typeofcat);
                }
            }
            userService userService = new userService();
            if (resultSet.next()) {
                Demande demande = new Demande(resultSet.getInt("id"), resultSet.getString("demande"), resultSet.getString("nameofobj"), resultSet.getString("stateofdem"), resultSet.getDate("dateofdem").toLocalDate(), category, userService.getUserById(resultSet.getInt("demandeuser_id")));
                return demande;
            } else {
                return null;
            }
        }
    }

    public List<Demande> getAllDemandes() throws SQLException {
        List<Demande> demandes = new ArrayList<>();
        String sql = "SELECT * FROM Demande";
        userService us = new userService();

        try (Statement statement = cnx.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String demandeText = resultSet.getString("demande");
                String nameofobj = resultSet.getString("nameofobj");
                String stateofdem = resultSet.getString("stateofdem");
                LocalDate dateOfDem = resultSet.getDate("dateofdem").toLocalDate();
                // Retrieve category_id
                int categoryId = resultSet.getInt("category_id");
                // Retrieve category information from the Category table using category_id
                Category category = null;
                String typeofcat = null;

                String getCategorySql = "SELECT typeofcat FROM Category WHERE id = ?";
                try (PreparedStatement getCategoryStatement = cnx.prepareStatement(getCategorySql)) {
                    getCategoryStatement.setInt(1, categoryId);
                    try (ResultSet categoryResultSet = getCategoryStatement.executeQuery()) {
                        if (categoryResultSet.next()) {
                            typeofcat = categoryResultSet.getString("typeofcat");
                            category = new Category(typeofcat);
                        }
                    }
                }
                // Retrieve user_demande_id
                int userDemandeId = resultSet.getInt("demandeuser_id");
                // Retrieve user information from the User table using user_demande_id
                user user = us.getUserById(userDemandeId);

                Demande demande = new Demande(id,demandeText, nameofobj, stateofdem, dateOfDem, category, user);
                demandes.add(demande);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return demandes;
    }

}
