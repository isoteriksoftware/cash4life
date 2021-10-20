package com.isoterik.cash4life

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Timer
import com.isoterik.cash4life.cashpuzzles.CashPuzzlesSplash
import com.isoterik.cash4life.doublecash.DoubleCashSplash
import com.isoterik.cash4life.iqtest.IQTestSplash
import io.github.isoteriktech.xgdx.Scene
import io.github.isoteriktech.xgdx.XGdx
import io.github.isoteriktech.xgdx.asset.GameAssetsLoader
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions

class UIHelper(private val canvas: Stage, private val xGdx: XGdx) {
    private val assetsLoader: GameAssetsLoader
    private var skin: Skin

    private lateinit var splashScene: Scene

    fun showHomeMenu() {
        val table = Table()

        val exitButton = Button(skin, "exit")
        exitButton.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                xGdx.exit()
            }
        })

        val logo = Label("Cash4Life", skin)
        logo.setFontScale(2f)

        val playButton = Button(skin, "play")
        playButton.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                table.clear()
                showGameSelectionMenu()
            }
        })
        val playButtonBg = Label("", skin, "button-square-background")
        val playButtonStack = Stack()
        playButtonStack.add(playButtonBg)
        playButtonStack.add(playButton)

        val settingsButton = Button(skin, "settings")
        val settingsButtonBg = Label("", skin, "button-square-background")
        val settingsButtonStack = Stack()
        settingsButtonStack.add(settingsButtonBg)
        settingsButtonStack.add(settingsButton)

        val profileButton = Button(skin, "profile")
        val profileButtonBg = Label("", skin, "button-square-background")
        val profileButtonStack = Stack()
        profileButtonStack.add(profileButtonBg)
        profileButtonStack.add(profileButton)

        val starButton = Button(skin, "star")
        val starButtonBg = Label("", skin, "button-square-background")
        val starButtonStack = Stack()
        starButtonStack.add(starButtonBg)
        starButtonStack.add(starButton)

        table.setFillParent(true)
        table.top()

        table.add(exitButton).padTop(20f).size(50f).colspan(3).left().padLeft(10f)
        table.row()

        table.add(logo).center().padTop(120f).colspan(3)
        table.row()

        table.add(playButtonStack).center().padTop(120f).size(80f).expandX().colspan(3)
        table.row()

        table.add(settingsButtonStack).padTop(120f).size(60f).expandX()
        table.add(profileButtonStack).padTop(120f).size(60f).expandX()
        table.add(starButtonStack).padTop(120f).size(60f).expandX()
        table.row()
        canvas.addActor(table)

    }

    fun showGameSelectionMenu() {
        skin = assetsLoader.getSkin(GlobalConstants.CASH_PUZZLES_SKIN)

        val window = Window("", skin)

        skin = assetsLoader.getSkin(GlobalConstants.SHARED_ASSETS_SKIN)

        val closeBtn = Button(skin, "exit")
        GlobalUtil.resizeUI(closeBtn, 0.5f)
        closeBtn.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                canvas.actors.removeValue(window, true)
                showHomeMenu()
            }
        })

        val doubleCashBtn = TextButton("Play Double Cash", skin)
        doubleCashBtn.label.wrap = true
        doubleCashBtn.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                splashScene = DoubleCashSplash()
                switchScene()
            }
        })

        val cashPuzzlesBtn = TextButton("Play Cash Puzzles", skin)
        cashPuzzlesBtn.label.wrap = true
        cashPuzzlesBtn.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                splashScene = CashPuzzlesSplash()
                switchScene()
            }
        })

        val iqTestBtn = TextButton("Play IQ Test", skin)
        iqTestBtn.label.wrap = true
        iqTestBtn.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                splashScene = IQTestSplash()
                switchScene()
            }
        })


        //window.setDebug(true);
        window.setFillParent(true)
        window.padTop(150f).padBottom(20f).padRight(10f).padLeft(10f)
        window.top()

        window.add(closeBtn).left().padLeft(12f).size(50f)
        window.row()

        window.add(doubleCashBtn).center().width(400f).height(100f).padTop(20f)
        window.row()
        window.add(cashPuzzlesBtn).center().width(400f).height(100f).padTop(50f)
        window.row()
        window.add(iqTestBtn).center().width(400f).height(100f).padTop(50f)

        canvas.addActor(window)
    }

    private fun switchScene() {
        Timer.post(object : Timer.Task() {
            override fun run() {
                xGdx.setScene(splashScene, SceneTransitions.slice(1f, SceneTransitionDirection.UP_DOWN,
                    15, Interpolation.pow5))
            }
        })
    }

    fun showLogin() {
        val table = Table()

        val loginLabel = Label("LOGIN", skin, "window-header")
        loginLabel.setAlignment(Align.center)

        val acceptLabel = Label("", skin, "button-square-background")
        val acceptButton = Button(skin, "accept")
        acceptButton.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                table.clear()
                showHomeMenu()
            }
        })
        val stack = Stack()
        stack.add(acceptLabel)
        stack.add(acceptButton)

        val usernameTextField = TextField("", skin)
        usernameTextField.alignment = Align.center
        usernameTextField.messageText = "Username"
        usernameTextField.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {

            }
        })

        val passwordTextField = TextField("", skin)
        passwordTextField.alignment = Align.center
        passwordTextField.messageText = "Password"
        passwordTextField.setPasswordCharacter('*')
        passwordTextField.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                passwordTextField.isPasswordMode = true
            }
        })

        val rememberMeCheckBox = CheckBox("Remember me", skin)
        rememberMeCheckBox.isChecked = true
        rememberMeCheckBox.label.setFontScale(0.7f)
        rememberMeCheckBox.label.setAlignment(Align.right)

        val window = Window("", skin)
        window.isMovable = false
        window.top()
        window.add(usernameTextField).expandX().width(210f).height(50f).padTop(40f)
        window.row()
        window.add(passwordTextField).expandX().width(210f).height(50f).padTop(30f)
        window.row()
        window.add(rememberMeCheckBox).expandX().width(5f).height(5f).padTop(30f)
        window.pack()

        table.setFillParent(true)
        table.top()
        table.padTop(200f)

        table.add(loginLabel).width(140f).height(70f)
        table.row()

        table.add(window)
        table.row()

        table.add(stack).size(70f)

        canvas.addActor(table)
    }

    fun resize(field: TextField, value: Int) {
        field.setSize(240f, 48f)
    }

    fun resize(box: CheckBox, value: Int) {
        box.setSize(box.width / value, box.height / value)
    }

    init {
        assetsLoader = XGdx.instance().assets
        skin = assetsLoader.getSkin(GlobalConstants.SHARED_ASSETS_SKIN)
    }
}