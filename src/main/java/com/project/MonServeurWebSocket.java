package com.project;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import com.project.controlleur.Connexion;

public class MonServeurWebSocket extends WebSocketServer {

    private final Map<WebSocket, Integer> userSession = new HashMap<>();

    public MonServeurWebSocket(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Integer userId = userSession.remove(conn);

        if (userId != null) {
            Connexion.mettreAJourStatusConnexionParId(userId, false);
            System.out.println("Utilisateur ID " + userId+ " s'est deconnecte. ");
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            JSONObject json = new JSONObject(message);
            if ("login".equals(json.getString("action"))) {
                String username = json.getString("username");
                String password = json.getString("password");

                int userId = Connexion.connexion(username, password);

                if (userId != -1) {
                    userSession.put(conn, userId);
                    conn.send("LOGIN_SUCCES");
                    System.out.println("Utilisateur " + username + " (ID: " + userId + ") est connecté.");
                }
            } else {
                conn.send("LOGIN_FAILED"); // Mauvais mot de passe
            }
        } catch (Exception e) {
            System.out.println("Erreur de lecture du message : " + e.getMessage());
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out
                .println("Nouvelle connexion reseau en attente d'authentification : " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onStart() {
        System.out.println("Serveur demmare sur le port " + getPort());
    }

}
