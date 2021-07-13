package com.isoterik.cash4life.cashpuzzles.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.isoterik.cash4life.GlobalConstants;
import com.isoterik.cash4life.cashpuzzles.utils.Cell;
import com.isoterik.cash4life.cashpuzzles.utils.Letter;
import com.isoterik.cash4life.cashpuzzles.WordManager;
import com.isoterik.cash4life.cashpuzzles.utils.Board;
import io.github.isoteriktech.xgdx.Component;
import io.github.isoteriktech.xgdx.GameObject;
import io.github.isoteriktech.xgdx.Transform;
import io.github.isoteriktech.xgdx.XGdx;
import io.github.isoteriktech.xgdx.x2d.components.renderer.SpriteRenderer;

import java.util.ArrayList;

public class LetterManager extends Component {
    private static LetterManager instance;

    public static LetterManager getInstance() {
        return instance;
    }

    private GameObject[][] letters;
    private Array<Cell[]> validCells;

    private GameObject hintHighlighter;

    private final Color on = new Color(1, 1, 1, 0.5f);
    private final Color off = new Color(0, 0, 0, 0);

    private float size;
    private float hintTime = 6;

    private boolean hintTaskRunning;

    private void singleton() {
        instance = this;
    }

    @Override
    public void start() {
        singleton();
        validCells = new Array<>();
    }

    protected void initLevel() {
        Board board = GameManager.getInstance().getBoard();
        Cell[][] cells = board.getCells();
        drawLetters(cells, board.getRows(), board.getColumns());
    }

    public float getSize() {
        return size;
    }

