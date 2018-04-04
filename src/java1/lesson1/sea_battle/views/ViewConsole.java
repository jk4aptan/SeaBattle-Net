package java1.lesson1.sea_battle.views;

/**
 * Консольный интерфейс приложения
 */
public class ViewConsole {
    private static ViewConsole instance;

    public static ViewConsole getInstance() {
        if (instance == null) {
            instance = new ViewConsole();
        }
        return instance;
    }


    private ViewConsole() {
    }


    /**
     * Выводит в консоль предложение игроку сделать выстрел
     *
     * @param name имя игрока делающего выстрел
     */
    public void renderMakeShotHandStart(String name) {
        System.out.println(name + ", ваш выстрел");
    }


    /**
     * Выводит в консоль координату выстрела
     *
     * @param value координата выстрела
     */
    public void renderMakeShotHandCoordinate(int value) {
        System.out.println(value);
    }
}
