package com.isoterik.cash4life.cashpuzzles;

import java.util.ArrayList;
import java.util.Collections;

public class WordManager {

    private static final WordManager instance = new WordManager();

    private ArrayList<String> words;
    private ArrayList<String> loadedWords;
    private ArrayList<String> foundWords;

    private WordManager() { }

    public static WordManager getInstance() {
        return instance;
    }

    public void loadConfigFile(String directory, String file){
        ArrayList<String> fileWords = FileUtilities.getListOfLinesFromFile(directory, file);

        Collections.sort(fileWords);
        words = new ArrayList<>(fileWords);
        loadedWords = new ArrayList<>();
        foundWords = new ArrayList<>();
    }

    public void addLoadedWord(String word) {
        loadedWords.add(word);
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public ArrayList<String> getLoadedWords() {
        return loadedWords;
    }

    public ArrayList<String> getFoundWords() {
        return foundWords;
    }
}
