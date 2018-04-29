package java1.lesson1.sea_battle.server.controllers;

import java1.lesson1.sea_battle.server.components.Enums.ShipOrientation;
import java1.lesson1.sea_battle.server.components.SeaBattleHandler;
import java1.lesson1.sea_battle.server.models.*;

import java.util.ArrayList;

/**
 * Контроллер приложения
 */
public class GameController {
    private Game game;
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
     * Запрашивает у GUI координату выстрела
     * @param idPlayer id делающего выстрел игрока
     */
    public void makeShot(int idPlayer) {
        if (idPlayer == player.getId()) {
            currentPlayer = player;
            playerNetHandler.sendMessage("turnOn", player.getName());
            adversaryNetHandler.sendMessage("turnOff", player.getName());
        } else {
            currentPlayer = adversary;
            adversaryNetHandler.sendMessage("turnOn", adversary.getName());
            playerNetHandler.sendMessage("turnOff", adversary.getName());
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
        message.append(currentPlayerName).append(";").append(sunkShipCoordinates.toString()).append(";").append(busySellsCoordinates.toString());

        playerNetHandler.sendMessage("setLastSunkShip", message.toString());
        adversaryNetHandler.sendMessage("setLastSunkShip", message.toString());
    }

    /**
     * Передает GUI результат выстрела в случаях: МИМО, РАНИЛ
     * @param currentPlayerName игрок сделавший ход
     * @param shot xoд игрока
     * @param result результат выстрела
     */
    public void setCurrentResult(String currentPlayerName, Shot shot, String result) {
        StringBuilder message = new StringBuilder();
        message.append(currentPlayerName).append(";").append(shot.getCoordinate().getValue()).append(";").append(result);

        playerNetHandler.sendMessage("setCurrentResult", message.toString());
        adversaryNetHandler.sendMessage("setCurrentResult", message.toString());
    }

    /**
     * Завершение игры
     * @param winnerName - имя победившего игрока
     */
    public void gameIsOver(String winnerName) {
        playerNetHandler.gameIsOver(winnerName);
        adversaryNetHandler.gameIsOver(winnerName);
    }

    /**
     * Размещает корабли игрока на игровом поле
     * @param playerSquadron эскадра игрока
     */
    public void initPlayerBattleField(Squadron playerSquadron) {
        playerNetHandler.sendMessage("initShipsCoordinates", shipsCoordinates(playerSquadron));
    }

    /**
     * Размещает корабли противника на игровом поле
     * @param adversarySquadron эскадра противника
     */
    public void initAdversaryBattleField(Squadron adversarySquadron) {
        adversaryNetHandler.sendMessage("initShipsCoordinates", shipsCoordinates(adversarySquadron));
    }

    /**
     * Создает строку координат кораблей эскадры
     * @param squadron эскадра
     * @return строка координат кораблей эскадры
     */
    private String shipsCoordinates(Squadron squadron) {
        final int VERTICAL = 1;
        final int HORIZONTAL = 0;

        StringBuilder stringBuilder = new StringBuilder();
        for (Ship ship : squadron.getShips()) {
            int orientation = ship.getOrientation().equals(ShipOrientation.VERTICAL) ? VERTICAL : HORIZONTAL;
            stringBuilder.append(ship.getDecks()).append(",").append(orientation).append(",");
            for (Coordinate coordinate : ship.getCoordinates()) {
                stringBuilder.append(coordinate.getValue()).append(",");
            }
            stringBuilder.append(";");
        }
        return stringBuilder.toString();
    }
}
//4,011,12,13,14,;3,157,67,77,;3,051,52,53,;2,083,84,;2,026,27,;2,097,98,;1,132,;1,181,;1,06,;1,079,;