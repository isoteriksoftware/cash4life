package com.isoterik.cash4life

import io.github.isoteriktech.xgdx.Scene
import io.github.isoteriktech.xgdx.XGdxGame
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions

class Cash4Life : XGdxGame() {
    override fun initGame(): Scene {
        xGdx.defaultSettings.VIEWPORT_WIDTH = GlobalConstants.GUI_WIDTH.toFloat()
        xGdx.defaultSettings.VIEWPORT_HEIGHT = GlobalConstants.GUI_HEIGHT.toFloat()

        splashTransition = SceneTransitions.fade(1f)
        return MainScene()
    }
}