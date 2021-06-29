package com.isoterik.cash4life.doublecash

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.isoterik.cash4life.GlobalConstants
import com.isoterik.cash4life.PreferenceHelper
import com.isoterik.cash4life.doublecash.components.Card
import com.isoterik.cash4life.doublecash.utils.UIHelper
import com.isoterik.cash4life.doublecash.utils.UIHelper.GameOverListener
import com.isoterik.cash4life.doublecash.utils.UIHelper.StakeListener
import io.github.isoteriktech.xgdx.ActorGameObject
import io.github.isoteriktech.xgdx.GameObject
import io.github.isoteriktech.xgdx.Scene
import io.github.isoteriktech.xgdx.Transform
import io.github.isoteriktech.xgdx.audio.AudioManager
import io.github.isoteriktech.xgdx.input.ITouchListener
import io.github.isoteriktech.xgdx.input.TouchEventData
import io.github.isoteriktech.xgdx.input.TouchTrigger
import io.github.isoteriktech.xgdx.ui.ActorAnimation
import io.github.isoteriktech.xgdx.utils.GameWorldUnits
import io.github.isoteriktech.xgdx.x2d.components.debug.BoxDebugRenderer
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions

class GamePlayScene : Scene() {
    private val cards: Array<ActorGameObject>
    private val pickedCards: Array<ActorGameObject?>
    private val cardBackSprite: TextureRegion
    private val table: GameObject
    private val opponent: GameObject
    private var userChoice: ActorGameObject? = null
    private var opponentChoice: ActorGameObject? = null

    enum class GameType {
        HIGHER, LOWER
    }

    private enum class Turn {
        USER, OPPONENT
    }

    private var gameType = GameType.HIGHER
    private var turn: Turn? = null
    private var stakeAmount = 0
    private var balance = 5000
    private var played = 0
    private var canPlay = false
    private var opponentHasPlayed = false
    private var uiHelper: UIHelper? = null
    private var stakeListener: StakeListener? = null
    private var gameOverListener: GameOverListener? = null
    private var onOpponentReady: Runnable? = null
    private var menuListener: UIHelper.MenuListener? = null

    private fun setupCamera() {
        this.gameWorldUnits = GameWorldUnits(Constants.GUI_WIDTH.toFloat(), Constants.GUI_HEIGHT.toFloat(),
                64f)

        mainCamera.setup(StretchViewport(this.gameWorldUnits.getWorldWidth(), this.gameWorldUnits.getWorldHeight()))
        setupAnimationCanvas(mainCamera.viewport)

        canvas = Stage(StretchViewport(gameWorldUnits.screenWidth, gameWorldUnits.screenHeight))

        input.inputMultiplexer.addProcessor(canvas)

        ActorAnimation.instance().setup(gameWorldUnits.screenWidth, gameWorldUnits.screenHeight)
        uiHelper = UIHelper(canvas)

        menuListener = object : UIHelper.MenuListener {
            override fun onAction(action: UIHelper.MenuListener.Action?) {
                when (action) {
                    UIHelper.MenuListener.Action.QUIT -> quit()
                    UIHelper.MenuListener.Action.HELP -> Gdx.net.openURI("https://cash4life.com.ng/double-cash/help")
                    UIHelper.MenuListener.Action.SOUND -> {
                    PreferenceHelper.instance()?.isSoundEnabled = !uiHelper!!.btnSound!!.isChecked
                    PreferenceHelper.instance()!!.saveChanges()
                }
                }
            }
        }

        uiHelper!!.setupUI(menuListener!!)
        uiHelper!!.balanceLabel!!.setText(balance)

        onOpponentReady = Runnable {
            val waitPeriod = MathUtils.random(1, 3)
            Timer.schedule(object : Timer.Task() {
                override fun run() {
                    if (userChoice == null) {
                        // If we're playing first, we randomly guess what the user will choose
                        playForOpponent(pickedCards.random())
                    } else playForOpponent()
                }
            }, waitPeriod.toFloat())
        }

        stakeListener = object : StakeListener {
            override fun onStake(gameType: GameType?, amount: Int) {
                this@GamePlayScene.gameType = gameType!!
                stakeAmount = amount
                balance -= amount
                uiHelper!!.balanceLabel!!.setText(balance)
                placeCards(false)
                Timer.schedule(object : Timer.Task() {
                    override fun run() {
                        if (MathUtils.randomBoolean(Constants.STARTING_CHANCE)) startUserTurn() else startOpponentTurn()
                    }
                }, 1f)
            }
        }

        gameOverListener = object : GameOverListener {
            override fun onAction(action: GameOverListener.Action?) {
                when (action) {
                    GameOverListener.Action.RESTART -> newGame()
                    GameOverListener.Action.HOME -> goHome()
                    GameOverListener.Action.QUIT -> quit()
                }
            }
        }
    }

