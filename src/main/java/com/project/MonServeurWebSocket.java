package com.project;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class MonServeurWebSocket extends WebSocketServer{

    public MonServeurWebSocket(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        conn.send("Recu : "+message);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Client connecte : " +conn.getRemoteSocketAddress());
    }

    @Override
    public void onStart() {
        System.out.println("Serveur demmare sur le port " + getPort());
    }

}
