package java1.lesson1.sea_battle.controllers;

import java1.lesson1.sea_battle.models.*;
import java1.lesson1.sea_battle.views.GameWindow;

import java.awt.*;
import java.util.ArrayList;

/**
 * Контроллер приложения
 */
public class GameController {
    private static GameController instance;
    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    private Game game;
    private GameWindow gameWindow;
    private Player player;


    private GameController() {
    }


    /**
     * Сохраняет объект GUI (графический интерфейс пользователя)
     *
     * @param gameWindow - объект графического интерфейса пользователя
     */
    public void setGameWindow(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }


    /**
     * Сохраняет объект игрока
     *
     * @param player игрок
     */
    public void setPlayer(Player player) {
        this.player = player;
    }


    /**
     * Запрашивает у GUI имя игрока
     *
     * @return имя игрока
     */
    public String getPlayerName() {
        return gameWindow.getPlayerName();
    }


    /**
     * Передает игроку полученное от GUI значение координаты выстрела
     *
     * @param shotCoordinate координата выстрела
     */
    public void setShotCoordinate(Coordinate shotCoordinate) {
        synchronized (Player.playerKey) {
            player.setShotCoordinate(shotCoordinate);
            player.setIsSetShotCoordinate(true);
            Player.playerKey.notify();
        }
    }


    /**
     * Передает GUI игровое поле игрока
     *
     * @return игровое поле игрока
     */
    public BattleField getPlayerBattleField() {
        return game.getPlayerBattleField();
    }


    /**
     * Сохраняет объект модели Game
     *
     * @param game объект модели Game
     */
    public void setGame(Game game) {
        this.game = game;
    }


    /**
     * Запрашивает у GUI координату выстрела
     *
     * @param name имя делающего выстрел игрока
     */
    public void makeShot(String name) {
        gameWindow.makeShot(name);
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
     * Передает GUI результат выстрела в случаях: МИМО, РАНИЛ
     *
     * @param currentPlayer - игрок сделавший выстрел
     * @param shot - выстрел
     * @param result - результат выстрела, выводится в ячейку поля
     * @param message - сообщение о результате выстрела
     */
    public void setCurrentResult(Player currentPlayer, Shot shot, Color result, String message) {
        gameWindow.setCurrentResult(currentPlayer, shot, result, message);
    }


    /**
     * Передает GUI результат выстрела в случае потопления корабля
     *
     * @param currentPlayer - игрок сделавший выстрел
     * @param lastSunkShip - потопленный корабль
     * @param message - сообщение о результате выстрела
     * @param busySells - ячейки поля вокруг потопленного корабля
     */
    public void setLastSunkShip(Player currentPlayer, Ship lastSunkShip, String message, ArrayList<Integer> busySells) {
        ArrayList<Coordinate> sells = new ArrayList<>();
        for (Integer busySell : busySells) {
            sells.add(new Coordinate(busySell));
        }
        gameWindow.setLastSunkShip(currentPlayer, lastSunkShip, message, sells);
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
}
