package com.isoterik.cash4life.cashpuzzles.components.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.isoterik.cash4life.GlobalConstants;
import com.isoterik.cash4life.cashpuzzles.utils.Words;
import io.github.isoteriktech.xgdx.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class WordManager extends Component {
    private HashMap<String, ArrayList<String>> fileWords;

    private ArrayList<String> stageWords;
    private ArrayList<String> loadedWords;
    private ArrayList<String> foundWords;

    @Override
    public void start() {
        fileWords = new HashMap<>();
        ArrayList<Words.Category> categories = new Json().fromJson(Words.class, getJsonFile()).getCategories();

        for (Words.Category category : categories) {
            fileWords.put(category.getName(), category.getWords());
        }
    }

    private FileHandle getJsonFile() {
        String currentPath = Gdx.files.internal(GlobalConstants.CASH_PUZZLES_ASSETS_HOME).path();
        String fileDirectory = currentPath + File.separatorChar + "json";
        return Gdx.files.internal(fileDirectory + File.separatorChar + "words.json");
    }

    public void loadCategoryWords(String categoryName) {
        ArrayList<String> words = fileWords.get(categoryName.toLowerCase(Locale.ROOT));

        Collections.sort(words);
        stageWords = new ArrayList<>(words);
        loadedWords = new ArrayList<>();
        foundWords = new ArrayList<>();
    }

    public void addLoadedWord(String word) {
        loadedWords.add(word);
    }

    public ArrayList<String> getStageWords() {
        return stageWords;
    }

    public ArrayList<String> getLoadedWords() {
        return loadedWords;
    }

    public ArrayList<String> getFoundWords() {
        return foundWords;
    }

    public void setFoundWords(ArrayList<String> foundWords) {
        this.foundWords = foundWords;
    }
}
