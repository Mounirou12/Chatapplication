package com.project.controlleur;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Conversation_Participant {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/chat_app";
    private static final String USERNAME = "mounir";
    private static final String PASSWORD = "agnila10";

    public static boolean insertParticipantConversation(int conversation_id, int user_id) {
        String queryPart = "INSERT INTO conversation_participants (conversation_id, user_id) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(queryPart)) {
            stmt.setInt(1, conversation_id);
            stmt.setInt(2, user_id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Utilisateur " + user_id + " ajouté à la conversation " + conversation_id);
                return true;
            }
            return false;
        } catch (SQLException e) {
                    System.out.println("Erreur lors de l'ajout du participant : " + e.getMessage());
                    if(e.getErrorCode() == 1062){// 1062 = Code d'erreur MySQL pour "Duplicate entry"
                        System.out.println("Cet utilisateur fait déjà partie de cette conversation.");
                    }
            return false;
        }
    }

}
