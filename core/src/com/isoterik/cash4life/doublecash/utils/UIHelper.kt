package com.isoterik.cash4life.doublecash.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.TextInputListener
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.isoterik.cash4life.GlobalConstants
import com.isoterik.cash4life.PreferenceHelper
import com.isoterik.cash4life.doublecash.GamePlayScene
import com.isoterik.cash4life.doublecash.utils.Util.playClickSound
import io.github.isoteriktech.xgdx.XGdx
import io.github.isoteriktech.xgdx.asset.GameAssetsLoader
import io.github.isoteriktech.xgdx.ui.ActorAnimation


class UIHelper(private val canvas: Stage) {
    private val assetsLoader: GameAssetsLoader
    private val skin: Skin
    var balanceLabel: Label? = null
    var btnDeposit: Button? = null
    var btnSettings: Button? = null
    var btnHelp: Button? = null
    var btnSound: Button? = null
    var btnQuit: Button? = null
    var menuWindow: Window? = null
    var menuSelectorWindow: Window? = null
    private var menuContainer: Table? = null

    fun showYourTurn() {
        val label = Label("YOUR  TURN", skin, "white64")
        label.setAlignment(Align.center)
        val window = newTypedWindow("info")
        window.pad(20f)
        window.add(label).padRight(20f).padLeft(20f)
        window.pack()
        centerActorOrigin(window)
        centerActor(window, canvas)
        canvas.addActor(window)
        ActorAnimation.instance().grow(window, .7f, Interpolation.bounceOut)
        window.addAction(Actions.delay(1.5f, Actions.run { ActorAnimation.instance().slideOutThenRemove(window, ActorAnimation.UP, .7f, Interpolation.swingOut) }))
    }

    fun showOpponentTurn(onReady: Runnable) {
        val label = Label("OPPONENT  TURN", skin, "white64")
        label.setAlignment(Align.center)
        val window = newTypedWindow("info")
        window.pad(20f)
        window.add(label).padRight(20f).padLeft(20f)
        window.pack()
        centerActorOrigin(window)
        centerActor(window, canvas)
        canvas.addActor(window)
        ActorAnimation.instance().grow(window, .7f, Interpolation.bounceOut)
        window.addAction(Actions.delay(1.5f, Actions.run {
            ActorAnimation.instance().slideOutThenRemove(window, ActorAnimation.UP, .7f, Interpolation.swingOut)
            onReady.run()
        }))
    }

    fun newWindow(): Window {
        val window = Window("", skin)
        window.setKeepWithinStage(false)
        window.isModal = true
        val pad = 40
        window.padLeft(pad.toFloat()).padRight(pad.toFloat()).padBottom(pad.toFloat())
        return window
    }

    fun newTypedWindow(type: String?): Window {
        val window = Window("", skin, type)
        window.setKeepWithinStage(false)
        window.isModal = true
        val pad = 40
        window.padLeft(pad.toFloat()).padRight(pad.toFloat()).padBottom(pad.toFloat())
        return window
    }

