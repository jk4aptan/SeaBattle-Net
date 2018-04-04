package java1.lesson1.sea_battle.models;

import java1.lesson1.sea_battle.views.ViewConsole;

/**
 * Автоматический игрок (computer)
 */
public class Computer extends Player{

    public Computer(String name, IMakeShotStrategy makeShotStrategy) {
        super(name, makeShotStrategy);
    }


    /**
     * Computer совершает выстрел в автоматическом режиме используя стратегию выстрелов
     *
     * @return произведенный выстрел
     */
    @Override
    public Shot makeShot() {
        gameController.setCurrentTurn(name);

        ViewConsole.getInstance().renderMakeShotHandStart(name);
        Coordinate shotCoordinate = makeShotStrategy.makeShotCoordinate();
        ViewConsole.getInstance().renderMakeShotHandCoordinate(shotCoordinate.getValue());

        return new Shot(shotCoordinate);
    }
}
