package com.isoterik.cash4life.iqtest.components

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Timer
import io.github.isoteriktech.xgdx.ActorGameObject
import io.github.isoteriktech.xgdx.Component
import io.github.isoteriktech.xgdx.GameObject
import io.github.isoteriktech.xgdx.Transform
import io.github.isoteriktech.xgdx.x2d.components.renderer.SpriteRenderer
import java.util.*

class SentenceManager : Component() {
    private var worldWidth: Float = 0.0F

    var missingWords: Array<String> = Array()

    lateinit var missingOptionTransforms: Array<Transform>
    lateinit var wordFromMissingOptionsTransforms: Array<Array<Transform>>

    override fun start() {
        worldWidth = scene.gameWorldUnits.worldWidth
    }

    fun onOptionTouched(optionTouched: SentenceOption) {
        optionTouched.removeComponent(SentenceOption::class.java)

        val optionWord = optionTouched.word
        val optionNeighbours = optionTouched.neighbours

        if (optionWord == missingWords[0]) {
            val firstOptionLetter = optionNeighbours.first()
            val totalOptionWidth = firstOptionLetter.transform.width * optionNeighbours.size

            val missingOptionTransform = missingOptionTransforms.removeIndex(0)
            val missingOptionTransformWidthFromScreenEdge = worldWidth - missingOptionTransform.position.x

            val optionWidth = firstOptionLetter.transform.width
            if (missingOptionTransformWidthFromScreenEdge >= totalOptionWidth) {
                var moveToX = missingOptionTransform.position.x
                val moveToY = missingOptionTransform.position.y

                for (neighbour in optionNeighbours) {
                    val actorGameObject: ActorGameObject = neighbour as ActorGameObject
                    actorGameObject.actorTransform.actor.addAction(
                        Actions.moveTo(
                        moveToX, moveToY, 1f, Interpolation.pow5Out
                    ))
                    neighbour.removeComponent(SentenceOption::class.java)

                    moveToX += optionWidth
                }

                horizontalDistributeEvenly(wordFromMissingOptionsTransforms.removeIndex(0), moveToX, moveToY)
            }
            else {
                var moveToX = 0.0f
                val moveToY = missingOptionTransform.position.y - (firstOptionLetter.transform.height * 1.5f)

                for (neighbour in optionNeighbours) {
                    val actorGameObject: ActorGameObject = neighbour as ActorGameObject
                    actorGameObject.actorTransform.actor.addAction(
                        Actions.moveTo(
                            moveToX, moveToY, 1f, Interpolation.pow5Out
                        ))
                    neighbour.removeComponent(SentenceOption::class.java)

                    moveToX += optionWidth
                }

                horizontalDistributeEvenly(wordFromMissingOptionsTransforms.removeIndex(0), moveToX, moveToY)
            }

            scene.removeGameObject(missingOptionTransform.gameObject)
            missingWords.removeIndex(0)

            if (missingWords.size == 0) {
                Timer.schedule(myTimerTask, 1.5f, 0f, 0)
            }
        }
        else {
            val uiManager = scene.findGameObject("uiManager").getComponent(UIManager::class.java)
            if (uiManager.tapCount >= uiManager.maxTaps) {
                println(2)
                uiManager.gameLost()
            }

            removeGameObjects(optionNeighbours)
            //optionTouchedGameObject.addComponent(optionTouched)
        }
    }

    private fun removeGameObjects(gameObjects: Array<GameObject>) {
        for (gameObject in gameObjects)
            removeGameObject(gameObject)
    }

    private val myTimerTask: Timer.Task = object : Timer.Task() {
        override fun run() {
            scene.findGameObject("gameManager").getComponent(SentenceGameManager::class.java).startLevel()
        }
    }

    private fun horizontalDistributeEvenly(transforms: Array<Transform>, xPosition: Float, yPosition: Float) {
        if (transforms.isEmpty) return

        val worldWidth = scene.gameWorldUnits.worldWidth
        val transformWidth = transforms[0].width
        val transformHeight = transforms[0].height

        var xPos = xPosition
        var yPos = yPosition

        val groupedTransforms = Stack<Transform>()
        for (transform in transforms) {
            if (transform.gameObject.getComponent(SpriteRenderer::class.java).isVisible) {
                groupedTransforms.push(transform)
                transform.setPosition(xPos, yPos)
                xPos += transformWidth
                if (transform.x > worldWidth) {
                    xPos = 0.0f
                    yPos -= (transformHeight * 1.5f)

                    for (gt in groupedTransforms) {
                        gt.setPosition(xPos, yPos)
                        xPos += transformWidth
                    }
                    groupedTransforms.clear()
                }
            } else {
                xPos += transformWidth
                groupedTransforms.clear()
            }
        }
    }
}