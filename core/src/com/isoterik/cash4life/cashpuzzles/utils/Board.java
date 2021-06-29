package com.isoterik.cash4life.cashpuzzles.utils;

import com.badlogic.gdx.math.Vector2;
import com.isoterik.cash4life.cashpuzzles.Cell;
import com.isoterik.cash4life.cashpuzzles.Point;
import com.isoterik.cash4life.cashpuzzles.WordManager;

import java.util.ArrayList;
import java.util.Random;

public class Board {
    private int rows;
    private int columns;
    private Cell[][] cells;

    private ArrayList<String> wordsList;

    public Board(int rows, int columns, ArrayList<String> words) {
        this.rows = rows;
        this.columns = columns;
        this.cells = new Cell[rows][columns];
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                cells[i][j] = new Cell(i,j);
            }
        }
        wordsList = new ArrayList<>(WordManager.getInstance().getWords());
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

        Random random = new Random();
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        char letter = ' ';
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                if(getCellAt(i,j).getLetter() == ' ') {
                    letter = alphabet.charAt(random.nextInt(alphabet.length()));
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
        Random random = new Random();
        boolean fitted = false;
        for (int i = 0; i < rows * columns; i++) {
            int randX = (int) (Math.random() * rows), randY = (int) (Math.random() * columns);
            Vector2 index = new Vector2(randX, randY);
            for (int j = 0; j < directions.length; j++) {
                Vector2 direction = directions[random.nextInt(directions.length)];
                if (isFit(word, index, direction)) {
                    for (char c : word.toCharArray()) {
                        getCellAt(randX, randY).setLetter(c);
                        randX += direction.x;
                        randY += direction.y;
                    }
                    fitted = true;
                }
                if (fitted) break;
            }
            if (fitted) break;
        }
    }

    public boolean isFit(String word, Vector2 index, Vector2 direction) {
        try {
            Vector2 tmp = index;
            for (char c : word.toCharArray()) {
                if (tmp.x < 0 || tmp.y < 0)
                    throw new IndexOutOfBoundsException();
                char letter = cells[(int) tmp.x][(int) tmp.y].getLetter();
                if (letter != ' ' && letter != c)   return false;
                tmp.x += direction.x;
                tmp.y += direction.y;
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
