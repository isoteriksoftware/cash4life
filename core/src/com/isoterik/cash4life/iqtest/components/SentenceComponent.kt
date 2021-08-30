package com.isoterik.cash4life.iqtest.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Json
import com.isoterik.cash4life.GlobalConstants
import com.isoterik.cash4life.iqtest.utils.Sentence
import io.github.isoteriktech.xgdx.Component
import java.io.File
import java.util.*

class SentenceComponent : Component() {
    lateinit var sentences: ArrayList<Sentence.Category>

    override fun start() {
        sentences = ArrayList()
        val categories = Json().fromJson(Sentence::class.java, getJsonFile()).categories
        for (category in categories) {
            sentences.add(category)
        }

        sentences.shuffle()
    }

    private fun getJsonFile(): FileHandle? {
        val currentPath = Gdx.files.internal(GlobalConstants.IQ_TEST_ASSETS_HOME).path()
        val fileDirectory = currentPath + File.separatorChar + "json"
        return Gdx.files.internal(fileDirectory + File.separatorChar + "complete_sentence.json")
    }
}