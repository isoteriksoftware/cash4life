package com.isoterik.cash4life.cashpuzzles.components;

import com.badlogic.gdx.utils.Array;
import com.isoterik.cash4life.DemoStorage;
import com.isoterik.cash4life.cashpuzzles.CashPuzzlesSplash;
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

    private static int levelTime;

    private Board board;

    private Level[] levels;
    private Level currentLevel;

    private int noOfWordsToFind = 7;
    private int maxLevels = 1;
    private int currentLevelIndex = 0;

    private final float REWARD_AMOUNT = 10000;

    private void singleton() {
        instance = this;
    }

    @Override
    public void start() {
        singleton();

        setLevels();

        maxLevels = levels.length;

        if (DemoStorage.IS_GAME_SAVED) {
            currentLevelIndex = DemoStorage.SAVED_GAME.stageStoppedAt;
            currentLevel = DemoStorage.SAVED_GAME.level;
        }
        else {
            currentLevel = levels[currentLevelIndex];
        }

        initLevel();
    }

    private void setLevels() {
        levels = new Level[]{
                new Level("Stones", Difficulty.EASY),
                new Level("Planets", Difficulty.EASY),
                new Level("Countries", Difficulty.MEDIUM),
                new Level("Automobiles", Difficulty.MEDIUM),
                new Level("Automobiles", Difficulty.MEDIUM),
                new Level("Nigerian States", Difficulty.HARD),
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

        ArrayList<String> words = WordManager.getInstance().getWords();
        if (DemoStorage.IS_GAME_SAVED) {
            ArrayList<String> remainingWords = new ArrayList<>();
            ArrayList<String> foundWords = DemoStorage.SAVED_GAME.foundWords;
            for (String word : words) {
                if (! foundWords.contains(word))
                    remainingWords.add(word);
            }
            UIManager.getInstance().fillWordsTable(remainingWords);
        }
        else
            UIManager.getInstance().fillWordsTable(words);

        int[] boardDimensions = currentLevel.dimensions;
        int boardRow = boardDimensions[0], boardColumn = boardDimensions[1];
        board = new Board(boardRow, boardColumn, words);
    }

    public void cont() {
        int level = currentLevelIndex + 1;
        String currentLevelText = currentLevelIndex == 9 ? String.valueOf(level) : "0" + level;
        String maxLevelsText = maxLevels >= 10 ? String.valueOf(maxLevels) : "0" + maxLevels;
        UIManager.getInstance().setLevelText(currentLevelText + "/" + maxLevelsText);
        UIManager.getInstance().setCategoryText(currentLevel.categoryName);
        LetterManager.getInstance().initLevel();

        if (DemoStorage.IS_GAME_SAVED) {
            UIManager.getInstance().setLevelTime(DemoStorage.SAVED_GAME.timeSavedAt);
            WordManager.getInstance().setFoundWords(DemoStorage.SAVED_GAME.foundWords);
            LetterManager.getInstance().shuffleCells();
        }
        else {
            if (currentLevelIndex == 0)
                UIManager.getInstance().setLevelTime(levelTime);
        }
    }

    protected void currentLevelFinished() {
        LetterManager.getInstance().getValidCells().clear();
        LetterManager.getInstance().destroyLetters();
        destroyAllSelectors();

        ++currentLevelIndex;
        if (currentLevelIndex < maxLevels) {
            currentLevel = levels[currentLevelIndex];
            initLevel();
            cont();
        }
        else {
            gameWon();
        }
    }

    public void saveGame() {
        DemoStorage.IS_GAME_SAVED = true;
        DemoStorage.SAVED_GAME = new DemoStorage.SavedGame(
                currentLevel,
                WordManager.getInstance().getFoundWords(),
                currentLevelIndex,
                UIManager.getInstance().getTimeInMins()
        );
    }

    private void gameWon() {
        CashPuzzlesSplash.user.deposit(REWARD_AMOUNT);
        UIManager.getInstance().gameFinished();
    }

    public void gameOver() {
        UIManager.getInstance().gameLost();
    }

    public void reInitializeBoard() {
        ArrayList<String> words = WordManager.getInstance().getWords();
        board = new Board(board.getRows(), board.getColumns(), words);
    }

    protected Board getBoard() {
        return board;
    }

    public int getNoOfWordsToFind() {
        return noOfWordsToFind;
    }

    void destroyAllSelectors() {
        Array<GameObject> selectors = scene.findGameObjects("selector");
        for (GameObject selector : selectors) {
            removeGameObject(selector);
        }
    }

    public static void setLevelTime(int time) {
        levelTime = time;
    }

    public static float getLevelTime() {
        return levelTime;
    }

    public static class Level {
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
            fileName = categoryName.replaceAll("\\s", "").concat(".txt");
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
