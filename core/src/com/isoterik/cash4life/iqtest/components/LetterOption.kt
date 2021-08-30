package com.isoterik.cash4life.iqtest.components

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import io.github.isoteriktech.xgdx.Component
import io.github.isoteriktech.xgdx.Transform
import io.github.isoteriktech.xgdx.input.InputManager

class LetterOption : Component() {
    var character: Char = ' '

    private var transform: Transform = Transform()

    private lateinit var uiManager: UIManager
    private lateinit var letterManager: LetterManager

    private fun initManagers() {
        uiManager = scene.findGameObject("uiManager").getComponent(UIManager::class.java)
        letterManager = scene.findGameObject("letterManager").getComponent(LetterManager::class.java)
    }

    override fun start() {
        initManagers()
        transform = gameObject.transform
    }

    override fun update(deltaTime: Float) {
        onTouch()
    }

    private fun onTouch() {
        if (scene == null) return
        if (input.isTouched) {
            val touchPos = Vector2(input.touchedX, input.touchedY)
            val withinBounds = touchPos.x >= transform.x &&
                    touchPos.x <= transform.x + transform.width &&
                    touchPos.y >= transform.y
                    && touchPos.y <= transform.y + transform.height
            if (withinBounds) {
                if (uiManager.tapCount < uiManager.maxTaps) {
                    uiManager.updateMaxTapsLabel()
                    onTouchLogic()
                }
                else
                    uiManager.gameLost()
            }
        }
    }

    private fun onTouchLogic() {
        letterManager.onOptionTouched(this)
    }

    private fun onMouseOver() {
        val touchPos = Vector2(input.touchedX, input.touchedY)
        val withinBounds = touchPos.x >= transform.x &&
                touchPos.x <= transform.x + transform.width &&
                touchPos.y >= transform.y
                && touchPos.y <= transform.y + transform.height
        if (withinBounds) {
            // onMouseOverLogic()
        }
    }
}