package com.isoterik.cash4life.cashpuzzles.utils;

import java.util.ArrayList;

public class Words {
    private ArrayList<Category> categories;

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public static class Category {
        private String name;
        private ArrayList<String> words;

        public String getName() {
            return name;
        }

        public ArrayList<String> getWords() {
            return words;
        }
    }
}
