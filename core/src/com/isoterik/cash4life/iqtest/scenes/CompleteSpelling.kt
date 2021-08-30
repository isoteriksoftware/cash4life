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
import com.isoterik.cash4life.iqtest.components.SpellingGameManager
import com.isoterik.cash4life.iqtest.components.LetterManager
import com.isoterik.cash4life.iqtest.components.UIManager
import io.github.isoteriktech.xgdx.*
import io.github.isoteriktech.xgdx.ui.ActorAnimation
import io.github.isoteriktech.xgdx.utils.GameWorldUnits

class CompleteSpelling : Scene() {
    private var levelTime: Float = 0.0f

    private lateinit var userManager: UserManager

    private lateinit var uiManager: UIManager

    private lateinit var spellingGameManager: SpellingGameManager

    private lateinit var letterManager: LetterManager

    private fun initManagers() {
        val userManagerGameObject = GameObject()
        userManagerGameObject.tag = "userManager"
        userManager = UserManager()
        userManagerGameObject.addComponent(userManager)
        addGameObject(userManagerGameObject)

        val uiManagerGameObject = GameObject()
        uiManagerGameObject.tag = "uiManager"
        uiManager = UIManager(xGdx, 0)
        uiManagerGameObject.addComponent(uiManager)
        addGameObject(uiManagerGameObject)

        val letterManagerGameObject = GameObject()
        letterManagerGameObject.tag = "letterManager"
        letterManager = LetterManager()
        letterManagerGameObject.addComponent(letterManager)
        addGameObject(letterManagerGameObject)

        val gameManagerGameObject = GameObject()
        gameManagerGameObject.tag = "gameManager"
        spellingGameManager = SpellingGameManager()
        gameManagerGameObject.addComponent(spellingGameManager)
        addGameObject(gameManagerGameObject)
        spellingGameManager.xGdx = xGdx
        spellingGameManager.init()
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

        val wordsJsonData = JsonReader().parse(Gdx.files.internal("${GlobalConstants.IQ_TEST_ASSETS_HOME}/json/complete_spelling.json"))
        val wordsArray = wordsJsonData.get("words").asStringArray()
        val gameManagerWordsArray = getArrayFromStringArray(wordsArray)
        gameManagerWordsArray.shuffle()
        spellingGameManager.wordsArray = gameManagerWordsArray

        uiManager.setMaxLevels(wordsArray.size)
        uiManager.setLevelTime(levelTime)

        spellingGameManager.startLevel()

        uiManager.scheduleTimer()
    }

    fun setTime(time: Float) {
        levelTime = time
    }

    private fun getArrayFromStringArray(stringArray: kotlin.Array<String>): Array<String> {
        val result = Array<String>()
        for (string in stringArray)
            result.add(string)

        return result
    }
}