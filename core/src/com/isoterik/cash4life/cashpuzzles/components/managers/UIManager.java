package com.isoterik.cash4life.cashpuzzles.components.managers;

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
import com.isoterik.cash4life.cashpuzzles.CashPuzzlesSplash;
import com.isoterik.cash4life.cashpuzzles.Constants;
import io.github.isoteriktech.xgdx.Component;
import io.github.isoteriktech.xgdx.XGdx;
import io.github.isoteriktech.xgdx.ui.ActorAnimation;
import io.github.isoteriktech.xgdx.x2d.components.renderer.SpriteRenderer;
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection;
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions;

public class UIManager extends Component {
    private UserManager userManager;
    private GameManager gameManager;
    private LetterManager letterManager;

    private float modX;
    private float modY;

    private final XGdx xGdx;

    private Stage canvas;

    private Skin skin;

    private Label levelLabel;
    private Label categoryNameLabel;
    private Label timerLabel;
    private Label hintPowerUpText;

    private float timeInMins;
    private int timeInSecs;

    private float reloadTimeAmount;
    private float reloadTimePrice;
    private int reloadTimeCount;

    private int showHintAmount = 10;

    public UIManager(XGdx xGdx) {
        this.xGdx = xGdx;
    }

    @Override
    public void start() {
        setupUI();

        Gdx.input.setCatchKey(Input.Keys.BACK, true);
    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            gameManager.saveGame();
            toSplashScene();
        }
    }

    public void initializeManagers() {
        userManager = gameObject.getHostScene().findGameObject("userManager").getComponent(UserManager.class);
        gameManager = gameObject.getHostScene().findGameObject("gameManager").getComponent(GameManager.class);
        letterManager = gameObject.getHostScene().findGameObject("letterManager").getComponent(LetterManager.class);
    }

    public void scheduleTimer() {
        Timer.schedule(myTimerTask, 1f, 0.5f);
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
                String time = hoursString + ":" + minutesString + ":" + secondsString;

                if (timeInSecs <= 60) timerLabel.setColor(Color.RED);
                timerLabel.setText(time);
            }
            else {
                if (reloadTimeCount > 0) {
                    timeInSecs = (int) reloadTimeAmount;
                    userManager.withdraw(reloadTimePrice);
                    reloadTimeCount--;
                }
                else {
                    gameManager.gameOver();
                    cancel();
                }
            }
        }
    };

    private void setupUI() {
        canvas = scene.getCanvas();

        Image bg = new Image(scene.findGameObject("background").getComponent(SpriteRenderer.class).getSprite());

        modX = canvas.getWidth() / bg.getWidth();
        modY = canvas.getHeight() / bg.getHeight();

        resizeUI(bg);

        if (Constants.RELOAD_TIME) {
            reloadTimePrice = Constants.RELOAD_TIME_PRICE;
            reloadTimeCount = 5;
        } else {
            reloadTimeCount = 0;
        }

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
                gameManager.saveGame();
                toSplashScene();
            }
        });
        resizeUI(btnBack);

        levelLabel = new Label("1/10", skin);

        categoryNameLabel = new Label("Category Name", skin);
        categoryNameLabel.setWrap(true);
        categoryNameLabel.setFontScale(1.2f);

        timerLabel = new Label("00:00", skin);

        hintPowerUpText = new Label(showHintAmount + " / 10", skin);
        hintPowerUpText.setColor(Color.GREEN);

        Button hintPowerUpBtn = new Button(skin, "hint");
        hintPowerUpBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (showHintAmount > 0) {
                    showHintPowerUp();
                    showHintAmount--;
                    hintPowerUpText.setText(showHintAmount + " / 3");
                } else {
                    hintPowerUpBtn.setVisible(false);
                    hintPowerUpText.setVisible(false);
                }
            }
        });
        resizeUI(hintPowerUpBtn);

        Table table = new Table();
        //table.setDebug(true);
        table.setFillParent(true);
        table.padTop(20f).padBottom(20f).padRight(10f).padLeft(10f);
        table.top();

        table.add(btnBack).left().width(btnBack.getWidth()).height(btnBack.getHeight());
        table.add(levelLabel).right();
        table.add(timerLabel).right();
        table.row();

        table.add(categoryNameLabel).left().expandX().colspan(3).padTop(20f);
        table.row();

        table.add(hintPowerUpBtn).center().colspan(3).expandX().padTop(525).width(hintPowerUpBtn.getWidth()).height(hintPowerUpBtn.getHeight());
        table.row();

        table.add(hintPowerUpText).center().expandX().colspan(3).padTop(5f);

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
                new CashPuzzlesSplash(),
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

    public void setLevelTime(float levelTime) {
        timeInMins = levelTime;
        timeInSecs = (int) (timeInMins * 60);
        reloadTimeAmount = timeInSecs;
    }

    public float getTimeInMins() {
        return timeInMins;
    }

    public int getReloadTimeCount() {
        return reloadTimeCount;
    }

    public void setReloadTimeCount(int reloadTimeCount) {
        this.reloadTimeCount = reloadTimeCount;
    }

    public void setHintPowerUpText(int hintCount) {
        showHintAmount = hintCount;
        hintPowerUpText.setText(hintCount + " / 3");
    }

    public void setLevelText(String level) {
        levelLabel.setText(level);
    }

    public void setCategoryText(String category) {
        categoryNameLabel.setText(category);
    }

    public int getShowHintAmount() {
        return showHintAmount;
    }

    private void showHintPowerUp() {
        Window window = new Window("", skin);

        String accountBalance = userManager.getUser().getAccountBalanceAsString();
        int showHintPrice = 100;

        String labelTitle = "Account Balance: " + accountBalance + "\n\nUse Hint?\n This will cost you N" + showHintPrice + "!";
        Label label = new Label(labelTitle, skin);
        label.setAlignment(Align.center);
        label.setWrap(true);

        Button yesBtn = new Button(skin, "yes");
        resizeUI(yesBtn);
        Button noBtn = new Button(skin, "no");
        resizeUI(noBtn);

        yesBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (userManager.getUser().getAccountBalance() >= showHintPrice) {
                    canvas.getActors().removeValue(window, true);
                    userManager.withdraw(showHintPrice);
                    letterManager.showHint();
                }
                else {
                    window.removeActor(yesBtn);
                    window.removeActor(noBtn);
                    label.setText("Insufficient Balance");
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            canvas.getActors().removeValue(window, true);
                        }
                    }, 1f, 0f, 0);
                }
            }
        });

        noBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                canvas.getActors().removeValue(window, true);
            }
        });

        //window.setDebug(true);
        window.setFillParent(true);
        window.padTop(20f).padBottom(20f).padRight(10f).padLeft(10f);
        window.center();

        window.add(label).center().expandX().colspan(2).padBottom(20f);
        window.row();

        window.add(noBtn).expandX().center().width(noBtn.getWidth()).height(noBtn.getHeight());
        window.add(yesBtn).expandX().center().width(yesBtn.getWidth()).height(yesBtn.getHeight());

        window.pack();
        canvas.addActor(window);
        ActorAnimation.instance().slideIn(window, ActorAnimation.DOWN, .7f, Interpolation.swingOut);
    }
}
