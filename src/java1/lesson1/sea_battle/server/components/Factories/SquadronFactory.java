package java1.lesson1.sea_battle.server.components.Factories;

import java1.lesson1.sea_battle.server.configs.Config;
import java1.lesson1.sea_battle.server.models.Coordinate;
import java1.lesson1.sea_battle.server.models.Ship;
import java1.lesson1.sea_battle.server.models.Squadron;

import java.util.ArrayList;

/**
 * Фабрика создания эскадры кораблей
 */
public class SquadronFactory {
    private static SquadronFactory instance;

    public static SquadronFactory getInstance() {
        if (instance == null) {
            instance = new SquadronFactory();
        }
        return instance;
    }

    /**
     * Занятые кораблями ячейки поля
     */
    private ArrayList<Integer> busySells;



    private SquadronFactory() {
        busySells = new ArrayList<>();
    }

    /**
     * Создание эскадры кораблей
     * @return эскадра кораблей
     */
    public synchronized Squadron createSquadron() {
        Squadron squadron = new Squadron();

        // для всех типов кораблей входящих в состав эскадры
        for (int shipType : Config.SQUADRON_CONFIG) {
            //создать корабль соответсвующего типа и проверить валидность его координат
            Ship ship = null;
            do {
                switch (Config.CREATING_SHIP_MODE) {
                    case AUTO:
                        ship = ShipFactory.getInstance().createShipAuto(shipType);
                        break;
                    case HAND:
                        ship = ShipFactory.getInstance().createShipHand(shipType);
                        break;
                }
            } while (!isShipValid(ship));

            // добавить корабль в эскадру
            squadron.addShip(ship);

            // запомнить занятые кораблем ячейки поля
            addBusySells(ship.getCoordinates());
        }
        busySells.clear();
        return squadron;
    }

    /**
     * Проверка валидности размещения корабля в эскадре
     *
     * @param ship проверяемый корабль
     * @return true - если координаты корабля не попадают на занятые ячейки поля, false - в противном случае
     */
    private boolean isShipValid(Ship ship) {
        for (Coordinate deck : ship.getCoordinates()) {
            if (busySells.contains(deck.getValue())) {
                return false;
            }
        }
        return true;
    }


    /**
     * Сохранить ячейки занятые кораблем и ячейки вокруг корабля.
     * Ячейки занятые кораблем и вокруг него немогут быть заняты другим кораблем.
     *
     * @param shipCoordinates координаты корабля
     */
    private void addBusySells(Coordinate[] shipCoordinates) {
        busySells.addAll(getBusySells(shipCoordinates));
    }


    /**
     * Вычисляет ячейки занятые кораблем и ячейки вокруг корабля.
     *
     * @param shipCoordinates координаты корабля
     * @return ячейки занятые кораблем и ячейки вокруг корабля
     */
    public synchronized ArrayList<Integer> getBusySells(Coordinate[] shipCoordinates) {
        final int COLUMN = 1;
        final int ROW = 10;

        ArrayList<Integer> sells = new ArrayList<>();

        for (Coordinate deck : shipCoordinates) {
            sells.add(deck.getValue());

            if (deck.getColumn() == 0) {
                switch (deck.getRow()) {
                    case 0:
                        sells.add(deck.getValue() + COLUMN);
                        sells.add(deck.getValue() + ROW);
                        sells.add(deck.getValue() + ROW + COLUMN);
                        break;
                    case 9:
                        sells.add(deck.getValue() + COLUMN);
                        sells.add(deck.getValue() - ROW);
                        sells.add(deck.getValue() - ROW + COLUMN);
                        break;
                    default:
                        sells.add(deck.getValue() + COLUMN);
                        sells.add(deck.getValue() - ROW);
                        sells.add(deck.getValue() - ROW + COLUMN);
                        sells.add(deck.getValue() + ROW);
                        sells.add(deck.getValue() + ROW + COLUMN);
                }
            }

            if (deck.getColumn() == 9) {
                switch (deck.getRow()) {
                    case 0:
                        sells.add(deck.getValue() - COLUMN);
                        sells.add(deck.getValue() + ROW);
                        sells.add(deck.getValue() + ROW - COLUMN);
                        break;
                    case 9:
                        sells.add(deck.getValue() - COLUMN);
                        sells.add(deck.getValue() - ROW);
                        sells.add(deck.getValue() - ROW - COLUMN);
                        break;
                    default:
                        sells.add(deck.getValue() - COLUMN);
                        sells.add(deck.getValue() - ROW);
                        sells.add(deck.getValue() - ROW - COLUMN);
                        sells.add(deck.getValue() + ROW);
                        sells.add(deck.getValue() + ROW - COLUMN);
                }
            }

            if (deck.getRow() == 0 && deck.getValue() != 0 && deck.getValue() != 9) {
                sells.add(deck.getValue() - COLUMN);
                sells.add(deck.getValue() + COLUMN);
                sells.add(deck.getValue() + ROW);
                sells.add(deck.getValue() + ROW - COLUMN);
                sells.add(deck.getValue() + ROW + COLUMN);
            }

            if (deck.getRow() == 9 && deck.getValue() != 90 && deck.getValue() != 99) {
                sells.add(deck.getValue() - COLUMN);
                sells.add(deck.getValue() + COLUMN);
                sells.add(deck.getValue() - ROW);
                sells.add(deck.getValue() - ROW - COLUMN);
                sells.add(deck.getValue() - ROW + COLUMN);
            }

            if (deck.getColumn() != 0 && deck.getColumn() != 9 && deck.getRow() != 0 && deck.getRow() != 9) {
                sells.add(deck.getValue() - COLUMN);
                sells.add(deck.getValue() + COLUMN);
                sells.add(deck.getValue() - ROW);
                sells.add(deck.getValue() - ROW - COLUMN);
                sells.add(deck.getValue() - ROW + COLUMN);
                sells.add(deck.getValue() + ROW);
                sells.add(deck.getValue() + ROW - COLUMN);
                sells.add(deck.getValue() + ROW + COLUMN);
            }
        }
        return sells;
    }
}