    private fun goHome() {
        this.xGdx.sceneManager.revertToPreviousScene(SceneTransitions.slide(1f, SceneTransitionDirection.DOWN,
                false, Interpolation.pow5Out))
    }

    private fun quit() {
        //this.xGdx.app.exit()
        goHome()
    }

    private fun pickRandomCards() {
        cards.shuffle()
        pickedCards.clear()
        for (i in 0 until Constants.MAX_CARDS) pickedCards.add(cards[i])

//        for (GameObject card : pickedCards)
//            System.out.print(card.getComponent(Card.class).number + " ");
    }

    private fun newGame() {
        if (played == 2) {
            pickedCards.add(userChoice, opponentChoice)
            for (gameObject in pickedCards) {
                gameObject!!.getComponent(Card::class.java).setRevealed(false)
                removeGameObject(gameObject)
            }
        }
        pickRandomCards()
        centerCards()
        for (gameObject in pickedCards) addGameObject(gameObject)
        userChoice = null
        opponentChoice = null
        opponentHasPlayed = false
        played = 0
        uiHelper!!.showStakeDialog(stakeListener!!, balance)
    }

    private fun placeCards(isGameOver: Boolean) {
        val max = pickedCards.size

        // Calculate the position of the middle card
        val middleIndex = max / 2
        val middle = pickedCards[middleIndex]
        val mx = table.transform.x + table.transform.width / 2f - middle!!.transform.width / 2f
        var my = table.transform.y + table.transform.height / 2f - middle.transform.height / 2f
        if (isGameOver) my -= this.gameWorldUnits.toWorldUnit(30f)
        middle.transform.setPosition(mx, my)
        val spacing: Float = this.gameWorldUnits.toWorldUnit(10f)
        for (i in 0 until middleIndex) {
            val card = pickedCards[i]
            val t = (middleIndex - i).toFloat()
            val x = mx - getRealWidth(card) * t - spacing * t
            card!!.actorTransform.actor.addAction(Actions.moveTo(x, my,
                    1f, Interpolation.pow5Out))
            card.transform.setPosition(mx, my)
        }
        for (i in middleIndex + 1 until max) {
            val card = pickedCards[i]
            val t = (i - middleIndex).toFloat()
            val x = mx + getRealWidth(card) * t + spacing * t
            card!!.actorTransform.actor.addAction(Actions.moveTo(x, my,
                    1f, Interpolation.pow5Out))
            card.transform.setPosition(mx, my)
        }

        // Play the sound
        AudioManager.instance().playSound(this.xGdx.assets.getSound(
            "${GlobalConstants.DOUBLE_CASH_ASSETS_HOME}/sfx/cardPlace.ogg"), 1f)
    }

    private fun centerCards() {
        val max = pickedCards.size

        // Calculate the position of the middle card
        val middleIndex = max / 2
        val middle = pickedCards[middleIndex]
        val mx = table.transform.x + table.transform.width / 2f - middle!!.transform.width / 2f
        val my = table.transform.y + table.transform.height / 2f - middle.transform.height / 2f
        for (gameObject in pickedCards) gameObject!!.actorTransform.setPosition(mx, my)
    }

    private fun userPlayed() {
        canPlay = false
        played++
        val ot: Transform = opponent.transform
        val card: Card = userChoice!!.getComponent(Card::class.java)
        val x = userChoice!!.transform.x
        val y = userChoice!!.transform.y
        userChoice!!.transform.setPosition(ot.getX() + (ot.getWidth() - card.realWidth) / 2f,
                ot.getY() - card.realHeight)
        userChoice!!.transform.setPosition(userChoice!!.transform().x - this.gameWorldUnits.toWorldUnit(15f),
                this.gameWorldUnits.toWorldUnit(10f))
        val realSize: Vector2 = card.realSize
        val actor = userChoice!!.actorTransform.actor
        actor.clearActions()
        val action1: Action = Actions.moveTo(userChoice!!.transform.x, userChoice!!.transform.y,
                .7f, Interpolation.pow5Out)
        val action2: Action = Actions.scaleTo(1f, 1f, .7f, Interpolation.pow5Out)
        actor.setSize(realSize.x, realSize.y)
        actor.setScale(Card.HIDDEN_SCALE)
        actor.setOrigin(realSize.x / 2f, realSize.y / 2f)
        actor.addAction(Actions.sequence(action1, Actions.run { card.setRevealed(true) }, action2,
                Actions.run {
                    // Play for opponent if not played. Else end the game
                    if (played == 1) startOpponentTurn() else finishGame()
                }))
        userChoice!!.transform.setPosition(x, y)
        pickedCards.removeValue(userChoice, true)
        playSlideSound()
    }

