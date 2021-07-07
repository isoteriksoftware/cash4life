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
import io.github.isoteriktech.xgdx.GameObject;
import io.github.isoteriktech.xgdx.Scene;
import io.github.isoteriktech.xgdx.Transform;
import io.github.isoteriktech.xgdx.ui.ActorAnimation;
import io.github.isoteriktech.xgdx.utils.GameWorldUnits;
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection;
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions;

public class GamePlayScene extends Scene {
    private float modX;
    private float modY;

    private static Label levelLabel;
    private static Label categoryNameLabel;
    private Label timerLabel;

    private final int TOTAL_TIME = 30; // In minutes
    private int stageTime = TOTAL_TIME * 60;

    private Timer.Task myTimerTask = new Timer.Task() {
        @Override
        public void run() {
            --stageTime;
            if (stageTime >= 0) {
                int minutes = stageTime / 60;
                int seconds = stageTime % 60;
                String minutesString = minutes >= 10 ? String.valueOf(minutes) : "0" + minutes;
                String secondsString = seconds >= 10 ? String.valueOf(seconds) : "0" + seconds;
                String time = minutesString + ":" + secondsString;
                timerLabel.setText(time);
            }
            else {
                GameManager.getInstance().gameOver();
                cancel();
                //myTimerTask.cancel();
            }
        }
    };

    private void setupCamera() {
        gameWorldUnits = new GameWorldUnits(CashPuzzlesSplash.RESOLUTION_WIDTH, CashPuzzlesSplash.RESOLUTION_HEIGHT,
                100f);
        getMainCamera().setup(
                new StretchViewport(gameWorldUnits.getWorldWidth(), gameWorldUnits.getWorldHeight())
        );

        canvas = new Stage(new StretchViewport(Constants.GUI_WIDTH, Constants.GUI_HEIGHT));

        input.getInputMultiplexer().addProcessor(canvas);

        ActorAnimation.instance().setup(gameWorldUnits.getScreenWidth(), gameWorldUnits.getScreenHeight());

        setupUI();
    }

    private void setupUI() {
        Image bg = new Image(xGdx.assets.regionForTexture(
                GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/images/bg1.png"
        ));

        modX = canvas.getWidth() / bg.getWidth();
        modY = canvas.getHeight() / bg.getHeight();

        Skin skin = xGdx.assets.getSkin(GlobalConstants.CASH_PUZZLES_SKIN);

        Button btnBack = new Button(skin, "back");
        btnBack.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toSplashScene();
            }
        });
        rescaleUI(btnBack);

        levelLabel = new Label("Level 1/10", skin, "whitebox");
        levelLabel.setAlignment(Align.center);
        rescaleUI(levelLabel);rescaleUI(levelLabel);

        categoryNameLabel = new Label("Category", skin, "whitebox");
        categoryNameLabel.setAlignment(Align.center);
        categoryNameLabel.setWrap(true);
        categoryNameLabel.setFontScale(0.5f);
        rescaleUI(categoryNameLabel);rescaleUI(categoryNameLabel);

        timerLabel = new Label("00:00", skin, "whitebox");
        timerLabel.setAlignment(Align.center);
        rescaleUI(timerLabel);rescaleUI(timerLabel);

        Image clockIcon = new Image(xGdx.assets.getAtlas(GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/skin/ui.atlas")
                .findRegion("clock-icon")
        );
        rescaleUI(clockIcon);rescaleUI(clockIcon);

        TextButton hintBtn = new TextButton("Show Hint", skin);
        hintBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                LetterManager.getInstance().showHint();
            }
        });

        Table table = new Table();
        //table.setDebug(true);
        table.setFillParent(true);
        table.padTop(20f).padRight(10f).padLeft(10f);
        table.top();
        table.add(btnBack).left().expandX().width(btnBack.getWidth()).height(btnBack.getHeight());
        table.add(levelLabel).right().expandX().width(levelLabel.getWidth()).height(levelLabel.getHeight());
        table.row();

        table.add(categoryNameLabel).left().padTop(50f).width(categoryNameLabel.getWidth()).height(categoryNameLabel.getHeight());
        //table.add(clockIcon).right().padTop(50f).padRight(5f).width(clockIcon.getWidth()).height(clockIcon.getHeight());
        table.add(timerLabel).right().padTop(50f).width(timerLabel.getWidth()).height(timerLabel.getHeight());
        table.row();

        table.add(hintBtn).center().expandX().colspan(2).padTop(30f).width(hintBtn.getWidth()).height(hintBtn.getHeight());

        canvas.addActor(table);
    }

    private void rescaleUI(Button button) {
        button.setSize(modX * button.getWidth(), modY * button.getHeight());
    }

    private void rescaleUI(Image image) {
        image.setSize(modX * image.getWidth(), modY * image.getHeight());
    }

    private void rescaleUI(Label label) {
        label.setSize(modX * label.getWidth(), modY * label.getHeight());
    }

    private void toSplashScene() {
        xGdx.sceneManager.revertToPreviousScene(SceneTransitions.slide(
                1f, SceneTransitionDirection.UP, true, Interpolation.pow5Out
        ));
    }

    public GamePlayScene() {
        setupCamera();

        setBackgroundColor(new Color(0.1f, 0.1f, 0.2f, 1.0f));

        start();
    }

    public static void setLevelText(String level) {
        levelLabel.setText(level);
    }

    public static void setCategoryText(String category) {
        categoryNameLabel.setText(category);
    }

    private void start() {
        GameObject background = newSpriteObject(
                this.xGdx.assets.getTexture(GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/images/bg1.png")
        );
        Transform backgroundTransform = background.transform;
        backgroundTransform.setSize(gameWorldUnits.getWorldWidth(), gameWorldUnits.getWorldHeight());
        addGameObject(background);

        GameObject letterManager = new GameObject();
        letterManager.addComponent(new LetterManager());
        addGameObject(letterManager);

        GameObject gameManager = new GameObject();
        gameManager.addComponent(new GameManager());
        addGameObject(gameManager);

        GameManager.getInstance().cont();

        Timer.schedule(myTimerTask, 1f, 1f);
    }
}
