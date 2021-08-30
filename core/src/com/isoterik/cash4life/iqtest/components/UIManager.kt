package com.isoterik.cash4life.iqtest.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Timer
import com.isoterik.cash4life.GlobalConstants
import com.isoterik.cash4life.GlobalUtil
import com.isoterik.cash4life.iqtest.IQTestSplash
import io.github.isoteriktech.xgdx.Component
import io.github.isoteriktech.xgdx.XGdx
import io.github.isoteriktech.xgdx.ui.ActorAnimation
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions

class UIManager(xGdx: XGdx, sceneIndex: Int) : Component() {
    private val xGdx: XGdx = xGdx
    private val sceneIndex: Int = sceneIndex

    private lateinit var skin: Skin

    private lateinit var canvas: Stage

    private lateinit var levelLabel: Label
    private lateinit var timerLabel: Label
    private lateinit var maxTapsLabel: Label

    private var timeInMins = 0f
    private var timeInSecs = 0

    private var currentLevelIndex = 0
    private var maxLevels = 0

    var tapCount = 0
    var maxTaps = 0

    override fun start() {
        setupUI()
        Gdx.input.setCatchKey(Input.Keys.BACK, true)
    }

    override fun update(deltaTime: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            toSplashScene()
        }
    }

    private fun setupUI() {
        canvas = scene.canvas

        skin = xGdx.assets.getSkin(GlobalConstants.CASH_PUZZLES_SKIN)
        val uiAtlas: TextureAtlas = xGdx.assets.getAtlas(
            GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/skin/ui.atlas"
        )

        val btnBack = Button(skin, "back")
        //btnBack.setColor(skin.getColor("gold"));
        //btnBack.setColor(skin.getColor("gold"));
        btnBack.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                toSplashScene()
            }
        })
        GlobalUtil.resizeUI(btnBack, 0.5f)

        levelLabel = Label("1/10", skin)

        timerLabel = Label("00:00", skin)

        maxTapsLabel = Label("Taps: $tapCount / $maxTaps", skin)

        val table = Table()
        //table.debug = true;
        table.setFillParent(true)
        table.padTop(20f).padBottom(20f).padRight(10f).padLeft(10f)
        table.top()

        table.add(btnBack).left().width(btnBack.width).height(btnBack.height).expandX()
        table.add(levelLabel).center().expandX()
        table.add(timerLabel).right().expandX()
        table.row()

        table.add(maxTapsLabel).left().expandX().padTop(10f)
        table.row()

        canvas.addActor(table)
    }

    private val myTimerTask: Timer.Task = object : Timer.Task() {
        override fun run() {
            --timeInSecs
            timeInMins = timeInSecs / 60f
            if (timeInSecs >= 0) {
                val hours: Int = timeInSecs / 3600
                val minutes: Int = timeInSecs / 60
                val seconds: Int = timeInSecs % 60
                val hoursString = if (hours >= 10) hours.toString() else "0$hours"
                val minutesString = if (minutes >= 10) minutes.toString() else "0$minutes"
                val secondsString = if (seconds >= 10) seconds.toString() else "0$seconds"
                val time = "$hoursString:$minutesString:$secondsString"
                if (timeInSecs <= 60) timerLabel.color = Color.RED
                timerLabel.setText(time)
            }
            else gameLost()
        }
    }

    fun setMaxLevels(max: Int) {
        maxLevels = max
    }

    fun setLevelTime(levelTime: Float) {
        timeInMins = levelTime
        timeInSecs = (timeInMins * 60).toInt()
    }

    fun updateLevelText() {
        val level: Int = currentLevelIndex + 1
        val currentLevelText = if (currentLevelIndex > 9) level.toString() else "0$level"
        val maxLevelsText = if (maxLevels >= 10) maxLevels.toString() else "0$maxLevels"
        levelLabel.setText("$currentLevelText/$maxLevelsText")

        currentLevelIndex++
    }

    fun setTaps(amount: Int) {
        tapCount = 0
        maxTaps = amount
        maxTapsLabel.setText("Taps: $tapCount / $maxTaps")
    }

    fun updateMaxTapsLabel() {
        tapCount++
        maxTapsLabel.setText("Taps: $tapCount / $maxTaps")
    }

    fun scheduleTimer() {
        Timer.schedule(myTimerTask, 1f, 1f)
    }

    fun gameFinished() {
        myTimerTask.cancel()
        showGameWonWindow()
    }

    fun gameLost() {
        showGameOverWindow()
    }

    private fun showGameWonWindow() {
        // Clear all contents of the board to avoid user interaction with one of the letter gameobjects
        if (sceneIndex == 0)
            scene.findGameObject("gameManager").getComponent(SpellingGameManager::class.java).clearScreen()
        else if (sceneIndex == 1)
            scene.findGameObject("gameManager").getComponent(SentenceGameManager::class.java).clearScreen()

        val window = Window("", skin)
        val labelTitle = "CONGRATULATIONS! YOU'VE WON THE GAME.\n\n +N10,000 has been added to your account!"
        val label = Label(labelTitle, skin, "confeti")
        label.setAlignment(Align.center)
        label.wrap = true
        GlobalUtil.resizeUI(label, 0.5f)
        val okBtn = Button(skin, "ok")
        GlobalUtil.resizeUI(okBtn, 0.5f)
        okBtn.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                toSplashScene()
            }
        })

        //window.setDebug(true);
        window.setFillParent(true)
        window.padTop(20f).padBottom(20f).padRight(10f).padLeft(10f)
        window.center()
        window.add(label).center().expandX().width(label.width).height(label.height)
        window.row()
        window.add(okBtn).center().expandX().width(okBtn.width).height(okBtn.height)
        window.pack()
        canvas.addActor(window)
        ActorAnimation.instance().slideIn(window, ActorAnimation.DOWN, .7f, Interpolation.swingOut)
    }

    private fun showGameOverWindow() {
        // Clear all contents of the board to avoid user interaction with one of the letter gameobjects
        if (sceneIndex == 0)
            scene.findGameObject("gameManager").getComponent(SpellingGameManager::class.java).clearScreen()
        else if (sceneIndex == 1)
            scene.findGameObject("gameManager").getComponent(SentenceGameManager::class.java).clearScreen()

        val window = Window("", skin)
        val labelTitle = "OOPS! TIME'S UP.\n You can do better!"
        val label = Label(labelTitle, skin)
        label.setAlignment(Align.center)
        label.wrap = true
        val okBtn = Button(skin, "ok")
        GlobalUtil.resizeUI(okBtn, 0.5f)
        okBtn.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                toSplashScene()
            }
        })

        //window.setDebug(true);
        window.setFillParent(true)
        window.padTop(20f).padBottom(20f).padRight(10f).padLeft(10f)
        window.center()
        window.add(label).center().expandX()
        window.row()
        window.add(okBtn).center().expandX().padTop(20f).width(okBtn.width).height(okBtn.height)
        window.pack()
        canvas.addActor(window)
        ActorAnimation.instance().slideIn(window, ActorAnimation.DOWN, .7f, Interpolation.swingOut)
    }

    private fun toSplashScene() {
        xGdx.setScene(
            IQTestSplash(),
            SceneTransitions.slide(
                1f, SceneTransitionDirection.UP, true, Interpolation.pow5Out
            )
        )
    }
}