package com.isoterik.cash4life.basketball.components.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.isoterik.cash4life.GlobalConstants;
import com.isoterik.cash4life.UserManager;
import com.isoterik.cash4life.basketball.BasketballSplash;
import com.isoterik.cash4life.basketball.Constants;
import com.isoterik.cash4life.cashpuzzles.CashPuzzlesSplash;
import com.isoterik.cash4life.cashpuzzles.components.managers.LetterManager;
import io.github.isoteriktech.xgdx.Component;
import io.github.isoteriktech.xgdx.XGdx;
import io.github.isoteriktech.xgdx.ui.ActorAnimation;
import io.github.isoteriktech.xgdx.x2d.components.renderer.SpriteRenderer;
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection;
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions;

public class UIManager extends Component {
    public float modX;
    public float modY;

    private final XGdx xGdx;

    private Stage canvas;

    private Skin skin;

    private Label scoreLabel;
    private Label timerLabel;
    private Label remainingBallsLabel;

    private float timeInMins;
    private int timeInSecs;

    public UIManager(XGdx xGdx) {
        this.xGdx = xGdx;
    }

    @Override
    public void start() {
        setupUI();

        Gdx.input.setCatchKey(Input.Keys.BACK, true);

        timeInMins = GameManager.getLevelTime();
        timeInSecs = (int) (timeInMins * 60f);
    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            toSplashScene();
        }
    }

    public void scheduleTimer() {
        Timer.schedule(myTimerTask, 1f, 1f);
    }

    private final Timer.Task myTimerTask = new Timer.Task() {
        @Override
        public void run() {
            --timeInSecs;
            timeInMins = timeInSecs / 60f;

            if (timeInSecs >= 0) {
                int hours = timeInSecs / 3600;
                int minutes = timeInSecs / 60;
                int seconds = timeInSecs % 60;

                String hoursString = hours >= 10 ? String.valueOf(hours) : "0" + hours;
                String minutesString = minutes >= 10 ? String.valueOf(minutes) : "0" + minutes;
                String secondsString = seconds >= 10 ? String.valueOf(seconds) : "0" + seconds;

                if (hours <= 0) hoursString = "";
                String time = hoursString + (hours > 0 ? ":" : "") + minutesString + ":" + secondsString;

                if (timeInSecs <= 60) timerLabel.setColor(Color.RED);
                timerLabel.setText(time);
            }
            else {
                cancel();
            }
        }
    };

    private void setupUI() {
        canvas = scene.getCanvas();

        Image bg = new Image(scene.findGameObject("background").getComponent(SpriteRenderer.class).getSprite());

        modX = canvas.getWidth() / bg.getWidth();
        modY = canvas.getHeight() / bg.getHeight();

        resizeUI(bg);

        skin = xGdx.assets.getSkin(GlobalConstants.BASKETBALL_SKIN);

        scoreLabel = new Label("0", skin, "score");
        scoreLabel.setAlignment(Align.center);
        resizeUI(scoreLabel);

        timerLabel = new Label("00:00", skin);

        remainingBallsLabel = new Label("0", skin);

        Label clockImage = new Label("", skin, "clock_image");
        resizeUI(clockImage);

        Button btnPause = new Button(skin, "back");
        //btnBack.setColor(skin.getColor("gold"));
        btnPause.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // toSplashScene();
            }
        });
        resizeUI(btnPause);

        Table timeTable = new Table();
        timeTable.add(clockImage).left().width(50f).height(50f);
        timeTable.add(timerLabel).padLeft(2f);
        timeTable.row();

        Table table = new Table();
        //table.setDebug(true);
        table.setFillParent(true);
        table.padTop(20f).padBottom(20f).padRight(10f).padLeft(10f);
        table.top();

        table.add(scoreLabel).left().expandX().width(scoreLabel.getWidth()).height(scoreLabel.getHeight());
        table.add(btnPause).right().expandX().width(btnPause.getWidth()).height(btnPause.getHeight());
        table.row();

        table.add(timeTable).left().padTop(10f);
        table.row();

        table.add(remainingBallsLabel).left().padTop(500f);

        canvas.addActor(table);
    }

    private void resizeUI(Button button) {
        button.setSize(modX * button.getWidth(), modY * button.getHeight());
    }

    private void resizeUI(Button button, float sizeFactor) {
        button.setSize(modX * button.getWidth() * sizeFactor, modY * button.getHeight() * sizeFactor);
    }

    private void resizeUI(Image image) {
        image.setSize(modX * image.getWidth(), modY * image.getHeight());
    }

    private void resizeUI(Label label) {
        label.setSize(modX * label.getWidth(), modY * label.getHeight());
    }

    private void toSplashScene() {
        xGdx.setScene(
                new BasketballSplash(),
                SceneTransitions.slide(
                        1f, SceneTransitionDirection.UP, true, Interpolation.pow5Out
                )
        );
    }

    public void gameFinished() {
        myTimerTask.cancel();

        showGameWonWindow();
    }

    public void gameLost() {
        showGameOverWindow();
    }

    private void showGameWonWindow() {
        Window window = new Window("", skin);

        String labelTitle = "CONGRATULATIONS! YOU'VE WON THE GAME.\n\n +N30,000 has been added to your account!";
        Label label = new Label(labelTitle, skin, "confeti");
        label.setAlignment(Align.center);
        label.setWrap(true);
        resizeUI(label);

        Button okBtn = new Button(skin, "ok");
        resizeUI(okBtn);
        okBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toSplashScene();
            }
        });

        //window.setDebug(true);
        window.setFillParent(true);
        window.padTop(20f).padBottom(20f).padRight(10f).padLeft(10f);
        window.center();

        window.add(label).center().expandX().width(label.getWidth()).height(label.getHeight());
        window.row();

        window.add(okBtn).center().expandX().width(okBtn.getWidth()).height(okBtn.getHeight());

        window.pack();
        canvas.addActor(window);
        ActorAnimation.instance().slideIn(window, ActorAnimation.DOWN, .7f, Interpolation.swingOut);
    }

    private void showGameOverWindow() {
        Window window = new Window("", skin);

        String labelTitle = "OOPS! TIME'S UP.\n You can do better!";
        Label label = new Label(labelTitle, skin);
        label.setAlignment(Align.center);
        label.setWrap(true);

        Button okBtn = new Button(skin, "ok");
        resizeUI(okBtn);
        okBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toSplashScene();
            }
        });

        //window.setDebug(true);
        window.setFillParent(true);
        window.padTop(20f).padBottom(20f).padRight(10f).padLeft(10f);
        window.center();

        window.add(label).center().expandX();
        window.row();

        window.add(okBtn).center().expandX().padTop(20f).width(okBtn.getWidth()).height(okBtn.getHeight());

        window.pack();
        canvas.addActor(window);
        ActorAnimation.instance().slideIn(window, ActorAnimation.DOWN, .7f, Interpolation.swingOut);
    }

    public void setRemaiiningBallsText(int count) {
        remainingBallsLabel.setText(count);
    }
}
