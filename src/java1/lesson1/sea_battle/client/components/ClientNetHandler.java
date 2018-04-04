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
     * Return Response
     * @param response
     */
    public void returnResponse(int response) {
        sendMessage("returnResponse " + response);
    }
}
