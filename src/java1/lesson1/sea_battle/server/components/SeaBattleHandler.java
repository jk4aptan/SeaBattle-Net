package java1.lesson1.sea_battle.server.components;

import java1.lesson1.sea_battle.server.controllers.GameController;
import java1.lesson1.sea_battle.server.models.Game;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SeaBattleHandler implements Runnable {
    private static List<SeaBattleHandler> handlers = Collections.synchronizedList(new ArrayList<>());
    private final Object key = new Object();

    private static Integer count = 0;
    private Thread gameThread;

    private static int incrementCount() {
        synchronized (count) {
            return ++count;
        }
    }

    private final String AGREE = "0";
    private final String REFUSE = "1";
    private final String CANCEL = "2";
    private final String LEFT_THE_GAME = "3";
    private final String BUSY = "4";

    private boolean exit;
    private int id;
    private boolean isBusy;
    private boolean isSet;
    private String response;
    private String playerName;
    private SeaBattleHandler adversary;
    private GameController controller;
    private final Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public SeaBattleHandler(Socket socket) {
        id = incrementCount();
        exit = false;
        isBusy = false;
        this.socket = socket;
        try {
            this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(this).start();
    }

    @Override
    public void run() {
        handlers.add(this);
        try {
            while (!exit) {
                String message = in.readUTF();

                if (message.startsWith("setPlayerName")) {
                    setPlayerName(message.substring(14));
                }
                else if (message.startsWith("getAdversaries")) {
                    returnAdversaries();
                }
                else if (message.startsWith("requestAdversary")) {
                    requestAdversary(message.substring(17));
                }
                else if (message.startsWith("returnResponse")) {
                    response = message.substring(15);
                    isSet = true;
                    synchronized (key) {
                        key.notifyAll();
                    }
                }
                else if (message.startsWith("setShotCoordinate")) {
                    controller.setShotCoordinate(message.substring(18));
                }
                else if (message.startsWith("exit")) {
                    exit = true;
                }
                else if (message.startsWith("setIsNotBusy")) {
                    isBusy = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            handlers.remove(this);
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * Request Adversary с предложением сыграть
     * @param id adversary
     */
    private void requestAdversary(String id) {
        isBusy = true;
        adversary = null;
        for (SeaBattleHandler handler : handlers) {
            if (handler.getId() == Integer.parseInt(id)) {
                adversary = handler;
                break;
            }
        }
        if (adversary == null) {
            //игрок вышел из игры
            response = LEFT_THE_GAME;
        }
        else if (adversary.getIsBusy()) {
            //игрок уже занят
            response = BUSY;
        }
        else {
            // Предложить сыграть
            response = adversary.letsPlay(this);
        }

        // Выбрать другого игрока
        if (response.equals(REFUSE) || response.equals(CANCEL) || response.equals(BUSY) || response.equals(LEFT_THE_GAME)) {
            isBusy = false;
            adversary = null;
            sendMessage("adversaryResponse " + REFUSE);
        }

        // Start game
        if (response.equals(AGREE)) {
            sendMessage("adversaryResponse " + AGREE);
            adversary.setAdversary(this);
            Game game = new Game(this, adversary);
            try {
                game.init();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            gameThread = new Thread(game);
            gameThread.start();
        }
    }

    /**
     * Устанавливает adversary для игрока принявшего предложение
     * @param playerNetHandler игрок сделавший предложение
     */
    private void setAdversary(SeaBattleHandler playerNetHandler) {
        this.adversary = playerNetHandler;
    }

    /**
     * Lets Play
     * @param player игрок сделавший предложение
     */
    public String letsPlay(SeaBattleHandler player) {
        if (isBusy) {
            return BUSY;
        }
        isBusy = true;
        isSet = false;
        sendMessage("letsPlay " + player.getPlayerName());
        synchronized (key) {
            while (!isSet) {
                try {
                    key.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (response.equals(REFUSE) || response.equals(CANCEL)) {
            isBusy = false;
        }
        return response;
    }

    /**
     * Send the adversaries to the client
     */
    private void returnAdversaries() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("adversaries ");
        synchronized (handlers) {
            for (SeaBattleHandler handler : handlers) {
                if (!handler.getIsBusy() && handler.getId() != this.id) {
                    stringBuilder.append(handler.getPlayerName()).append(":").append(handler.getId()).append(";");
                }
            }
            sendMessage(stringBuilder.toString());
        }
    }

    /**
     * Send Message to the client
     * @param message пересылаемое сообщение
     */
    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter Id
     * @return Id
     */
    public int getId() {
        return id;
    }

    /**
     * Getter isBusy
     * @return true if busy
     */
    public boolean getIsBusy() {
        return isBusy;
    }

    /**
     * Setter playerName
     * @param playerName имя игрока
     */
    private void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Getter playerName
     * @return playerName имя игрока
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Setter controller
     * @param controller контроллер игры
     */
    public void setController(GameController controller) {
        this.controller = controller;
    }

    /**
     * Завершение игры
     * @param winnerName имя победившего игрока
     */
    public void gameIsOver(String winnerName) {
        sendMessage("gameIsOver " + winnerName);
        adversary = null;
        controller = null;
    }
}
