package java1.lesson1.sea_battle.client.views;

import java1.lesson1.sea_battle.client.controllers.ClientController;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GameWindow extends JFrame {
    public static final Object key = new Object();

    private static final int FIELD_SIZE = 10;
    private static final int ROW_STEP = 10;
    private static final String WINDOW_TITLE = "Sea Battle";
    private static final String PLAYER_FIELD = "ВАШЕ ПОЛЕ";
    private static final String ADVERSARY_FIELD = "ПОЛЕ ПРОТИВНИКА";
    private static final Color DEFAULT_COLOR = Color.lightGray;


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
     * Current turn
     */
    private JLabel turn;
    /**
     * Current shot result
     */
    private JLabel shotResult;
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
        setSize(1050, 380);
        setResizable(false);
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
        turn = new JLabel();
        shotResult = new JLabel();

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


    // ??????
    public void start() {
//        init();
        setPlayerName();
        gameField.add(getChooseAdversaryPanel(), BorderLayout.CENTER);
        setVisible(true);
    }


    /**
     * Create WinPanel
     * @param winner
     * @return WinPanel
     */
    private JPanel getWinPanel(String winner) {
        JButton gameButton = new JButton("ИГРАТЬ СНОВА");
        gameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameField.remove(infoPanel);
                gameField.add(getChooseAdversaryPanel(), BorderLayout.CENTER);
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

        JLabel label = new JLabel("Победил " + winner);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "ПОБЕДА!!!", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);

        winPanel = new JPanel();
        winPanel.setLayout(new BorderLayout());
        winPanel.setBorder(border);
        winPanel.add(label, BorderLayout.CENTER);
        winPanel.add(buttonPanel, BorderLayout.SOUTH);
        return winPanel;
    }


    /**
     * Update Adversaries Panel
     */
    private synchronized void updateAdversariesPanel() {
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
     *
     * @param gameSels Player's playing sels
     * @param fieldName Player's name
     * @param playerName
     * @return Panel with player's playing field
     */
    private JPanel getGameField(JButton[][] gameSels, String fieldName, JLabel playerName) {
        JLabel gameFieldName = new JLabel(fieldName + ": ");
//        gameFieldName.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel fieldPlayerName = new JPanel(new FlowLayout(FlowLayout.CENTER));
        fieldPlayerName.add(gameFieldName);
        fieldPlayerName.add(playerName);

        JPanel gameSeaArea = new JPanel();
        gameSeaArea.setLayout(new GridLayout(FIELD_SIZE, FIELD_SIZE, 2, 2));
        for (int row = 0; row < FIELD_SIZE; row++) {
            for (int column = 0; column < FIELD_SIZE; column++) {
                JButton jButton = new JButton();
                jButton.setBackground(DEFAULT_COLOR);
                if (fieldName.equals(ADVERSARY_FIELD)) {
                    int finalColumn = column;
                    int finalRow = row;
                    jButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (makeShot) {
                                makeShot = false;
//                                gameController.setShotCoordinate(finalRow * ROW_STEP + finalColumn);
                            }
                        }
                    });
                }
                gameSels[row][column] = jButton;
                gameSeaArea.add(jButton);
            }
        }

        JPanel gameField = new JPanel();
        gameField.setLayout(new BorderLayout());
        gameField.add(gameSeaArea);
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
     * Set flag allowing the player to make a shot
     * @param value if true then to make a shot
     */
    public void setMakeShot(boolean value) {
        this.makeShot = value;
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


        //todo - process response
        //todo заглушка
        if (response.equals(AGREE)) {
            adversariesPanel.add(new JLabel("Start game"));
            adversariesPanel.revalidate();
        }
    }


    /**
     * Setter response
     * @param response
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
    public synchronized void letsPlay(String playerName) {
        String message = this.playerName + ", " + playerName + " предложил сыграть ?";
        Integer response = JOptionPane.showConfirmDialog(null, message);
        controller.returnResponse(response);

        if (response.toString().equals(AGREE)) {
            synchronized (this) {
                isGame = true;
                isSet = false;
                while (!isSet) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
