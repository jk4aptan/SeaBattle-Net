package java1.lesson1.sea_battle.server;

import java1.lesson1.sea_battle.server.components.SeaBattleHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SeaBattleServer {
    private static final int PORT = 8086;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                new SeaBattleHandler(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
