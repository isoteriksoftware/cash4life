package com.isoterik.cash4life.iqtest

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.isoterik.cash4life.GlobalConstants
import com.isoterik.cash4life.GlobalUtil
import com.isoterik.cash4life.UserManager
import com.isoterik.cash4life.iqtest.scenes.CompleteSentence
import com.isoterik.cash4life.iqtest.scenes.CompleteSpelling
import io.github.isoteriktech.xgdx.GameObject
import io.github.isoteriktech.xgdx.Scene
import io.github.isoteriktech.xgdx.ui.ActorAnimation
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions
import java.util.*

class IQTestSplash : Scene() {
    private lateinit var skin: Skin

    private lateinit var gamePlayScene: Scene

    private lateinit var userManager: UserManager

    private lateinit var timeAndPrice: HashMap<Float, Int>

    init {
        initializeManagers()

        setBackgroundColor(Color(0.1f, 0.1f, 0.2f, 1.0f))
        setUpCamera()
        setUpUI()

        Constants.init()
    }

    private fun initializeManagers() {
        val g = GameObject()
        g.addComponent(UserManager())
        addGameObject(g)

        userManager = g.getComponent(UserManager::class.java)
    }

    private fun setUpCamera() {
        canvas = Stage(StretchViewport(Constants.GUI_WIDTH.toFloat(), Constants.GUI_HEIGHT.toFloat()))

        input.inputMultiplexer.addProcessor(canvas)

        GlobalUtil.init(gameWorldUnits, canvas, 800f, 800f)
    }

    private fun setUpUI() {
        skin = xGdx.assets.getSkin(GlobalConstants.IQ_TEST_SKIN)

        val background = Image(xGdx.assets.regionForTexture(
            "${GlobalConstants.IQ_TEST_ASSETS_HOME}/images/background-jpg.jpg")
        )
        background.setSize(canvas.width, canvas.height)
        canvas.addActor(background)

        val completeSpellingBtn = TextButton("Complete the Spelling", skin, "green")
        completeSpellingBtn.label.wrap = true
        GlobalUtil.resizeUI(completeSpellingBtn, 0.5f)
        completeSpellingBtn.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                gamePlayScene = CompleteSpelling()
                buyTime(Constants.SPELLING_TIME_AND_PRICES)
            }
        })

        skin = xGdx.assets.getSkin(GlobalConstants.IQ_TEST_SKIN)

        val completeSentenceBtn = TextButton("Complete the Sentence", skin, "green")
        completeSentenceBtn.label.wrap = true
        GlobalUtil.resizeUI(completeSentenceBtn, 0.5f)
        completeSentenceBtn.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                gamePlayScene = CompleteSentence()
                buyTime(Constants.SENTENCE_TIME_AND_PRICES)
            }
        })

        val table = Table()
        table.setFillParent(true)

        table.top()
        table.add(completeSpellingBtn).center().width(completeSpellingBtn.width).height((completeSpellingBtn.height)).padTop(300f)
        table.row()
        table.add(completeSentenceBtn).center().width(completeSentenceBtn.width).height((completeSentenceBtn.height)).padTop(50f)

        canvas.addActor(table)
    }

    private fun buyTime(timeAndPrice: HashMap<Float, Int>) {
        skin = xGdx.assets.getSkin(GlobalConstants.CASH_PUZZLES_SKIN)
        val window = Window("", skin)

        this.timeAndPrice = timeAndPrice

        val timeAndPricesTable = generatePricingTable(window)

        popUpBuyTimeWindow(window, timeAndPricesTable)
    }

    private fun generatePricingTable(window: Window): Table {
        val timeArray: Array<Any> = timeAndPrice.keys.toTypedArray()
        Arrays.sort(timeArray)

        val timeAndPricesButton = arrayOfNulls<TextButton>(timeAndPrice.size)
        val styleNames = arrayOf(
            "p_pink", "p_purple", "p_cyan", "p_blue", "p_green", "p_orange"
        )
        for (i in timeAndPricesButton.indices) {
            val time = timeArray[i] as Float
            val price = timeAndPrice[time]

            val spaces = "              "
            val displayName = """$spaces${timeToString(time)}
$spaces$price naira"""
            val styleName = styleNames[i]

            timeAndPricesButton[i] = TextButton(displayName, skin, styleName)
            timeAndPricesButton[i]!!.label.setAlignment(Align.left)
            timeAndPricesButton[i]!!.label.setFontScale(0.7f)
            timeAndPricesButton[i]?.setSize(418.7654f, 85.1602f)

            if (userManager.user.accountBalance < price!!) {
                timeAndPricesButton[i]!!.isDisabled = true
                timeAndPricesButton[i]!!.label.color = Color.GRAY
            }

            timeAndPricesButton[i]!!.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent, actor: Actor) {
                    (gamePlayScene as CompleteSpelling).setTime(time)
                    userManager.withdraw(price.toFloat())
                    //userManager.reset()
                    canvas.actors.removeValue(window, true)
                    toGamePlayScene()
                }
            })
        }

        val timeAndPricesTable = Table()
        for (textButton in timeAndPricesButton) {
            timeAndPricesTable.add(textButton).center().expandX().padBottom(50f).width(textButton!!.width).height(
                textButton!!.height
            )
            timeAndPricesTable.row()
        }

        return timeAndPricesTable
    }

    private fun popUpBuyTimeWindow(window: Window, timeAndPricesTable: Table) {
        val accountBalanceLabel = Label(userManager.user.accountBalanceAsString, skin)
        accountBalanceLabel.setFontScale(0.7f)

        val closeBtn = Button(skin, "close")
        GlobalUtil.resizeUI(closeBtn, 0.5f)
        closeBtn.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                canvas.actors.removeValue(window, true)
            }
        })

        val titleLabel = Label("BUY TIME", skin, "header")
        GlobalUtil.resizeUI(titleLabel, 0.5f)
        titleLabel.setAlignment(Align.center)

        val scrollPane = ScrollPane(timeAndPricesTable)
        scrollPane.setScrollingDisabled(true, false)

        //window.setDebug(true);

        //window.setDebug(true);
        window.setFillParent(true)
        window.padTop(20f).padBottom(20f).padRight(10f).padLeft(10f)
        window.top()

        window.add(closeBtn).left().width(closeBtn.width).height(closeBtn.height)
        window.add(accountBalanceLabel).right()
        window.row()

        window.add(titleLabel).center().colspan(2).padTop(10f).width(titleLabel.width).height(titleLabel.height)
        window.row()

        window.add(scrollPane).colspan(2).expandX().padTop(20f).width(450f)
        window.pack()
        canvas.addActor(window)
        ActorAnimation.instance().slideIn(window, ActorAnimation.DOWN, .7f, Interpolation.swingOut)
    }

    private fun toGamePlayScene() {
        xGdx.setScene(
            gamePlayScene, SceneTransitions.slide(1f, SceneTransitionDirection.UP,
                true, Interpolation.pow5Out))
        (gamePlayScene as CompleteSpelling).init()
    }

    private fun timeToString(timeInMins: Float): String? {
        var timeString = "${timeInMins.toInt()} minutes"
        if (timeInMins < 1f) {
            val timeInSecs = (timeInMins * 60).toInt()
            timeString = "$timeInSecs seconds"
        } else if (timeInMins > 60f) {
            val timeInHours = (timeInMins / 60).toInt()
            timeString = "$timeInHours hours"
        }
        return timeString
    }
}