    private void drawLetters(Cell[][] cells, int row, int column) {
        letters = new GameObject[row][column];

        float baseSize = 0.3f;
        size = (10 * baseSize) / row;

        float xW = scene.getGameWorldUnits().getWorldWidth(), xM = row * size, xK = row + 1;
        float xOffset = (xW - xM) / xK;

        float yW = scene.getGameWorldUnits().getWorldWidth(), yM = column * size, yK = column + 1;
        float yOffset = (yW - yM) / yK;

        float yPositionOffset = size * 2;

        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < column; ++j) {
                char letter = cells[i][j].getLetter();
                Letter letter1 = new Letter(letter);
                GameObject letterGameObject = scene.newSpriteObject(letter1.getTextureRegion());
                letters[i][j] = letterGameObject;

                Transform letterTransform = letterGameObject.transform;
                letterTransform.setSize(size, size);
                Vector2 position = new Vector2(
                        xOffset + (xOffset * j) + (size * j),
                        yW - yOffset - (yOffset * i) - (size * i)
                );
                letterTransform.setPosition(position.x, position.y + yPositionOffset);
                cells[i][j].setPosition(position.x, position.y + yPositionOffset);

                LetterComponent letterComponent = new LetterComponent();
                letterComponent.setFoundedSprite(letter1.getFoundedSprite());
                letterGameObject.addComponent(letterComponent);
                addGameObject(letterGameObject);
            }
        }
    }

    private final Timer.Task myTimerTask = new Timer.Task() {
        @Override
        public void run() {
            SpriteRenderer highlighterSr = hintHighlighter.getComponent(SpriteRenderer.class);

            --hintTime;
            if (hintTime >= 0) {
                if (hintTime % 2 == 0) {
                    highlighterSr.setColor(on);
                }
                else {
                    highlighterSr.setColor(off);
                }
            }
            else {
                stopMyTimerTask();
            }
        }
    };

    private void stopMyTimerTask() {
        hintTime = 6;
        hintTaskRunning = false;
        removeGameObject(hintHighlighter);
        myTimerTask.cancel();
    }

    public void showHint() {
        if (hintTaskRunning) stopMyTimerTask();

        Cell[] validCell = validCells.get(MathUtils.random(validCells.size - 1));
        if (WordManager.getInstance().getFoundWords().contains(getWordFromCell(validCell))) {
            showHint();
            return;
        }
        Cell startCell = validCell[0], stopCell = validCell[validCell.length - 1];
        Vector2 startPosition = startCell.getPosition(), stopPosition = stopCell.getPosition();

        hintHighlighter = scene.newSpriteObject(XGdx.instance().assets.getTexture(
                GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/images/square.png")
        );

        hintHighlighter.transform.setPosition(startPosition.x, startPosition.y);
        hintHighlighter.transform.setOrigin(size / 2, size / 2);

        float magnitude = Math.abs(startPosition.dst(stopPosition));
        hintHighlighter.transform.setSize(size, magnitude + size);

        float angle = getAngle(startPosition, stopPosition);
        hintHighlighter.transform.setRotation(angle);

        hintHighlighter.getComponent(SpriteRenderer.class).setColor(on);
        addGameObject(hintHighlighter);

        Timer.schedule(myTimerTask, 0.3f, 0.3f);
        hintTaskRunning = true;
    }

    private String getWordFromCell(Cell[] cell) {
        StringBuilder result = new StringBuilder();
        for (Cell c : cell) {
            result.append(c.getLetter());
        }

        return result.toString();
    }

    private float getAngle(Vector2 startPosition, Vector2 stopPosition) {
        float angle = 0f;
        Vector2 magnitudePos = Vector2.Zero;
        // 2nd quadrant
        if (startPosition.x >= stopPosition.x && startPosition.y <= stopPosition.y) {
            float xDelta = startPosition.x - stopPosition.x;
            float yDelta = -startPosition.y + stopPosition.y;
            float div = yDelta / xDelta;
            angle = 90 - (float) Math.toDegrees(Math.atan(div));
        }
        // 3rd quadrant
        else if (startPosition.x >= stopPosition.x && startPosition.y >= stopPosition.y) {
            float xDelta = startPosition.x - stopPosition.x;
            float yDelta = startPosition.y - stopPosition.y;
            float div = yDelta / xDelta;
            angle = 90 + (float) Math.toDegrees(Math.atan(div));
        }
        // 4th quadrant
        else if (startPosition.x <= stopPosition.x && startPosition.y >= stopPosition.y) {
            float xDelta = -startPosition.x + stopPosition.x;
            float yDelta = startPosition.y - stopPosition.y;
            float div = yDelta / xDelta;
            angle = 270 - (float) Math.toDegrees(Math.atan(div));
        }
        // 1st quadrant
        else if (startPosition.x <= stopPosition.x && startPosition.y <= stopPosition.y) {
            float xDelta = -startPosition.x + stopPosition.x;
            float yDelta = -startPosition.y + stopPosition.y;
            float div = yDelta / xDelta;
            angle = 270 + (float) Math.toDegrees(Math.atan(div));
        }

        return angle;
    }

    protected GameObject[][] getLetters() {
        return letters;
    }

    public void addValidCell(Cell[] cell) {
        validCells.add(cell);
    }

    public Array<Cell[]> getValidCells() {
        return validCells;
    }

    public void destroyLetters() {
        for (GameObject[] gameObjects : letters) {
            for (int j = 0; j < letters[0].length; j++) {
                GameObject letter = gameObjects[j];
                removeGameObject(letter);
            }
        }
    }

    public Timer.Task animatedCellsShuffle = new Timer.Task() {
        private int maxShuffles = 5;

        @Override
        public void run() {
            if (maxShuffles > 0) {
                destroyLetters();
                validCells.clear();

                GameManager.getInstance().destroyAllSelectors();
                GameManager.getInstance().reInitializeBoard();

                initLevel();

                maxShuffles--;
            }
            else {
                maxShuffles = 5;
                putSelectors();
                cancel();
            }
        }
    };

    public void shuffleCells() {
        if (animatedCellsShuffle.isScheduled()) return;

        Timer.schedule(animatedCellsShuffle, 0.1f, 0.1f);
    }

    private GameObject getGameObjectFromCell(Cell cell) {
        return letters[cell.getRow()][cell.getColumn()];
    }

    private void putSelectors() {
        ArrayList<String> foundWords = WordManager.getInstance().getFoundWords();

        for (Cell[] cellArray : validCells) {
            if (! foundWords.contains(getWordFromCell(cellArray))) continue;

            for (Cell cell : cellArray) {
                GameObject letterGameObject = getGameObjectFromCell(cell);
                LetterComponent letterComponent = letterGameObject.getComponent(LetterComponent.class);
                SpriteRenderer letterSr = letterGameObject.getComponent(SpriteRenderer.class);
                letterSr.setSprite(letterComponent.getFoundedSprite());
                letterGameObject.transform.setSize(size, size);
            }
        }
    }
}
