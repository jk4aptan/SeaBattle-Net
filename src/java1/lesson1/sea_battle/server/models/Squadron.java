package java1.lesson1.sea_battle.server.models;

import java1.lesson1.sea_battle.server.components.Enums.ShipState;
import java.util.ArrayList;

/**
 * Эскадра кораблей
 */
public class Squadron {
    public static ShipState shotResult = ShipState.UNHARMED;

    /**
     * Корабли эскадры
     */
    private ArrayList<Ship> ships;
    /**
     * Последний, на текущий момент, потопленный корабль в эскадре
     */
    private Ship lastSunkShip;



    public Squadron() {
        ships = new ArrayList<>();
    }


    /**
     * Присоединяет корабль к эскадре
     *
     * @param ship присоединяемый корабль
     */
    public void addShip(Ship ship) {
        ships.add(ship);
    }


    public ArrayList<Ship> getShips() {
        return ships;
    }


    /**
     * Получить результат по выстрелу
     *
     * @param shot выстрел противника
     * @return результат выстрела противника по кораблям эскадры
     */
    ShipState getResult(Shot shot) {
        shotResult = ShipState.UNHARMED;
        for (Ship ship : ships) {
            shotResult = ship.getResult(shot);
            if (shotResult == ShipState.WOUNDED) {
                break;
            }
            if (shotResult == ShipState.SUNK) {
                lastSunkShip = ship;
                break;
            }
        }
        return shotResult;
    }


    /**
     * Проверяет, все ли корабли потопили в эскадре
     *
     * @return true - все корабли эскадры потоплены, false - в эскадре есть непотопленные корабли
     */
    boolean isLosing() {
        int sunkenShipsCount = 0;
        for (Ship ship : ships) {
            if (ship.getState().equals(ShipState.SUNK)) {
                sunkenShipsCount++;
            }
        }

        return sunkenShipsCount == ships.size();
    }


    /**
     * @return пооследний, на текущий момент, потопленный корабль в эскадре
     */
    Ship getLastSunkShip() {
        return lastSunkShip;
    }
}
