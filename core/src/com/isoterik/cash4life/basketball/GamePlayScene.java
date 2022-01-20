package com.isoterik.cash4life.basketball;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.isoterik.cash4life.GlobalConstants;
import com.isoterik.cash4life.UserManager;
import com.isoterik.cash4life.basketball.components.SwipeFilterComponent;
import com.isoterik.cash4life.basketball.components.managers.BasketballManager;
import com.isoterik.cash4life.basketball.components.managers.UIManager;
import io.github.isoteriktech.xgdx.GameObject;
import io.github.isoteriktech.xgdx.Scene;
import io.github.isoteriktech.xgdx.Transform;
import io.github.isoteriktech.xgdx.XGdx;
import io.github.isoteriktech.xgdx.physics2d.colliders.BoxCollider;
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

        createSwipeFilter();

        uiManager.getComponent(UIManager.class).scheduleTimer();
    }

    private void createSwipeFilter() {
        GameObject filter = newSpriteObject(XGdx.instance().assets.getTexture(
                GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/images/bg.png")
        );
        filter.setTag("swipeFilter");
        filter.getComponent(SpriteRenderer.class).setOpacity(0f);
        filter.transform.setSize(getGameWorldUnits().getWorldWidth(), getGameWorldUnits().getWorldHeight());
        filter.addComponent(new BoxCollider());
        filter.addComponent(new SwipeFilterComponent());
        addGameObject(filter);
    }
}
