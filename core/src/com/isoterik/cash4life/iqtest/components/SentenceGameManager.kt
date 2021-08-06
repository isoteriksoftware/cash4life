package com.isoterik.cash4life.iqtest.components

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.utils.Array
import com.isoterik.cash4life.GlobalConstants
import com.isoterik.cash4life.GlobalUtil
import com.isoterik.cash4life.iqtest.IQTestSplash
import io.github.isoteriktech.xgdx.Component
import io.github.isoteriktech.xgdx.GameObject
import io.github.isoteriktech.xgdx.Transform
import io.github.isoteriktech.xgdx.XGdx
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.random.Random

class SentenceGameManager : Component() {
    private val whiteLettersAtlas = XGdx.instance().assets.getAtlas(
        GlobalConstants.IQ_TEST_ASSETS_HOME + "/spritesheets/white.atlas"
    )
    private val blackLettersAtlas = XGdx.instance().assets.getAtlas(
        GlobalConstants.IQ_TEST_ASSETS_HOME + "/spritesheets/black.atlas"
    )

    private var sentencesArrayCount = 0
    var sentencesArray = Array<String>()

    lateinit var xGdx: XGdx

    //lateinit var letterManager: LetterManager

    fun init() {
        //letterManager = gameObject.hostScene.findGameObject("letterManager").getComponent(LetterManager::class.java)
    }

    fun startLevel() {
        if (sentencesArrayCount >= sentencesArray.size) {
            gameWon()
            return
        }

        clearScreen()

        val sentenceToFind = sentencesArray[sentencesArrayCount++]

        val sentence = sentenceToFind.substring(0, sentenceToFind.indexOf('/'))
        println(sentence)
        val missingWords = getMissingWords(sentenceToFind)
        println(missingWords)
        val options = getOptions(sentenceToFind)
        println(options)

        val strippedSentence = stripSentence(sentence, missingWords)
        val strippedSentenceGameObjects = getStrippedSentenceGameObjects(strippedSentence)
    }

    private fun gameWon() {
        xGdx.setScene(
            IQTestSplash(), SceneTransitions.slide(1f, SceneTransitionDirection.UP,
                true, Interpolation.pow5Out))
    }

    private fun clearScreen() {
        removeGameObjects(scene.findGameObjects("blackLetter"))
        removeGameObjects(scene.findGameObjects("whiteLetter"))
    }

    private fun removeGameObjects(gameObjects: Array<GameObject>) {
        for (gameObject in gameObjects)
            removeGameObject(gameObject)
    }

    private fun addGameObjects(gameObjects: Array<GameObject>) {
        for (gameObject in gameObjects)
            addGameObject(gameObject)
    }

    private fun getMissingWords(sentence: String): Array<String> {
        val substringIndices = HashMap<Int, Int>()
        var start = 0
        var started = false
        for ((index, char) in sentence.withIndex()) {
            if (start == 0 && (char == '/' || started)) {
                start = index + 1
                started = false
            }
            else if (start > 0 && (char == '/' || char == '{')) {
                substringIndices.put(start, index)
                start = 0
                started = true
            }
        }

        val words = Array<String>()
        for (startIndex in substringIndices.keys) {
            val word = sentence.substring(startIndex, substringIndices.getValue(startIndex))
            words.add(word)
        }

        return words
    }

    private fun getOptions(sentence: String): Array<String> {
        val start = sentence.indexOf('{') + 1
        val stop = sentence.indexOf('}')
        val stripped = sentence.substring(start, stop).replace(",", "")

        val optionsList = stripped.split(" ")
        val options = Array<String>()
        for (option in optionsList) {
            options.add(option)
        }

        return options
    }

    private fun stripSentence(sentence: String, missingWords: Array<String>): String {
        for (word in missingWords) {
            sentence.replace(word, "?")
        }

        return sentence
    }

    private fun getStrippedSentenceGameObjects(sentence: String): ArrayList<Array<GameObject>> {
        val result = ArrayList<Array<GameObject>>()

        return result
    }

    private fun createStrippedWordGameObjects(strippedWord: String): Array<GameObject> {
        val letterGameObjects = Array<GameObject>()
        for (letter in strippedWord) {
            val letterGameObject = createLetterGameObject(letter, blackLettersAtlas, false)
            letterGameObjects.add(letterGameObject)
        }

        return letterGameObjects
    }

    private fun createLetterGameObject(letter: Char, lettersAtlas: TextureAtlas, isWhite: Boolean): GameObject {
        val keyword = if (letter == ' ') "empty" else letter.toString()

        val gameObject = scene.newActorSpriteObject(lettersAtlas.findRegion(keyword.toUpperCase(Locale.ROOT)))
        gameObject.tag = if (isWhite) "whiteLetter" else "blackLetter"

        return gameObject
    }

    private fun getTransformsFromGameObjects(gameObjects: Array<GameObject>): Array<Transform> {
        val transforms = Array<Transform>()
        for (gameObject in gameObjects)
            transforms.add(gameObject.transform)

        return transforms
    }

    private fun getMissingLettersGameObjects(gameObjects: Array<GameObject>, strippedWord: String): Array<GameObject> {
        val result = Array<GameObject>()
        for ((index, gameObject) in gameObjects.withIndex()) {
            val isLetterMissing = strippedWord[index] == ' '
            if (isLetterMissing)
                result.add(gameObject)
        }

        return result
    }

    private fun addComponents(gameObjects: Array<GameObject>, componentType: Class<out Any>) {
        for (gameObject in gameObjects) {
            gameObject.addComponent(componentType.newInstance() as Component?)
        }
    }
}