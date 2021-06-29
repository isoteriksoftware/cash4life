package com.isoterik.cash4life.cashpuzzles;

import com.badlogic.gdx.utils.Timer;
import io.github.isoteriktech.xgdx.Scene;
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions;

public class CashPuzzlesSplash extends Scene {
    public static final String GAME_TITLE = "Puzzles";

    public static final int RESOLUTION_WIDTH = 480;
    public static final int RESOLUTION_HEIGHT = 780;

    @Override
    public void transitionedToThisScene(Scene previousScene) {
        // Load the game scene.
        // You'll modify this when a real splash UI is ready

        Timer.post(new Timer.Task() {
            @Override
            public void run() {
                xGdx.setScene(new GameScene(), SceneTransitions.fade(1f));
            }
        });
    }
}
