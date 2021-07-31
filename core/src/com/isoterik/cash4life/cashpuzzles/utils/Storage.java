package com.isoterik.cash4life.cashpuzzles.utils;

import java.util.ArrayList;

public class Storage {
    private boolean isSaved;
    private ArrayList<String> foundWords;
    private int stage;
    private float time;
    private int timeReloadCount;
    private int hintCount;

    public Storage() {

    }

    public Storage(boolean isSaved, ArrayList<String> foundWords, int stage, float time, int timeReloadCount, int hintCount) {
        this.isSaved = isSaved;
        this.foundWords = foundWords;
        this.stage = stage;
        this.time = time;
        this.timeReloadCount = timeReloadCount;
        this.hintCount = hintCount;
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

    public int getTimeReloadCount() {
        return timeReloadCount;
    }

    public int getHintCount() {return hintCount; }
}
