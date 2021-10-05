package com.isoterik.cash4life

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Timer
import com.isoterik.cash4life.cashpuzzles.CashPuzzlesSplash
import com.isoterik.cash4life.doublecash.DoubleCashSplash
import com.isoterik.cash4life.iqtest.IQTestSplash
import io.github.isoteriktech.xgdx.Scene
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions

class MainScene : Scene() {
    private lateinit var skin: Skin

    private lateinit var splashScene: Scene

    override fun transitionedToThisScene(previousScene: Scene?) {
        setUpUI()
    }

    private fun setUpUI() {
        val color = Color(0 / 255f, 0 / 255f, 0 / 255f, 1f)

        skin = xGdx.assets.getSkin(GlobalConstants.IQ_TEST_SKIN)

        val background = Image(xGdx.assets.regionForTexture(
            GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/images/bg.png")
        )
        background.setSize(canvas.width, canvas.height)
        background.color = color
        canvas.addActor(background)

        val doubleCashBtn = TextButton("Play Double Cash", skin, "green")
        doubleCashBtn.label.wrap = true
        GlobalUtil.resizeUI(doubleCashBtn, 0.5f)
        doubleCashBtn.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                splashScene = DoubleCashSplash()
                switchScene()
            }
        })

        val cashPuzzlesBtn = TextButton("Play Cash Puzzles", skin, "green")
        cashPuzzlesBtn.label.wrap = true
        GlobalUtil.resizeUI(cashPuzzlesBtn, 0.5f)
        cashPuzzlesBtn.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                splashScene = CashPuzzlesSplash()
                switchScene()
            }
        })

        val iqTestBtn = TextButton("Play IQ Test", skin, "green")
        iqTestBtn.label.wrap = true
        GlobalUtil.resizeUI(iqTestBtn, 0.5f)
        iqTestBtn.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                splashScene = IQTestSplash()
                switchScene()
            }
        })

        val table = Table()
        table.setFillParent(true)

        table.top()

        table.add(doubleCashBtn).center().width(iqTestBtn.width).height((iqTestBtn.height)).padTop(400f)
        table.row()
        table.add(cashPuzzlesBtn).center().width(cashPuzzlesBtn.width).height((cashPuzzlesBtn.height)).padTop(50f)
        table.row()
        table.add(iqTestBtn).center().width(iqTestBtn.width).height((iqTestBtn.height)).padTop(50f)

        canvas.addActor(table)
    }

    private fun switchScene() {
        Timer.post(object : Timer.Task() {
            override fun run() {
                xGdx.setScene(splashScene, SceneTransitions.slice(1f, SceneTransitionDirection.UP_DOWN,
                    15, Interpolation.pow5))
            }
        })
    }
}