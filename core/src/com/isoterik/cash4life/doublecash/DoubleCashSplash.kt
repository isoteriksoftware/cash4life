package com.isoterik.cash4life.doublecash

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.isoterik.cash4life.GlobalConstants
import com.isoterik.cash4life.PreferenceHelper
import com.isoterik.cash4life.doublecash.utils.Util
import io.github.isoteriktech.xgdx.Scene
import io.github.isoteriktech.xgdx.ui.ActorAnimation
import io.github.isoteriktech.xgdx.utils.GameWorldUnits
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions


class DoubleCashSplash : Scene() {
    private var btnSound: Button? = null

    private fun setupCamera() {
        canvas = Stage(StretchViewport(Constants.GUI_WIDTH.toFloat(), Constants.GUI_HEIGHT.toFloat()))

        input.inputMultiplexer.addProcessor(canvas)
    }

    private fun setupUI() {
        val bg: Image = Image(this.xGdx.assets.regionForTexture(
                "${GlobalConstants.DOUBLE_CASH_ASSETS_HOME}/images/background_red.png"))
        canvas.addActor(bg)

        val logo: Image = Image(this.xGdx.assets.regionForTexture(
                "${GlobalConstants.DOUBLE_CASH_ASSETS_HOME}/images/logo.png", true))

        val root = Table()
        root.setFillParent(true)

        val skin: Skin = this.xGdx.assets.getSkin(GlobalConstants.DOUBLE_CASH_SKIN)

        val btnPlay = TextButton("Play", skin, "large")
        btnPlay.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                Util.playClickSound()
                toGamePlayScene()
            }
        })

        val btnHelp = Button(skin, "help")
        btnHelp.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                Util.playClickSound()
                Gdx.net.openURI("https://cash4life.com.ng/double-cash/help")
            }
        })

        btnSound = Button(skin, "sound")
        btnSound!!.isChecked = !PreferenceHelper.instance()!!.isSoundEnabled
        btnSound!!.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                Util.playClickSound()
                PreferenceHelper.instance()?.isSoundEnabled = !btnSound!!.isChecked
                PreferenceHelper.instance()!!.saveChanges()
            }
        })

        val btnQuit = Button(skin, "quit")
        btnQuit.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                Util.playClickSound()
                this@DoubleCashSplash.xGdx.app.exit()
            }
        })

        val tbl = Table()
        tbl.padBottom(50f).padRight(50f).padTop(150f)
        tbl.add(btnPlay).expand().fillX().colspan(3)
        tbl.row()
        tbl.add(btnHelp)
        tbl.add<Button>(btnSound)
        tbl.add(btnQuit)
        root.left()
        root.add(logo).left().expandX().padLeft(50f)
        root.add(tbl).expand().fill()
        ActorAnimation.instance().setup(canvas.getWidth(), canvas.getHeight())
        canvas.addActor(root)
    }

    private fun toGamePlayScene() {
        this.xGdx.setScene(GamePlayScene(), SceneTransitions.slide(1f, SceneTransitionDirection.UP,
                true, Interpolation.pow5Out))
    }

    override fun transitionedToThisScene(previousScene: Scene?) {
        btnSound!!.isChecked = !PreferenceHelper.instance()!!.isSoundEnabled
    }

    init {
        PreferenceHelper.init(GlobalConstants.DOUBLE_CASH_PREFERENCES)

        setupCamera()
        setBackgroundColor(Color(.1f, .1f, .2f, 1f))
        setupUI()
    }
}
