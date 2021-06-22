package com.isoterik.cash4life

import com.isoterik.cash4life.doublecash.DoubleCashSplash
import io.github.isoteriktech.xgdx.Scene
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions

class MainScene : Scene() {
    override fun transitionedToThisScene(previousScene: Scene?) {
        xGdx.setScene(DoubleCashSplash(), SceneTransitions.fade(3f))
    }
}