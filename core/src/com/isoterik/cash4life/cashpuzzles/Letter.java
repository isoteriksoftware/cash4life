package com.isoterik.cash4life.cashpuzzles;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.isoterik.cash4life.GlobalConstants;
import io.github.isoteriktech.xgdx.XGdx;

public class Letter {

    private char letter;
    private TextureRegion foundedSprite;

    // Static variable! Only one copy exists for all letter objects
    private static final TextureAtlas lettersAtlas = XGdx.instance().assets.getAtlas(
            GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/spritesheets/white.atlas"
    );
    private static final TextureAtlas foundedLettersAtlas = XGdx.instance().assets.getAtlas(
            GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/spritesheets/orange.atlas"
    );

    public Letter(char letter) {
        this.letter = letter;
        foundedSprite = foundedLettersAtlas.findRegion(String.valueOf(getLetter()).toUpperCase());
    }

    public char getLetter() {
        return letter;
    }

    public TextureRegion getTextureRegion() {
        return lettersAtlas.findRegion(String.valueOf(getLetter()).toUpperCase());
    }

    public TextureRegion getFoundedSprite() {
        return foundedSprite;
    }
}
