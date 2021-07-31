package com.isoterik.cash4life.cashpuzzles.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.isoterik.cash4life.cashpuzzles.components.managers.*;
import com.isoterik.cash4life.cashpuzzles.utils.Cell;
import com.isoterik.cash4life.cashpuzzles.utils.Board;
import io.github.isoteriktech.xgdx.Component;
import io.github.isoteriktech.xgdx.GameObject;
import io.github.isoteriktech.xgdx.Transform;
import io.github.isoteriktech.xgdx.x2d.components.renderer.SpriteRenderer;

import java.util.ArrayList;
import java.util.Random;

public class SelectorComponent extends Component {
    private WordManager wordManager;
    private UIManager uiManager;
    private LetterManager letterManager;
    private GameManager gameManager;

    private Transform transform;
    private SpriteRenderer spriteRenderer;

    private ArrayList<String> loadedWords;
    private Board board;
    private Array<Cell> selectedWords;
    private GameObject[][] letters;

    private Vector2 initialSize;
    private Vector2 firstTouchPosition;
    private Vector2 mousePos;
    private Vector2 stopDragPosition;
    private Vector2 magnitudePosition;

    private boolean flag = true;
    private boolean doOnce;

    private float keepAngle;
    private float size;

    public void setLoadedWords(ArrayList<String> loadedWords) {
        this.loadedWords = loadedWords;
    }

    private void initializeManagers() {
        wordManager = gameObject.getHostScene().findGameObject("wordManager").getComponent(WordManager.class);
        uiManager = gameObject.getHostScene().findGameObject("uiManager").getComponent(UIManager.class);
        letterManager = gameObject.getHostScene().findGameObject("letterManager").getComponent(LetterManager.class);
        gameManager = gameObject.getHostScene().findGameObject("gameManager").getComponent(GameManager.class);
    }

    @Override
    public void start() {
        initializeManagers();

        transform = gameObject.transform;
        spriteRenderer = gameObject.getComponent(SpriteRenderer.class);

        initialSize = new Vector2(
                transform.getWidth(),
                transform.getHeight()
        );
        firstTouchPosition = Vector2.Zero;

        spriteRenderer.setColor(new Color(1,1,1,0.5f));
        spriteRenderer.setVisible(false);

        board = gameManager.getBoard();
        letters = letterManager.getLetters();
        selectedWords = new Array<>();
        transform.setOrigin(transform.getWidth() / 2, transform.getHeight() / 2);
    }

    @Override
    public void update(float deltaTime) {
        if (input.isTouched()) {
            spriteRenderer.setVisible(true);
            mousePos = new Vector2(input.getTouchedX(), input.getTouchedY());

            if (flag) {
                firstTouchPosition = new Vector2(
                        mousePos.x,
                        mousePos.y
                );
                magnitudePosition = new Vector2(
                        firstTouchPosition.x,
                        firstTouchPosition.y
                );
                flag = false;
            }

            float angle = 0f;
            Vector2 magnitudePos = Vector2.Zero;
            // 2nd quadrant
            if (firstTouchPosition.x >= mousePos.x && firstTouchPosition.y <= mousePos.y) {
                float xDelta = firstTouchPosition.x - mousePos.x;
                float yDelta = -firstTouchPosition.y + mousePos.y;
                float div = yDelta / xDelta;
                angle = 90 - (float) Math.toDegrees(Math.atan(div));

                magnitudePos = magnitudePosition;
            }
            // 3rd quadrant
            else if (firstTouchPosition.x >= mousePos.x && firstTouchPosition.y >= mousePos.y) {
                float xDelta = firstTouchPosition.x - mousePos.x;
                float yDelta = firstTouchPosition.y - mousePos.y;
                float div = yDelta / xDelta;
                angle = 90 + (float) Math.toDegrees(Math.atan(div));

                magnitudePos = magnitudePosition;
            }
            // 4th quadrant
            else if (firstTouchPosition.x <= mousePos.x && firstTouchPosition.y >= mousePos.y) {
                float xDelta = -firstTouchPosition.x + mousePos.x;
                float yDelta = firstTouchPosition.y - mousePos.y;
                float div = yDelta / xDelta;
                angle = 270 - (float) Math.toDegrees(Math.atan(div));

                magnitudePos = magnitudePosition;
            }
            // 1st quadrant
            else if (firstTouchPosition.x <= mousePos.x && firstTouchPosition.y <= mousePos.y) {
                float xDelta = -firstTouchPosition.x + mousePos.x;
                float yDelta = -firstTouchPosition.y + mousePos.y;
                float div = yDelta / xDelta;
                angle = 270 + (float) Math.toDegrees(Math.atan(div));

                magnitudePos = magnitudePosition;
            }

            transform.setRotation(angle);

            float magnitude = mousePos.dst(magnitudePos);
            transform.setSize(transform.getWidth(), Math.max(magnitude, initialSize.y) + initialSize.y / 2);

            doOnce = true;
        }

        if (! input.isTouched()) {
            flag = true;

            if (doOnce) {
                checkValid();
                doOnce = false;
            }
        }
    }

