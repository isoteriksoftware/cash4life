package com.isoterik.cash4life.cashpuzzles.components.managers;

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
import com.isoterik.cash4life.UserManager;
import com.isoterik.cash4life.cashpuzzles.CashPuzzlesSplash;
import com.isoterik.cash4life.cashpuzzles.Constants;
import io.github.isoteriktech.xgdx.Component;
import io.github.isoteriktech.xgdx.XGdx;
import io.github.isoteriktech.xgdx.ui.ActorAnimation;
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection;
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class UIManager extends Component {
    private UserManager userManager;
    private GameManager gameManager;
    private LetterManager letterManager;

    private final HashMap<Float, Integer> timeAndPrice;

    private float modX;
    private float modY;

    private final XGdx xGdx;

    private Stage canvas;

    private Skin skin;
    private Table wordsTbl;

    private Label backBtnLabel;
    private Label levelLabel;
    private Label categoryNameLabel;
    private Label timerLabel;

    private float timeInMins;
    private int timeInSecs;

    public UIManager(XGdx xGdx) {
        this.xGdx = xGdx;

        Constants constants = new Constants();
        timeAndPrice = constants.getTimeAndPrice();
    }

    @Override
    public void start() {
        setupUI();
    }

    public void initializeManagers() {
        userManager = gameObject.getHostScene().findGameObject("userManager").getComponent(UserManager.class);
        gameManager = gameObject.getHostScene().findGameObject("gameManager").getComponent(GameManager.class);
        letterManager = gameObject.getHostScene().findGameObject("letterManager").getComponent(LetterManager.class);
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
                String time = hoursString + ":" + minutesString + ":" + secondsString;

                if (timeInSecs <= 60) timerLabel.setColor(Color.RED);
                timerLabel.setText(time);
            }
            else {
                gameManager.gameOver();
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
                gameManager.saveGame();
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

        Button hintPowerUpBtn = new Button(skin, "hint");
        hintPowerUpBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showHintPowerUp();
            }
        });
        resizeUI(hintPowerUpBtn);

        Button addTimePowerUpBtn = new Button(skin, "add_time");
        addTimePowerUpBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                addTimePowerUp();
            }
        });
        resizeUI(addTimePowerUpBtn);

        Button shufflePowerUpBtn = new Button(skin, "shuffle");
        shufflePowerUpBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                letterManager.shuffleCells();
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

    private void addTimePowerUp() {
        Window window = new Window("", skin);

        Label accountBalanceLabel = new Label(userManager.getUser().getAccountBalanceAsString(), skin);
        accountBalanceLabel.setFontScale(0.7f);

        Button closeBtn = new Button(skin, "close");
        resizeUI(closeBtn);
        closeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                canvas.getActors().removeValue(window, true);
            }
        });

        Label titleLabel = new Label("BUY TIME", skin, "header");
        resizeUI(titleLabel);
        titleLabel.setAlignment(Align.center);

        Object[] timeArray = timeAndPrice.keySet().toArray();
        Arrays.sort(timeArray);

        TextButton[] timeAndPricesButton = new TextButton[timeAndPrice.size()];
        String[] styleNames = new String[] {
                "p_pink", "p_purple", "p_cyan", "p_blue", "p_green", "p_orange"
        };
        for (int i = 0; i < timeAndPricesButton.length; i++) {
            float time = (float) timeArray[i];
            float price = timeAndPrice.get(time);

            String spaces = "              ";
            String displayName = spaces + timeToString(time) + "\n" + spaces + price + " naira";
            String styleName = styleNames[i];

            timeAndPricesButton[i] = new TextButton(displayName, skin, styleName);
            timeAndPricesButton[i].getLabel().setAlignment(Align.left);
            timeAndPricesButton[i].getLabel().setFontScale(0.7f);
            resizeUI(timeAndPricesButton[i]);
            resizeUI(timeAndPricesButton[i], 2);

            if (userManager.getUser().getAccountBalance() < price) {
                timeAndPricesButton[i].setDisabled(true);
                timeAndPricesButton[i].getLabel().setColor(Color.GRAY);
            }
            timeAndPricesButton[i].addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    timeInSecs += (time * 60);
                    userManager.withdraw(price);
                    canvas.getActors().removeValue(window, true);
                }
            });
        }

        Table timeAndPricesTable = new Table();
        for (TextButton textButton : timeAndPricesButton) {
            timeAndPricesTable.add(textButton).center().expandX().padBottom(50f).width(textButton.getWidth()).height(textButton.getHeight());
            timeAndPricesTable.row();
        }

        ScrollPane scrollPane = new ScrollPane(timeAndPricesTable);
        scrollPane.setScrollingDisabled(true, false);

        //window.setDebug(true);
        window.setFillParent(true);
        window.padTop(20f).padBottom(20f).padRight(10f).padLeft(10f);
        window.top();

        window.add(closeBtn).left().width(closeBtn.getWidth()).height(closeBtn.getHeight());
        window.add(accountBalanceLabel).right();
        window.row();

        window.add(titleLabel).center().colspan(2).padTop(10f).width(titleLabel.getWidth()).height(titleLabel.getHeight());
        window.row();

        window.add(scrollPane).colspan(2).expandX().padTop(20f).width(450f);
        window.pack();
        canvas.addActor(window);
        ActorAnimation.instance().slideIn(window, ActorAnimation.DOWN, .7f, Interpolation.swingOut);
    }

    private String timeToString(float timeInMins) {
        String timeString = timeInMins + " minutes";
        if (timeInMins < 1f) {
            int timeInSecs = (int) (timeInMins * 60);
            timeString = timeInSecs + " seconds";
        }
        else if (timeInMins > 60f) {
            int timeInHours = (int) (timeInMins / 60);
            timeString = timeInHours + " hours";
        }
        return timeString;
    }
}
