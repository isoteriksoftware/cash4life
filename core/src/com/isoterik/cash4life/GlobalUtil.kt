package com.isoterik.cash4life

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Array
import com.isoterik.cash4life.cashpuzzles.Constants
import io.github.isoteriktech.xgdx.Transform
import io.github.isoteriktech.xgdx.utils.GameWorldUnits

class GlobalUtil {
    companion object {
        private lateinit var gameWorldUnits: GameWorldUnits

        private var modX = 0f
        private var modY = 0f

        fun init(gameWorldUnits: GameWorldUnits, canvas: Stage, modWidth: Float, modHeight: Float) {
            this.gameWorldUnits = gameWorldUnits

            modX = canvas.width / modWidth
            modY = canvas.height / modHeight
        }

        fun center(transform: Transform) {
            transform.setPosition(
                (gameWorldUnits.worldWidth - transform.width) / 2f,
                (gameWorldUnits.worldHeight - transform.height) / 2f
            )
        }

        fun horizontalDistributeEvenly(transforms: Array<Transform>, yPosition: Float) {
            val worldWidth = gameWorldUnits.worldWidth
            val transformWidth = transforms[0].width

            val size = transforms.size
            val xOffset = (worldWidth - (size * transformWidth)) / (size + 1)
            for ((index, transform) in transforms.withIndex()) {
                val xPosition = xOffset + (xOffset * index) + (transformWidth * index)
                transform.setPosition(xPosition, yPosition)
            }
        }

        fun centerDistributeEvenly(transforms: Array<Transform>) {
            val yPosition = (gameWorldUnits.worldHeight - transforms[0].height) / 2f
            horizontalDistributeEvenly(transforms, yPosition)
        }

        fun topDistributeEvenly(transforms: Array<Transform>) {
            val yPosition = (gameWorldUnits.worldHeight - transforms[0].height)
            horizontalDistributeEvenly(transforms, yPosition)
        }

        fun bottomDistributeEvenly(transforms: Array<Transform>) {
            val yPosition = 0f
            horizontalDistributeEvenly(transforms, yPosition)
        }

        fun resizeTransforms(transforms: Array<Transform>) {
            for (transform in transforms) {
                val size = transform.size
                transform.setSize(size.x * modX, size.y * modY)
            }
        }

        fun resizeTransforms(transforms: Array<Transform>, magnitude: Float) {
            for (transform in transforms) {
                val size = transform.size
                transform.setSize(size.y * magnitude, size.y * magnitude)
            }
        }

        fun resizeTransforms(transforms: Array<Transform>, magnitudeX: Float, magnitudeY: Float) {
            for (transform in transforms) {
                val size = transform.size
                transform.setSize(size.x * magnitudeX, size.y * magnitudeY)
            }
        }

        fun resizeUI(button: Button) {
            button.setSize(modX * button.width, modY * button.height)
        }

        fun resizeUI(button: Button, sizeFactor: Float) {
            button.setSize(button.width * sizeFactor, button.height * sizeFactor)
        }

        fun resizeUI(label: Label) {
            label.setSize(modX * label.width, modY * label.height)
        }

    }
}