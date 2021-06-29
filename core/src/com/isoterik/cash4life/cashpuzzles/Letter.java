package com.isoterik.cash4life.cashpuzzles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Letter {

    private char letter;
    private Sprite sprite;

    public Letter(char letter) {
        this.letter = letter;
        //sprite = new Sprite(getTextureRegion());
    }

    public char getLetter() {
        return letter;
    }

    public TextureRegion getTextureRegion() {
        return GameAssetsManager.getInstance().getLetters()
                .findRegion(String.valueOf(getLetter()).toLowerCase());
    }
}
