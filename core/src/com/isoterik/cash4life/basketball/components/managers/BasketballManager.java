package com.isoterik.cash4life.basketball.components.managers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.isoterik.cash4life.GlobalConstants;
import com.isoterik.cash4life.basketball.Constants;
import com.isoterik.cash4life.basketball.components.BallComponent;
import com.isoterik.cash4life.basketball.components.BasketComponent;
import io.github.isoteriktech.xgdx.*;
import io.github.isoteriktech.xgdx.physics2d.PhysicsMaterial2d;
import io.github.isoteriktech.xgdx.physics2d.colliders.CircleCollider;

public class BasketballManager extends Component {
    private UIManager uiManager;

    private GameObject currentBall;

    private float modX;
    private float modY;

    private int remainingBalls;

    private final TextureAtlas assetsAtlas = XGdx.instance().assets.getAtlas(
            GlobalConstants.BASKETBALL_ASSETS_HOME + "/skin/ui.atlas"
    );

    public boolean creatable;

    @Override
    public void start() {
        uiManager = scene.findGameObject("uiManager").getComponent(UIManager.class);

        remainingBalls = Constants.SELECTED_DATA.getTotalBalls();

        modX = uiManager.modX;
        modY = uiManager.modY;

        uiManager.setRemainingBallsText(remainingBalls);

        createRing();
        createBall();
    }

    private void createRing() {
        TextureRegion pollTexture = assetsAtlas.findRegion("Ring Stand");
        GameObject pollGameObject = scene.newSpriteObject(pollTexture);

        float xPollPos = scene.getGameWorldUnits().getWorldWidth() / 2;
        float yPollPos = 5.1f;
        pollGameObject.transform.setPosition(xPollPos + 0.02f, yPollPos);
        resizeTransform(pollGameObject.transform);
        pollGameObject.transform.setSize(
                pollGameObject.transform.getWidth() + 0.2f,
                pollGameObject.transform.getHeight() + 0.2f
        );
        centerTransformOrigin(pollGameObject.transform);
        addGameObject(pollGameObject);

        TextureRegion basketTexture = assetsAtlas.findRegion("Ring Normal");
        GameObject basketGameObject = scene.newSpriteObject(basketTexture);
        basketGameObject.addComponent(new BasketComponent());
        basketGameObject.setTag("basket");

        float xPos = scene.getGameWorldUnits().getWorldWidth() / 2;
        float yPos = 6.2f;
        basketGameObject.transform.setPosition(xPos, yPos);
        resizeTransform(basketGameObject.transform);
        basketGameObject.transform.setSize(
                basketGameObject.transform.getWidth() + 0.2f,
                basketGameObject.transform.getHeight() + 0.2f
        );
        centerTransformOrigin(basketGameObject.transform);
        addGameObject(basketGameObject);
    }

    public void createBall() {
        String[] ballSkins = {"Ball Shoot 1", "Ball Shoot 2", "Ball Shoot 3"};
        TextureRegion ballTexture = assetsAtlas.findRegion(ballSkins[MathUtils.random(0, ballSkins.length - 1)]);
        GameObject ballGameObject = scene.newActorSpriteObject(ballTexture);

        CircleCollider circleCollider = new CircleCollider();
        PhysicsMaterial2d physicsMaterial2d = new PhysicsMaterial2d();
        physicsMaterial2d.bounciness = 0.4f;
        circleCollider.setMaterial(physicsMaterial2d);

        ballGameObject.addComponent(circleCollider);

        BallComponent ballComponent = new BallComponent();
        ballComponent.setBasketballManager(this);
        ballGameObject.addComponent(ballComponent);
        ballGameObject.setTag("ball");

        int[] choices = {1, 1, 2, 3, 3};
        int randomChoice = choices[MathUtils.random(0, choices.length - 1)];
        float xPos = scene.getGameWorldUnits().getWorldWidth() / 2f;
        if (randomChoice == 1) {
            xPos = MathUtils.random(0.6f, 1.2f);
        }
        else if (randomChoice == 3) {
            xPos = MathUtils.random(3.6f, 4.2f);
        }
        float yPos = 1f;

        ballGameObject.transform.setPosition(xPos, yPos);
        ballGameObject.transform.setSize(1f, 1f);

        centerOrigin(ballGameObject.transform);
        centerTransformOrigin(ballGameObject.transform);

        addGameObject(ballGameObject);
        currentBall = ballGameObject;
    }

    private void resizeTransform(Transform transform) {
        transform.setSize(modX * transform.getWidth(), modY * transform.getHeight());
    }

    private void centerOrigin(Transform transform) {
        transform.setOrigin(transform.getWidth() / 2, transform.getHeight() / 2);
    }

    private void centerTransformOrigin(Transform transform) {
        transform.setPosition(transform.getX() - transform.getWidth() / 2,
                transform.getY() - transform.getHeight() / 2);
    }

    public void throwBall(float swipeMagnitude) {
        currentBall.getComponent(BallComponent.class).shoot(swipeMagnitude);

        remainingBalls--;
        uiManager.setRemainingBallsText(remainingBalls);
        creatable = remainingBalls > 0;
    }

    public int getRemainingBalls() {
        return remainingBalls;
    }
}
