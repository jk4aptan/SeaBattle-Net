package java1.lesson1.sea_battle;

import java1.lesson1.sea_battle.models.Game;
import java1.lesson1.sea_battle.views.GameWindow;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        GameWindow gw = new GameWindow();
        Game game = new Game();

        game.init();
        gw.init();

        game.start();
    }
}
