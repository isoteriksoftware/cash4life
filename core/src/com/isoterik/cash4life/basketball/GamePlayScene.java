package com.isoterik.cash4life.basketball;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.isoterik.cash4life.GlobalConstants;
import com.isoterik.cash4life.UserManager;
import com.isoterik.cash4life.basketball.components.managers.BasketballManager;
import com.isoterik.cash4life.basketball.components.managers.GameManager;
import com.isoterik.cash4life.basketball.components.managers.UIManager;
import io.github.isoteriktech.xgdx.GameObject;
import io.github.isoteriktech.xgdx.Scene;
import io.github.isoteriktech.xgdx.Transform;
import io.github.isoteriktech.xgdx.ui.ActorAnimation;
import io.github.isoteriktech.xgdx.utils.GameWorldUnits;

public class GamePlayScene extends Scene {
    public GamePlayScene() {
        setupCamera();

        setBackgroundColor(new Color(0.1f, 0.1f, 0.2f, 1.0f));

        start();
    }

    private void setupCamera() {
        gameWorldUnits = new GameWorldUnits(BasketballSplash.RESOLUTION_WIDTH, BasketballSplash.RESOLUTION_HEIGHT,
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
                this.xGdx.assets.getTexture(GlobalConstants.BASKETBALL_ASSETS_HOME + "/images/game_bg.jpg")
        );
        background.setTag("background");
        Transform backgroundTransform = background.transform;
        backgroundTransform.setSize(gameWorldUnits.getWorldWidth(), gameWorldUnits.getWorldHeight());
        addGameObject(background);

        GameObject userManager = new GameObject();
        userManager.setTag("userManager");
        userManager.addComponent(new UserManager());
        addGameObject(userManager);

        GameObject uiManager = new GameObject();
        uiManager.setTag("uiManager");
        uiManager.addComponent(new UIManager(xGdx));
        addGameObject(uiManager);

        GameObject basketballManager = new GameObject();
        basketballManager.setTag("basketballManager");
        basketballManager.addComponent(new BasketballManager());
        addGameObject(basketballManager);

        GameObject gameManager = new GameObject();
        gameManager.setTag("gameManager");
        gameManager.addComponent(new GameManager());
        addGameObject(gameManager);

        uiManager.getComponent(UIManager.class).scheduleTimer();
    }
}
