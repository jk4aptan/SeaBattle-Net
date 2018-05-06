package java1.lesson1.sea_battle.server.models;

/**
 * Автоматический игрок (computer)
 */
public class Computer extends Player{

    public Computer(String name, IMakeShotStrategy makeShotStrategy) {
        super(name, makeShotStrategy);
    }


    /**
     * Computer совершает выстрел в автоматическом режиме используя стратегию выстрелов
     * @return произведенный выстрел
     */
    @Override
    public Shot makeShot() {
        return new Shot(makeShotStrategy.makeShotCoordinate());
    }
}
