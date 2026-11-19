package com.project;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = 8887;
        MonServeurWebSocket serveur = new MonServeurWebSocket(port);
        Thread serverThread = new Thread(() -> {
            serveur.start();
            System.out.println("Serveur WebSocket démarré sur le port " + port);
        });
        serverThread.start();

        Thread.sleep(1000);

        URI serverUri = new URI("ws://localhost:" + port);

        WebSocketClient client = new WebSocketClient(serverUri) {

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("Client : Connexion établie avec le serveur.");
                JSONObject loginMsg = new JSONObject();
                loginMsg.put("action", "login");
                loginMsg.put("username", "monor12");
                loginMsg.put("password", "agnila10");

                System.out.println("Client : Envoi de la demande de login...");
                send(loginMsg.toString());
            }

            @Override
            public void onMessage(String message) {
                // 4. Afficher la réponse du serveur
                System.out.println("Client : Réponse reçue du serveur -> " + message);

                // Si on reçoit LOGIN_SUCCESS, on peut fermer la connexion pour finir le test
                if (message.equals("LOGIN_SUCCESS")) {
                    System.out.println("Test réussi ! Fermeture.");
                    close();
                } else if (message.equals("LOGIN_FAILED")) {
                    System.out.println("Test échoué : Mauvais mot de passe ou utilisateur inconnu.");
                    close();
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("Client : Connexion fermée.");
            }

            @Override
            public void onError(Exception ex) {
                System.err.println("Client : Erreur -> " + ex.getMessage());
            }

        };

        client.connectBlocking();
        Thread.sleep(5000);
        serveur.stop();
        System.out.println("Serveur arrêté.");
    }
}