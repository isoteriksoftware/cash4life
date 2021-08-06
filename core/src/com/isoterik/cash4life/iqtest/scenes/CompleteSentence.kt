package com.isoterik.cash4life.iqtest.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.isoterik.cash4life.GlobalConstants
import com.isoterik.cash4life.GlobalUtil
import com.isoterik.cash4life.iqtest.Constants
import com.isoterik.cash4life.iqtest.components.SpellingGameManager
import com.isoterik.cash4life.iqtest.components.LetterManager
import com.isoterik.cash4life.iqtest.components.SentenceGameManager
import io.github.isoteriktech.xgdx.GameObject
import io.github.isoteriktech.xgdx.Scene
import io.github.isoteriktech.xgdx.ui.ActorAnimation
import io.github.isoteriktech.xgdx.utils.GameWorldUnits

class CompleteSentence : Scene() {
    lateinit var gameManagerGameObject: GameObject
    lateinit var sentenceGameManager: SentenceGameManager

    lateinit var sentenceManagerGameObject: GameObject
    lateinit var sentenceManager: LetterManager

    private fun initManagers() {
        sentenceManagerGameObject = GameObject()
        sentenceManagerGameObject.tag = "sentenceManager"
        sentenceManager = LetterManager()
        sentenceManagerGameObject.addComponent(sentenceManager)
        addGameObject(sentenceManagerGameObject)

        gameManagerGameObject = GameObject()
        gameManagerGameObject.tag = "gameManager"
        sentenceGameManager = SentenceGameManager()
        gameManagerGameObject.addComponent(sentenceGameManager)
        addGameObject(gameManagerGameObject)
        sentenceGameManager.xGdx = xGdx
        sentenceGameManager.init()
    }

    private fun setUpCamera() {
        gameWorldUnits = GameWorldUnits(Constants.GUI_WIDTH.toFloat(), Constants.GUI_HEIGHT.toFloat(), 100f)

        mainCamera.setup(StretchViewport(gameWorldUnits.worldWidth, gameWorldUnits.worldHeight))
        setupAnimationCanvas(mainCamera.viewport)

        canvas = Stage(StretchViewport(gameWorldUnits.screenWidth, gameWorldUnits.screenHeight))

        input.inputMultiplexer.addProcessor(canvas)

        ActorAnimation.instance().setup(gameWorldUnits.screenWidth, gameWorldUnits.screenHeight)

        GlobalUtil.init(gameWorldUnits, canvas, 800f, 800f)
    }

    init {
        setUpCamera()
        initManagers()

        val background = newSpriteObject(xGdx.assets.getTexture("${GlobalConstants.IQ_TEST_ASSETS_HOME}/images/background-png.png"))
        background.tag = "background"
        background.transform.setSize(gameWorldUnits.worldWidth, gameWorldUnits.worldHeight)
        addGameObject(background)

        val sentencesJsonData = JsonReader().parse(Gdx.files.internal("${GlobalConstants.IQ_TEST_ASSETS_HOME}/json/complete_sentence.json"))
        val sentencesArray = sentencesJsonData.get("sentences").asStringArray()
        val gameManagerSentencesArray = getArrayFromStringArray(sentencesArray)
        gameManagerSentencesArray.shuffle()
        sentenceGameManager.sentencesArray = gameManagerSentencesArray
        sentenceGameManager.startLevel()
    }

    private fun getArrayFromStringArray(stringArray: kotlin.Array<String>): Array<String> {
        val result = Array<String>()
        for (string in stringArray)
            result.add(string)

        return result
    }
}