package com.isoterik.cash4life.cashpuzzles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.isoterik.cash4life.GlobalConstants;
import io.github.isoteriktech.xgdx.XGdx;

public class Letter {

    private char letter;
    private Sprite sprite;

    // Static variable! Only one copy exists for all letter objects
    private static final TextureAtlas lettersAtlas = XGdx.instance().assets.getAtlas(
            GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/spritesheets/white.atlas"
    );

    public Letter(char letter) {
        this.letter = letter;
        //sprite = new Sprite(getTextureRegion());
    }

    public char getLetter() {
        return letter;
    }

    public TextureRegion getTextureRegion() {
        return lettersAtlas.findRegion(String.valueOf(getLetter()).toUpperCase());
    }
}
