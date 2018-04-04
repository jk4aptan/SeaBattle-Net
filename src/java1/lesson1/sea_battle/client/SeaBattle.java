package java1.lesson1.sea_battle.client;

import java1.lesson1.sea_battle.client.components.ClientNetHandler;
import java1.lesson1.sea_battle.client.controllers.ClientController;
import java1.lesson1.sea_battle.client.views.GameWindow;

import java.io.IOException;
import java.net.Socket;

public class SeaBattle {
    public static void main(String[] args) {
        String server = "localhost";
        String port = "8086";
        Socket socket = null;

        try {
            socket = new Socket(server, Integer.parseInt(port));
            ClientNetHandler netHandler = new ClientNetHandler(socket);
            GameWindow gameWindow = new GameWindow();
            ClientController controller = new ClientController(gameWindow, netHandler);
            gameWindow.setController(controller);
            netHandler.setController(controller);

            new Thread(netHandler).start();
            gameWindow.start();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
