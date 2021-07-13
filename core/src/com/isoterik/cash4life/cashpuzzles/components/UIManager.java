package com.isoterik.cash4life.cashpuzzles.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.Timer;
import com.isoterik.cash4life.GlobalConstants;
import com.isoterik.cash4life.cashpuzzles.CashPuzzlesSplash;
import com.isoterik.cash4life.cashpuzzles.GamePlayScene;
import io.github.isoteriktech.xgdx.Component;
import io.github.isoteriktech.xgdx.XGdx;
import io.github.isoteriktech.xgdx.ui.ActorAnimation;
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection;
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions;

import java.util.ArrayList;

public class UIManager extends Component {
    private static UIManager instance;

    public static UIManager getInstance() {
        return instance;
    }

    private float modX;
    private float modY;

    private final XGdx xGdx;

    private Stage canvas;

    private Skin skin;
    private Table wordsTbl;

    private Button hintPowerUpBtn;
    private Button shufflePowerUpBtn;
    private Button addTimePowerUpBtn;

    private Label backBtnLabel;
    private Label levelLabel;
    private Label categoryNameLabel;
    private Label timerLabel;

    private float timeInMins;
    private int timeInSecs;

    public UIManager(XGdx xGdx) {
        this.xGdx = xGdx;
    }

    private void singleton() {
        instance = this;
    }

    @Override
    public void start() {
        singleton();

        setupUI();
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
                int minutes = timeInSecs / 60;
                int seconds = timeInSecs % 60;
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

    private void setupUI() {
        canvas = scene.getCanvas();

        Image bg = new Image(xGdx.assets.regionForTexture(
                GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/images/bg.png"
        ));

        modX = canvas.getWidth() / bg.getWidth();
        modY = canvas.getHeight() / bg.getHeight();

        resizeUI(bg);

        skin = xGdx.assets.getSkin(GlobalConstants.CASH_PUZZLES_SKIN);
        TextureAtlas uiAtlas = xGdx.assets.getAtlas(
                GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/skin/ui.atlas"
        );

        Image findImage = new Image(uiAtlas.findRegion("find"));
        resizeUI(findImage);

        Button btnBack = new Button(skin, "back");
        //btnBack.setColor(skin.getColor("gold"));
        btnBack.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameManager.getInstance().saveGame();
                toSplashScene();
            }
        });
        resizeUI(btnBack);

        backBtnLabel = new Label("BACK", skin, "text_bold");
        backBtnLabel.setFontScale(0.5f);
        backBtnLabel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                myTimerTask.cancel();
                toSplashScene();
            }
        });

        levelLabel = new Label("1/10", skin);

        categoryNameLabel = new Label("Category Name", skin);
        categoryNameLabel.setWrap(true);

        timerLabel = new Label("00:00", skin);

        wordsTbl = new Table();
        ScrollPane wordsScrollPane = new ScrollPane(wordsTbl);

        hintPowerUpBtn = new Button(skin, "hint");
        hintPowerUpBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                LetterManager.getInstance().showHint();
            }
        });
        resizeUI(hintPowerUpBtn);

        addTimePowerUpBtn = new Button(skin, "add_time");
        addTimePowerUpBtn.addListener(new ChangeListener() {
            private final float TIME_INCREMENT = 5;
            private final float timeIncrement = TIME_INCREMENT * 60; //In seconds

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                addTime();
            }

            private void addTime() {
                timeInSecs += timeIncrement;
            }
        });
        resizeUI(addTimePowerUpBtn);

        shufflePowerUpBtn = new Button(skin, "shuffle");
        shufflePowerUpBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                LetterManager.getInstance().shuffleCells();
            }
        });
        resizeUI(shufflePowerUpBtn);

        Table table = new Table();
        //table.setDebug(true);
        table.setFillParent(true);
        table.padTop(20f).padBottom(20f).padRight(10f).padLeft(10f);
        table.top();

        table.add(btnBack).left().width(btnBack.getWidth()).height(btnBack.getHeight());
        //table.add(backBtnLabel).left().expandX();
        table.add(levelLabel).right();
        table.add(timerLabel).right();
        table.row();

        //table.add(categoryNameLabel).left().expandX().colspan(2).padTop(10f);
        //table.add(timerLabel).right().expandX().padTop(10f);
        //table.row();

        table.add(findImage).width(50f).padTop(15f);
        table.add(wordsScrollPane).colspan(2).expandX().padTop(20f).width(400f).height(100f);
        table.row();

        Table powerUpsTbl = new Table();
        powerUpsTbl.center().bottom();
        powerUpsTbl.add(hintPowerUpBtn).center().width(hintPowerUpBtn.getWidth()).height(hintPowerUpBtn.getHeight()).padLeft(40f).padRight(40f);
        powerUpsTbl.add(addTimePowerUpBtn).center().width(addTimePowerUpBtn.getWidth()).height(addTimePowerUpBtn.getHeight()).padLeft(40f).padRight(40f).padTop(10f);
        powerUpsTbl.add(shufflePowerUpBtn).center().width(shufflePowerUpBtn.getWidth()).height(shufflePowerUpBtn.getHeight()).padLeft(40f).padRight(40f).padTop(5f);

        table.add(powerUpsTbl).center().colspan(3).expandX().pad(500, 5, 0, 5);

        canvas.addActor(table);
    }

    private void resizeUI(Button button) {
        button.setSize(modX * button.getWidth(), modY * button.getHeight());
    }

    private void resizeUI(Image image) {
        image.setSize(modX * image.getWidth(), modY * image.getHeight());
    }

    private void resizeUI(Label label) {
        label.setSize(modX * label.getWidth(), modY * label.getHeight());
    }

    private void toSplashScene() {
        //xGdx.sceneManager.revertToPreviousScene(SceneTransitions.slide(
        //        1f, SceneTransitionDirection.UP, true, Interpolation.pow5Out
        //));

        xGdx.setScene(
                new CashPuzzlesSplash(),
                SceneTransitions.slide(
                        1f, SceneTransitionDirection.UP, true, Interpolation.pow5Out
                )
        );
    }

    void fillWordsTable(ArrayList<String> words) {
        wordsTbl.clearChildren();

        int wordsTblChildrenCount = 0;
        for (String word : words) {
            Label wordLabel = new Label(word, skin);
            wordLabel.setFontScale(0.7f);
            wordLabel.setColor(Color.WHITE);

            if (wordsTblChildrenCount != 0 && wordsTblChildrenCount % 3 == 0) {
                wordsTbl.row();
            }

            wordsTbl.add(wordLabel).center().expandX().padBottom(5f);
            wordsTblChildrenCount++;
        }
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

        String labelTitle = "CONGRATULATIONS! YOU'VE WON THE GAME.\n\n +N10000 has been added to your account!";
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

    public void setLevelTime(float levelTime) {
        timeInMins = levelTime;
        timeInSecs = (int) (timeInMins * 60);
    }

    public float getTimeInMins() {
        return timeInMins;
    }

    public void setLevelText(String level) {
        levelLabel.setText(level);
    }

    public void setCategoryText(String category) {
        categoryNameLabel.setText(category);
    }

    public void setAccountBalance(float amount) {
        backBtnLabel.setText("N" + amount);
    }

    public void removeFoundWord(String word) {
        SnapshotArray<Actor> children = wordsTbl.getChildren();
        for (Actor actor : children) {
            Label label = (Label) actor;
            String s = label.getText().toString();
            if (s.equalsIgnoreCase(word))
                wordsTbl.removeActor(actor);
        }
    }
}
