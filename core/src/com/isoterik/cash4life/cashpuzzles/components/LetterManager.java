package com.isoterik.cash4life.cashpuzzles.components;

import com.badlogic.gdx.math.Vector2;
import com.isoterik.cash4life.cashpuzzles.Cell;
import com.isoterik.cash4life.cashpuzzles.Letter;
import com.isoterik.cash4life.cashpuzzles.WordManager;
import com.isoterik.cash4life.cashpuzzles.utils.Board;
import io.github.isoteriktech.xgdx.Component;
import io.github.isoteriktech.xgdx.GameObject;
import io.github.isoteriktech.xgdx.Transform;

public class LetterManager extends Component {
    private static LetterManager instance;

    public static LetterManager getInstance() {
        return instance;
    }

    private GameObject[][] letters;

    private float size;

    private void singleton() {
        instance = this;
    }

    @Override
    public void start() {
        singleton();
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

        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < column; ++j) {
                char letter = cells[i][j].getLetter();
                GameObject letterGameObject = scene.newSpriteObject(new Letter(letter).getTextureRegion());
                letters[i][j] = letterGameObject;

                Transform letterTransform = letterGameObject.transform;
                letterTransform.setSize(size, size);
                Vector2 position = new Vector2(
                        xOffset + (xOffset * j) + (size * j),
                        yW - yOffset - (yOffset * i) - (size * i)
                );
                letterTransform.setPosition(position.x, position.y);
                cells[i][j].setPosition(position);

                LetterComponent letterComponent = new LetterComponent();
                letterGameObject.addComponent(letterComponent);
                addGameObject(letterGameObject);
            }
        }
    }

    protected void destroyLetters() {
        for (int i = 0; i < letters.length; i++) {
            for (int j = 0; j < letters[0].length; j++) {
                GameObject letter = letters[i][j];
                removeGameObject(letter);
            }
        }
    }
}
