package com.isoterik.cash4life.basketball.components.managers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.isoterik.cash4life.GlobalConstants;
import com.isoterik.cash4life.basketball.components.BallComponent;
import com.isoterik.cash4life.basketball.components.BasketComponent;
import io.github.isoteriktech.xgdx.*;
import io.github.isoteriktech.xgdx.physics2d.Physics2d;
import io.github.isoteriktech.xgdx.physics2d.PhysicsManager2d;
import io.github.isoteriktech.xgdx.physics2d.PhysicsMaterial2d;
import io.github.isoteriktech.xgdx.physics2d.RigidBody2d;
import io.github.isoteriktech.xgdx.physics2d.colliders.BoxCollider;
import io.github.isoteriktech.xgdx.physics2d.colliders.CircleCollider;

public class BasketballManager extends Component {
    private UIManager uiManager;

    private ActorGameObject currentBall;

    private float modX;
    private float modY;

    private int remainingBalls = 100;

    private final TextureAtlas assetsAtlas = XGdx.instance().assets.getAtlas(
            GlobalConstants.BASKETBALL_ASSETS_HOME + "/skin/ui.atlas"
    );

    @Override
    public void start() {
        uiManager = scene.findGameObject("uiManager").getComponent(UIManager.class);

        modX = uiManager.modX;
        modY = uiManager.modY;

        uiManager.setRemaiiningBallsText(remainingBalls);

        createRing();
        createBall();
    }

    private void createRing() {
        TextureRegion pollTexture = assetsAtlas.findRegion("Ring Stand");
        GameObject pollGameObject = scene.newSpriteObject(pollTexture);

        float xPollPos = scene.getGameWorldUnits().getWorldWidth() / 2;
        float yPollPos = 5f;
        pollGameObject.transform.setPosition(xPollPos + 0.02f, yPollPos);
        resizeTransform(pollGameObject.transform);
        centerTransformOrigin(pollGameObject.transform);
        addGameObject(pollGameObject);

        TextureRegion basketTexture = assetsAtlas.findRegion("Ring Normal");
        GameObject basketGameObject = scene.newSpriteObject(basketTexture);
        basketGameObject.addComponent(new BasketComponent());

        float xPos = scene.getGameWorldUnits().getWorldWidth() / 2;
        float yPos = 6.1f;
        basketGameObject.transform.setPosition(xPos, yPos);
        resizeTransform(basketGameObject.transform);
        centerTransformOrigin(basketGameObject.transform);
        addGameObject(basketGameObject);
    }

    private void createBall() {
        TextureRegion ballTexture = assetsAtlas.findRegion("Ball Normal");
        ActorGameObject ballGameObject = scene.newActorSpriteObject(ballTexture);

        CircleCollider circleCollider = new CircleCollider();
        PhysicsMaterial2d physicsMaterial2d = new PhysicsMaterial2d();
        physicsMaterial2d.bounciness = 0.4f;
        circleCollider.setMaterial(physicsMaterial2d);

        ballGameObject.addComponent(circleCollider);
        ballGameObject.addComponent(new BallComponent());
        ballGameObject.setTag("ball");

        float xPos = MathUtils.random(1f, 3.8f);
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

    public void shoot(float swipeMagnitude) {
        currentBall.getComponent(BallComponent.class).shoot(swipeMagnitude);

        if (remainingBalls > 0) {
            createBall();
            remainingBalls--;

            uiManager.setRemaiiningBallsText(remainingBalls);
        }
    }
}
