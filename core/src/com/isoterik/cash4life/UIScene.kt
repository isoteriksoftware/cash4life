package com.isoterik.cash4life

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.isoterik.cash4life.iqtest.Constants
import io.github.isoteriktech.xgdx.Scene

class UIScene : Scene() {
    private var uiHelper: UIHelper? = null

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
            "${GlobalConstants.SHARED_ASSETS_HOME}/images/Background.png")
        )
        background.setSize(canvas.width, canvas.height)
        canvas.addActor(background)

        uiHelper = UIHelper(canvas, xGdx)
        uiHelper!!.showLogin()
    }
}