package com.isoterik.cash4life.doublecash.utils

import com.isoterik.cash4life.GlobalConstants
import io.github.isoteriktech.xgdx.XGdx
import io.github.isoteriktech.xgdx.audio.AudioManager

object Util {
    fun playClickSound() {
        AudioManager.instance().playSound(XGdx.instance().assets.getSound(
                "${GlobalConstants.DOUBLE_CASH_ASSETS_HOME}/sfx/click.ogg"), 1f)
    }
}