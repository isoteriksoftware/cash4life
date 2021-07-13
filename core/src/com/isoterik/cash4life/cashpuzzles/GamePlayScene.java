package com.isoterik.cash4life.cashpuzzles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.isoterik.cash4life.GlobalConstants;
import com.isoterik.cash4life.cashpuzzles.components.GameManager;
import com.isoterik.cash4life.cashpuzzles.components.LetterManager;
import com.isoterik.cash4life.cashpuzzles.components.UIManager;
import io.github.isoteriktech.xgdx.GameObject;
import io.github.isoteriktech.xgdx.Scene;
import io.github.isoteriktech.xgdx.Transform;
import io.github.isoteriktech.xgdx.ui.ActorAnimation;
import io.github.isoteriktech.xgdx.utils.GameWorldUnits;
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection;
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions;

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
        Transform backgroundTransform = background.transform;
        backgroundTransform.setSize(gameWorldUnits.getWorldWidth(), gameWorldUnits.getWorldHeight());
        addGameObject(background);

        GameObject uiManager = new GameObject();
        uiManager.addComponent(new UIManager(xGdx));
        addGameObject(uiManager);

        GameObject letterManager = new GameObject();
        letterManager.addComponent(new LetterManager());
        addGameObject(letterManager);

        GameObject gameManager = new GameObject();
        gameManager.addComponent(new GameManager());
        addGameObject(gameManager);

        GameManager.getInstance().cont();
        UIManager.getInstance().scheduleTimer();
    }
}
