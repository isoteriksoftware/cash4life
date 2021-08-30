package com.isoterik.cash4life.iqtest.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.isoterik.cash4life.GlobalConstants
import com.isoterik.cash4life.GlobalUtil
import com.isoterik.cash4life.UserManager
import com.isoterik.cash4life.iqtest.Constants
import com.isoterik.cash4life.iqtest.components.*
import io.github.isoteriktech.xgdx.GameObject
import io.github.isoteriktech.xgdx.Scene
import io.github.isoteriktech.xgdx.ui.ActorAnimation
import io.github.isoteriktech.xgdx.utils.GameWorldUnits

class CompleteSentence : Scene() {
    private var levelTime: Float = 0.0f

    private lateinit var userManager: UserManager

    private lateinit var uiManager: UIManager

    private lateinit var sentenceGameManager: SentenceGameManager

    private lateinit var sentenceComponent: SentenceComponent

    private lateinit var sentenceManager: SentenceManager

    private fun initManagers() {
        val userManagerGameObject = GameObject()
        userManagerGameObject.tag = "userManager"
        userManager = UserManager()
        userManagerGameObject.addComponent(userManager)
        addGameObject(userManagerGameObject)

        val uiManagerGameObject = GameObject()
        uiManagerGameObject.tag = "uiManager"
        uiManager = UIManager(xGdx, 1)
        uiManagerGameObject.addComponent(uiManager)
        addGameObject(uiManagerGameObject)

        val sentenceManagerGameObject = GameObject()
        sentenceManagerGameObject.tag = "sentenceManager"
        sentenceManager = SentenceManager()
        sentenceManagerGameObject.addComponent(sentenceManager)
        addGameObject(sentenceManagerGameObject)

        val gameManagerGameObject = GameObject()
        gameManagerGameObject.tag = "gameManager"
        sentenceGameManager = SentenceGameManager()
        gameManagerGameObject.addComponent(sentenceGameManager)
        addGameObject(gameManagerGameObject)
        sentenceGameManager.xGdx = xGdx
        sentenceGameManager.init()

        val sentenceComponentGameObject = GameObject()
        sentenceComponentGameObject.tag = "sentenceComponent"
        sentenceComponent = SentenceComponent()
        sentenceComponentGameObject.addComponent(sentenceComponent)
        addGameObject(sentenceComponentGameObject)
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
    }

    fun init() {
        initManagers()

        val background = newSpriteObject(xGdx.assets.getTexture("${GlobalConstants.IQ_TEST_ASSETS_HOME}/images/background-png.png"))
        background.tag = "background"
        background.transform.setSize(gameWorldUnits.worldWidth, gameWorldUnits.worldHeight)
        addGameObject(background)

        val sentences = sentenceComponent.sentences
        sentenceGameManager.sentences = sentences

        uiManager.setMaxLevels(sentences.size)
        uiManager.setLevelTime(levelTime)

        sentenceGameManager.startLevel()

        uiManager.scheduleTimer()
    }

    fun setTime(time: Float) {
        levelTime = time
    }
}