package java1.lesson1.sea_battle.server.components.Strategies;

import java1.lesson1.sea_battle.server.configs.Config;
import java1.lesson1.sea_battle.server.models.Coordinate;
import java1.lesson1.sea_battle.server.models.IMakeShotStrategy;
import java1.lesson1.sea_battle.server.models.Squadron;

import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

/**
 * Реализует стратегию стрельбы по кораблям противника описанную в https://refactoring.guru/ru/design-patterns/strategy/java/example
 */
public class ImproveAutoMakeShotStrategy implements IMakeShotStrategy{
    private static final int ROWS_STEP = 10;
    private static final int COLUMNS_STEP = 1;

    private LinkedList<Integer> luckyShots;
    private LinkedList<Integer> shots;
    private Set<Integer> madeShots;
    private LinkedList<Integer> afterFirstWoundedShots;
    private LinkedList<Integer> afterRepeatWoundedShots;
    private int shot;

    private boolean isFirstWoundedState;
    private boolean isRepeatWoundedState;


    public ImproveAutoMakeShotStrategy() {
        isFirstWoundedState = false;
        isRepeatWoundedState = false;

        luckyShots = new LinkedList<>();
        madeShots = new TreeSet<>();
        afterFirstWoundedShots = new LinkedList<>();
        afterRepeatWoundedShots = new LinkedList<>();

        shots = new LinkedList<>();
        // координаты стрельбы по 4-х палубным кораблям
        makeImproveShots(Config.FOUR_DECK_SHIP);
        // координаты стрельбы по 3-х и 2-х палубным кораблям
        makeImproveShots(Config.TWO_DECK_SHIP);
        // координаты стрельбы по 1-х палубным кораблям
        makeImproveShots(Config.ONE_DECK_SHIP);
    }

    /**
     * Реализует улучшенный алгоритм стрельбы по кораблям противника.
     * @return координату выстрела
     */
    @Override
    public Coordinate makeShotCoordinate() {
        switch (Squadron.shotResult) {
            case WOUNDED:
                if (!isFirstWoundedState) {
                    isFirstWoundedState = true;
                    luckyShots.add(shot);
                    makeAfterFirstWoundedShots(shot);

                    do {
                        shot = afterFirstWoundedShots.pop();
                    } while (madeShots.contains(shot));
                } else {
                    isRepeatWoundedState = true;
                    luckyShots.add(shot);
                    makeAfterRepeatWoundedShots(luckyShots);

                    do {
                        shot = afterRepeatWoundedShots.pop();
                    } while (madeShots.contains(shot));
                }
                break;
            case SUNK:
                isFirstWoundedState = false;
                isRepeatWoundedState = false;

                luckyShots.add(shot);
                addMadeShots(luckyShots);
                luckyShots.clear();

                do {
                    shot = shots.pop();
                } while (madeShots.contains(shot));
                break;
            default:
                if (!isFirstWoundedState && !isRepeatWoundedState) {
                    do {
                        shot = shots.pop();
                    } while (madeShots.contains(shot));
                } else {
                    if (isRepeatWoundedState) {
                        do {
                            shot = afterRepeatWoundedShots.pop();
                        } while (madeShots.contains(shot));
                    } else {
                        do {
                            shot = afterFirstWoundedShots.pop();
                        } while (madeShots.contains(shot));
                    }
                }
        }
        madeShots.add(shot);
        return new Coordinate(shot);
    }

    /**
     * Задает базовую последовательность координат выстрелов по кораблям
     * @param shipDecks колличество палуб корабля
     */
    private void makeImproveShots(int shipDecks) {
        if (shipDecks == Config.ONE_DECK_SHIP) {
            for (int i = 0; i < Config.MAX_COORDINATE; i++) {
                if (!shots.contains(i)) {
                    shots.add(i);
                }
            }
        } else {
            int firstColumn = shipDecks - 1;
            int startColumn = firstColumn;
            int shot = 0;

            for (int row = 0; row < Config.MAX_COORDINATE; row += ROWS_STEP) {
                for (int column = startColumn; column < Config.BATTLE_FIELD_COLUMNS_COUNT; column += shipDecks) {
                    shot = row + column;
                    if (!shots.contains(shot)) {
                        shots.add(shot);
                    }
                }
                startColumn = (startColumn == 0) ? firstColumn : startColumn - 1;
            }
        }
    }

