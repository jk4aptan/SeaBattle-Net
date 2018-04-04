package java1.lesson1.sea_battle.models;

/**
 * Класс Coordinate реализует координату ячейки игрового поля.
 * Координата представлена целым числом в интервале от 0 до 99.
 * Единицы координат представляют столбцы игрового поля, а десятки представляют строки.
 * Например: 39
 * 3 - номер строки
 * 9 - номер столбца
 */
public class Coordinate {
    public static final int MIN_COORDINATE = 0;
    public static final int MAX_COORDINATE = 99;

    private final int DELIMITER = 10;
    /**
     * Значение координаты
     */
    private int value;

    public Coordinate(int value) {
        this.value = value;
    }

    public int getColumn() {
        return value % DELIMITER;
    }

    public int getRow() {
        return value / DELIMITER;
    }

    public int getValue() {
        return value;
    }
}
