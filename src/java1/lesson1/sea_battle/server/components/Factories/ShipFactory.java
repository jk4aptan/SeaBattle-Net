package java1.lesson1.sea_battle.server.components.Factories;

import java1.lesson1.sea_battle.server.components.Enums.ShipOrientation;
import java1.lesson1.sea_battle.server.components.Enums.ShipState;
import java1.lesson1.sea_battle.server.models.Coordinate;
import java1.lesson1.sea_battle.server.models.Ship;

/**
 * Фабрика создания кораблей
 */
class ShipFactory {
    private static ShipFactory instance;

    public static ShipFactory getInstance() {
        if (instance == null) {
            instance = new ShipFactory();
        }
        return instance;
    }


    private ShipFactory() {
    }

    /**
     * Создание корабля в автоматическом режиме
     * @param shipDecks колличество палуб корабля
     * @return Ship n-палубный корабль
     */
    synchronized Ship createShipAuto(int shipDecks) {
        final int VERTICAL_COORDINATE_STEP = 10;
        final int HORIZONTAL_COORDINATE_STEP = 1;
        final int MAX_COORDINATE = 100;

        ShipOrientation orientation = ((int) (Math.random() * 10)) > 5 ? ShipOrientation.VERTICAL : ShipOrientation.HORIZONTAL;
        int step = orientation.equals(ShipOrientation.VERTICAL) ? VERTICAL_COORDINATE_STEP : HORIZONTAL_COORDINATE_STEP;

        Coordinate[] coordinates = new Coordinate[shipDecks];
        do {
            coordinates[0] = new Coordinate((int) (Math.random() * MAX_COORDINATE));
            for (int deck = 1; deck < shipDecks; deck++) {
                coordinates[deck] = new Coordinate(coordinates[0].getValue() + step * deck);
            }
        } while (!isShipCoordinatesValid(coordinates, orientation));

        Ship ship = new Ship();
        ship.setDecks(shipDecks);
        ship.setOrientation(orientation);
        ship.setCoordinates(coordinates);
        ship.setState(ShipState.UNHARMED);

        ShipState[] state = new ShipState[coordinates.length];
        for (int i = 0; i < state.length; i++) {
            state[i] = ShipState.UNHARMED;
        }
        ship.setDecksState(state);
        return ship;
    }

    /**
     * Проверка валидности координат корабля
     * @param coordinates координаты корабля
     * @param orientation ориентация корабля
     * @return true - если координаты корабля находятся в пределах игрового поля, false - если выходят за границу игрового поля
     */
    private boolean isShipCoordinatesValid(Coordinate[] coordinates, ShipOrientation  orientation) {
        int row = -1;
        for (Coordinate deck : coordinates) {
            // координаты корабля выходят за границы игрового поля
            if (deck.getValue() < Coordinate.MIN_COORDINATE || deck.getValue() > Coordinate.MAX_COORDINATE) {
                return false;
            }

            // перенос координат корабля на другую строку
            if (orientation.equals(ShipOrientation.HORIZONTAL)) {
                if (row == -1) {
                    row = deck.getRow();
                    continue;
                }

                if (deck.getRow() != row) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * (заглушка)
     * Создание корабля в ручном режиме
     *
     * @param shipDecks колличество палуб корабля
     * @return Ship n-палубный корабль
     */
    synchronized Ship createShipHand(int shipDecks) {
        return new Ship();
    }
}
