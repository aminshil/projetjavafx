package sample;

import entite.user;

import java.time.LocalDate;

public class SessionManager {
    private static user currentUser;

    public static void setCurrentUser(user user) {
        currentUser = user;
    }

    public static user getCurrentUser() {
        return currentUser;
    }

    public static void clearSession() {
        currentUser = null;
    }

    // Getters for specific fields of the current user
    public static int getCurrentUserId() {
        return currentUser.getId();
    }

    public static String getCurrentUserNom() {
        return currentUser.getNom();
    }
    public static LocalDate getCurrentUserDate() {
        return currentUser.getDateNaissance();
    }

    public static String getCurrentUserPrenom() {
        return currentUser.getPrenom();
    }

    public static String getCurrentUserEmail() {
        return currentUser.getEmail();
    }

    public static String getCurrentUserRole() {
        return currentUser.getRole();
    }


    // Setters for specific fields of the current user
    public static void setCurrentUserId(int id) {
        currentUser.setId(id);
    }

    public static void setCurrentUserNom(String nom) {
        currentUser.setNom(nom);
    }

    public static void setCurrentUserPrenom(String prenom) {
        currentUser.setPrenom(prenom);
    }

    public static void setCurrentUserEmail(String email) {
        currentUser.setEmail(email);
    }

    public static void setCurrentUserRole(String Role) {
        currentUser.setRole(Role);
    }

    public static void setCurrentUserDate(LocalDate DateNaissance) {currentUser.setDateNaissance(DateNaissance);}

    // Add more getters and setters for other fields if needed
}
