package com.isoterik.cash4life.cashpuzzles;

import java.util.ArrayList;
import java.util.Collections;

public class WordManager {

    private static final WordManager instance = new WordManager();

    private ArrayList<String> words;
    private ArrayList<String> wordsCopy;

    private WordManager() {
    }

    public static WordManager getInstance() {
        return instance;
    }

    public void loadConfigFile(String directory, String file){
        ArrayList<String> fileWords = FileUtilities.getListOfLinesFromFile(directory, file);

        Collections.sort(fileWords);
        wordsCopy = new ArrayList<>(fileWords);
        words = new ArrayList<>(fileWords);
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public ArrayList<String> getWordsCopy() {
        return wordsCopy;
    }
}
