package com.isoterik.cash4life.cashpuzzles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.isoterik.cash4life.GlobalConstants;
import com.isoterik.cash4life.MainScene;
import com.isoterik.cash4life.UIScene;
import com.isoterik.cash4life.UserManager;
import com.isoterik.cash4life.cashpuzzles.components.managers.GameManager;
import com.isoterik.cash4life.cashpuzzles.components.managers.StorageManager;
import com.isoterik.cash4life.cashpuzzles.utils.Storage;
import io.github.isoteriktech.xgdx.GameObject;
import io.github.isoteriktech.xgdx.Scene;
import io.github.isoteriktech.xgdx.ui.ActorAnimation;
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection;
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions;

import java.text.ParseException;
import java.util.*;

public class CashPuzzlesSplash extends Scene {
    public static final int RESOLUTION_WIDTH = 480;
    public static final int RESOLUTION_HEIGHT = 780;

    private StorageManager storageManager;
    private UserManager userManager;

    private final Storage storage;

    private float modX;
    private float modY;

    private Skin skin;

    private final HashMap<Float, Integer> timeAndPrice;

    public CashPuzzlesSplash() {
        initializeManagers();

        storage = storageManager.getStorage();

        Constants constants = new Constants();
        timeAndPrice = constants.getTimeAndPrice();

        setupCamera();
        setBackgroundColor(new Color(.1f, .1f, .2f, 1f));
        setupUI();
    }

    private void initializeManagers() {
        GameObject g1 = new GameObject();
        g1.addComponent(new StorageManager());
        addGameObject(g1);

        GameObject g2 = new GameObject();
        g2.addComponent(new UserManager());
        addGameObject(g2);

        storageManager = g1.getComponent(StorageManager.class);
        userManager = g2.getComponent(UserManager.class);
    }

    private void setupCamera() {
        canvas = new Stage(new StretchViewport(Constants.GUI_WIDTH, Constants.GUI_HEIGHT));

        input.getInputMultiplexer().addProcessor(canvas);
    }

    private void setupUI() {
        Color milk = new Color(250f/255f, 168f/255f, 71f/255f, 1f);

        Image bg = new Image(this.xGdx.assets.regionForTexture(
                GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/images/bg.png"
        ));
        bg.setColor(milk);

        modX = canvas.getWidth() / bg.getWidth();
        modY = canvas.getHeight() / bg.getHeight();

        resizeUI(bg);
        canvas.addActor(bg);

        Image logo = new Image(this.xGdx.assets.regionForTexture(
                GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/images/logo.png", true
        ));
        resizeUI(logo);

        skin = this.xGdx.assets.getSkin(GlobalConstants.CASH_PUZZLES_SKIN);

        Button newGameBtn = new Button(skin, "new_game");
        newGameBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //userManager.reset();
                if (storage.isSaved()) {
                    popUpNewGameDialog();
                }
                else {
                    try {
                        popUpBuyTimeWindow();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        resizeUI(newGameBtn);

        Button continueBtn = new Button(skin, "continue");
        continueBtn.setVisible(storage.isSaved());
        continueBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toGamePlayScene();
            }
        });
        resizeUI(continueBtn);