    /**
     * Вычисляет последовательность координат выстрелов после первого ранения корабля
     * @param luckyShot поразивший корабль выстрел
     */
    private void makeAfterFirstWoundedShots(int luckyShot) {
        afterFirstWoundedShots.clear();
        if (luckyShot % ROWS_STEP == 0) {
            // 0 20 30 40 50 60 70 80 90
            if (luckyShot / ROWS_STEP == 0) {
                // 0
                afterFirstWoundedShots.add(luckyShot + COLUMNS_STEP);
                afterFirstWoundedShots.add(luckyShot + ROWS_STEP);
            } else {
                if (luckyShot / ROWS_STEP == 9) {
                    // 90
                    afterFirstWoundedShots.add(luckyShot + COLUMNS_STEP);
                    afterFirstWoundedShots.add(luckyShot - ROWS_STEP);
                } else {
                    // 20 30 40 50 60 70 80
                    afterFirstWoundedShots.add(luckyShot - ROWS_STEP);
                    afterFirstWoundedShots.add(luckyShot + COLUMNS_STEP);
                    afterFirstWoundedShots.add(luckyShot + ROWS_STEP);
                }
            }
        } else {
            if (luckyShot % ROWS_STEP == 9) {
                // 9 19 29 39 49 59 69 79 89 99
                if (luckyShot / ROWS_STEP == 0) {
                    // 9
                    afterFirstWoundedShots.add(luckyShot - COLUMNS_STEP);
                    afterFirstWoundedShots.add(luckyShot + ROWS_STEP);
                } else {
                    if (luckyShot / ROWS_STEP == 9) {
                        // 99
                        afterFirstWoundedShots.add(luckyShot - COLUMNS_STEP);
                        afterFirstWoundedShots.add(luckyShot - ROWS_STEP);
                    } else {
                        // 19 29 39 49 59 69 79 89
                        afterFirstWoundedShots.add(luckyShot - ROWS_STEP);
                        afterFirstWoundedShots.add(luckyShot - COLUMNS_STEP);
                        afterFirstWoundedShots.add(luckyShot + ROWS_STEP);
                    }
                }
            } else {
                if (luckyShot / ROWS_STEP == 0) {
                    // 1 2 3 4 5 6 7 8
                    afterFirstWoundedShots.add(luckyShot - COLUMNS_STEP);
                    afterFirstWoundedShots.add(luckyShot + ROWS_STEP);
                    afterFirstWoundedShots.add(luckyShot + COLUMNS_STEP);
                } else {
                    if (luckyShot / ROWS_STEP == 9) {
                        // 91 92 93 94 95 96 97 98
                        afterFirstWoundedShots.add(luckyShot - COLUMNS_STEP);
                        afterFirstWoundedShots.add(luckyShot - ROWS_STEP);
                        afterFirstWoundedShots.add(luckyShot + COLUMNS_STEP);
                    } else {
                        // все остальные 11 12 .. 18
                        //               21 22 .. 28
                        //               81 82 .. 88
                        afterFirstWoundedShots.add(luckyShot - ROWS_STEP);
                        afterFirstWoundedShots.add(luckyShot + COLUMNS_STEP);
                        afterFirstWoundedShots.add(luckyShot + ROWS_STEP);
                        afterFirstWoundedShots.add(luckyShot - COLUMNS_STEP);
                    }
                }
            }
        }
    }

    /**
     * Вычисляет последовательность координат выстрелов после повторного ранения корабля
     * @param luckyShots поразивший корабль выстрел
     */
    private void makeAfterRepeatWoundedShots(LinkedList<Integer> luckyShots) {
        afterRepeatWoundedShots.clear();
        luckyShots.sort(Integer::compareTo);

        int step = (luckyShots.getFirst() / ROWS_STEP == luckyShots.getLast() / ROWS_STEP) ? COLUMNS_STEP : ROWS_STEP;
        int newFirstShot = luckyShots.getFirst() - step;
        int newLastShot = luckyShots.getLast() + step;
        switch (step) {
            case COLUMNS_STEP:
                if ((luckyShots.getFirst() % ROWS_STEP) != 0 && (newFirstShot % ROWS_STEP) >= 0 && (newFirstShot % ROWS_STEP) <= 9 && newFirstShot >= Config.MIN_COORDINATE) {
                    afterRepeatWoundedShots.add(newFirstShot);
                }
                if ((luckyShots.getLast() % ROWS_STEP) != 9 && (newLastShot % ROWS_STEP) >= 0 && (newLastShot % ROWS_STEP) <= 9 && newLastShot <= Config.MAX_COORDINATE) {
                    afterRepeatWoundedShots.add(newLastShot);
                }
                break;
            case ROWS_STEP:
                if ((luckyShots.getFirst() / ROWS_STEP) != 0 && (newFirstShot % ROWS_STEP) >= 0 && (newFirstShot % ROWS_STEP) <= 9 && newFirstShot >= Config.MIN_COORDINATE) {
                    afterRepeatWoundedShots.add(newFirstShot);
                }
                if ((luckyShots.getLast() / ROWS_STEP) != 9 && (newLastShot % ROWS_STEP) >= 0 && (newLastShot % ROWS_STEP) <= 9 && newLastShot <= Config.MAX_COORDINATE) {
                    afterRepeatWoundedShots.add(newLastShot);
                }
                break;
        }
    }