    public void setSize(float size) {
        this.size = size;
    }

    private boolean withinBounds(Vector2 target, Vector2 primary) {
        //float size = 0.3f;
        boolean xBounded = target.x >= primary.x && target.x <= primary.x + size;
        boolean yBounded = target.y >= primary.y && target.y <= primary.y + size;

        return xBounded && yBounded;
    }

    private void checkValid() {
        Cell start = null, stop = null;
        Cell[][] cells = board.getCells();
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                Cell cell = cells[i][j];
                Vector2 cellPosition = cell.getPosition();
                if (withinBounds(firstTouchPosition, cellPosition))
                    start = cell;
                if (withinBounds(mousePos, cellPosition))
                    stop = cell;
            }
        }

        if (start == null || stop == null) {
            notValid();
        }
        else {
            valid(start, stop);
        }
    }

    private void notValid() {
        //removeComponent(SelectorController.class);
        removeGameObject(gameObject);
    }

    private void validate(String selection, float angle) {
        if (loadedWords.contains(selection) && !wordManager.getFoundWords().contains(selection)) {
            keepAngle = angle;
            wordManager.getFoundWords().add(selection);
            keep();

            boolean b1 = wordManager.getFoundWords().size() == loadedWords.size();
            boolean b2 = wordManager.getFoundWords().size() == gameManager.getNoOfWordsToFind();
            if (b1 || b2) {
                gameManager.currentLevelFinished();
            }
        }
        else {
            notValid();
        }
    }

    private void valid(Cell start, Cell stop) {
        Cell[][] cells = board.getCells();

        int startRow = start.getRow(), startColumn = start.getColumn();
        int stopRow = stop.getRow(), stopColumn = stop.getColumn();

        stopDragPosition = stop.getPosition();
        selectedWords.clear();

        // Vertical up
        if (startColumn == stopColumn && startRow > stopRow) {
            StringBuilder selection = new StringBuilder();
            int currPosX = startRow;
            while (currPosX >= stopRow) {
                Cell cell = cells[currPosX][startColumn];
                selectedWords.add(cell);
                char letter = cell.getLetter();
                selection.append(letter);
                --currPosX;
            }

            validate(selection.toString(), 0f);
        }
        // Vertical down
        else if (startColumn == stopColumn && startRow < stopRow) {
            StringBuilder selection = new StringBuilder();
            int currPosX = startRow;
            while (currPosX <= stopRow) {
                Cell cell = cells[currPosX][startColumn];
                selectedWords.add(cell);
                char letter = cell.getLetter();
                selection.append(letter);
                ++currPosX;
            }

            validate(selection.toString(), 180f);
        }
        // Horizontal right
        else if (startRow == stopRow && startColumn < stopColumn) {
            StringBuilder selection = new StringBuilder();
            int currPosY = startColumn;
            while (currPosY <= stopColumn) {
                Cell cell = cells[startRow][currPosY];
                selectedWords.add(cell);
                char letter = cell.getLetter();
                selection.append(letter);
                ++currPosY;
            }

            validate(selection.toString(), 270f);
        }
        // Horizontal left
        else if (startRow == stopRow && startColumn > stopColumn) {
            StringBuilder selection = new StringBuilder();
            int currPosY = startColumn;
            while (currPosY >= stopColumn) {
                Cell cell = cells[startRow][currPosY];
                selectedWords.add(cell);
                char letter = cell.getLetter();
                selection.append(letter);
                --currPosY;
            }

            validate(selection.toString(), 90f);
        }
        else {
            boolean b = Math.abs(startRow - stopRow) == Math.abs(startColumn - stopColumn);
            // Diagonal up /
            if (startRow > stopRow && startColumn < stopColumn) {
                if (b) {
                    StringBuilder selection = new StringBuilder();
                    int currPosX = startRow, currPosY = startColumn;
                    while (currPosX >= stopRow && currPosY <= stopColumn) {
                        Cell cell = cells[currPosX][currPosY];
                        selectedWords.add(cell);
                        char letter = cell.getLetter();
                        selection.append(letter);
                        --currPosX; ++currPosY;
                    }

                    validate(selection.toString(), 315f);
                }
                else {
                    notValid();
                }
            }
            // Diagonal down /
            else if (startRow < stopRow && startColumn > stopColumn) {
                if (b) {
                    StringBuilder selection = new StringBuilder();
                    int currPosX = startRow, currPosY = startColumn;
                    while (currPosX <= stopRow && currPosY >= stopColumn) {
                        Cell cell = cells[currPosX][currPosY];
                        selectedWords.add(cell);
                        char letter = cell.getLetter();
                        selection.append(letter);
                        ++currPosX; --currPosY;
                    }

                    validate(selection.toString(), 135f);
                }
                else {
                    notValid();
                }
            }
            // Diagonal up \
            else if (startRow > stopRow) {
                if (b) {
                    StringBuilder selection = new StringBuilder();
                    int currPosX = startRow, currPosY = startColumn;
                    while (currPosX >= stopRow && currPosY >= stopColumn) {
                        Cell cell = cells[currPosX][currPosY];
                        selectedWords.add(cell);
                        char letter = cell.getLetter();
                        selection.append(letter);
                        --currPosX; --currPosY;
                    }

                    validate(selection.toString(), 45f);
                }
                else {
                    notValid();
                }
            }
            // Diagonal down \
            else if (startRow < stopRow) {
                if (b) {
                    StringBuilder selection = new StringBuilder();
                    int currPosX = startRow, currPosY = startColumn;
                    while (currPosX <= stopRow && currPosY <= stopColumn) {
                        Cell cell = cells[currPosX][currPosY];
                        selectedWords.add(cell);
                        char letter = cell.getLetter();
                        selection.append(letter);
                        ++currPosX; ++currPosY;
                    }

                    validate(selection.toString(), 225f);
                }
                else {
                    notValid();
                }
            }
            else {
                notValid();
            }
        }
    }

    private Color getRandomColor() {
        float opacity = 0.5f;
        Color[] colors = {
                new Color(1.0f, 0.0f, 0.0f, opacity),
                new Color(0.0f, 1.0f, 0.0f, opacity),
                new Color(1.0f, 0.0f, 1.0f, opacity),
                new Color(1.0f, 1.0f, 0.0f, opacity),
                new Color(1.0f, 0.0f, 1.0f, opacity),
                new Color(0.0f, 1.0f, 1.0f, opacity)
        };

        return colors[new Random().nextInt(colors.length)];
    }

    public void keep() {
        for (Cell cell : selectedWords) {
            int row = cell.getRow(), column = cell.getColumn();
            GameObject letterGameObject = letters[row][column];
            LetterComponent letterComponent = letterGameObject.getComponent(LetterComponent.class);
            SpriteRenderer letterSr = letterGameObject.getComponent(SpriteRenderer.class);
            letterSr.setSprite(letterComponent.getFoundedSprite());
            letterGameObject.transform.setSize(size, size);
        }
        removeGameObject(gameObject);
        float magnitude = firstTouchPosition.dst(stopDragPosition);
        //transform.setSize(transform.getWidth(), magnitude);
        transform.setRotation(keepAngle);
        getComponent(SpriteRenderer.class).setColor(getRandomColor());
        removeComponent(SelectorComponent.class);
    }
}
