package com.isoterik.cash4life.cashpuzzles.utils;

import com.isoterik.cash4life.cashpuzzles.components.managers.GameManager;

import java.util.ArrayList;

public class Storage {
    private boolean isSaved;
    private ArrayList<String> foundWords;
    private int stage;
    private float time;

    public Storage() {

    }

    public Storage(boolean isSaved, ArrayList<String> foundWords, int stage, float time) {
        this.isSaved = isSaved;
        this.foundWords = foundWords;
        this.stage = stage;
        this.time = time;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public ArrayList<String> getFoundWords() {
        return foundWords;
    }

    public int getStage() {
        return stage;
    }

    public float getTime() {
        return time;
    }
}
