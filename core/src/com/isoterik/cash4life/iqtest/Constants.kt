package com.isoterik.cash4life.iqtest

object Constants {
    const val GUI_WIDTH  = 480
    const val GUI_HEIGHT = 780

    val SPELLING_TIME_AND_PRICES = HashMap<Float, Int>()

    val SENTENCE_TIME_AND_PRICES = HashMap<Float, Int>()

    fun init() {
        SPELLING_TIME_AND_PRICES[1f] = 500
        SPELLING_TIME_AND_PRICES[2f] = 1000
        SPELLING_TIME_AND_PRICES[5f] = 2000
        SPELLING_TIME_AND_PRICES[10f] = 3000
        SPELLING_TIME_AND_PRICES[20f] = 5000
        SPELLING_TIME_AND_PRICES[30f] = 10000

        SENTENCE_TIME_AND_PRICES[1f] = 500
        SENTENCE_TIME_AND_PRICES[2f] = 1000
        SENTENCE_TIME_AND_PRICES[5f] = 2000
        SENTENCE_TIME_AND_PRICES[10f] = 3000
        SENTENCE_TIME_AND_PRICES[20f] = 5000
        SENTENCE_TIME_AND_PRICES[30f] = 10000
    }
}
