package java1.lesson1.sea_battle.server.models;

import java1.lesson1.sea_battle.server.controllers.GameController;

/**
 * Игрок
 */
public class Player implements IMakeShot {
    public static final Object playerKey = new Object();

    /**
     * Имя игрока
     */
    protected String name;
    /**
     * id игрока
     */
    private int id;
    /**
     * Используемая стратегия выстрелов
     */
    protected IMakeShotStrategy makeShotStrategy;
    /**
     * Контроллер игры
     */
    protected GameController gameController;
    /**
     * Координата выстрела
     */
    private Coordinate shotCoordinate;
    /**
     * Флаг получения координаты выстрела
     */
    private boolean isSetShotCoordinate;


    /**
     * Создает игрока с заданым именем
     * @param name имя игрока
     * @param id игрока
     */
    public Player(String name, int id) {
        this.name = name;
        this.id = id;
    }

    /**
     * Создает играка с заданым именем и стратегией выстрелов
     * @param name имя игрока
     * @param makeShotStrategy применяемая стратегия выстрелов
     */
    public Player(String name, IMakeShotStrategy makeShotStrategy) {
        this.name = name;
        this.makeShotStrategy = makeShotStrategy;
    }

    /**
     * Стреляет по кораблю
     * @return координату выстрела
     */
    @Override
    public Shot makeShot() {
        isSetShotCoordinate = false;
        gameController.makeShot(id);
        synchronized (playerKey) {
            while (!isSetShotCoordinate) {
                try {
                    playerKey.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return new Shot(shotCoordinate);
    }

    /**
     * Возвращает имя игрока
     * @return имя игрока
     */
    public String getName() {
        return name;
    }

    /**
     * Сохраняет полученную координату выстрела
     * @param shotCoordinate полученная координата выстрела
     */
    public void setShotCoordinate(Coordinate shotCoordinate) {
        this.shotCoordinate = shotCoordinate;
    }

    /**
     * Устанавливает флаг получения координаты выстрела
     * @param value true - координата выстрела получена
     */
    public void setIsSetShotCoordinate(boolean value) {
        isSetShotCoordinate = value;
    }

    /**
     * Setter controller
     * @param controller контроллер игры
     */
    public void setController(GameController controller) {
        gameController = controller;
    }

    public int getId() {
        return id;
    }
}
