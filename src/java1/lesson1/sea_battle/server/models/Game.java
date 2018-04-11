package java1.lesson1.sea_battle.server.models;

import java1.lesson1.sea_battle.server.components.Enums.ShipState;
import java1.lesson1.sea_battle.server.components.Factories.PlayerFactory;
import java1.lesson1.sea_battle.server.components.Factories.SquadronFactory;
import java1.lesson1.sea_battle.server.components.SeaBattleHandler;
import java1.lesson1.sea_battle.server.controllers.GameController;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс реализующий модель игры
 */
public class Game implements Runnable {
    public static final Object gameKey = new Object();
    /**
     * Флаг инициализации новой игры
     */
    private static boolean isNewGame = false;
    /**
     * Флаг старта новой игры
     */
    private static boolean isGameStart = false;

    /**
     * Эскадры игроков
     */
    private Map<Player, Squadron> squadrons;
    /**
     * Игрок
     */
    private Player player;
    /**
     * Противник
     */
    private Player adversary;
    /**
     * Контроллер игры
     */
    private GameController gameController;
    /**
     * Флаг завершения игры
     */
    private boolean exit;


    /**
     *
     * @param playerNetHandler игрока сделавшего предложение об игре
     * @param adversaryNetHandler игрока принявшего предложение об игре
     */
    public Game(SeaBattleHandler playerNetHandler, SeaBattleHandler adversaryNetHandler) {
        exit = false;
        player = PlayerFactory.getInstance().createPlayer(playerNetHandler.getPlayerName(), playerNetHandler.getId());
        adversary = PlayerFactory.getInstance().createPlayer(adversaryNetHandler.getPlayerName(), adversaryNetHandler.getId());
        gameController = new GameController(this, player, adversary, playerNetHandler, adversaryNetHandler);
    }


    /**
     * Инициализация игры
     */
    public void init() throws InterruptedException {
        Squadron playerSquadron = SquadronFactory.getInstance().createSquadron();
        Squadron adversarySquadron = SquadronFactory.getInstance().createSquadron();
        squadrons = new HashMap<>();
        squadrons.put(player, playerSquadron);
        squadrons.put(adversary, adversarySquadron);

        gameController.initPlayerBattleField(playerSquadron);
        gameController.initAdversaryBattleField(adversarySquadron);
    }



    @Override
    public void run() {
        Player currentPlayer = player;
        Player currentAdversary;
        boolean isSuccess = false;

        while (!exit) {
            // Установить текущего игрока
            if (!isSuccess) {
                currentPlayer = currentPlayer == player ? adversary : player;
            }
            currentAdversary = currentPlayer == player ? adversary : player;

            // Сделать выстрел
            Shot shot = currentPlayer.makeShot();

            // Обработать выстрел
            ShipState result = squadrons.get(currentAdversary).getResult(shot);
            switch (result) {
                case UNHARMED:
                    gameController.setCurrentResult(currentPlayer.getName(), shot, "UNHARMED");
                    isSuccess = false;
                    break;
                case WOUNDED:
                    gameController.setCurrentResult(currentPlayer.getName(), shot, "WOUNDED");
                    isSuccess = true;
                    break;
                case SUNK:
                    Ship sunkShip = squadrons.get(currentAdversary).getLastSunkShip();
                    ArrayList<Integer> busySells = SquadronFactory.getInstance().getBusySells(sunkShip.getCoordinates());

                    gameController.setLastSunkShip(currentPlayer.getName(), sunkShip, busySells);
                    isSuccess = true;
                    break;
            }

            // Проверить на окончание игры
//            if (squadrons.get(currentAdversary).isLosing()) {
//                isNewGame = false;
//                gameController.gameIsOver(currentPlayer);
//
//                // ждать решения, будет новая игра или нет
//                try {
//                    synchronized (gameKey) {
//                        while (!isNewGame) {
//                            gameKey.wait();
//                        }
//                    }
//
//                    // инициализировать игру
//                    init();
//                    gameController.initView();
//
//                    synchronized (gameKey) {
//                        while (!isGameStart) {
//                            gameKey.wait();
//                        }
//                    }
//
//                    // запустить новую игру
//                    start();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }

        }
    }




    /**
     * Устанавливает флаг новой игры
     *
     * @param value true - новая игра
     */
    public static void setIsNewGame(boolean value) {
        Game.isNewGame = value;
    }


    /**
     * Устанавливает флаг старта новой игры
     * @param value true - старт новой игры
     */
    public static void setIsGameStart(boolean value) {
        Game.isGameStart = value;
    }

    /**
     * Устанавливает флаг завершения игры
     * @param value true - завершение игры
     */
    public void setExit(boolean value) {
        this.exit = value;
    }
}
