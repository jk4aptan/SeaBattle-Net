package java1.lesson1.sea_battle.client.controllers;

import java1.lesson1.sea_battle.client.components.ClientNetHandler;
import java1.lesson1.sea_battle.client.views.GameWindow;

public class ClientController {

    private GameWindow gameWindow;
    private ClientNetHandler netHandler;

    public ClientController(GameWindow gameWindow, ClientNetHandler netHandler) {

        this.gameWindow = gameWindow;
        this.netHandler = netHandler;
    }


    /**
     * Close outputstream and socket on exit
     */
    public void exit() {
        netHandler.setExit(true);
    }


    /**
     * Get the adversaries
     */
    public void getAdversaries() {
        netHandler.getAdversaries();
    }

    /**
     * Send Player's name to the server
     * @param playerName player's name
     */
    public void sendPlayerName(String playerName) {
        netHandler.sendPlayerName(playerName);
    }

    /**
     * Setter Adversaries
     * @param adversaries all adversaries
     */
    public void setAdversaries(String adversaries) {
        synchronized (gameWindow) {
            gameWindow.setAdversaries(adversaries);
            gameWindow.setIsSet(true);
            gameWindow.notifyAll();
        }
    }

    /**
     * Request Adversary
     * @param id adversary
     */
    public void requestAdversary(String id) {
        netHandler.requestAdversary(id);
    }

    /**
     * Lets Play
     * @param playerName игрок сделавший предложение
     */
    public void letsPlay(String playerName) {
        gameWindow.letsPlay(playerName);
    }

    /**
     * Return Response
     * @param response
     */
    public void returnResponse(int response) {
        netHandler.returnResponse(response);
    }

    /**
     * Ответ игрока на предложение сыграть
     * @param response Ответ игрока
     */
    public void adversaryResponse(String response) {
        synchronized (gameWindow) {
            gameWindow.setResponse(response);
            gameWindow.setIsSet(true);
            gameWindow.notifyAll();
        }
    }
}
