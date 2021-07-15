package com.isoterik.cash4life.cashpuzzles.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.isoterik.cash4life.cashpuzzles.components.managers.WordManager;
import com.isoterik.cash4life.cashpuzzles.components.managers.LetterManager;

import java.util.*;

public class Board {
    private WordManager wordManager;
    private LetterManager letterManager;

    private final int rows;
    private final int columns;
    private ArrayList<String> words;

    private final Cell[][] cells;

    public Board(int rows, int columns, ArrayList<String> words) {
        this.rows = rows;
        this.columns = columns;
        this.words = words;

        this.cells = new Cell[rows][columns];
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                cells[i][j] = new Cell(i,j);
            }
        }
    }

    public void initializeManagers(WordManager wordManager, LetterManager letterManager) {
        this.wordManager = wordManager;
        this.letterManager = letterManager;
    }

    public void generatePuzzle(){
        //sort words by descending length
        ArrayList<String> wordsCopy = new ArrayList<>(words);
        ArrayList<String> wordsInOrder = new ArrayList<>();
        while(!wordsCopy.isEmpty()) {
            int index = 0;
            for(int i = 0; i < wordsCopy.size(); i++) {
                if(wordsCopy.get(i).length() > wordsCopy.get(index).length()) {
                    index = i;
                }
            }
            wordsInOrder.add(wordsCopy.get(index));
            wordsCopy.remove(index);
        }

        //insert each word in order
        for(String s : wordsInOrder)
            my(s); //fillCell(s);

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
                        catch (IndexOutOfBoundsException ignored) {
                        }
                    }
                    fitted = true;
                    wordManager.addLoadedWord(word);
                    letterManager.addValidCell(cells);
                }
                if (fitted) break;
            }
            if (fitted) break;
        }
    }

    public boolean isFit(String word, Vector2 index, Vector2 direction) {
        try {
            char[] arr = word.toCharArray();
            Vector2 vector = new Vector2(index.x, index.y);
            for (char c : arr) {
                if (vector.x < 0 || vector.y < 0 || vector.x >= rows || vector.y >= columns)
                    throw new IndexOutOfBoundsException();
                char letter = getCellAt(vector).getLetter();
                if (letter != ' ' && letter != c)   return false;
                vector.x += direction.x;
                vector.y += direction.y;
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

    public Cell getCellAt(Vector2 point) {
        return cells[(int) point.x][(int) point.y];
    }

    public Cell getCellAt(int row, int column) {
        return cells[row][column];
    }

    private void my(String word) {
        VisitedCell[] visitedCells = new VisitedCell[rows * columns];
        initializeCells(visitedCells);
        ArrayList<VisitedCell> visited = new ArrayList<>(Arrays.asList(visitedCells));
        Collections.shuffle(visited);
        for (VisitedCell visitedCell : visited) {
            Cell cell = visitedCell.getCell();
            ArrayList<Vector2> directions = visitedCell.getDirections();
            Vector2 index = new Vector2(cell.getRow(), cell.getColumn());
            boolean fitted = false;
            for (Vector2 direction : directions) {
                if (isFit(word, index, direction)) {
                    Cell[] cells = new Cell[word.length()];
                    int count = 0;
                    for (char c : word.toCharArray()) {
                        getCellAt(index).setLetter(c);
                        cells[count++] = getCellAt(index);
                        index.x += direction.x;
                        index.y += direction.y;
                    }
                    fitted = true;
                    wordManager.addLoadedWord(word);
                    letterManager.addValidCell(cells);
                }
                if (fitted) break;
            }
            if (fitted) break;
        }
    }

    private void initializeCells(VisitedCell[] visitedCells) {
        int count = 0;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                visitedCells[count++] = new VisitedCell(new Cell(row, column));
            }
        }
    }

    private final ArrayList<Vector2> directions = new ArrayList<Vector2>() {
        {
            add(new Vector2(1, 0));
            add(new Vector2(0, 1));
            add(new Vector2(-1, 0));
            add(new Vector2(0, -1));
            add(new Vector2(1, -1));
            add(new Vector2(-1, 1));
            add(new Vector2(1, 1));
            add(new Vector2(-1, -1));
        }
    };

    class VisitedCell {
        private Cell cell;
        private ArrayList<Vector2> directions;

        public VisitedCell(Cell cell) {
            this.cell = cell;
            directions = new ArrayList<>(Board.this.directions);
            Collections.shuffle(directions);
        }

        public Cell getCell() {
            return cell;
        }

        public ArrayList<Vector2> getDirections() {
            return directions;
        }
    }
}
