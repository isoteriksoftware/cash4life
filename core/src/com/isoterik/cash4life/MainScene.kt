package com.isoterik.cash4life

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.utils.Timer
import com.isoterik.cash4life.cashpuzzles.CashPuzzlesSplash
import com.isoterik.cash4life.doublecash.DoubleCashSplash
import com.isoterik.cash4life.iqtest.IQTestSplash
import io.github.isoteriktech.xgdx.Scene
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions

class MainScene : Scene() {
    override fun transitionedToThisScene(previousScene: Scene?) {
        Timer.post(object : Timer.Task() {
            override fun run() {
                xGdx.setScene(CashPuzzlesSplash(), SceneTransitions.slice(1f, SceneTransitionDirection.UP_DOWN,
                        15, Interpolation.pow5))
            }
        })
    }
}