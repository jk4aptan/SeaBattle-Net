package java1.lesson1.sea_battle.views;

import java1.lesson1.sea_battle.server.configs.Config;
import java1.lesson1.sea_battle.server.controllers.GameController;
import java1.lesson1.sea_battle.server.models.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Графический интерфейс приложения
 */
public class GameWindow extends JFrame {
    public static final Object gwKey = new Object();
    /**
     * Флаг инициализации игрового поля перед началом новой игры
     */
    private static boolean isInitBattleField = false;
    /**
     * Флаг разрешения игроку сделать выстрел
     */
    private static boolean isPlayerShot = false;

    private static final int FIELD_SIZE = 10;
    private static final int HEADER_H_SIZE = FIELD_SIZE + 1;
    private static final int HEADER_V = 1;
    private static final int HEADER_H = 1;
    private static final int PLAYERS_NAMES_PANEL_ROWS = 1;
    private static final int PLAYERS_NAMES_PANEL_COLUMNS = 2;
    private static final int INFO_PANEL_ROWS = 3;
    private static final int INFO_PANEL_COLUMNS = 1;
    private static final int ROW_STEP = 10;
    private static final String PLAYER_FIELD = "Ваше поле";
    private static final String ADVERSARY_FIELD = "Поле противника";
    private static final String WINDOW_TITLE = "Sea Battle Game";
    private static final char START_COLUMN_HEADER_SYMBOL = 'A';
    private static final char START_ROW_HEADER_SYMBOL = '0';

    /**
     * Игровое поле игрока
     */
    private JButton[][] playerSels;
    /**
     * Игровое поле противника
     */
    private JButton[][] adversarySels;

    /**
     * Имя игрока
     */
    private JLabel playerName;
    /**
     * Имя противника
     */
    private JLabel adversaryName;
    /**
     * Текущий ход
     */
    private JLabel currentTurn;
    /**
     * Текущий результат выстрела
     */
    private JLabel currentResult;

    /**
     * Информационная панель
     */
    private JPanel infoPanel;
    /**
     * Панель с информацией о победителе
     */
    private JPanel winPanel;

    /**
     * Контроллер приложения
     */
//    private final GameController gameController;



    public GameWindow() {
//        gameController = GameController.getInstance();
//        gameController.setGameWindow(this);

        setSize(1000, 350);
        setTitle(WINDOW_TITLE);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        playerSels = new JButton[FIELD_SIZE][FIELD_SIZE];
        adversarySels = new JButton[FIELD_SIZE][FIELD_SIZE];
        currentTurn = new JLabel();
        currentResult = new JLabel();

        infoPanel = getInfoPanel();

        JPanel gameField = new JPanel();
        gameField.setLayout(new BorderLayout());
        gameField.add(getGameField(adversarySels, ADVERSARY_FIELD), BorderLayout.WEST);
        gameField.add(infoPanel, BorderLayout.CENTER);
        gameField.add(getGameField(playerSels, PLAYER_FIELD), BorderLayout.EAST);

        JLabel offsetRight = new JLabel("    ");

        add(getPlayersNamesField(), BorderLayout.NORTH);
        add(offsetRight, BorderLayout.EAST);
        add(gameField, BorderLayout.CENTER);

        playerName.setText(JOptionPane.showInputDialog("Input your name"));
        adversaryName.setText(Config.DEFAULT_PLAYER_NAME);
    }


    /**
     * Инициализация графического интерфейса приложения
     */
    public void init() {
        initBattleField();
        setVisible(true);
    }


    /**
     * Создает панель с информацией о победителе и предложением новой игры
     *
     * @param winnerName имя победителя
     * @return панель с информацией о победителе и предложением новой игры
     */
    private JPanel getWinPanel(String winnerName) {
        JLabel winMessage = new JLabel(winnerName + ", вы выиграли!");
        winMessage.setHorizontalAlignment(SwingConstants.CENTER);

        JButton newGameButton = new JButton("New game");
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isInitBattleField = false;
                infoPanel.remove(winPanel);
                infoPanel.repaint();

//                gameController.initGame();

                synchronized (gwKey) {
                    while (!isInitBattleField) {
                        try {
                            gwKey.wait();
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

                init();
//                gameController.startNewGame();
            }
        });

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        winPanel = new JPanel();
        winPanel.add(winMessage);
        winPanel.add(newGameButton);
        winPanel.add(exitButton);
        return winPanel;
    }

