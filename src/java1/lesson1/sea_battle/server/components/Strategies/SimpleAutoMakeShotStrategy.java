package java1.lesson1.sea_battle.server.components.Strategies;

import java1.lesson1.sea_battle.server.configs.Config;
import java1.lesson1.sea_battle.server.models.Coordinate;
import java1.lesson1.sea_battle.server.models.IMakeShotStrategy;

import java.util.ArrayList;

/**
 * Реализует простейшую стратегию стрельбы в автоматическом режиме.
 * Коодината выстрела задается случайным образом.
 */
public class SimpleAutoMakeShotStrategy implements IMakeShotStrategy {
    private ArrayList<Integer> values =  new ArrayList<>();

    public SimpleAutoMakeShotStrategy() {
        for (int i = 0; i < Config.MAX_COORDINATE; i++) {
            values.add(i);
        }
    }

    @Override
    public Coordinate makeShotCoordinate() {
        int index = (int) (Math.random() * values.size());
        Coordinate coordinate = new Coordinate(values.get(index));
        values.remove(index);

        return coordinate;
    }
}
