package com.isoterik.cash4life;

// This is a useless class. Used for testing saved game in cash puzzles

import com.isoterik.cash4life.cashpuzzles.components.GameManager;

import java.util.ArrayList;

public class DemoStorage {
    public static boolean IS_GAME_SAVED = false;

    public static SavedGame SAVED_GAME;

    public static void RESET() {
        IS_GAME_SAVED = false;
        SAVED_GAME = null;
    }

    public static class SavedGame {
        public GameManager.Level level;
        public ArrayList<String> foundWords;
        public int stageStoppedAt;
        public float timeSavedAt;

        public SavedGame(GameManager.Level level, ArrayList<String> foundWords, int stageStoppedAt, float timeSavedAt) {
            this.level = level;
            this.foundWords = foundWords;
            this.stageStoppedAt = stageStoppedAt;
            this.timeSavedAt = timeSavedAt;
        }
    }
}
