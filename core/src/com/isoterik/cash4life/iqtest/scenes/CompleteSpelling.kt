package com.isoterik.cash4life.iqtest.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.isoterik.cash4life.GlobalConstants
import com.isoterik.cash4life.GlobalUtil
import com.isoterik.cash4life.iqtest.Constants
import com.isoterik.cash4life.iqtest.components.GameManager
import com.isoterik.cash4life.iqtest.components.LetterManager
import com.isoterik.cash4life.iqtest.components.LetterOption
import io.github.isoteriktech.xgdx.*
import io.github.isoteriktech.xgdx.ui.ActorAnimation
import io.github.isoteriktech.xgdx.utils.GameWorldUnits
import java.util.*
import kotlin.random.Random

class CompleteSpelling : Scene() {
    lateinit var gameManagerGameObject: GameObject
    lateinit var gameManager: GameManager

    lateinit var letterManagerGameObject: GameObject
    lateinit var letterManager: LetterManager

    private fun initManagers() {
        letterManagerGameObject = GameObject()
        letterManagerGameObject.tag = "letterManager"
        letterManager = LetterManager()
        letterManagerGameObject.addComponent(letterManager)
        addGameObject(letterManagerGameObject)

        gameManagerGameObject = GameObject()
        gameManagerGameObject.tag = "gameManager"
        gameManager = GameManager()
        gameManagerGameObject.addComponent(gameManager)
        addGameObject(gameManagerGameObject)
        gameManager.xGdx = xGdx
        gameManager.init()
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

        val wordsJsonData = JsonReader().parse(Gdx.files.internal("${GlobalConstants.IQ_TEST_ASSETS_HOME}/json/words.json"))
        val wordsArray = wordsJsonData.get("words").asStringArray()
        val gameManagerWordsArray = getArrayFromStringArray(wordsArray)
        gameManagerWordsArray.shuffle()
        gameManager.wordsArray = gameManagerWordsArray
        gameManager.startLevel()
    }

    private fun getArrayFromStringArray(stringArray: kotlin.Array<String>): Array<String> {
        val result = Array<String>()
        for (string in stringArray)
            result.add(string)

        return result
    }
}