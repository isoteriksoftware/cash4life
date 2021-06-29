package com.isoterik.cash4life.cashpuzzles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.isoterik.cash4life.cashpuzzles.components.LetterComponent;
import com.isoterik.cash4life.cashpuzzles.components.Properties;
import com.isoterik.cash4life.cashpuzzles.utils.Board;
import io.github.isoteriktech.xgdx.GameObject;
import io.github.isoteriktech.xgdx.Scene;
import io.github.isoteriktech.xgdx.Transform;
import io.github.isoteriktech.xgdx.utils.GameWorldUnits;

import java.util.ArrayList;

public class GameScene extends Scene {
    private Board board;

    private ArrayList<String> words;

    public GameScene() {
        setBackgroundColor(new Color(0.1f, 0.1f, 0.2f, 1.0f));

        gameWorldUnits = new GameWorldUnits(CashPuzzlesSplash.RESOLUTION_WIDTH, CashPuzzlesSplash.RESOLUTION_HEIGHT,
                100f);
        getMainCamera().setup(
                new StretchViewport(gameWorldUnits.getWorldWidth(), gameWorldUnits.getWorldHeight()
                        )
        );

        Start();
    }

    private void Start() {
        String wordsFileLocation = "SecretFiles";
        String fileName = "ProgrammingLanguages.txt";
        WordManager.getInstance().loadConfigFile(wordsFileLocation, fileName);
        words = WordManager.getInstance().getWords();

        GameObject background = newSpriteObject(GameAssetsManager.getInstance().getBackground());
        Transform backgroundTransform = background.transform;
        backgroundTransform.setSize(gameWorldUnits.getWorldWidth(), gameWorldUnits.getWorldHeight());
        addGameObject(background);

        int boardRow = 10, boardColumn = 10;
        board = new Board(boardRow, boardColumn, words);
        Cell[][] cells = board.getCells();
        Properties.board = board;
        drawLetters(cells, board.getRows(), board.getColumns());
    }

    private void drawLetters(Cell[][] cells, int row, int column) {
        GameObject[][] letters = new GameObject[row][column];

        float size = 0.3f;
        float xW = gameWorldUnits.getWorldWidth(), xM = row * size, xK = row + 1;
        float xOffset = (xW - xM) / xK;

        float yW = gameWorldUnits.getWorldWidth(), yM = column * size, yK = column + 1;
        float yOffset = (yW - yM) / yK;

        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < column; ++j) {
                char letter = cells[i][j].getLetter();
                GameObject letterGameObject = newSpriteObject(new Letter(letter).getTextureRegion());
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
                letterComponent.setLetter(letter);
                letterGameObject.addComponent(letterComponent);
                addGameObject(letterGameObject);
            }
        }

        //return letters;
    }

    private void centerObject(Transform objectTransform) {
        objectTransform.setPosition(
                (gameWorldUnits.getWorldWidth() - objectTransform.getWidth()) / 2f,
                (gameWorldUnits.getWorldHeight() - objectTransform.getHeight()) / 2f
        );
    }
}
