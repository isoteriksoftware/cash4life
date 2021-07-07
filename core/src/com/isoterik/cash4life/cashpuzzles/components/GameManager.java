package com.isoterik.cash4life.cashpuzzles.components;

import com.badlogic.gdx.utils.Array;
import com.isoterik.cash4life.cashpuzzles.GamePlayScene;
import com.isoterik.cash4life.cashpuzzles.WordManager;
import com.isoterik.cash4life.cashpuzzles.utils.Board;
import io.github.isoteriktech.xgdx.Component;
import io.github.isoteriktech.xgdx.GameObject;

import java.util.ArrayList;
import java.util.Locale;

public class GameManager extends Component {
    private static GameManager instance;

    public static GameManager getInstance() {
        return instance;
    }

    enum Difficulty {
        EASY,
        MEDIUM,
        HARD,
        INSANE,
    }

    private Board board;

    private ArrayList<String> words;

    private Level[] levels;
    private Level currentLevel;

    private final int MAX_LEVELS = 10;
    private int currentLevelIndex = 0;

    private void singleton() {
        if (instance != null)
        {
            removeGameObject(gameObject);
        }
        else
        {
            instance = this;
        }
    }

    @Override
    public void start() {
        singleton();

        setLevels();

        currentLevel = levels[currentLevelIndex];

        initLevel();
    }

    private void setLevels() {
        levels = new Level[]{
                new Level("States", Difficulty.EASY),
                new Level("States", Difficulty.EASY),
                new Level("Automobiles", Difficulty.MEDIUM),
                new Level("Automobiles", Difficulty.MEDIUM),
                new Level("Automobiles", Difficulty.MEDIUM),
                new Level("Programming Languages", Difficulty.HARD),
                new Level("Programming Languages", Difficulty.HARD),
                new Level("Programming Languages", Difficulty.HARD),
                new Level("Chemical Elements", Difficulty.INSANE),
                new Level("Chemical Elements", Difficulty.INSANE),
        };
    }

    private void initLevel() {
        String wordsFileLocation = "words";
        String fileName = currentLevel.fileName;
        WordManager.getInstance().loadConfigFile(wordsFileLocation, fileName);

        words = WordManager.getInstance().getWords();
        int[] boardDimensions = currentLevel.dimensions;
        int boardRow = boardDimensions[0], boardColumn = boardDimensions[1];
        board = new Board(boardRow, boardColumn, words);
    }

    public void cont() {
        int level = currentLevelIndex + 1;
        String levelText = currentLevelIndex == 9 ? String.valueOf(level) : "0" + level;
        GamePlayScene.setLevelText(levelText + "/" + MAX_LEVELS);
        GamePlayScene.setCategoryText(currentLevel.categoryName);
        LetterManager.getInstance().initLevel();
    }

    public void currentLevelFinished() {
        LetterManager.getInstance().destroyLetters();
        destroyAllSelectors();

        if (currentLevelIndex < MAX_LEVELS) {
            currentLevel = levels[++currentLevelIndex];
            initLevel();
            cont();
        }
    }

    public void gameOver() {
    }

    protected Board getBoard() {
        return board;
    }

    private void destroyAllSelectors() {
        Array<GameObject> selectors = scene.findGameObjects("selector");
        for (GameObject selector : selectors) {
            removeGameObject(selector);
        }
    }

    class Level {
        private final String categoryName;
        private final Difficulty difficulty;

        private String fileName;
        private int[] dimensions;

        public Level(String categoryName, Difficulty difficulty) {
            this.categoryName = categoryName;
            this.difficulty = difficulty;

            init();
        }

        public Level(String categoryName) {
            this(categoryName, Difficulty.MEDIUM);
        }

        private void init() {
            fileName = categoryName.replaceAll("\\s", "").toLowerCase(Locale.ROOT).concat(".txt");
            dimensions = setDimensions();
        }

        private int[] setDimensions() {
            switch (difficulty) {
                case EASY:
                    return new int[]{8, 8};
                case MEDIUM:
                    return new int[]{10, 10};
                case HARD:
                    return new int[]{12, 12};
                case INSANE:
                    return new int[]{14, 14};
            }

            return new int[]{10, 10};
        }

        public String getCategoryName() {
            return categoryName;
        }
    }
}
