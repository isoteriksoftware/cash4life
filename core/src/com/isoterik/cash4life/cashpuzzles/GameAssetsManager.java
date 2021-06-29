package com.isoterik.cash4life.cashpuzzles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.isoteriktech.xgdx.XGdx;
import io.github.isoteriktech.xgdx.asset.GameAssetsLoader;

public final class GameAssetsManager {

    private static final GameAssetsManager instance = new GameAssetsManager();

    private GameAssetsLoader assetsLoader;

    private Texture background;
    private Texture square;
    private TextureAtlas letters;

    private GameAssetsManager() {
    }

    public static GameAssetsManager getInstance() {
        return instance;
    }

    public void enqueue(String basePath) {
        assetsLoader = XGdx.instance().assets;

        assetsLoader.enqueueTexture(basePath + "/sprites/bg.png");
        assetsLoader.enqueueTexture(basePath + "/sprites/square.png");
        assetsLoader.enqueueAtlas(basePath + "/sprites/letters.atlas");
    }

    public void load(String basePath) {
        background = assetsLoader.getTexture(basePath + "/sprites/bg.png");
        square = assetsLoader.getTexture(basePath + "/sprites/square.png");
        letters = assetsLoader.getAtlas(basePath + "/sprites/letters.atlas");
    }

    public Texture getBackground() {
        return background;
    }

    public Texture getSquare() {
        return square;
    }

    public TextureAtlas getLetters() {
        return letters;
    }
}
