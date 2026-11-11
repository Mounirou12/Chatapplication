package com.project;

public class Main {
    public static void main(String[] args) {
        MonServeurWebSocket serveur = new MonServeurWebSocket(8887);
        serveur.start();
    }
}