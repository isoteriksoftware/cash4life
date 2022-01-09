package com.isoterik.cash4life

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Timer
import com.isoterik.cash4life.basketball.BasketballSplash
import com.isoterik.cash4life.cashpuzzles.CashPuzzlesSplash
import com.isoterik.cash4life.doublecash.DoubleCashSplash
import com.isoterik.cash4life.doublecash.utils.Util
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

        val doubleCashBtn = Button(skin, "doublecash")
        doubleCashBtn.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                splashScene = DoubleCashSplash()
                switchScene()
            }
        })

        val cashPuzzlesBtn = Button(skin, "puzzles")
        cashPuzzlesBtn.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                splashScene = CashPuzzlesSplash()
                switchScene()
            }
        })

        val iqTestBtn = Button(skin, "fill")
        iqTestBtn.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                splashScene = IQTestSplash()
                switchScene()
            }
        })

        val basketballBtn = Button(skin, "basketball")
        basketballBtn.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                splashScene = BasketballSplash()
                switchScene()
            }
        })


        //window.setDebug(true);
        window.setFillParent(true)
        window.padTop(150f)
        window.top()

        window.add(closeBtn).left().padLeft(20f).size(50f)
        window.row()

        val size = 150f

        window.add(doubleCashBtn).left().center().expandX().size(size).padTop(50f)
        window.add(cashPuzzlesBtn).right().center().expandX().size(size).padTop(50f)
        window.row()
        window.add(iqTestBtn).left().center().expandX().size(size).padTop(50f)
        window.add(basketballBtn).right().center().expandX().size(size).padTop(50f)

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
        usernameTextField.messageText = " Username"
        usernameTextField.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {

            }
        })

        val passwordTextField = TextField("", skin)
        passwordTextField.messageText = " Password"
        passwordTextField.setPasswordCharacter('*')
        passwordTextField.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                passwordTextField.isPasswordMode = true
            }
        })

        val noAccountLabel = Label("Don't have an account?", skin, "default2")
        noAccountLabel.setFontScale(0.5f)

        val signUpLabel = Label(" Sign up! ", skin)
        signUpLabel.setFontScale(0.5f)
        signUpLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                table.clear()
                showRegister()
            }
        })

        val window = Window("", skin)
        //window.debug = true
        window.isMovable = false
        window.top()
        window.add(usernameTextField).expandX().colspan(2).width(210f).height(50f).padTop(40f)
        window.row()
        window.add(passwordTextField).expandX().colspan(2).width(210f).height(50f).padTop(30f)
        window.row().padTop(20f)
        window.add(noAccountLabel).expandX().right()
        window.add(signUpLabel).expandX().left()
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

    fun showRegister() {
        val table = Table()

        val registerLabel = Label("REGISTER", skin, "window-header")
        registerLabel.setAlignment(Align.center)

        val acceptLabel = Label("", skin, "button-square-background")
        val acceptButton = Button(skin, "accept")
        acceptButton.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                table.clear()
                showLogin()
            }
        })
        val stack = Stack()
        stack.add(acceptLabel)
        stack.add(acceptButton)

        val fullnameTextField = TextField("", skin)
        fullnameTextField.messageText = " Fullname"
        fullnameTextField.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {

            }
        })

        val emailTextField = TextField("", skin)
        emailTextField.messageText = " Email"
        emailTextField.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {

            }
        })

        val usernameTextField = TextField("", skin)
        usernameTextField.messageText = " Username"
        usernameTextField.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {

            }
        })

        val newPasswordTextField = TextField("", skin)
        newPasswordTextField.messageText = " New Password"
        newPasswordTextField.setPasswordCharacter('*')
        newPasswordTextField.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                newPasswordTextField.isPasswordMode = true
            }
        })

        val confirmPasswordTextField = TextField("", skin)
        confirmPasswordTextField.messageText = " Repeat Password"
        confirmPasswordTextField.setPasswordCharacter('*')
        confirmPasswordTextField.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                confirmPasswordTextField.isPasswordMode = true
            }
        })

        val haveAccountLabel = Label("Have an account?", skin, "default2")
        haveAccountLabel.setFontScale(0.5f)

        val signInLabel = Label(" Sign in! ", skin)
        signInLabel.setFontScale(0.5f)
        signInLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                table.clear()
                showLogin()
            }
        })

        val window = Window("", skin)
        window.isMovable = false
        window.top()
        window.add(fullnameTextField).expandX().colspan(2).width(210f).height(50f).padTop(40f)
        window.row()
        window.add(emailTextField).expandX().colspan(2).width(210f).height(50f).padTop(40f)
        window.row()
        window.add(usernameTextField).expandX().colspan(2).width(210f).height(50f).padTop(40f)
        window.row()
        window.add(newPasswordTextField).expandX().colspan(2).width(210f).height(50f).padTop(30f)
        window.row()
        window.add(confirmPasswordTextField).expandX().colspan(2).width(210f).height(50f).padTop(30f).padBottom(40f)
        window.pack()

        table.setFillParent(true)
        table.top()
        table.padTop(80f)

        table.add(registerLabel).width(200f).height(70f)
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