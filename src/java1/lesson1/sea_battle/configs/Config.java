package java1.lesson1.sea_battle.configs;

import java1.lesson1.sea_battle.components.Enums.CreatingShipMode;
import java1.lesson1.sea_battle.components.Enums.MakeShotStrategy;

import java.util.ArrayList;

/**
 * Конфигурация приложения
 */
public class Config {
    public static final int ONE_DECK_SHIP = 1;
    public static final int TWO_DECK_SHIP = 2;
    public static final int THREE_DECK_SHIP = 3;
    public static final int FOUR_DECK_SHIP = 4;
    public static final int BATTLE_FIELD_COLUMNS_COUNT;
    public static final int BATTLE_FIELD_ROWS_COUNT;
    public static final int MIN_COORDINATE = 0;
    public static final int MAX_COORDINATE;
    public static final String DEFAULT_PLAYER_NAME;
    public static final ArrayList<Integer> SQUADRON_CONFIG;
    public static final MakeShotStrategy MAKE_SHOT_STRATEGY;
    public static final CreatingShipMode CREATING_SHIP_MODE;

    static {
        BATTLE_FIELD_COLUMNS_COUNT = 10;
        BATTLE_FIELD_ROWS_COUNT = 10;
        MAX_COORDINATE = BATTLE_FIELD_COLUMNS_COUNT * BATTLE_FIELD_ROWS_COUNT;

        DEFAULT_PLAYER_NAME = "Computer";

        // CREATING_SHIP_MODE's values - AUTO, HAND
        CREATING_SHIP_MODE = CreatingShipMode.AUTO;

        // MakeShotStrategy's values - SIMPLE, IMPROVE
        MAKE_SHOT_STRATEGY = MakeShotStrategy.IMPROVE;

        SQUADRON_CONFIG = new ArrayList<>();
        SQUADRON_CONFIG.add(FOUR_DECK_SHIP);
        SQUADRON_CONFIG.add(THREE_DECK_SHIP);
        SQUADRON_CONFIG.add(THREE_DECK_SHIP);
        SQUADRON_CONFIG.add(TWO_DECK_SHIP);
        SQUADRON_CONFIG.add(TWO_DECK_SHIP);
        SQUADRON_CONFIG.add(TWO_DECK_SHIP);
        SQUADRON_CONFIG.add(ONE_DECK_SHIP);
        SQUADRON_CONFIG.add(ONE_DECK_SHIP);
        SQUADRON_CONFIG.add(ONE_DECK_SHIP);
        SQUADRON_CONFIG.add(ONE_DECK_SHIP);
    }
}
