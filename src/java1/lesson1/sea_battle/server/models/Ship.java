package java1.lesson1.sea_battle.server.models;

import java1.lesson1.sea_battle.server.components.Enums.ShipState;

/**
 * N-палубный корабль
 */
public class Ship {
    /**
     * Координаты корабля
     */
    private Coordinate[] coordinates;
    /**
     * Колличество палуб корабля
     */
    private int decks;
    /**
     * Состояние палуб корабля
     */
    private ShipState[] decksState;
    /**
     * Состояние корабля
     */
    private ShipState state;

    public Ship() {
    }

    /**
     * Определяет состояние корабля после выстрела
     * @param shot - выстрел противника
     * @return состояние корабля после выстрела противника
     */
    ShipState getResult(Shot shot) {
        for (int deck = 0; deck < decks; deck++) {
            if (coordinates[deck].getValue() == shot.getCoordinate().getValue()) {
                decksState[deck] = ShipState.WOUNDED;
                int damagedDeckCount = 0;
                for (ShipState deckState : decksState) {
                    if (deckState.equals(ShipState.WOUNDED)) {
                        damagedDeckCount++;
                    }
                }
                if (damagedDeckCount == decks) {
                    state = ShipState.SUNK;
                    return ShipState.SUNK;
                } else {
                    state = ShipState.WOUNDED;
                    return ShipState.WOUNDED;
                }
            }
        }
        return ShipState.UNHARMED;
    }

    public void setCoordinates(Coordinate[] coordinates) {
        this.coordinates = coordinates;
    }

    public Coordinate[] getCoordinates() {
        return coordinates;
    }

    public void setDecks(int decks) {
        this.decks = decks;
    }

    public void setDecksState(ShipState[] state) {
        decksState = state;
    }

    public void setState(ShipState state) {
        this.state = state;
    }

    ShipState getState() {
        return state;
    }
}