    /**
     * После того как корабль потоплен, данный метод сохраняет ячейки поля, которые располагаются вокруг корабля.
     * Эти ячейки не будут использоваться для стрельбы.
     * @param luckyShots поразивший корабль выстрел
     */
    private void addMadeShots(LinkedList<Integer> luckyShots) {
        for (Integer luckyShot : luckyShots) {
            madeShots.add(luckyShot);
            if (luckyShot % ROWS_STEP == 0) {
                // 0 20 30 40 50 60 70 80 90
                if (luckyShot / ROWS_STEP == 0) {
                    // 0
                    madeShots.add(luckyShot + COLUMNS_STEP);
                    madeShots.add(luckyShot + ROWS_STEP);
                    madeShots.add(luckyShot + ROWS_STEP  + COLUMNS_STEP);
                } else {
                    if (luckyShot / ROWS_STEP == 9) {
                        // 90
                        madeShots.add(luckyShot + COLUMNS_STEP);
                        madeShots.add(luckyShot - ROWS_STEP);
                        madeShots.add(luckyShot - ROWS_STEP + COLUMNS_STEP);
                    } else {
                        // 20 30 40 50 60 70 80
                        madeShots.add(luckyShot - ROWS_STEP);
                        madeShots.add(luckyShot - ROWS_STEP + COLUMNS_STEP);
                        madeShots.add(luckyShot + COLUMNS_STEP);
                        madeShots.add(luckyShot + ROWS_STEP);
                        madeShots.add(luckyShot + ROWS_STEP + COLUMNS_STEP);
                    }
                }
            } else {
                if (luckyShot % ROWS_STEP == 9) {
                    // 9 19 29 39 49 59 69 79 89 99
                    if (luckyShot / ROWS_STEP == 0) {
                        // 9
                        madeShots.add(luckyShot - COLUMNS_STEP);
                        madeShots.add(luckyShot + ROWS_STEP);
                        madeShots.add(luckyShot + ROWS_STEP - COLUMNS_STEP);
                    } else {
                        if (luckyShot / ROWS_STEP == 9) {
                            // 99
                            madeShots.add(luckyShot - COLUMNS_STEP);
                            madeShots.add(luckyShot - ROWS_STEP);
                            madeShots.add(luckyShot - ROWS_STEP - COLUMNS_STEP);
                        } else {
                            // 19 29 39 49 59 69 79 89
                            madeShots.add(luckyShot - ROWS_STEP);
                            madeShots.add(luckyShot - ROWS_STEP - COLUMNS_STEP);
                            madeShots.add(luckyShot - COLUMNS_STEP);
                            madeShots.add(luckyShot + ROWS_STEP);
                            madeShots.add(luckyShot + ROWS_STEP - COLUMNS_STEP);
                        }
                    }
                } else {
                    if (luckyShot / ROWS_STEP == 0) {
                        // 1 2 3 4 5 6 7 8
                        madeShots.add(luckyShot - COLUMNS_STEP);
                        madeShots.add(luckyShot + ROWS_STEP - COLUMNS_STEP);
                        madeShots.add(luckyShot + ROWS_STEP);
                        madeShots.add(luckyShot + ROWS_STEP + COLUMNS_STEP);
                        madeShots.add(luckyShot + COLUMNS_STEP);
                    } else {
                        if (luckyShot / ROWS_STEP == 9) {
                            // 91 92 93 94 95 96 97 98
                            madeShots.add(luckyShot - COLUMNS_STEP);
                            madeShots.add(luckyShot - ROWS_STEP - COLUMNS_STEP);
                            madeShots.add(luckyShot - ROWS_STEP);
                            madeShots.add(luckyShot - ROWS_STEP + COLUMNS_STEP);
                            madeShots.add(luckyShot + COLUMNS_STEP);
                        } else {
                            // все остальные 11 12 .. 18
                            //               21 22 .. 28
                            //               81 82 .. 88
                            madeShots.add(luckyShot - ROWS_STEP);
                            madeShots.add(luckyShot - ROWS_STEP + COLUMNS_STEP);
                            madeShots.add(luckyShot + COLUMNS_STEP);
                            madeShots.add(luckyShot + ROWS_STEP + COLUMNS_STEP);
                            madeShots.add(luckyShot + ROWS_STEP);
                            madeShots.add(luckyShot + ROWS_STEP - COLUMNS_STEP);
                            madeShots.add(luckyShot - COLUMNS_STEP);
                            madeShots.add(luckyShot - ROWS_STEP - COLUMNS_STEP);
                        }
                    }
                }
            }
        }
    }
}
