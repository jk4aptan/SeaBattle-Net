package java1.lesson1.sea_battle.models;

import java1.lesson1.sea_battle.components.Enums.FieldSymbol;
import java1.lesson1.sea_battle.configs.Config;

/**
 * Игровое поле приложения
 */
public class BattleField {
    /**
     * Поле игрока с размещенной на нем эскадрой игрока
     */
    private Field playerSeaArea;

    /**
     * Поле противника, по которому игрок производит выстрелы
     */
    private Field adversarySeaArea;


    public BattleField() {
        playerSeaArea = new Field();
        adversarySeaArea = new Field();

        for (int row = 0; row < Config.BATTLE_FIELD_ROWS_COUNT; row++) {
            for (int column = 0; column < Config.BATTLE_FIELD_COLUMNS_COUNT; column++) {
                playerSeaArea.setCell(row, column, FieldSymbol.SEA.getValue());
                adversarySeaArea.setCell(row, column, FieldSymbol.SEA.getValue());
            }
        }
    }


    public Field getPlayerSeaArea() {
        return playerSeaArea;
    }


    /**
     * Размещает эскадру на поле игрока
     *
     * @param squadron эскадра игрока
     */
    public void initWithSquadron(Squadron squadron) {
        for (Ship ship : squadron.getShips()) {
            for (Coordinate deck : ship.getCoordinates()) {
                playerSeaArea.setCell(deck.getRow(), deck.getColumn(), FieldSymbol.SHIP.getValue());
            }
        }
    }
}
