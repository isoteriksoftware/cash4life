package com.isoterik.cash4life.cashpuzzles.utils;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.isoterik.cash4life.GlobalConstants;
import io.github.isoteriktech.xgdx.XGdx;

public class Letter {

    private final char letter;
    private final TextureRegion foundedSprite;

    private final TextureAtlas lettersAtlas = XGdx.instance().assets.getAtlas(
            GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/spritesheets/white.atlas"
    );

    public Letter(char letter) {
        this.letter = letter;

        TextureAtlas foundedLettersAtlas = XGdx.instance().assets.getAtlas(
                GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/spritesheets/orange.atlas"
        );
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
