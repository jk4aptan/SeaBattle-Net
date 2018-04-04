package java1.lesson1.sea_battle.models;

import java1.lesson1.sea_battle.components.Enums.ShipState;
import java1.lesson1.sea_battle.components.Factories.PlayerFactory;
import java1.lesson1.sea_battle.components.Factories.SquadronFactory;
import java1.lesson1.sea_battle.controllers.GameController;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс реализующий модель игры
 */
public class Game {
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
     * Игровые поля игроков
     */
    private Map<Player, BattleField> battleFields;
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
    private final GameController gameController;


    /**
     * Конструктор игры
     */
    public Game() {
        gameController = GameController.getInstance();
        gameController.setGame(this);

        player = PlayerFactory.getInstance().createPlayer();
        gameController.setPlayer(player);
    }


    /**
     * Инициализация игры
     */
    public void init() throws InterruptedException {
        adversary = PlayerFactory.getInstance().createAdversary();

        squadrons = new HashMap<>();
        Squadron squadron1 = SquadronFactory.getInstance().createSquadron();
        Squadron squadron2 = SquadronFactory.getInstance().createSquadron();
        squadrons.put(player, squadron1);
        squadrons.put(adversary, squadron2);

        battleFields = new HashMap<>();
        BattleField battleField1 = new BattleField();
        battleField1.initWithSquadron(squadron1);
        BattleField battleField2 = new BattleField();
        battleField2.initWithSquadron(squadron2);
        battleFields.put(player, battleField1);
        battleFields.put(adversary, battleField2);
    }


    /**
     * Игровой цикл
     */
    public void start() {
        Player currentPlayer = player;
        Player currentAdversary;
        boolean isSuccess = false;

        while (true) {
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
                    gameController.setCurrentResult(currentPlayer, shot, Color.WHITE, "МИМО");
                    isSuccess = false;
                    break;
                case WOUNDED:
                    gameController.setCurrentResult(currentPlayer, shot, Color.ORANGE, "РАНИЛ");
                    isSuccess = true;
                    break;
                case SUNK:
                    Ship sunkShip = squadrons.get(currentAdversary).getLastSunkShip();
                    ArrayList<Integer> busySells = SquadronFactory.getInstance().getBusySells(sunkShip.getCoordinates());
                    gameController.setLastSunkShip(currentPlayer, sunkShip, "ПОТОПИЛ", busySells);
                    isSuccess = true;
                    break;
            }

            // Проверить на окончание игры
            if (squadrons.get(currentAdversary).isLosing()) {
                isNewGame = false;
                gameController.gameIsOver(currentPlayer);

                // ждать решения, будет новая игра или нет
                try {
                    synchronized (gameKey) {
                        while (!isNewGame) {
                            gameKey.wait();
                        }
                    }

                    // инициализировать игру
                    init();
                    gameController.initView();

                    synchronized (gameKey) {
                        while (!isGameStart) {
                            gameKey.wait();
                        }
                    }

                    // запустить новую игру
                    start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    /**
     * Передает игровое поле игрока
     *
     * @return игровое поле игрока
     */
    public BattleField getPlayerBattleField() {
        return battleFields.get(player);
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
     *
     * @param value true - старт новой игры
     */
    public static void setIsGameStart(boolean value) {
        Game.isGameStart = value;
    }
}
