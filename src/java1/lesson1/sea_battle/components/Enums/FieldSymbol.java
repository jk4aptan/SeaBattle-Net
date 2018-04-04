package java1.lesson1.sea_battle.components.Enums;

/**
 * Значения ячеек поля
 * SEA - поле незанятое кораблем
 * SHIP - поле занятое кораблем
 * START_COLUMN, START_COLUMN_LOW, START_ROW - значения использующиеся в графическом интерфейсе пользователя
 */
public enum FieldSymbol {
    SEA(' '), START_COLUMN('A'), START_COLUMN_LOW('a'), START_ROW('0'), SHIP('|');

    private char value;

    FieldSymbol(char c) {
        value = c;
    }

    public char getValue() {
        return value;
    }
}
