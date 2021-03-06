package java1.lesson1.sea_battle.client.views;

import java1.lesson1.sea_battle.client.controllers.ClientController;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GameWindow extends JFrame {
    private static final int FIELD_SIZE = 10;
    private static final int ICON_SIZE = 35;
    private static final int ROW_STEP = 10;
    private static final int DELIMITER = 10;
    private static final String WINDOW_TITLE = "Sea Battle";
    private static final String PLAYER_FIELD = "ВАШЕ ПОЛЕ";
    private static final String ADVERSARY_FIELD = "ПОЛЕ ПРОТИВНИКА";

    private final ImageIcon SEA = new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/sea.png"));
    private final ImageIcon SHOT = new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/shot.png"));
    private final ImageIcon SUNK = new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/sunk.png"));


    /**
     * Flag allowing the player to make a shot
     */
    private boolean makeShot;
    /**
     * Player's playing sels
     */
    private JButton[][] playerSels;
    /**
     * Adversary's playing sels
     */
    private JButton[][] adversarySels;
    /**
     * All adversaries
     */
    private ArrayList<JButton> adversaries;
    /**
     * Player's name
     */
    private JLabel playerName;
    /**
     * Adversary's name
     */
    private JLabel adversaryName;
    /**
     * Имя игрока которому делается запрос на игру
     */
    private String requestAdversaryName;
    /**
     * Game's controller
     */
    private ClientController controller;
    /**
     * Game's field
     */
    private JPanel gameField;


    private final String AGREE = "0";
    private final String REFUSE = "1";
    private String response;

    private boolean isSet;
    private boolean isGame;
    private JTextArea infoOut;
    private JPanel chooseAdversaryPanel;
    private JPanel adversariesPanel;
    private JPanel infoPanel;
    private JPanel winPanel;


    public GameWindow() {
        super(WINDOW_TITLE);
        setSize(1100, 470);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                controller.exit();
            }
        });

        isGame = false;
        adversaries = new ArrayList<>();
        makeShot = false;
        playerSels = new JButton[FIELD_SIZE][FIELD_SIZE];
        adversarySels = new JButton[FIELD_SIZE][FIELD_SIZE];

        playerName = new JLabel();
        adversaryName = new JLabel();

        JPanel adversaryPlayingField = getGameField(adversarySels, ADVERSARY_FIELD, adversaryName);
        JPanel playerPlayingField = getGameField(playerSels, PLAYER_FIELD, playerName);

        gameField = new JPanel();
        gameField.setBorder(BorderFactory.createEmptyBorder(10,15,10,15));
        gameField.setLayout(new BorderLayout(15,0));
        gameField.add(playerPlayingField, BorderLayout.EAST);
        gameField.add(adversaryPlayingField, BorderLayout.WEST);
        add(gameField, BorderLayout.CENTER);
    }

    /**
     * Инициализация графического интерфейса пользователя
     */
    public void init() {
        setPlayerName();
        chooseAdversaryPanel = getChooseAdversaryPanel();
        gameField.add(chooseAdversaryPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    /**
     * Setter InfoPanel
     */
    public void setInfoPanel() {
        gameField.remove(chooseAdversaryPanel);
        infoPanel = getInfoPanel();
        gameField.add(infoPanel, BorderLayout.CENTER);
        gameField.revalidate();
    }

    /**
     * Update Adversaries Panel
     */
    private synchronized void updateAdversariesPanel() {
        if (isGame) {
            return;
        }
        // запросить всех доступных для игры игроков
        controller.getAdversaries();

        isSet = false;
        while (!isSet) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        adversariesPanel.removeAll();
        adversariesPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        if (!adversaries.isEmpty()) {
            for (JButton adversary : adversaries) {
                adversariesPanel.add(adversary);
            }
        }
        adversariesPanel.revalidate();
    }

    /**
     * Create Choose Adversary Panel
     * @return Choose Adversary Panel
     */
    private JPanel getChooseAdversaryPanel() {
        // запросить всех доступных для игры игроков
        controller.getAdversaries();

        synchronized (this) {
            isSet = false;
            while (!isSet) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        adversariesPanel = new JPanel();
        adversariesPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        if (!adversaries.isEmpty()) {
            for (JButton adversary : adversaries) {
                adversariesPanel.add(adversary);
            }
        }

        JButton updateList = new JButton("ОБНОВИТЬ ИГРОКОВ");
        updateList.setBackground(Color.lightGray);
        updateList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAdversariesPanel();
            }
        });

        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "ВЫБЕРИТЕ ПРОТИВНИКА", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);

        chooseAdversaryPanel = new JPanel();
        chooseAdversaryPanel.setLayout(new BorderLayout());
        chooseAdversaryPanel.setBorder(border);
        chooseAdversaryPanel.add(adversariesPanel, BorderLayout.CENTER);
        chooseAdversaryPanel.add(updateList, BorderLayout.SOUTH);
        return chooseAdversaryPanel;
    }

    /**
     * Create info panel
     * @return info panel
     */
    private JPanel getInfoPanel() {
        infoOut = new JTextArea();
        infoOut.setMargin(new Insets(5,10,0,10));
        infoOut.setLineWrap(true);
        infoOut.setEditable(false);

        DefaultCaret caret = (DefaultCaret) infoOut.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollPane = new JScrollPane(infoOut);

        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "ХОД ИГРЫ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);

        infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.add(scrollPane, BorderLayout.CENTER);
        infoPanel.setBorder(border);

        infoOut.setBackground(infoPanel.getBackground());
        scrollPane.setBorder(BorderFactory.createLineBorder(infoPanel.getBackground()));
        return infoPanel;
    }

    /**
     * Create a panel with player's playing field
     * @param gameSels Player's playing sels
     * @param fieldName метка с указанием имени игрока
     * @param playerName Player's name
     * @return Panel with player's playing field
     */
    private JPanel getGameField(JButton[][] gameSels, String fieldName, JLabel playerName) {
        JLabel gameFieldName = new JLabel(fieldName + ": ");
        JPanel fieldPlayerName = new JPanel(new FlowLayout(FlowLayout.CENTER));
        fieldPlayerName.add(gameFieldName);
        fieldPlayerName.add(playerName);

        JPanel seaArea = new JPanel();
        seaArea.setLayout(new GridLayout(FIELD_SIZE, FIELD_SIZE, 1, 1));
        for (int row = 0; row < FIELD_SIZE; row++) {
            for (int column = 0; column < FIELD_SIZE; column++) {
                JButton jButton = new JButton();
                jButton.setBorder(null);
                jButton.setIcon(SEA);
                if (fieldName.equals(ADVERSARY_FIELD)) {
                    int finalColumn = column;
                    int finalRow = row;
                    jButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (makeShot) {
                                makeShot = false;
                                controller.setShotCoordinate(finalRow * ROW_STEP + finalColumn);
                            }
                        }
                    });
                }
                gameSels[row][column] = jButton;
                seaArea.add(jButton);
            }
        }

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.rowHeights = new int[]{FIELD_SIZE * ICON_SIZE};
        gridBagLayout.columnWidths = new int[]{FIELD_SIZE * ICON_SIZE};

        JPanel gameSeaArea = new JPanel(gridBagLayout);
        gameSeaArea.add(seaArea);

        JPanel gameField = new JPanel();
        gameField.setLayout(new BorderLayout());
        gameField.add(gameSeaArea, BorderLayout.CENTER);
        gameField.add(fieldPlayerName, BorderLayout.SOUTH);

        return gameField;
    }

    /**
     * Set Player's name
     */
    private void setPlayerName() {
        String name = JOptionPane.showInputDialog("Input your name");
        playerName.setText(name);
        controller.sendPlayerName(name);
    }

    /**
     * Set Game's controller
     * @param controller game's controller
     */
    public void setController(ClientController controller) {
        this.controller = controller;
    }

    /**
     * Setter Adversaries
     * @param items Adversaries
     */
    public void setAdversaries(String items) {
        adversaries.clear();

        String[] players = items.split(";");
        for (String player : players) {
            String[] values = player.split(":");
            if (!values[0].isEmpty()) {
                requestAdversaryName = values[0];
                JButton jButton = new JButton(values[0]);
                jButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        requestAdversary(values[1]);
                    }
                });
                adversaries.add(jButton);
            }
        }
    }

    /**
     * Request Adversary
     * @param id adversary
     */
    private synchronized void requestAdversary(String id) {
        if (isGame) {
            return;
        }
        // Предложить сыграть
        controller.requestAdversary(id);
        isSet = false;
        while (!isSet) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (response.equals(REFUSE)) {
            JOptionPane.showMessageDialog(null, "Вам отказали. Выберите другого игрока");
        }
        if (response.equals(AGREE)) {
            isGame = true;
            adversaryName.setText(requestAdversaryName);
        }
    }

    /**
     * Setter response
     * @param response ответ игрока, которому предложили сыграть
     */
    public void setResponse(String response) {
        this.response = response;
    }

    /**
     * Setter isSet
     * @param value if is set then true
     */
    public void setIsSet(boolean value) {
        isSet = value;
    }

    /**
     * Lets Play
     * @param playerName игрок сделавший предложение
     */
    public void letsPlay(String playerName) {
        String message = this.playerName.getText() + ", " + playerName + " предложил сыграть ?";
        Integer response = JOptionPane.showConfirmDialog(null, message);
        controller.returnResponse(response);

        if (response.toString().equals(AGREE)) {
            isGame = true;
            adversaryName.setText(playerName);
        }
    }

    /**
     * Размещает корабли на игровом поле
     * @param shipsCoordinates координаты кораблей
     */
    public void initShipCoordinates(String shipsCoordinates) {
        ImageIcon[] ship4v = {
                new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/ship4v1.png")),
                new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/ship4v2.png")),
                new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/ship4v3.png")),
                new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/ship4v4.png"))
        };
        ImageIcon[] ship4h = {
                new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/ship4h1.png")),
                new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/ship4h2.png")),
                new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/ship4h3.png")),
                new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/ship4h4.png"))
        };
        ImageIcon[] ship3v = {
                new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/ship3v1.png")),
                new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/ship3v2.png")),
                new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/ship3v3.png")),
        };
        ImageIcon[] ship3h = {
                new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/ship3h1.png")),
                new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/ship3h2.png")),
                new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/ship3h3.png")),
        };
        ImageIcon[] ship2v = {
                new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/ship2v1.png")),
                new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/ship2v2.png")),
        };
        ImageIcon[] ship2h = {
                new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/ship2h1.png")),
                new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/ship2h2.png")),
        };
        ImageIcon[] ship1 = {
                new ImageIcon(getClass().getClassLoader().getResource("java1/lesson1/sea_battle/client/images/ship1.png")),
        };

        final int DECKS = 0;
        final int DECKS_4_SHIP = 4;
        final int DECKS_3_SHIP = 3;
        final int DECKS_2_SHIP = 2;
        final int DECKS_1_SHIP = 1;
        final int ORIENTATION = 1;
        final int VERTICAL = 1;
        final int OFFSET = 2;

        for (int row = 0; row < FIELD_SIZE; row++) {
            for (int column = 0; column < FIELD_SIZE; column++) {
                playerSels[row][column].setIcon(SEA);
                adversarySels[row][column].setIcon(SEA);
            }
        }

        for (String shipDate : shipsCoordinates.split(";")) {
            String[] ship = shipDate.split(",");

            ImageIcon[] shipIcon = null;
            switch (Integer.parseInt(ship[DECKS])) {
                case DECKS_4_SHIP:
                    shipIcon = Integer.parseInt(ship[ORIENTATION]) == VERTICAL ? ship4v : ship4h;
                    break;
                case DECKS_3_SHIP:
                    shipIcon = Integer.parseInt(ship[ORIENTATION]) == VERTICAL ? ship3v : ship3h;
                    break;
                case DECKS_2_SHIP:
                    shipIcon = Integer.parseInt(ship[ORIENTATION]) == VERTICAL ? ship2v : ship2h;
                    break;
                case DECKS_1_SHIP:
                    shipIcon = ship1;
                    break;
            }

            for (int i = OFFSET; i < (Integer.parseInt(ship[DECKS]) + OFFSET); i++) {
                Integer row = Integer.parseInt(ship[i]) / DELIMITER;
                Integer column = Integer.parseInt(ship[i]) % DELIMITER;

                playerSels[row][column].setIcon(shipIcon[i - OFFSET]);
            }
        }
    }

    /**
     * Выводит на InfoPanel сообщение
     * @param message выводимое сообщение
     */
    private void showMessage(String message) {
        infoOut.append(message + "\n");
    }

    /**
     * Сообщает о том, что текущему игроку необходимо сделать ход
     * @param playerName текущий игрок
     */
    public void setTurnOn(String playerName) {
        showMessage("ХОД: " + playerName);
        makeShot = true;
    }

    /**
     * Сообщает противнику о том, что сейчас не его ход
     * @param playerName текущий игрок
     */
    public void setTurnOff(String playerName) {
        showMessage("ХОД: " + playerName);
    }

    /**
     * Выводит результат хода текущего игрока
     * @param data содержит имя текущего игрока и xoд игрока
     */
    public void setCurrentResult(String data) {
        String[] resultData = data.split(";");
        String currentPlayerName = resultData[0];

        int coordinate = Integer.parseInt(resultData[1]);
        int row = coordinate / DELIMITER;
        int column = coordinate % DELIMITER;

        ImageIcon icon = null;
        String message = null;
        String result = resultData[2];
        switch (result) {
            case "UNHARMED":
                icon = SHOT;
                message = "МИМО";
                break;
            case "WOUNDED":
                icon = SUNK;
                message = "РАНИЛ";
        }

        if (currentPlayerName.equals(playerName.getText())) {
            adversarySels[row][column].setIcon(icon);
        } else {
            playerSels[row][column].setIcon(icon);
        }
        showMessage(message);
    }

    /**
     * Выводит данные по потопленному кораблю
     * @param data данные по потопленному кораблю
     */
    public void setLastSunkShip(String data) {
        String[] resultData = data.split(";");
        String currentPlayerName = resultData[0];

        for (String busySellsCoordinates : resultData[2].split(",")) {
            int coordinate = Integer.parseInt(busySellsCoordinates);
            int row = coordinate / DELIMITER;
            int column = coordinate % DELIMITER;

            if (currentPlayerName.equals(playerName.getText())) {
                adversarySels[row][column].setIcon(SHOT);
            } else {
                playerSels[row][column].setIcon(SHOT);
            }
        }

        for (String sunkShipCoordinate : resultData[1].split(",")) {
            int coordinate = Integer.parseInt(sunkShipCoordinate);
            int row = coordinate / DELIMITER;
            int column = coordinate % DELIMITER;

            if (currentPlayerName.equals(playerName.getText())) {
                adversarySels[row][column].setIcon(SUNK);
            } else {
                playerSels[row][column].setIcon(SUNK);
            }
        }
        showMessage("ПОТОПИЛ");
    }

    /**
     * Завершение игры
     * @param winnerName имя победившего игрока
     */
    public void gameIsOver(String winnerName) {
        gameField.remove(infoPanel);
        winPanel = getWinPanel(winnerName);
        gameField.add(winPanel, BorderLayout.CENTER);
        gameField.revalidate();
    }

    /**
     * Create WinPanel
     * @param winnerName имя победившего игрока
     * @return WinPanel
     */
    private JPanel getWinPanel(String winnerName) {
        JButton gameButton = new JButton("ИГРАТЬ СНОВА");
        gameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int row = 0; row < FIELD_SIZE; row++) {
                    for (int column = 0; column < FIELD_SIZE; column++) {
                        playerSels[row][column].setIcon(SEA);
                        adversarySels[row][column].setIcon(SEA);
                    }
                }
                isGame = false;
                controller.setIsNotBusy();

                gameField.remove(winPanel);
                chooseAdversaryPanel = getChooseAdversaryPanel();
                gameField.add(chooseAdversaryPanel, BorderLayout.CENTER);
                gameField.revalidate();
            }
        });

        JButton exitButton = new JButton("ВЫЙТИ");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.exit();
                System.exit(0);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(gameButton);
        buttonPanel.add(exitButton);

        JLabel label = new JLabel("Победил " + winnerName);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "ПОБЕДА!!!", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);

        winPanel = new JPanel();
        winPanel.setLayout(new BorderLayout());
        winPanel.setBorder(border);
        winPanel.add(label, BorderLayout.CENTER);
        winPanel.add(buttonPanel, BorderLayout.SOUTH);
        return winPanel;
    }
}