    private fun playForOpponent(userChoice: ActorGameObject? = this.userChoice) {
        opponentChoice = pickedCards.random()
        played++

        // Get the user chosen number
        val userNumber: Int = userChoice!!.getComponent(Card::class.java).number
        if (MathUtils.randomBoolean(Constants.WINNING_CHANCE)) {
            //System.out.println("User to win");

            // Make sure the user wins
            opponentChoice = if (gameType == GameType.HIGHER) minimumPick else maximumPick
        } else {
            // Make sure the opponent wins

            // If by chance the cards are equal, let it be
            if (userNumber != opponentChoice!!.getComponent(Card::class.java).number) {
                if (gameType == GameType.HIGHER) {
                    // If the user chose the highest card then the user wins else we look for a higher card
                    if (userNumber < maximumPick!!.getComponent(Card::class.java).number) {
                        while (userNumber > opponentChoice!!.getComponent(Card::class.java).number) opponentChoice = pickedCards.random()
                    }
                } else {
                    // If the user chose the lowest card then the user wins else we look for a lower card
                    if (userNumber > minimumPick!!.getComponent(Card::class.java).number) {
                        while (userNumber < opponentChoice!!.getComponent(Card::class.java).number) opponentChoice = pickedCards.random()
                    }
                }
            }
        }
        val ot: Transform = opponent.transform
        val card: Card = opponentChoice!!.getComponent(Card::class.java)
        card.setOpponentSelected()
        val x = opponentChoice!!.transform.x
        val y = opponentChoice!!.transform.y
        opponentChoice!!.transform.setPosition(ot.getX() + (ot.getWidth() - card.realWidth) / 2f,
                ot.getY() - card.realHeight)
        val actor = opponentChoice!!.actorTransform.actor
        actor.clearActions()
        val action1: Action = Actions.moveTo(opponentChoice!!.transform.x, opponentChoice!!.transform.y,
                .7f, Interpolation.pow5Out)
        actor.setSize(opponentChoice!!.transform.width, opponentChoice!!.transform.height)
        actor.addAction(Actions.sequence(action1, Actions.run { if (played == 1) startUserTurn() else finishGame() }))
        opponentChoice!!.transform.setPosition(x, y)
        pickedCards.removeValue(opponentChoice, true)
        playSlideSound()
    }

    private fun startUserTurn() {
        canPlay = true
        turn = Turn.USER
        uiHelper!!.showYourTurn()
    }

    private fun startOpponentTurn() {
        canPlay = false
        turn = Turn.OPPONENT
        uiHelper!!.showOpponentTurn(onOpponentReady!!)
    }

    private fun finishGame() {
        revealChoices()
        Timer.schedule(object : Timer.Task() {
            override fun run() {
                val userNumber: Int = userChoice!!.getComponent(Card::class.java).number
                val opponentNumber: Int = opponentChoice!!.getComponent(Card::class.java).number
                if (userNumber == opponentNumber) {
                    val won = stakeAmount / 2
                    balance += won
                    uiHelper!!.showGameOverDialog("IT's A TIE", "+$won", gameOverListener!!)
                    playTieSound()
                } else {
                    val stake = stakeAmount
                    var won = 0
                    if (gameType == GameType.HIGHER) {
                        if (userNumber > opponentNumber) {
                            won = stake * 2
                            uiHelper!!.showGameOverDialog("YOU WIN", "+$won", gameOverListener!!)
                            playWinSound()
                        } else {
                            uiHelper!!.showGameOverDialog("YOU LOSE", "-$stake", gameOverListener!!)
                            playLoseSound()
                        }
                    } else {
                        if (userNumber < opponentNumber) {
                            won = stake * 2
                            uiHelper!!.showGameOverDialog("YOU WIN", "+$won", gameOverListener!!)
                            playWinSound()
                        } else {
                            uiHelper!!.showGameOverDialog("YOU LOSE", "-$stake", gameOverListener!!)
                            playLoseSound()
                        }
                    }
                    balance += won
                }
                uiHelper!!.balanceLabel!!.setText(balance)
            }
        }, 2f)
    }

    private fun revealChoices() {
        // Reveal and resize the remaining cards
        for (card in pickedCards) card!!.getComponent(Card::class.java).setGameOverRevealed()
        placeCards(true)

        // Reveal the opponent's card
        val card: Card = opponentChoice!!.getComponent(Card::class.java)
        card.setRevealed(true)
        val ot: Transform = opponent.transform
        opponentChoice!!.transform.setPosition(ot.getX() + (ot.getWidth() - card.realWidth) / 2f,
                ot.getY() - card.realHeight)
        val actor = opponentChoice!!.actorTransform.actor
        actor.setPosition(opponentChoice!!.transform.x, opponentChoice!!.transform.y)
        actor.setSize(opponentChoice!!.transform.width, opponentChoice!!.transform.height)
    }