        Button backBtn = new Button(skin, "back");
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                xGdx.setScene(new UIScene());
            }
        });
        backBtn.setColor(Color.GREEN);
        resizeUI(backBtn);

        Label accountBalanceLabel = new Label(userManager.getUser().getAccountBalanceAsString(), skin);
        accountBalanceLabel.setFontScale(0.7f);
        accountBalanceLabel.setColor(Color.GREEN);

        Table table = new Table();
        //table.setDebug(true);
        table.setFillParent(true);
        table.padTop(20f).padBottom(20f).padRight(10f).padLeft(10f);
        table.top();

        table.add(backBtn).left().expandX().width(backBtn.getWidth()).height(backBtn.getHeight());
        table.add(accountBalanceLabel).right().expandX();
        table.row();

        table.add(logo).colspan(2).expandX().width(logo.getWidth()).height(logo.getHeight()).top().padTop(100f);
        table.row();

        table.add(newGameBtn).colspan(2).expandX().width(newGameBtn.getWidth()).height(newGameBtn.getHeight()).padTop(100f);
        table.row();

        table.add(continueBtn).colspan(2).expandX().width(continueBtn.getWidth()).height(continueBtn.getHeight()).padTop(50f);
        ActorAnimation.instance().setup(canvas.getWidth(), canvas.getHeight());
        canvas.addActor(table);
    }

    private void resizeUI(Image image) {
        image.setSize(modX * image.getWidth(), modY * image.getHeight());
    }

    private void resizeUI(Button button) {
        button.setSize(modX * button.getWidth(), modY * button.getHeight());
    }

    private void resizeUI(Button button, float sizeFactor) {
        button.setSize(modX * button.getWidth() * sizeFactor, modY * button.getHeight() * sizeFactor);
    }

    private void resizeUI(Label label) {
        label.setSize(modX * label.getWidth(), modY * label.getHeight());
    }

    private void popUpNewGameDialog() {
        Window window = new Window("", skin);

        String labelTitle = "Start a new game?\n All saved progress will be lost!";
        Label label = new Label(labelTitle, skin);
        label.setAlignment(Align.center);
        label.setWrap(true);

        Button yesBtn = new Button(skin, "yes");
        resizeUI(yesBtn);
        yesBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    popUpBuyTimeWindow();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                canvas.getActors().removeValue(window, true);
            }
        });

        Button noBtn = new Button(skin, "no");
        resizeUI(noBtn);
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

    private void popUpBuyTimeWindow() throws ParseException {
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

        CheckBox reloadTime = new CheckBox("Reload Time?", skin);
        //reloadTime.getCells().get(0).size(10, 10);
        reloadTime.getLabel().setFontScale(0.7f);
        reloadTime.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Constants.RELOAD_TIME = reloadTime.isChecked();
            }
        });

        Object[] timeArray = timeAndPrice.keySet().toArray();
        Arrays.sort(timeArray);

        TextButton[] timeAndPricesButton = new TextButton[timeAndPrice.size()];
        String[] styleNames = new String[] {
            "p_pink", "p_purple", "p_cyan", "p_blue", "p_green", "p_orange"
        };
        for (int i = 0; i < timeAndPricesButton.length; i++) {
            float time = (float) timeArray[i];
            int price = timeAndPrice.get(time);

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
                    storageManager.reset();

                    GameManager.setLevelTime(time);
                    Constants.RELOAD_TIME_PRICE = price;
                    userManager.withdraw(price);
                    userManager.setLastPlayedDate(new Date());
                    canvas.getActors().removeValue(window, true);
                    toGamePlayScene();
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

        boolean isPlayable = userManager.getUser().isPlayable();
        Label cantPlayLabel = null;
        if (!isPlayable) {
            String message = "Oops! Come back after " + userManager.getUser().getWaitTimeToPlay();
            cantPlayLabel = new Label(message, skin);
            cantPlayLabel.setWrap(true);
            resizeUI(cantPlayLabel);
            cantPlayLabel.setAlignment(Align.center);
        }

        //window.setDebug(true);
        window.setFillParent(true);
        window.padTop(20f).padBottom(20f).padRight(10f).padLeft(10f);
        window.top();

        window.add(closeBtn).left().width(closeBtn.getWidth()).height(closeBtn.getHeight());
        window.add(accountBalanceLabel).right();
        window.row();

        if (!isPlayable) {
            window.add(cantPlayLabel).center().colspan(2).expandX().padTop(350f).width(cantPlayLabel.getWidth()).height(cantPlayLabel.getHeight());
            window.row();

            window.pack();
            canvas.addActor(window);
            ActorAnimation.instance().slideIn(window, ActorAnimation.DOWN, .7f, Interpolation.swingOut);

            return;
        }

        window.add(titleLabel).center().colspan(2).padTop(10f).width(titleLabel.getWidth()).height(titleLabel.getHeight());
        window.row();

        //window.add(reloadTime).center().colspan(2).padTop(10f);
        //window.row();

        window.add(scrollPane).colspan(2).expandX().padTop(20f).width(450f);
        window.pack();
        canvas.addActor(window);
        ActorAnimation.instance().slideIn(window, ActorAnimation.DOWN, .7f, Interpolation.swingOut);
    }

    private String timeToString(float timeInMins) {
        String timeString = (int) timeInMins + " minutes";
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

    private void toGamePlayScene() {
        xGdx.setScene(
                new GamePlayScene(),
                SceneTransitions.slide(
                        1f, SceneTransitionDirection.UP, true, Interpolation.pow5Out
                )
        );
    }
}
