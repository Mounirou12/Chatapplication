package com.project.controlleur;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.cj.xdevapi.Statement;

public class Message {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/chat_app";
    private static final String USERNAME = "mounir";
    private static final String PASSWORD = "agnila10";

    public static int sendMessage(int conversation_id, int sender_id, String message) {
        String queryMS = "INSERT INTO messages (conversation_id, sender_id, content) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(queryMS, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, conversation_id);
            stmt.setInt(2, sender_id);
            stmt.setString(3, message);
            stmt.executeUpdate();

            int rowAffected = stmt.executeUpdate();
            if (rowAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int newMessageId = rs.getInt(1);
                        System.out.println("Message envoyé avec succès ! ID : " + newMessageId);
                        return newMessageId;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur d'envoi de message : " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

}