    private fun playSlideSound() {
        // Play the sound
        AudioManager.instance().playSound(this.xGdx.assets.getSound(
                "${GlobalConstants.DOUBLE_CASH_ASSETS_HOME}/sfx/cardSlide.ogg"), 1f)
    }

    private fun playWinSound() {
        // Play the sound
        AudioManager.instance().playSound(this.xGdx.assets.getSound(
                "${GlobalConstants.DOUBLE_CASH_ASSETS_HOME}/sfx/you_win.ogg"), 1f)
    }

    private fun playLoseSound() {
        // Play the sound
        AudioManager.instance().playSound(this.xGdx.assets.getSound(
                "${GlobalConstants.DOUBLE_CASH_ASSETS_HOME}/sfx/you_lose.ogg"), 1f)
    }

    private fun playTieSound() {
        // Play the sound
        AudioManager.instance().playSound(this.xGdx.assets.getSound(
                "${GlobalConstants.DOUBLE_CASH_ASSETS_HOME}/sfx/its_a_tie.ogg"), 1f)
    }

    private val maximumPick: ActorGameObject?
        get() {
            var max = 0
            var currentCard = pickedCards.first()
            for (card in pickedCards) {
                val number: Int = card!!.getComponent(Card::class.java).number
                if (number > max) {
                    currentCard = card
                    max = number
                }
            }
            return currentCard
        }

    private val minimumPick: ActorGameObject?
        get() {
            var currentCard = pickedCards.first()
            var min: Int = currentCard!!.getComponent(Card::class.java).number
            for (card in pickedCards) {
                val number: Int = card!!.getComponent(Card::class.java).number
                if (number < min) {
                    currentCard = card
                    min = number
                }
            }
            return currentCard
        }

    private fun getRealWidth(gameObject: GameObject?): Float {
        return gameObject!!.transform.width * gameObject.transform.scaleX
    }

    inner class CardClickListener : ITouchListener {
        override fun onTouch(mappingName: String?, touchEventData: TouchEventData) {
            if (!canPlay || turn != Turn.USER) return
            for (card in pickedCards) {
                if (card!!.hostScene != null && card.getComponent(Card::class.java).isTouched(touchEventData.touchX, touchEventData.touchY)) {
                    userChoice = card
                    userPlayed()
                    break
                }
            }
        }
    }

    init {
        setupCamera()

        //setRenderCustomDebugLines(true);
        setBackgroundColor(Color(0f, 0f, 0f, 1f))

        val bg: GameObject = newSpriteObject(this.xGdx.assets.regionForTexture(
                "${GlobalConstants.DOUBLE_CASH_ASSETS_HOME}/images/background.png"))
        addGameObject(bg)

        table = newSpriteObject(this.xGdx.assets.regionForTexture(
                "${GlobalConstants.DOUBLE_CASH_ASSETS_HOME}/images/table.png"))
        val tableTransform: Transform = table.transform
        tableTransform.setPosition((this.gameWorldUnits.getWorldWidth() - tableTransform.getWidth()) / 2f,
                this.gameWorldUnits.toWorldUnit(10f))
        addGameObject(table)
        table.addComponent(BoxDebugRenderer())

        opponent = newSpriteObject(this.xGdx.assets.regionForTexture(
                "${GlobalConstants.DOUBLE_CASH_ASSETS_HOME}/images/opponent.png"))
        val opponentTransform: Transform = opponent.transform
        opponentTransform.setSize(opponentTransform.getWidth(), opponentTransform.getHeight() * 0.8f)
        opponentTransform.setPosition(tableTransform.getX() + tableTransform.getWidth() / 2f - this.gameWorldUnits.toWorldUnit(180f),
                tableTransform.getY() + tableTransform.getHeight() - this.gameWorldUnits.toWorldUnit(70f))
        addGameObject(opponent)

        cards = Array()
        pickedCards = Array()
        cardBackSprite = this.xGdx.assets.getAtlas(
                "${GlobalConstants.DOUBLE_CASH_ASSETS_HOME}/spritesheets/cards.atlas").findRegion("shirt_red")
        for (region in this.xGdx.assets.getAtlas(
                "${GlobalConstants.DOUBLE_CASH_ASSETS_HOME}/spritesheets/cards.atlas")
                .getRegions()) {
            if (region.name.startsWith("shirt")) continue
            val card = newActorSpriteObject("Card", cardBackSprite)
            card.addComponent(Card(region, cardBackSprite, this))
            cards.add(card)
            card.addComponent(BoxDebugRenderer())
        }
        newGame()

        input.addListener(TouchTrigger.touchDownTrigger(), CardClickListener())
    }
}
