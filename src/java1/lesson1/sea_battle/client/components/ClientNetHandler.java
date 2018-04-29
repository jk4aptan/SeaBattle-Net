package java1.lesson1.sea_battle.client.components;

import java1.lesson1.sea_battle.client.controllers.ClientController;
import java1.lesson1.sea_battle.client.models.Message;
import java1.lesson1.sea_battle.server.models.Messageable;

import java.io.*;
import java.net.Socket;

public class ClientNetHandler implements Runnable {
    private boolean exit;
    private Socket socket;
    private ClientController controller;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientNetHandler(Socket socket) {
        exit = false;
        this.socket = socket;
        try {
            out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            out.flush();
            this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (!exit) {
                Messageable message = (Messageable) in.readObject();

                switch (message.getType()) {
                    case "adversaries":
                        controller.setAdversaries(message.getContent());
                        break;
                    case "letsPlay":
                        controller.letsPlay(message.getContent());
                        break;
                    case "adversaryResponse":
                        controller.adversaryResponse(message.getContent());
                        break;
                    case "initShipsCoordinates":
                        controller.initShipsCoordinates(message.getContent());
                        break;
                    case "turnOn":
                        controller.setTurnOn(message.getContent());
                        break;
                    case "turnOff":
                        controller.setTurnOff(message.getContent());
                        break;
                    case "setCurrentResult":
                        controller.setCurrentResult(message.getContent());
                        break;
                    case "setLastSunkShip":
                        controller.setLastSunkShip(message.getContent());
                        break;
                    case "gameIsOver":
                        controller.gameIsOver(message.getContent());
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send Message to the server
     * @param type message's type
     * @param content message's content
     */
    private void sendMessage(String type, String content) {
        try {
            Messageable message = new Message(type, content);
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get Adversaries
     */
    public void getAdversaries() {
        sendMessage("getAdversaries", "");
    }

    /**
     * Set the game controller
     * @param controller game's controller
     */
    public void setController(ClientController controller) {
        this.controller = controller;
    }

    /**
     * Close outputstream and socket on exit
     * @param exit if true then close
     */
    public void setExit(boolean exit) {
        sendMessage("exit", "");
        this.exit = exit;
    }

    /**
     * Setter playerName
     * @param playerName player's name
     */
    public void sendPlayerName(String playerName) {
        sendMessage("setPlayerName", playerName);
    }

    /**
     * Request Adversary
     * @param id adversary
     */
    public void requestAdversary(String id) {
        sendMessage("requestAdversary", id);
    }

    /**
     * Ответ игрока на предложение сыграть
     * @param response Ответ игрока
     */
    public void returnResponse(Integer response) {
        sendMessage("returnResponse", response.toString());
    }

    /**
     * Setter shot's coordinate
     * @param shotCoordinate shot's coordinate
     */
    public void setShotCoordinate(Integer shotCoordinate) {
        sendMessage("setShotCoordinate", shotCoordinate.toString());
    }

    /**
     * Освобождает флаг занятости
     */
    public void setIsNotBusy() {
        sendMessage("setIsNotBusy", "");
    }
}
