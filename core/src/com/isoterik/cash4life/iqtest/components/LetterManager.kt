package com.isoterik.cash4life.iqtest.components

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Timer
import io.github.isoteriktech.xgdx.ActorGameObject
import io.github.isoteriktech.xgdx.Component
import io.github.isoteriktech.xgdx.GameObject

class LetterManager : Component() {
    var missingLettersGameObjects: Array<GameObject> = Array()
    var missingLetters: Array<Char> = Array()
    var optionsGameObject: Array<GameObject> = Array()

    fun onOptionTouched(optionTouched: LetterOption) {
        val optionTouchedGameObject = optionTouched.gameObject
        optionTouched.removeComponent(LetterOption::class.java)

        val letterOnTouch = optionTouched.character

        if (letterOnTouch == missingLetters[0]) {
            val moveToPosition = missingLettersGameObjects[0].transform.position
            val actorGameObject: ActorGameObject = optionTouchedGameObject as ActorGameObject
            actorGameObject.actorTransform.actor.addAction(Actions.moveTo(
                moveToPosition.x, moveToPosition.y, 1f, Interpolation.pow5Out
            ))

            missingLetters.removeIndex(0)
            removeGameObject(missingLettersGameObjects[0])
            missingLettersGameObjects.removeIndex(0)

            if (missingLetters.size == 0) {
                Timer.schedule(myTimerTask, 1.5f, 0f, 0)
            }
        }
        else {
            val uiManager = scene.findGameObject("uiManager").getComponent(UIManager::class.java)
            if (uiManager.tapCount >= uiManager.maxTaps) {
                println(2)
                uiManager.gameLost()
            }

            removeGameObject(optionTouchedGameObject)
            //optionTouchedGameObject.addComponent(optionTouched)
        }

    }

    fun showHint() {
        var hintComponent = LetterOption()
        for (letter in optionsGameObject) {
            if (letter.hasComponent(LetterOption::class.java)) {
                hintComponent = letter.getComponent(LetterOption::class.java)
                if (hintComponent.character == missingLetters[0]) {
                    break
                }
            }
        }

        val optionTouchedGameObject = hintComponent.gameObject
        val letterOnTouch = hintComponent.character

        if (letterOnTouch == missingLetters[0]) {
            val moveToPosition = missingLettersGameObjects[0].transform.position
            val actorGameObject: ActorGameObject = optionTouchedGameObject as ActorGameObject
            actorGameObject.actorTransform.actor.addAction(Actions.moveTo(
                moveToPosition.x, moveToPosition.y, 1f, Interpolation.pow5Out
            ))

            missingLetters.removeIndex(0)
            removeGameObject(missingLettersGameObjects[0])
            missingLettersGameObjects.removeIndex(0)

            if (missingLetters.size == 0) {
                Timer.schedule(myTimerTask, 1.5f, 0f, 0)
            }
        }
    }

    private val myTimerTask: Timer.Task = object : Timer.Task() {
        override fun run() {
            scene.findGameObject("gameManager").getComponent(SpellingGameManager::class.java).startLevel()
        }
    }
}