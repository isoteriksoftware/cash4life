package com.isoterik.cash4life.cashpuzzles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.isoterik.cash4life.GlobalConstants;
import io.github.isoteriktech.xgdx.Scene;
import io.github.isoteriktech.xgdx.ui.ActorAnimation;
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection;
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions;

public class CashPuzzlesSplash extends Scene {
    public static final String GAME_TITLE = "Puzzles";

    public static final int RESOLUTION_WIDTH = 480;
    public static final int RESOLUTION_HEIGHT = 780;

    private float modX;
    private float modY;

    private void setupCamera() {
        canvas = new Stage(new StretchViewport(Constants.GUI_WIDTH, Constants.GUI_HEIGHT));

        input.getInputMultiplexer().addProcessor(canvas);
    }

    private void setupUI() {
        Image bg = new Image(this.xGdx.assets.regionForTexture(
                GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/images/bg1.png"
        ));

        modX = canvas.getWidth() / bg.getWidth();
        modY = canvas.getHeight() / bg.getHeight();

        rescaleUI(bg);
        canvas.addActor(bg);

        Image logo = new Image(this.xGdx.assets.regionForTexture(
                GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/images/logo.png", true
        ));
        rescaleUI(logo);

        Skin skin = this.xGdx.assets.getSkin(GlobalConstants.CASH_PUZZLES_SKIN);

        Button btnPlay = new Button(skin, "play");
        btnPlay.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toGamePlayScene();
            }
        });
        rescaleUI(btnPlay);

        Table root = new Table();
        root.setFillParent(true);
        root.top();
        //root.padBottom(250f).padRight(50f).padLeft(50f).padTop(150f);
        root.add(logo).expandX().width(logo.getWidth()).height(logo.getHeight()).top().padTop(100f);
        root.row();
        root.add(btnPlay).expandX().width(btnPlay.getWidth()).height(btnPlay.getHeight()).padTop(200f);
        ActorAnimation.instance().setup(canvas.getWidth(), canvas.getHeight());
        canvas.addActor(root);
    }

    private void rescaleUI(Image image) {
        image.setSize(modX * image.getWidth(), modY * image.getHeight());
    }

    private void rescaleUI(Button button) {
        button.setSize(modX * button.getWidth(), modY * button.getHeight());
    }

    private void toGamePlayScene() {
        xGdx.setScene(
                new GamePlayScene(),
                SceneTransitions.slide(
                        1f, SceneTransitionDirection.UP, true, Interpolation.pow5Out
                )
        );
    }

    @Override
    public void transitionedToThisScene(Scene previousScene) {
        // Load the game scene.
        // You'll modify this when a real splash UI is ready

        //Timer.post(new Timer.Task() {
        //    @Override
        //    public void run() {
        //        xGdx.setScene(new GamePlayScene(), SceneTransitions.fade(1f));
        //    }
        //});
    }

    public CashPuzzlesSplash() {
        setupCamera();
        setBackgroundColor(new Color(.1f, .1f, .2f, 1f));
        setupUI();
    }
}
