package java1.lesson1.sea_battle.models;

import java1.lesson1.sea_battle.controllers.GameController;

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
     * Используемая стратегия выстрелов
     */
    protected IMakeShotStrategy makeShotStrategy;

    /**
     * Контроллер игры
     */
    protected final GameController gameController;
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
     *
     * @param name имя игрока
     */
    public Player(String name) {
        this.name = name;
        this.gameController = GameController.getInstance();
    }


    /**
     * Создает играка с заданым именем и стратегией выстрелов
     *
     * @param name имя игрока
     * @param makeShotStrategy применяемая стратегия выстрелов
     */
    public Player(String name, IMakeShotStrategy makeShotStrategy) {
        this(name);
        this.makeShotStrategy = makeShotStrategy;
    }


    /**
     * Стреляет по кораблю
     *
     * @return координату выстрела
     */
    @Override
    public Shot makeShot() {
        isSetShotCoordinate = false;
        gameController.makeShot(name);
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
     *
     * @return имя игрока
     */
    public String getName() {
        return name;
    }


    /**
     * Сохраняет полученную координату выстрела
     *
     * @param shotCoordinate полученная координата выстрела
     */
    public void setShotCoordinate(Coordinate shotCoordinate) {
        this.shotCoordinate = shotCoordinate;
    }


    /**
     * Устанавливает флаг получения координаты выстрела
     *
     * @param value true - координата выстрела получена
     */
    public void setIsSetShotCoordinate(boolean value) {
        isSetShotCoordinate = value;
    }
}
