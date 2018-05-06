package java1.lesson1.sea_battle.server.models;

/**
 * Выстрел
 */
public class Shot {
    /**
     * Координата выстрела
     */
    private Coordinate coordinate;

    public Shot(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
}
