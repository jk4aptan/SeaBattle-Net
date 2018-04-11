package java1.lesson1.sea_battle.server.models;

import java1.lesson1.sea_battle.server.configs.Config;

/**
 * Поле игрока для размещения кораблей
 */
public class Field {
    /**
     * Ячейки поля. Размер поля задается в конфигурации приложения.
     */
    private char[][] fieldArea;

    public Field() {
        fieldArea = new char[Config.BATTLE_FIELD_COLUMNS_COUNT][Config.BATTLE_FIELD_ROWS_COUNT];
    }

    public char getCell(int column, int row) {
        return fieldArea[column][row];
    }

    public void setCell(int column, int row, char symbol) {
        fieldArea[column][row] = symbol;
    }
}
