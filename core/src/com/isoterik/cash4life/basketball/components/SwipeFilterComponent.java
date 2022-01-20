package com.isoterik.cash4life.basketball.components;

import com.badlogic.gdx.math.Vector2;
import com.isoterik.cash4life.basketball.components.managers.BasketballManager;
import io.github.isoteriktech.xgdx.Component;

public class SwipeFilterComponent extends Component {
    private BasketballManager basketballManager;

    private boolean swipeOngoing;
    private boolean doOnce;

    private Vector2 initialTouchPos;
    private Vector2 finalTouchPos;

    @Override
    public void start() {
        basketballManager = scene.findGameObject("basketballManager").getComponent(BasketballManager.class);
    }

    @Override
    public void update(float deltaTime) {
        if (input.isTouched()) {
            finalTouchPos = new Vector2(input.getTouchedX(), input.getTouchedY());
            if (!swipeOngoing) {
                initialTouchPos = finalTouchPos;
                swipeOngoing = true;
                doOnce = true;
            }
        }
        else {
            if (doOnce) {
                float swipeMagnitude = Math.abs(finalTouchPos.y - initialTouchPos.y);
                if (swipeMagnitude == 0) return;

                basketballManager.throwBall(swipeMagnitude);

                swipeOngoing = false;
                doOnce = false;
            }
        }
    }
}
