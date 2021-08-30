package com.isoterik.cash4life.iqtest.utils

class Sentence {
    lateinit var categories: ArrayList<Category>

    class Category {
        lateinit var sentence: String
        lateinit var missing: ArrayList<String>
        lateinit var options: ArrayList<String>
    }
}