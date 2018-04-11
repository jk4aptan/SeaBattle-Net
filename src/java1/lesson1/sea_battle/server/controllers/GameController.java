package java1.lesson1.sea_battle.server.controllers;

import java1.lesson1.sea_battle.server.components.SeaBattleHandler;
import java1.lesson1.sea_battle.server.models.*;
import java1.lesson1.sea_battle.views.GameWindow;

import java.awt.*;
import java.util.ArrayList;

/**
 * Контроллер приложения
 */
public class GameController {
    private Game game;
    private GameWindow gameWindow;
    private Player player;
    private Player adversary;
    private Player currentPlayer;
    private SeaBattleHandler playerNetHandler;
    private SeaBattleHandler adversaryNetHandler;


    /**
     * Конструктор
     * @param game game
     * @param player игрок сделавший предложение об игре
     * @param adversary игрок принявший предложение об игре
     * @param playerNetHandler игрока сделавшего предложение об игре
     * @param adversaryNetHandler игрока принявшего предложение об игре
     */
    public GameController(Game game, Player player, Player adversary, SeaBattleHandler playerNetHandler, SeaBattleHandler adversaryNetHandler) {
        this.game = game;
        this.player = player;
        this.adversary = adversary;
        this.playerNetHandler = playerNetHandler;
        this.adversaryNetHandler = adversaryNetHandler;

        player.setController(this);
        adversary.setController(this);
        playerNetHandler.setController(this);
        adversaryNetHandler.setController(this);
    }


    /**
     * Сохраняет объект модели Game
     * @param game объект модели Game
     */
    public void setGame(Game game) {
        this.game = game;
    }



    /**
     * Запрашивает у GUI координату выстрела
     * @param idPlayer id делающего выстрел игрока
     */
    public void makeShot(int idPlayer) {
        if (idPlayer == player.getId()) {
            currentPlayer = player;
            playerNetHandler.sendMessage("turnOn " + player.getName());
            adversaryNetHandler.sendMessage("turnOff " + player.getName());
        } else {
            currentPlayer = adversary;
            adversaryNetHandler.sendMessage("turnOn " + adversary.getName());
            playerNetHandler.sendMessage("turnOff " + adversary.getName());
        }
    }


    /**
     * Передает игроку полученное от GUI значение координаты выстрела
     * @param shotCoordinate координата выстрела
     */
    public void setShotCoordinate(String shotCoordinate) {
        synchronized (Player.playerKey) {
            Coordinate coordinate = new Coordinate(Integer.parseInt(shotCoordinate));
            currentPlayer.setShotCoordinate(coordinate);
            currentPlayer.setIsSetShotCoordinate(true);
            Player.playerKey.notifyAll();
        }
    }


    /**
     * Передает GUI имя автоматического (computer) игрока
     *
     * @param name имя автоматического (computer) игрока
     */
    public void setCurrentTurn(String name) {
        gameWindow.setCurrentTurn(name);
    }


    /**
     * Передает GUI результат выстрела в случаях: ПОТОПИЛ
     * @param currentPlayerName игрок сделавший ход
     * @param sunkShip потопленный корабль
     * @param busySells ячейки вокруг потопленного корабля
     */
    public void setLastSunkShip(String currentPlayerName, Ship sunkShip, ArrayList<Integer> busySells) {
        StringBuilder sunkShipCoordinates = new StringBuilder();
        for (Coordinate coordinate : sunkShip.getCoordinates()) {
            sunkShipCoordinates.append(coordinate.getValue()).append(",");
        }
        sunkShipCoordinates.deleteCharAt(sunkShipCoordinates.length() - 1);

        StringBuilder busySellsCoordinates = new StringBuilder();
        for (Integer busySell : busySells) {
            busySellsCoordinates.append(busySell).append(",");
        }
        busySellsCoordinates.deleteCharAt(busySellsCoordinates.length() - 1);

        StringBuilder message = new StringBuilder();
        message.append("setLastSunkShip ").append(currentPlayerName).append(";").append(sunkShipCoordinates.toString()).append(";").append(busySellsCoordinates.toString());

        playerNetHandler.sendMessage(message.toString());
        adversaryNetHandler.sendMessage(message.toString());
    }


    /**
     * Передает GUI результат выстрела в случаях: МИМО, РАНИЛ
     * @param currentPlayerName игрок сделавший ход
     * @param shot xoд игрока
     * @param result результат выстрела
     */
    public void setCurrentResult(String currentPlayerName, Shot shot, String result) {
        StringBuilder message = new StringBuilder();
        message.append("setCurrentResult ").append(currentPlayerName).append(";").append(shot.getCoordinate().getValue()).append(";").append(result);

        playerNetHandler.sendMessage(message.toString());
        adversaryNetHandler.sendMessage(message.toString());
    }


    /**
     * Передает GUI победившего игрока
     *
     * @param currentPlayer - победивший игрок
     */
    public void gameIsOver(Player currentPlayer) {
        gameWindow.gameIsOver(currentPlayer);
    }


    /**
     * Инициализирует модель игры
     */
    public void initGame() {
        synchronized (Game.gameKey) {
            Game.setIsNewGame(true);
            Game.gameKey.notify();
        }
    }

    /**
     * Инициализирует GUI
     */
    public void initView() {
        synchronized (GameWindow.gwKey) {
            GameWindow.setIsInitBattleField(true);
            GameWindow.gwKey.notify();
        }
    }

    /**
     * Стартует новую игру для пользователя
     */
    public void startNewGame() {
        synchronized (Game.gameKey) {
            Game.setIsGameStart(true);
            Game.gameKey.notify();
        }
    }

    /**
     * Размещает корабли игрока на игровом поле
     * @param playerSquadron эскадра игрока
     */
    public void initPlayerBattleField(Squadron playerSquadron) {
        String message = "initShipsCoordinates " + shipsCoordinates(playerSquadron);
        playerNetHandler.sendMessage(message);
    }

    /**
     * Размещает корабли противника на игровом поле
     * @param adversarySquadron эскадра противника
     */
    public void initAdversaryBattleField(Squadron adversarySquadron) {
        String message = "initShipsCoordinates " + shipsCoordinates(adversarySquadron);
        adversaryNetHandler.sendMessage(message);
    }

    /**
     * Создает строку координат кораблей эскадры
     * @param squadron эскадра
     * @return строка координат кораблей эскадры
     */
    private String shipsCoordinates(Squadron squadron) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Ship ship : squadron.getShips()) {
            for (Coordinate coordinate : ship.getCoordinates()) {
                stringBuilder.append(coordinate.getValue()).append(";");
            }
        }
        return stringBuilder.toString();
    }

    public void exit(int id) {
        //todo - по !id вычислить оставшегося игрока
        // послать ему уведомление о том, что соперник покинул игру
        // предоложить ему выбрать другого соперника
        game.setExit(true);
    }
}
