package java1.lesson1.sea_battle.components.Factories;

import java1.lesson1.sea_battle.components.Strategies.ImproveAutoMakeShotStrategy;
import java1.lesson1.sea_battle.components.Strategies.SimpleAutoMakeShotStrategy;
import java1.lesson1.sea_battle.configs.Config;
import java1.lesson1.sea_battle.controllers.GameController;
import java1.lesson1.sea_battle.models.Computer;
import java1.lesson1.sea_battle.models.Player;

/**
 * Фабрика создания игроков
 */
public class PlayerFactory {
    private static PlayerFactory instance;

    public static PlayerFactory getInstance() {
        if (instance == null) {
            instance = new PlayerFactory();
        }
        return instance;
    }


    private PlayerFactory() {
    }


    /**
     * Создает игрока с заданым именем
     *
     * @return игрок с заданым именем
     */
    public Player createPlayer() {
        return new Player(GameController.getInstance().getPlayerName());
    }

    /**
     * Создает автоматического игрока по умолчанию с заданой стратегией выстрелов.
     * Стратегия выстрелов задается в конфигурации приложения.
     *
     * @return автоматический игрок с заданой стратегией выстрелов
     */
    public Player createAdversary() {
        Player player = null;

        switch (Config.MAKE_SHOT_STRATEGY) {
            case SIMPLE:
                player = new Computer(Config.DEFAULT_PLAYER_NAME, new SimpleAutoMakeShotStrategy());
                break;
            case IMPROVE:
                player = new Computer(Config.DEFAULT_PLAYER_NAME, new ImproveAutoMakeShotStrategy());
                break;
        }

        return player;
    }
}
