package com.isoterik.cash4life.cashpuzzles.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.isoterik.cash4life.cashpuzzles.Cell;
import com.isoterik.cash4life.cashpuzzles.WordManager;
import com.isoterik.cash4life.cashpuzzles.components.LetterManager;

import java.util.ArrayList;

public class Board {
    private final int rows;
    private final int columns;
    private final Cell[][] cells;

    public Board(int rows, int columns, ArrayList<String> words) {
        this.rows = rows;
        this.columns = columns;
        this.cells = new Cell[rows][columns];
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                cells[i][j] = new Cell(i,j);
            }
        }
        generatePuzzle(words);
    }

    public void generatePuzzle(ArrayList<String> words){
        //sort words by descending length
        ArrayList<String> wordsInOrder = new ArrayList<>();
        while(!words.isEmpty()) {
            int index = 0;
            for(int i = 0; i < words.size(); i++) {
                if(words.get(i).length() > words.get(index).length()) {
                    index = i;
                }
            }
            wordsInOrder.add(words.get(index));
            words.remove(index);
        }

        //insert each word in order
        for(String s : wordsInOrder)
            fillCell(s);

        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        char letter;
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                if(getCellAt(i,j).getLetter() == ' ') {
                    letter = alphabet.charAt(MathUtils.random(alphabet.length() - 1));
                    getCellAt(i,j).setLetter(letter);
                }
            }
        }
    }

    public void fillCell(String word) {
        Vector2[] directions = new Vector2[] {
                new Vector2(1, 0), new Vector2(0, 1), new Vector2(-1, 0),
                new Vector2(0, -1), new Vector2(1, -1), new Vector2(-1, 1),
                new Vector2(1, 1), new Vector2(-1, -1)
        };

        boolean fitted = false;
        for (int i = 0; i < rows * columns; i++) {
            int randX = MathUtils.random(rows - 1), randY = MathUtils.random(columns - 1);
            Vector2 index = new Vector2(randX, randY);
            for (int j = 0; j < directions.length; j++) {
                Vector2 direction = directions[MathUtils.random(directions.length - 1)];
                if (isFit(word, index, direction)) {
                    Cell[] cells = new Cell[word.length()]; int count = 0;
                    for (char c : word.toCharArray()) {
                        try {
                            getCellAt(randX, randY).setLetter(c);
                            cells[count++] = getCellAt(randX, randY);
                            randX += direction.x;
                            randY += direction.y;
                        }
                        catch (IndexOutOfBoundsException e) {
                            continue;
                        }
                    }
                    fitted = true;
                    WordManager.getInstance().addLoadedWord(word);
                    LetterManager.getInstance().addValidCell(cells);
                }
                if (fitted) break;
            }
            if (fitted) break;
        }
    }

    public boolean isFit(String word, Vector2 index, Vector2 direction) {
        try {
            char[] arr = word.toCharArray();
            for (char c : arr) {
                if (index.x < 0 || index.y < 0)
                    throw new IndexOutOfBoundsException();
                char letter = cells[(int) index.x][(int) index.y].getLetter();
                if (letter != ' ' && letter != c)   return false;
                index.x += direction.x;
                index.y += direction.y;
            }
        }
        catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public Cell getCellAt(int row, int column) {
        return cells[row][column];
    }
}
