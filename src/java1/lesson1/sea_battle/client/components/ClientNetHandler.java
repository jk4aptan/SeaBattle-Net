package java1.lesson1.sea_battle.client.components;

import java1.lesson1.sea_battle.client.controllers.ClientController;

import java.io.*;
import java.net.Socket;

public class ClientNetHandler implements Runnable {
    private boolean exit;
    private Socket socket;
    private ClientController controller;
    private DataInputStream in;
    private DataOutputStream out;

    public ClientNetHandler(Socket socket) {
        exit = false;
        this.socket = socket;
        try {
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (!exit) {
                String message = in.readUTF();

                if (message.startsWith("adversaries")) {
                    controller.setAdversaries(message.substring(12));
                }
                else if (message.startsWith("letsPlay")) {
                    controller.letsPlay(message.substring(9));
                }
                else if (message.startsWith("adversaryResponse")) {
                    controller.adversaryResponse(message.substring(18));
                }
                else if (message.startsWith("initShipsCoordinates")) {
                    controller.initShipCoordinates(message.substring(21));
                }
                else if (message.startsWith("turnOn")) {
                    controller.setTurnOn(message.substring(7));
                }
                else if (message.startsWith("turnOff")) {
                    controller.setTurnOff(message.substring(8));
                }
                else if (message.startsWith("setCurrentResult")) {
                    controller.setCurrentResult(message.substring(17));
                }
                else if (message.startsWith("setLastSunkShip")) {
                    controller.setLastSunkShip(message.substring(16));
                }
                else if (message.startsWith("gameIsOver")) {
                    controller.gameIsOver(message.substring(11));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
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
     * Get Adversaries
     */
    public void getAdversaries() {
        sendMessage("getAdversaries");
    }

    /**
     * Send Message to the server
     * @param message sending message
     */
    private void sendMessage(String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        sendMessage("exit");
        this.exit = exit;
    }

    /**
     * Setter playerName
     * @param playerName player's name
     */
    public void sendPlayerName(String playerName) {
        sendMessage("setPlayerName " + playerName);
    }

    /**
     * Request Adversary
     * @param id adversary
     */
    public void requestAdversary(String id) {
        sendMessage("requestAdversary " + id);
    }

    /**
     * Ответ игрока на предложение сыграть
     * @param response Ответ игрока
     */
    public void returnResponse(int response) {
        sendMessage("returnResponse " + response);
    }

    /**
     * Setter shot's coordinate
     * @param shotCoordinate shot's coordinate
     */
    public void setShotCoordinate(int shotCoordinate) {
        sendMessage("setShotCoordinate " + shotCoordinate);
    }

    /**
     * Освобождает флаг занятости
     */
    public void setIsNotBusy() {
        sendMessage("setIsNotBusy");
    }
}
