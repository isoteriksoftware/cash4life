package com.isoterik.cash4life

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.isoterik.cash4life.cashpuzzles.CashPuzzlesSplash
import com.isoterik.cash4life.doublecash.Constants
import com.isoterik.cash4life.doublecash.DoubleCashSplash
import com.isoterik.cash4life.iqtest.IQTestSplash
import io.github.isoteriktech.xgdx.Scene
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions

class MainScene : Scene() {
    private val myTimerTask: Timer.Task = object : Timer.Task() {
        override fun run() {
            xGdx.setScene(UIScene())
        }
    }

    init {
        setBackgroundColor(Color(0.1f, 0.1f, 0.2f, 1.0f))
        setUpCamera()
        setUpUI()
    }

    private fun setUpCamera() {
        canvas = Stage(StretchViewport(com.isoterik.cash4life.iqtest.Constants.GUI_WIDTH.toFloat(), com.isoterik.cash4life.iqtest.Constants.GUI_HEIGHT.toFloat()))

        input.inputMultiplexer.addProcessor(canvas)

        GlobalUtil.init(gameWorldUnits, canvas, 800f, 800f)
    }

    private fun setUpUI() {
        val splashImage = Image(xGdx.assets.regionForTexture(
            "${GlobalConstants.SHARED_ASSETS_HOME}/images/splash.jpg")
        )
        splashImage.setSize(canvas.width, canvas.height)
        canvas.addActor(splashImage)

        Timer.schedule(myTimerTask, 5f)
    }
}