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
     * @param response ответ
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

    /**
     * Размещает корабли на игровом поле
     * @param coordinates координаты кораблей
     */
    public void initShipCoordinates(String coordinates) {
        gameWindow.initShipCoordinates(coordinates);
        gameWindow.setInfoPanel();
    }

    /**
     * Сообщает игроку о том, что ему необходимо сделать ход
     * @param playerName текущий игрок
     */
    public void setTurnOn(String playerName) {
        gameWindow.setTurnOn(playerName);
    }

    /**
     * Сообщает противнику о том, что сейчас не его ход
     * @param playerName текущий игрок
     */
    public void setTurnOff(String playerName) {
        gameWindow.setTurnOff(playerName);
    }

    /**
     * Setter shot's coordinate
     * @param shotCoordinate shot's coordinate
     */
    public void setShotCoordinate(int shotCoordinate) {
        netHandler.setShotCoordinate(shotCoordinate);
    }

    /**
     * Выводит результат хода текущего игрока
     * @param data имя текущего игрока и xoд игрока
     */
    public void setCurrentResult(String data) {
        gameWindow.setCurrentResult(data);
    }

    /**
     * Выводит данные по потопленному кораблю
     * @param data данные по потопленному кораблю
     */
    public void setLastSunkShip(String data) {
        gameWindow.setLastSunkShip(data);
    }

    /**
     * Завершение игры
     * @param winnerName имя победившего игрока
     */
    public void gameIsOver(String winnerName) {
        gameWindow.gameIsOver(winnerName);
    }

    /**
     * Освобождает флаг занятости
     */
    public void setIsNotBusy() {
        netHandler.setIsNotBusy();
    }
}
