package com.isoterik.cash4life.cashpuzzles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.isoterik.cash4life.GlobalConstants;
import com.isoterik.cash4life.UserManager;
import com.isoterik.cash4life.cashpuzzles.components.managers.*;
import io.github.isoteriktech.xgdx.GameObject;
import io.github.isoteriktech.xgdx.Scene;
import io.github.isoteriktech.xgdx.Transform;
import io.github.isoteriktech.xgdx.ui.ActorAnimation;
import io.github.isoteriktech.xgdx.utils.GameWorldUnits;
import io.github.isoteriktech.xgdx.x2d.components.renderer.SpriteRenderer;

public class GamePlayScene extends Scene {
    public GamePlayScene() {
        setupCamera();

        setBackgroundColor(new Color(0.1f, 0.1f, 0.2f, 1.0f));

        start();
    }

    private void setupCamera() {
        gameWorldUnits = new GameWorldUnits(CashPuzzlesSplash.RESOLUTION_WIDTH, CashPuzzlesSplash.RESOLUTION_HEIGHT,
                100f);
        getMainCamera().setup(
                new StretchViewport(gameWorldUnits.getWorldWidth(), gameWorldUnits.getWorldHeight())
        );

        canvas = new Stage(new StretchViewport(Constants.GUI_WIDTH, Constants.GUI_HEIGHT));

        input.getInputMultiplexer().addProcessor(canvas);

        ActorAnimation.instance().setup(gameWorldUnits.getScreenWidth(), gameWorldUnits.getScreenHeight());
    }

    private void start() {
        GameObject background = newSpriteObject(
                this.xGdx.assets.getTexture(GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/images/bg.png")
        );
        background.setTag("background");
        background.getComponent(SpriteRenderer.class).setColor(new Color(250f/255f, 168f/255f, 71f/255f, 1f));
        Transform backgroundTransform = background.transform;
        backgroundTransform.setSize(gameWorldUnits.getWorldWidth(), gameWorldUnits.getWorldHeight());
        addGameObject(background);

        GameObject userManager = new GameObject();
        userManager.setTag("userManager");
        userManager.addComponent(new UserManager());
        addGameObject(userManager);

        GameObject storageManager = new GameObject();
        storageManager.setTag("storageManager");
        storageManager.addComponent(new StorageManager());
        addGameObject(storageManager);

        GameObject wordManager = new GameObject();
        wordManager.setTag("wordManager");
        wordManager.addComponent(new WordManager());
        addGameObject(wordManager);

        GameObject uiManager = new GameObject();
        uiManager.setTag("uiManager");
        uiManager.addComponent(new UIManager(xGdx));
        addGameObject(uiManager);

        GameObject letterManager = new GameObject();
        letterManager.setTag("letterManager");
        letterManager.addComponent(new LetterManager());
        addGameObject(letterManager);

        GameObject gameManager = new GameObject();
        gameManager.setTag("gameManager");
        gameManager.addComponent(new GameManager());
        addGameObject(gameManager);

        letterManager.getComponent(LetterManager.class).initializeManagers();

        gameManager.getComponent(GameManager.class).cont();

        uiManager.getComponent(UIManager.class).initializeManagers();
        uiManager.getComponent(UIManager.class).scheduleTimer();
    }
}
