package com.isoterik.cash4life

import io.github.isoteriktech.xgdx.Scene
import io.github.isoteriktech.xgdx.XGdxGame
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions

class Cash4Life : XGdxGame() {
    override fun initGame(): Scene {
        splashTransition = SceneTransitions.fade(1f)
        return Scene()
    }
}