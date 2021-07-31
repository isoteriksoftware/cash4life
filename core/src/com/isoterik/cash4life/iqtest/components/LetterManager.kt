package com.isoterik.cash4life.iqtest.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Timer
import com.isoterik.cash4life.iqtest.IQTestSplash
import io.github.isoteriktech.xgdx.ActorGameObject
import io.github.isoteriktech.xgdx.Component
import io.github.isoteriktech.xgdx.GameObject
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions
import java.util.*

class LetterManager : Component() {
    var missingLettersGameObjects: Array<GameObject> = Array()
    var missingLetters: Array<Char> = Array()
    var options: Array<Char> = Array()

    fun onOptionTouched(optionTouched: LetterOption) {
        val letterOnTouch = options[optionTouched.index]
        if (letterOnTouch == missingLetters[0]) {
            val moveToPosition = missingLettersGameObjects[0].transform.position
            val actorGameObject: ActorGameObject = optionTouched.gameObject as ActorGameObject
            actorGameObject.actorTransform.actor.addAction(Actions.moveTo(
                moveToPosition.x, moveToPosition.y, 1f, Interpolation.pow5Out
            ))
            optionTouched.gameObject.removeComponent(LetterOption::class.java)

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
            scene.findGameObject("gameManager").getComponent(GameManager::class.java).startLevel()
        }
    }
}