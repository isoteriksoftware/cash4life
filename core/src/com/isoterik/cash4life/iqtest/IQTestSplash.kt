package com.isoterik.cash4life.iqtest

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.isoterik.cash4life.GlobalConstants
import com.isoterik.cash4life.GlobalUtil
import com.isoterik.cash4life.iqtest.scenes.CompleteSpelling
import io.github.isoteriktech.xgdx.Scene
import io.github.isoteriktech.xgdx.ui.ActorAnimation
import io.github.isoteriktech.xgdx.utils.GameWorldUnits
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions

class IQTestSplash : Scene() {
    init {
        setBackgroundColor(Color(0.1f, 0.1f, 0.2f, 1.0f))
        setUpCamera()
        setUpUI()
    }

    private fun setUpCamera() {
        canvas = Stage(StretchViewport(Constants.GUI_WIDTH.toFloat(), Constants.GUI_HEIGHT.toFloat()))

        input.inputMultiplexer.addProcessor(canvas)

        GlobalUtil.init(gameWorldUnits, canvas, 800f, 800f)
    }

    private fun setUpUI() {
        val background = Image(xGdx.assets.regionForTexture(
            "${GlobalConstants.IQ_TEST_ASSETS_HOME}/images/background-jpg.jpg")
        )
        background.setSize(canvas.width, canvas.height)
        canvas.addActor(background)

        val skin = xGdx.assets.getSkin(GlobalConstants.IQ_TEST_SKIN)

        val btn = TextButton("Play", skin, "green")
        GlobalUtil.resizeUI(btn, 0.5f)
        btn.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                xGdx.setScene(
                    CompleteSpelling(), SceneTransitions.slide(1f, SceneTransitionDirection.UP,
                        true, Interpolation.pow5Out))
            }
        })

        val table = Table()
        table.setFillParent(true)

        table.add(btn).center().width(btn.width).height((btn.height))

        canvas.addActor(table)
    }
}