package com.isoterik.cash4life.iqtest.components

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.utils.Array
import com.isoterik.cash4life.GlobalConstants
import com.isoterik.cash4life.GlobalUtil
import com.isoterik.cash4life.iqtest.IQTestSplash
import com.isoterik.cash4life.iqtest.scenes.CompleteSpelling
import io.github.isoteriktech.xgdx.Component
import io.github.isoteriktech.xgdx.GameObject
import io.github.isoteriktech.xgdx.Transform
import io.github.isoteriktech.xgdx.XGdx
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions
import java.util.*
import kotlin.random.Random

class GameManager : Component() {
    private val whiteLettersAtlas = XGdx.instance().assets.getAtlas(
        GlobalConstants.IQ_TEST_ASSETS_HOME + "/spritesheets/white.atlas"
    )
    private val blackLettersAtlas = XGdx.instance().assets.getAtlas(
        GlobalConstants.IQ_TEST_ASSETS_HOME + "/spritesheets/black.atlas"
    )

    private var wordsArrayCount = 0
    var wordsArray = Array<String>()

    lateinit var xGdx: XGdx

    lateinit var letterManager: LetterManager

    fun init() {
        letterManager = gameObject.hostScene.findGameObject("letterManager").getComponent(LetterManager::class.java)
    }

    fun startLevel() {
        if (wordsArrayCount >= wordsArray.size) {
            gameWon()
            return
        }

        clearScreen()

        val wordToFind = wordsArray[wordsArrayCount++]

        val numberOfMissingLetters = Random.nextInt(2, 4)
        val strippedWord = getStrippedWord(wordToFind, numberOfMissingLetters)
        val missingLetters = getMissingLetters(wordToFind, strippedWord)

        letterManager.missingLetters = missingLetters

        val strippedWordGameObjects = createStrippedWordGameObjects(strippedWord)
        val strippedWordTransforms = getTransformsFromGameObjects(strippedWordGameObjects)

        letterManager.missingLettersGameObjects = getMissingLettersGameObjects(strippedWordGameObjects, strippedWord)

        GlobalUtil.resizeTransforms(strippedWordTransforms, 0.15f)
        GlobalUtil.centerDistributeEvenly(strippedWordTransforms)

        addGameObjects(strippedWordGameObjects)

        val letterOptionsGameObject = createLetterOptionsGameObject(missingLetters)
        val letterOptionsTransform = getTransformsFromGameObjects(letterOptionsGameObject)

        GlobalUtil.resizeTransforms(letterOptionsTransform, 0.15f)
        GlobalUtil.horizontalDistributeEvenly(letterOptionsTransform, 1f)

        addComponents(letterOptionsGameObject, LetterOption::class.java)

        setLetterOptionComponentIndices(letterOptionsGameObject)
        addGameObjects(letterOptionsGameObject)
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
        for (gameObject in gameObjects) {
            if (gameObject.tag == "blackLetter")
                gameObject.removeComponent(LetterOption::class.java)
            removeGameObject(gameObject)
        }
    }

    private fun addGameObjects(gameObjects: Array<GameObject>) {
        for (gameObject in gameObjects)
            addGameObject(gameObject)
    }

    private fun getStrippedWord(wordToFind: String, numberOfMissingLetters: Int): String {
        val indicesToStrip = Array<Int>()
        while (indicesToStrip.size != numberOfMissingLetters) {
            val randomIndex = Random.nextInt(wordToFind.length)
            if (!indicesToStrip.contains(randomIndex)) indicesToStrip.add(randomIndex)
        }

        val strippedWord = StringBuilder(wordToFind)
        for (index in indicesToStrip) {
            strippedWord[index] = ' '
        }

        return strippedWord.toString()
    }

    private fun getMissingLetters(wordToFind: String, strippedWord: String): Array<Char> {
        val result = Array<Char>()
        for (index in wordToFind.indices) {
            val char = wordToFind[index]
            if (strippedWord[index] != char) result.add(char)
        }

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

    private fun createLetterOptionsGameObject(missingLetters: Array<Char>): Array<GameObject> {
        val availableOptions = 7
        val options = generateRandomLetters(missingLetters, availableOptions)
        letterManager.options = options

        val gameObjects = Array<GameObject>()
        for (option in options) {
            val gameObject = createLetterGameObject(option, whiteLettersAtlas, true)
            gameObjects.add(gameObject)
        }

        return gameObjects
    }

    private fun generateRandomLetters(from: Array<Char>, limit: Int): Array<Char> {
        val options = "abcdefghijklmnopqrstuvwxyz"
        var optionsArray = Array<Char>()
        for (char in options)
            if (!from.contains(char)) optionsArray.add(char)
        val optionsSize = optionsArray.size

        val fromSize = from.size

        val result = Array<Char>()
        for (i in 0 until limit) {
            if (i < fromSize) {
                result.add(from[i])
                continue
            }
            result.add(optionsArray[Random.nextInt(optionsSize)])
        }

        result.shuffle()

        return result
    }

    private fun addComponents(gameObjects: Array<GameObject>, componentType: Class<out Any>) {
        for (gameObject in gameObjects) {
            gameObject.addComponent(componentType.newInstance() as Component?)
        }
    }

    private fun setLetterOptionComponentIndices(gameObjects: Array<GameObject>) {
        for (index in 0 until gameObjects.size) {
            gameObjects[index].getComponent(LetterOption::class.java).index = index
        }
    }
}