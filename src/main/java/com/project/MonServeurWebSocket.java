package com.project;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;

import com.project.controlleur.Connexion;
import com.project.controlleur.Conversation;
import com.project.controlleur.Conversation_Participant;

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
            System.out.println("Utilisateur ID " + userId + " s'est deconnecte. ");
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
                } else if ("create_group".equals("action")) {
                    String groupName = json.getString("name");
                    JSONArray membersArray = json.getJSONArray("members");

                    int newGroupId = Conversation.createConversation(groupName);
                    if (newGroupId != -1) {
                        for (int i = 0; i < membersArray.length(); i++) {
                            int userId2 = membersArray.getInt(i);
                            Conversation_Participant.insertParticipantConversation(newGroupId, userId2);
                        }
                        JSONObject response = new JSONObject();
                        response.put("action", "group_created");
                        response.put("group_id", newGroupId);
                        response.put("name", groupName);
                        response.put("members", membersArray);

                        for (int i = 0; i < membersArray.length(); i++) {
                            int targetUserId = membersArray.getInt(i);
                            envoyerAMembre(targetUserId, response.toString());
                        }
                        conn.send("{\"status\": \"success\", \"message\": \"Groupe créé\"}");
                    }

                }
            } else {
                conn.send("LOGIN_FAILED"); // Mauvais mot de passe
                conn.send("{\"status\": \"error\", \"message\": \"Erreur lors de la création du groupe\"}");
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

    private void envoyerAMembre(int targetUserId, String jsonMessage) {
        for (Map.Entry<WebSocket, Integer> entry : userSession.entrySet()) {
            if (entry.getValue() == targetUserId) {
                WebSocket targetConn = entry.getKey();
                if (targetConn.isOpen()) {
                    targetConn.send(jsonMessage);
                }
            }
        }
    }

}
