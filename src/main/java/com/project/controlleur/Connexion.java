package com.project.controlleur;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import org.mindrot.jbcrypt.BCrypt;

public class Connexion {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/chat_app";
    private static final String USERNAME = "mounir";
    private static final String PASSWORD = "agnila10";

    public static boolean insertUser(String username, String passwordHash,
            String displayName, boolean is_online, LocalDate lastSeen) {
        is_online = false;
        String queryCR = "INSERT INTO users(username,password_hash,display_name,is_online,last_seen) VALUES (?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(queryCR, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, BCrypt.hashpw(passwordHash, BCrypt.gensalt()));
            stmt.setString(3, displayName);
            stmt.setBoolean(4, is_online);
            stmt.setObject(5, lastSeen);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int newUserId = rs.getInt(1);
                        Session.setCurrentUser(
                                newUserId,
                                username,
                                displayName,
                                lastSeen);
                        return true; // Succès total
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean connexion(String identifiant, String passwordHash) {
        // 1. On cherche l'utilisateur par email OU firstName (sans le mot de passe)
        String queryCn = "SELECT id,username,password_hash,display_name,is_online,last_seen FROM Users "
                + "WHERE username = ? ";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(queryCn)) {

            stmt.setString(1, identifiant); // Pour le nom de l'utilisateur

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hasCode = rs.getString("password_hash");
                    if (BCrypt.checkpw(passwordHash, hasCode)) {
                        Date sqlLastSeen = rs.getDate("last_seen");
                        LocalDate lastseeen = (sqlLastSeen != null)
                                ? sqlLastSeen.toLocalDate()
                                : null;
                        int userId = rs.getInt("id");
                        String username = rs.getString("username");
                        String sqlLastseen = rs.getString("display_name");

                        mettreAJourStatusConnexion(conn, userId, true);
                        Session.setCurrentUser(
                                userId,
                                username,
                                sqlLastseen,
                                lastseeen);
                        return true;
                    }
                }
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification en base de données : " + e.getMessage());
            return false;
        }
    }

    private static void mettreAJourStatusConnexion(Connection conn, int userId, boolean isOnline) {
        String query = isOnline
                ? "UPDATE users SET is_online = TRUE WHERE id = ?"
                : "UPDATE users SET is_online = FALSE, last_seen = CURRENT_DATE WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du statut en ligne : " + e.getMessage());
        }
    }

    public static boolean updateProfilUser(int id, String firstName, String lastName, String email, String phone) {
        String queryUPU = "UPDATE Users SET firstName=?,lastName=?,email=?,phone=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(queryUPU)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setInt(5, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification en base de données : " + e.getMessage());
            return false;
        }
    }

    public static boolean passwordChange(int userId, String oldPassword, String newPassword, String confirmPassword) {

        // ÉTAPE 1 : Vérifier si le nouveau mot de passe et la confirmation sont
        // identiques
        if (newPassword == null || !newPassword.equals(confirmPassword)) {
            return false;
        }
        // Connexion à la base de données
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {

            // ÉTAPE 2 : Vérifier si l'ancien mot de passe est correct
            String sqlVerif = "SELECT passwordHash FROM Users WHERE id = ?";
            try (PreparedStatement pstmtVerif = conn.prepareStatement(sqlVerif)) {
                pstmtVerif.setInt(1, userId);

                try (ResultSet rs = pstmtVerif.executeQuery()) {
                    if (rs.next()) {
                        String passwordStck = rs.getString("passwordHash");

                        // Sécurité : Si vous utilisez du hac]hage (ex: BCrypt), utilisez :
                        // !BCrypt.checkpw(ancienMdp, mdpStocke)
                        if (!BCrypt.checkpw(oldPassword, passwordStck)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }

            // ÉTAPE 3 : Mettre à jour le mot de passe dans la base de données
            String sqlUpdate = "UPDATE Users SET passwordHash = ? WHERE id = ?";
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                // Sécurité : Dans un vrai projet, hachez "nouveauMdp" avant de l'injecter (ex:
                // BCrypt.hashpw(...))
                pstmtUpdate.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
                pstmtUpdate.setInt(2, userId);

                int lignesModifiees = pstmtUpdate.executeUpdate();
                return lignesModifiees > 0;
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
            return false;
        }
    }

}
