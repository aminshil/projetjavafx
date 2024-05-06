package service;

import entite.Category;
import util.DateSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryService {

    private Connection cnx;

    public CategoryService() throws SQLException {
        cnx = DateSource.getInstance().getConnection();
    }

    public void add(Category category) {
        String sql = "INSERT INTO Category (typeofcat) VALUES (?)";

        try (PreparedStatement pstmt = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, category.getTypeofcat());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                category.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Category> getAll() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM Category";

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setTypeofcat(rs.getString("typeofcat"));

                categories.add(category);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categories;
    }

    public void update(Category category) {
        String sql = "UPDATE Category SET typeofcat = ? WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setString(1, category.getTypeofcat());
            pstmt.setInt(2, category.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM Category WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Category getCategoryById(int id) {
        String sql = "SELECT * FROM Category WHERE id = ?";
        Category category = null;

        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                category = new Category();
                category.setId(rs.getInt("id"));
                category.setTypeofcat(rs.getString("typeofcat"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return category;
    }
    public Category findById(int id) throws SQLException {
        PreparedStatement statement = cnx.prepareStatement("SELECT * FROM Category WHERE id = ?");
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return new Category(resultSet.getString("typeofcat"));
        } else {
            return null;
        }
    }


    public Category getCategoryByType(String type) throws SQLException {
        String sql = "SELECT * FROM Category WHERE typeofcat = ?";
        Category category = null;

        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                category = new Category();
                category.setId(rs.getInt("id"));
                category.setTypeofcat(rs.getString("typeofcat"));
            }
        } catch (SQLException e) {
            throw e;
        }
        return category;
    }
}