    /**
     * Создает информационную панель.
     * Панель будет предоставлять информацию: о текущем игроке, о результате выстрела, о победителе
     *
     * @return информационную панель
     */
    private JPanel getInfoPanel() {
        currentTurn.setHorizontalAlignment(SwingConstants.CENTER);
        currentResult.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(INFO_PANEL_ROWS, INFO_PANEL_COLUMNS));
        infoPanel.add(currentTurn);
        infoPanel.add(currentResult);
        return infoPanel;
    }


    /**
     * Создает панель с именами игроков
     *
     * @return панель с именами игроков
     */
    private JPanel getPlayersNamesField() {
        adversaryName = new JLabel();
        adversaryName.setHorizontalAlignment(SwingConstants.CENTER);

        playerName = new JLabel();
        playerName.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel playersName = new JPanel();
        playersName.setLayout(new GridLayout(PLAYERS_NAMES_PANEL_ROWS, PLAYERS_NAMES_PANEL_COLUMNS));
        playersName.add(adversaryName);
        playersName.add(playerName);
        return playersName;
    }


    /**
     * Создает панель с игровым полем игрока
     *
     * @param gameSels игровое поле игрока
     * @param fieldName описание поля игрока
     * @return панель с игровым полем игрока
     */
    private JPanel getGameField(JButton[][] gameSels, String fieldName) {
        JPanel headerFieldColumns = new JPanel();
        headerFieldColumns.setLayout(new GridLayout(HEADER_H, HEADER_H_SIZE));
        for (int column = 0; column < HEADER_H_SIZE; column++) {
            for (int row = 0; row < HEADER_H; row++) {
                Character currentColumn = ' ';
                if (column != 0) {
                    currentColumn = (char) (START_COLUMN_HEADER_SYMBOL + column - 1);
                }
                JLabel jLabel = new JLabel(currentColumn.toString());
                jLabel.setHorizontalAlignment(SwingConstants.CENTER);
                headerFieldColumns.add(jLabel);
            }
        }

        JPanel headerFieldRows = new JPanel();
        headerFieldRows.setLayout(new GridLayout(FIELD_SIZE, HEADER_V));
        for (int column = 0; column < HEADER_V; column++) {
            for (int row = 0; row < FIELD_SIZE; row++) {
                Character currentRow = (char) (START_ROW_HEADER_SYMBOL + row);
                JLabel jLabel = new JLabel("     " + currentRow.toString() + "  ");
                headerFieldRows.add(jLabel);
            }
        }

        JPanel gameSeaArea = new JPanel();
        gameSeaArea.setLayout(new GridLayout(FIELD_SIZE, FIELD_SIZE));
        for (int row = 0; row < FIELD_SIZE; row++) {
            for (int column = 0; column < FIELD_SIZE; column++) {
                JButton jButton = new JButton();
                if (fieldName.equals(ADVERSARY_FIELD)) {
                    int finalColumn = column;
                    int finalRow = row;
                    jButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (isPlayerShot) {
                                isPlayerShot = false;
//                                gameController.setShotCoordinate(new Coordinate(finalRow * ROW_STEP + finalColumn));
                            }
                        }
                    });
                }
                gameSels[row][column] = jButton;
                gameSeaArea.add(jButton);
            }
        }

        JLabel gameFieldName = new JLabel(fieldName);
        gameFieldName.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel gameField = new JPanel();
        gameField.setLayout(new BorderLayout());
        gameField.add(headerFieldColumns, BorderLayout.NORTH);
        gameField.add(gameSeaArea, BorderLayout.CENTER);
        gameField.add(headerFieldRows, BorderLayout.WEST);
        gameField.add(gameFieldName, BorderLayout.SOUTH);

        return gameField;
    }


    /**
     * Инициализирует игровое поле приложения.
     * Размещает корабли эскадры на игровом поле перед началом игры.
     */
    private void initBattleField() {
        BattleField battleField = new BattleField();
//        BattleField battleField = gameController.getPlayerBattleField();

        Field playerSeaArea = battleField.getPlayerSeaArea();
        final char SEA = ' ';
        final char SHIP = '|';

        for (int row = 0; row < FIELD_SIZE; row++) {
            for (int column = 0; column < FIELD_SIZE; column++) {
                adversarySels[row][column].setBackground(Color.BLUE);
                switch (playerSeaArea.getCell(row, column)) {
                    case SEA:
                        playerSels[row][column].setBackground(Color.BLUE);
                        break;
                    case SHIP:
                        playerSels[row][column].setBackground(Color.DARK_GRAY);
                        break;
                    default:
                        playerSels[row][column].setBackground(Color.BLUE);
                }
            }
        }
    }


    /**
     * Пауза для отображения результата выстрела
     */
    private void pause() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * @return имя игрока
     */
    public String getPlayerName() {
        return playerName.getText();
    }


    /**
     * Поднимает флаг о запросе на получение координаты выстрела
     *
     * @param name имя игрока, который должен сделать ход
     */
    public void makeShot(String name) {
        setCurrentTurn(name);
        isPlayerShot = true;
    }


    /**
     * Отображает информоцию о том, чей сейчас ход
     *
     * @param name имя игрока, который должен сделать ход
     */
    public void setCurrentTurn(String name) {
        currentTurn.setText(name + ", your turn");
        currentResult.setText("");
    }


    /**
     * Отображает результат выстрела в случаях: МИМО, РАНИЛ
     *
     * @param currentPlayer - игрок сделавший выстрел
     * @param shot - выстрел
     * @param result - результат выстрела, выводится в ячейку поля
     * @param message - сообщение о результате выстрела
     */
    public void setCurrentResult(Player currentPlayer, Shot shot, Color result, String message) {
        currentResult.setText(message);
        JButton[][] sells = currentPlayer.getName().equals(playerName.getText()) ? adversarySels : playerSels;

        sells[shot.getCoordinate().getRow()][shot.getCoordinate().getColumn()].setBackground(result);
        pause();
    }


    /**
     * Отображает результат выстрела в случае потопления корабля
     *
     * @param currentPlayer - игрок сделавший выстрел
     * @param lastSunkShip - потопленный корабль
     * @param message - сообщение о результате выстрела
     * @param busySells - ячейки поля вокруг потопленного корабля
     */
    public void setLastSunkShip(Player currentPlayer, Ship lastSunkShip, String message, ArrayList<Coordinate> busySells) {
        currentResult.setText(message);
        JButton[][] sells = currentPlayer.getName().equals(playerName.getText()) ? adversarySels : playerSels;

        for (Coordinate busySell : busySells) {
            sells[busySell.getRow()][busySell.getColumn()].setBackground(Color.WHITE);
        }
        for (Coordinate coordinate : lastSunkShip.getCoordinates()) {
            sells[coordinate.getRow()][coordinate.getColumn()].setBackground(Color.RED);
        }
        pause();
    }


    /**
     * Отображает панель с информацией о победившем игроке
     *
     * @param currentPlayer - победивший игрок
     */
    public void gameIsOver(Player currentPlayer) {
        infoPanel.add(getWinPanel(currentPlayer.getName()));
        infoPanel.revalidate();
    }


    /**
     * Поднимает флаг для инициализации графического интерфейса приложения перед новой игрой
     *
     * @param value true - выполнить инициализацию графического интерфейса приложения
     */
    public static void setIsInitBattleField(boolean value) {
        GameWindow.isInitBattleField = value;
    }
}
