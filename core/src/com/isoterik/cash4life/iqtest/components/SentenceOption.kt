package com.isoterik.cash4life.iqtest.components

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import io.github.isoteriktech.xgdx.Component
import io.github.isoteriktech.xgdx.GameObject
import io.github.isoteriktech.xgdx.Transform

class SentenceOption : Component() {
    var neighbours: Array<GameObject> = Array()

    var word: String = String()

    private lateinit var transform: Transform

    private lateinit var uiManager: UIManager
    private lateinit var sentenceManager: SentenceManager

    private fun initManagers() {
        uiManager = scene.findGameObject("uiManager").getComponent(UIManager::class.java)
        sentenceManager = scene.findGameObject("sentenceManager").getComponent(SentenceManager::class.java)
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
        sentenceManager.onOptionTouched(this)
    }
}