    fun showStakeDialog(stakeListener: StakeListener, balance: Int) {
        val window = newWindow()
        val title = Label("What's your stake?".toUpperCase(), skin, "green")
        title.setAlignment(Align.center)
        val amount = Label("Amount: ", skin, "green24")
        val stake = TextField("100", skin)
        stake.clearListeners()
        stake.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                playClickSound()
                Gdx.input.getTextInput(object : TextInputListener {
                    override fun input(text: String) {
                        try {
                            val amount = text.toInt()
                            if (amount in 50..balance) stake.text = text
                        } catch (ignored: Exception) {
                        }
                    }

                    override fun canceled() {}
                }, "Enter Stake Amount", stake.text, "", Input.OnscreenKeyboardType.NumberPad)
            }
        })
        val btnHighest = TextButton("Highest Card", skin)
        btnHighest.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                playClickSound()
                val duration = .5f
                ActorAnimation.instance().slideOutThenRemove(window, ActorAnimation.UP, duration, Interpolation.pow5Out)
                window.addAction(Actions.delay(duration, Actions.run { stakeListener.onStake(GamePlayScene.GameType.HIGHER, stake.text.toInt()) }))
            }
        })
        val btnLowest = TextButton("Lowest Card", skin)
        btnLowest.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                playClickSound()
                val duration = .5f
                ActorAnimation.instance().slideOutThenRemove(window, ActorAnimation.UP, duration, Interpolation.pow5Out)
                window.addAction(Actions.delay(duration, Actions.run { stakeListener.onStake(GamePlayScene.GameType.LOWER, stake.text.toInt()) }))
            }
        })
        val stakeTbl = Table()
        stakeTbl.left()
        stakeTbl.add(amount).padRight(20f)
        stakeTbl.add(stake).height(40f).expandX().fillX()
        window.top().padTop(50f)
        window.add(title).expandX().fillX().left().colspan(2)
        window.row().padTop(50f)
        window.add(stakeTbl).expandX().fillX().colspan(2)
        window.row().padTop(30f)
        window.add(btnHighest).expandX().fillX().padRight(30f)
        window.add(btnLowest).expandX().fillX()
        window.pack()
        canvas.addActor(window)
        centerActor(window, canvas)
        centerActorOrigin(window)
        ActorAnimation.instance().slideIn(window, ActorAnimation.DOWN, .7f, Interpolation.swingOut)
    }

    fun showGameOverDialog(status: String?, earning: String?, gameOverListener: GameOverListener) {
        val window = newWindow()
        val statusLbl = Label(status, skin, "green64")
        statusLbl.setAlignment(Align.center)
        val earningsLabel = Label("EARNINGS", skin, "main32")
        val earnings = Label(earning, skin, "money")
        val labelTbl = Table()
        labelTbl.left()
        labelTbl.add(earningsLabel).padRight(30f)
        labelTbl.add(earnings).expandX().fillX()
        val btnHome = Button(skin, "home")
        btnHome.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                playClickSound()
                val duration = .5f
                ActorAnimation.instance().slideOutThenRemove(window, ActorAnimation.UP, duration, Interpolation.pow5Out)
                window.addAction(Actions.delay(duration, Actions.run { gameOverListener.onAction(GameOverListener.Action.HOME) }))
            }
        })
        val btnRestart = Button(skin, "restart")
        btnRestart.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                playClickSound()
                val duration = .5f
                ActorAnimation.instance().slideOutThenRemove(window, ActorAnimation.UP, duration, Interpolation.pow5Out)
                window.addAction(Actions.delay(duration, Actions.run { gameOverListener.onAction(GameOverListener.Action.RESTART) }))
            }
        })
        val btnQuit = Button(skin, "quit")
        btnQuit.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                playClickSound()
                val duration = .5f
                ActorAnimation.instance().slideOutThenRemove(window, ActorAnimation.UP, duration, Interpolation.pow5Out)
                window.addAction(Actions.delay(duration, Actions.run { gameOverListener.onAction(GameOverListener.Action.QUIT) }))
            }
        })
        val bottom = Table()
        val pad = 20
        bottom.add(btnHome).expandX().padRight(pad.toFloat())
        bottom.add(btnRestart).expandX().padRight(pad.toFloat())
        bottom.add(btnQuit).expandX()
        window.top().padTop(50f)
        window.add(statusLbl).expandX().fillX().left().colspan(2)
        window.row().padTop(100f)
        window.add(labelTbl).expandX().fillX()
        window.row().padTop(50f)
        window.add(bottom).bottom().expand().fillX()
        canvas.addActor(window)
        window.pack()
        centerActor(window, canvas)
        centerActorOrigin(window)
        ActorAnimation.instance().slideIn(window, ActorAnimation.DOWN, .7f, Interpolation.swingOut)
    }

    fun setupUI(menuListener: MenuListener) {
        val window = newTypedWindow("balance")
        window.isModal = false
        window.pad(0f).padLeft(20f)
        menuSelectorWindow = newTypedWindow("balance")
        menuSelectorWindow!!.isModal = false
        menuSelectorWindow!!.pad(0f)
        menuWindow = newTypedWindow("transparent")
        menuWindow!!.pad(0f).padTop(5f)
        balanceLabel = Label("000000", skin, "main32")
        //balanceLabel.setAlignment(Align.center);
        balanceLabel!!.pack()
        val settings = Label("Menu", skin, "main32")
        settings.pack()
        btnDeposit = Button(skin, "plus-small")
        btnSettings = Button(skin, "settings-small")
        btnQuit = Button(skin, "quit-small")
        btnSound = Button(skin, "sound-small")
        btnSound!!.isChecked = !PreferenceHelper.instance()!!.isSoundEnabled
        btnHelp = Button(skin, "help-small")
        btnSettings!!.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                playClickSound()
                showMenu()
            }
        })
        btnQuit!!.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                playClickSound()
                menuContainer!!.addAction(Actions.delay(.5f, Actions.run { menuListener.onAction(MenuListener.Action.QUIT) }))
            }
        })
        btnHelp!!.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                playClickSound()
                menuContainer!!.addAction(Actions.delay(.5f, Actions.run { menuListener.onAction(MenuListener.Action.HELP) }))
            }
        })
        btnSound!!.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                playClickSound()
                menuContainer!!.addAction(Actions.delay(.5f, Actions.run { menuListener.onAction(MenuListener.Action.SOUND) }))
            }
        })
        val size = 70f
        val spacing = 30
        window.add<Label>(balanceLabel).left().expandX().padRight(20f)
        window.add<Button>(btnDeposit).right().size(size)
        menuSelectorWindow!!.add<Button>(btnSettings).size(size).left()
        menuSelectorWindow!!.add(settings).right().expandX().padRight(20f)
        menuWindow!!.add<Button>(btnHelp).size(size).row()
        menuWindow!!.add<Button>(btnSound).size(size).padTop(5f).padBottom(5f).row()
        menuWindow!!.add<Button>(btnQuit).size(size).row()
        menuSelectorWindow!!.pack()
        menuSelectorWindow!!.x = canvas.width - menuSelectorWindow!!.width - spacing
        menuSelectorWindow!!.y = canvas.height - menuSelectorWindow!!.height - spacing
        window.pack()
        window.x = spacing.toFloat()
        window.y = canvas.height - window.height - spacing
        canvas.addActor(window)
        canvas.addActor(menuSelectorWindow)
    }

    private fun closeMenu() {
        val duration = .5f
        menuWindow!!.addAction(Actions.scaleTo(1f, 0f, duration, Interpolation.pow5))
        menuContainer!!.addAction(Actions.delay(duration, Actions.removeActor()))
    }

    private fun showMenu() {
        val duration = .5f
        menuContainer = Table()
        menuContainer!!.setFillParent(true)
        menuContainer!!.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                if (menuContainer!!.hasParent()) {
                    closeMenu()
                }
            }
        })
        menuWindow!!.pack()
        val width = 90
        menuContainer!!.top().right()
        menuContainer!!.add(menuWindow).width(width.toFloat()).padTop(menuSelectorWindow!!.height + 30)
                .padRight(menuSelectorWindow!!.width - width + 40)
        menuWindow!!.originX = width / 2f
        menuWindow!!.originY = menuWindow!!.height
        ActorAnimation.instance().grow(menuWindow, duration,
                Interpolation.pow5Out)
        menuWindow!!.setScale(1f, 0f)
        canvas.addActor(menuContainer)
    }

    interface StakeListener {
        fun onStake(gameType: GamePlayScene.GameType?, amount: Int)
    }

    interface GameOverListener {
        enum class Action {
            HOME, RESTART, QUIT
        }

        fun onAction(action: Action?)
    }

    interface MenuListener {
        enum class Action {
            HELP, SOUND, QUIT
        }

        fun onAction(action: Action?)
    }

    companion object {
        fun centerActor(actor: Actor, canvas: Stage) {
            actor.x = (canvas.width - actor.width) / 2f
            actor.y = (canvas.height - actor.height) / 2f
        }

        fun centerActorOrigin(actor: Actor) {
            actor.originX = actor.width / 2f
            actor.originY = actor.height / 2f
        }
    }

    init {
        assetsLoader = XGdx.instance().assets
        skin = assetsLoader.getSkin(GlobalConstants.DOUBLE_CASH_SKIN)
    }
}
