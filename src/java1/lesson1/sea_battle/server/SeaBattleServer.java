package java1.lesson1.sea_battle.server;

import java1.lesson1.sea_battle.server.components.SeaBattleHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SeaBattleServer {
    public static void main(String[] args) {
        int port = 8086;
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
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
