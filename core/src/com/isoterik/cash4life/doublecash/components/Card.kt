package com.isoterik.cash4life.doublecash.components

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import io.github.isoteriktech.xgdx.Component
import io.github.isoteriktech.xgdx.Scene
import io.github.isoteriktech.xgdx.Transform
import io.github.isoteriktech.xgdx.x2d.components.renderer.SpriteRenderer

class Card(region: AtlasRegion, val cardBackSprite: TextureRegion, scene: Scene) : Component() {
    val number: Int
    val cardSprite: TextureRegion

    companion object {
        var HIDDEN_SCALE = 0.7f
        var GAME_OVER_SCALE = 0.7f
        var OPPONENT_SELECTED_SCALE = 0.5f
    }

    override fun attach() {
        super.attach()
        setRevealed(false)
    }

    val realSize: Vector2
        get() = scene.gameWorldUnits.toWorldUnit(cardSprite)

    fun setRevealed(isRevealed: Boolean) {
        val spriteRenderer: SpriteRenderer = getComponent(SpriteRenderer::class.java)
        val realSize = realSize
        if (isRevealed) {
            spriteRenderer.sprite = cardSprite
        } else {
            spriteRenderer.sprite = cardBackSprite
            realSize.scl(HIDDEN_SCALE)
        }
        gameObject.transform.setSize(realSize.x, realSize.y)
    }

    fun setGameOverRevealed() {
        val spriteRenderer: SpriteRenderer = getComponent(SpriteRenderer::class.java)
        spriteRenderer.sprite = cardSprite
        val realSize = realSize
        realSize.scl(GAME_OVER_SCALE)
        gameObject.transform.setSize(realSize.x, realSize.y)
    }

    fun setOpponentSelected() {
        val realSize: Vector2 = scene.gameWorldUnits.toWorldUnit(cardSprite)
        realSize.scl(OPPONENT_SELECTED_SCALE)
        gameObject.transform.setSize(realSize.x, realSize.y)
    }

    fun isTouched(touchX: Float, touchY: Float): Boolean {
        val t: Transform = gameObject.transform
        return touchX >= t.getX() && touchX <= t.getX() + realWidth && touchY >= t.getY() && touchY <= t.getY() + realHeight
    }

    val realWidth: Float
        get() = gameObject.transform.getWidth() * gameObject.transform.getScaleX()
    val realHeight: Float
        get() = gameObject.transform.getHeight() * gameObject.transform.getScaleY()

    init {
        this@Card.scene = scene
        cardSprite = region
        number = region.name.replace("[^0-9]".toRegex(), "").toInt()
    }
}
