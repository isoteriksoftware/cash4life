package com.isoterik.cash4life

import com.badlogic.gdx.Preferences
import io.github.isoteriktech.xgdx.XGdx
import io.github.isoteriktech.xgdx.audio.AudioManager

class PreferenceHelper(val prefrenceName: String) {
    private val preferences: Preferences

    var isSoundEnabled: Boolean
        get() = preferences.getBoolean("soundEnabled", true)
        set(enabled) {
            preferences.putBoolean("soundEnabled", enabled)
            AudioManager.instance().setSoundEnabled(enabled)
        }

    fun saveChanges() {
        XGdx.instance().app.postRunnable(preferences::flush)
    }

    companion object {
        private var instance: PreferenceHelper? = null
        fun init(prefrenceName: String) {
            instance = PreferenceHelper(prefrenceName)
        }

        fun instance(): PreferenceHelper? {
            return instance
        }
    }

    init {
        preferences = XGdx.instance().app.getPreferences(prefrenceName)
        AudioManager.instance().setSoundEnabled(isSoundEnabled)
    }
}
