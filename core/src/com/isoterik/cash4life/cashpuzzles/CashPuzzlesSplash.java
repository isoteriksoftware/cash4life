package com.isoterik.cash4life.cashpuzzles;

import com.badlogic.gdx.ai.steer.behaviors.Alignment;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.isoterik.cash4life.Cash4Life;
import com.isoterik.cash4life.DemoStorage;
import com.isoterik.cash4life.DemoUser;
import com.isoterik.cash4life.GlobalConstants;
import com.isoterik.cash4life.cashpuzzles.components.GameManager;
import io.github.isoteriktech.xgdx.Scene;
import io.github.isoteriktech.xgdx.ui.ActorAnimation;
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection;
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions;

import javax.swing.*;
import java.util.*;

public class CashPuzzlesSplash extends Scene {
    public static final String GAME_TITLE = "Puzzles";

    public static final int RESOLUTION_WIDTH = 480;
    public static final int RESOLUTION_HEIGHT = 780;

    private float modX;
    private float modY;

    private Skin skin;

    private final HashMap<Integer, Integer> timeAndPrice = new HashMap<>();

    public static final DemoUser user = new DemoUser("", "", "", 5000f);

    public CashPuzzlesSplash() {
        setupCamera();
        setBackgroundColor(new Color(.1f, .1f, .2f, 1f));
        setupUI();
    }

    private void setupCamera() {
        canvas = new Stage(new StretchViewport(Constants.GUI_WIDTH, Constants.GUI_HEIGHT));

        input.getInputMultiplexer().addProcessor(canvas);
    }

    private void setupUI() {
        Image bg = new Image(this.xGdx.assets.regionForTexture(
                GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/images/bg.png"
        ));

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
                if (DemoStorage.IS_GAME_SAVED) {
                    popUpNewGameDialog();
                }
                else {
                    popUpBuyTimeWindow();
                }
            }
        });
        resizeUI(newGameBtn);

        Button continueBtn = new Button(skin, "continue");
        continueBtn.setVisible(DemoStorage.IS_GAME_SAVED);
        continueBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toGamePlayScene();
            }
        });
        resizeUI(continueBtn);

        Button settingsBtn = new Button(skin, "settings");
        resizeUI(settingsBtn);

        Label accountBalanceLabel = new Label(user.getAccountBalance(0), skin);
        accountBalanceLabel.setFontScale(0.7f);

        Table table = new Table();
        //table.setDebug(true);
        table.setFillParent(true);
        table.padTop(20f).padBottom(20f).padRight(10f).padLeft(10f);
        table.top();

        table.add(settingsBtn).left().expandX().width(settingsBtn.getWidth()).height(settingsBtn.getHeight());
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

    private void resizeUI(Label label, float sizeFactor) {
        label.setSize(modX * label.getWidth() * sizeFactor, modY * label.getHeight() * sizeFactor);
    }

    private void resizeUI(Window window) {
        window.setSize(modX * window.getWidth(), modY * window.getHeight());
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
                popUpBuyTimeWindow();
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

    private void popUpBuyTimeWindow() {
        Window window = new Window("", skin);

        Label accountBalanceLabel = new Label(user.getAccountBalance(0), skin);
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

        loadTimeAndPrices();
        Object[] timeArray = timeAndPrice.keySet().toArray();
        Arrays.sort(timeArray);

        TextButton[] timeAndPricesButton = new TextButton[timeAndPrice.size()];
        String[] styleNames = new String[] {
            "p_pink", "p_purple", "p_blue", "p_green", "p_orange"
        };
        for (int i = 0; i < timeAndPricesButton.length; i++) {
            int time = (int) timeArray[i];
            int price = timeAndPrice.get(time);

            String spaces = "              ";
            String displayName = spaces + time + " minutes\n" + spaces + price + " naira";
            String styleName = styleNames[i];

            timeAndPricesButton[i] = new TextButton(displayName, skin, styleName);
            timeAndPricesButton[i].getLabel().setAlignment(Align.left);
            timeAndPricesButton[i].getLabel().setFontScale(0.7f);
            resizeUI(timeAndPricesButton[i]);
            resizeUI(timeAndPricesButton[i], 2);

            if (user.getAccountBalance() < price) {
                timeAndPricesButton[i].setDisabled(true);
                timeAndPricesButton[i].getLabel().setColor(Color.GRAY);
            }
            timeAndPricesButton[i].addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    DemoStorage.RESET();

                    GameManager.setLevelTime(time);
                    user.withdraw(price);
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

    private void loadTimeAndPrices() {
        timeAndPrice.put(2, 500);
        timeAndPrice.put(5, 1000);
        timeAndPrice.put(10, 2000);
        timeAndPrice.put(30, 3500);
        timeAndPrice.put(60, 7000);
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
}
