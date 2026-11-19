package com.project.controlleur;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Conversation {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/chat_app";
    private static final String USERNAME = "mounir";
    private static final String PASSWORD = "agnila10";

    public static int createConversation(String name) {
        String queryCN = "INSERT INTO conversations(name) VALUES(?)";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(queryCN, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur création conversation : " + e.getMessage());
        }
        return -1;
    }

}
