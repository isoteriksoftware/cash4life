package com.isoterik.cash4life.basketball.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Timer;
import com.isoterik.cash4life.GlobalConstants;
import com.isoterik.cash4life.basketball.components.managers.BasketballManager;
import com.isoterik.cash4life.basketball.components.managers.UIManager;
import io.github.isoteriktech.xgdx.*;
import io.github.isoteriktech.xgdx.physics2d.*;
import io.github.isoteriktech.xgdx.physics2d.colliders.BoxCollider;
import io.github.isoteriktech.xgdx.physics2d.colliders.CircleCollider;
import io.github.isoteriktech.xgdx.x2d.components.debug.CircleDebugRenderer;
import io.github.isoteriktech.xgdx.x2d.components.renderer.SpriteRenderer;

public class BallComponent extends Component {
    private BasketballManager basketballManager;
    private BasketComponent basketComponent;

    private Transform transform;

    private float centeredYWinPosition = 7f;

    private boolean checkCenterWin;
    private boolean isScoreTaskScheduled;

    @Override
    public void start() {
        basketComponent = scene.findGameObject("basket").getComponent(BasketComponent.class);

        transform = gameObject.transform;

        checkCenterWin = MathUtils.isEqual(transform.getX(), 1.9f);
    }

    @Override
    public void update(float deltaTime) {
        if (checkCenterWin) {
            if (gameObject.hasComponent(RigidBody2d.class)) {
                RigidBody2d rigidBody2d = gameObject.getComponent(RigidBody2d.class);
                float yPos = rigidBody2d.getBody().getPosition().y;
                //if (yPos > 6f) System.out.println(yPos);
                if (yPos >= centeredYWinPosition && !isScoreTaskScheduled) {
                    Timer.schedule(scoreTimerTask, 1f);
                    isScoreTaskScheduled = true;
                }
            }
        }
    }

    private final Timer.Task scoreTimerTask = new Timer.Task() {
        @Override
        public void run() {
            basketComponent.ballWin = true;
            scene.findGameObject("uiManager").getComponent(UIManager.class).updateScoreText();
            cancel();
        }
    };

    public void setBasketballManager(BasketballManager basketballManager) {
        this.basketballManager = basketballManager;
    }

    public void shoot(float swipeMagnitude) {
        PhysicsManager2d physicsManager2d = PhysicsManager2d.setup(scene);

        setBasketEdgeColsRigidBody(physicsManager2d);
        setBasketFlowColsRigidBody(physicsManager2d);
        basketComponent.flowIndices.clear();

        RigidBody2d rigidBody2d = new RigidBody2d(BodyDef.BodyType.DynamicBody, physicsManager2d);
        gameObject.addComponent(rigidBody2d);
        rigidBody2d.getBody().setFixedRotation(true);

        float x = ((scene.getGameWorldUnits().getWorldWidth() / 2) - (transform.getX() + transform.getWidth() / 2));
        Vector2 force = new Vector2(x * 0.5f, 9f + (swipeMagnitude - 1) * 0.5f);
        rigidBody2d.getComponent(RigidBody2d.class).getBody().applyLinearImpulse(
                force,
                rigidBody2d.getBody().getPosition(),
                true
        );

        Timer.schedule(resizeTransformTimerTask, 0f, 0.08f, 5);
        Timer.schedule(removeGameObjectTimerTask, 2f);
    }

    private void setBasketEdgeColsRigidBody(PhysicsManager2d physicsManager2d) {
        GameObject[] edgeCols = basketComponent.getEdgeCols();
        for (GameObject edgeCol: edgeCols) {
            if (edgeCol.hasComponent(RigidBody2d.class))
                edgeCol.removeComponent(RigidBody2d.class);
            edgeCol.addComponent(new RigidBody2d(BodyDef.BodyType.StaticBody, physicsManager2d));
        }
    }

    private void setBasketFlowColsRigidBody(PhysicsManager2d physicsManager2d) {
        GameObject[] flowCols = basketComponent.getFlowCols();
        for (GameObject flowCol: flowCols) {
            if (flowCol.hasComponent(RigidBody2d.class))
                flowCol.removeComponent(RigidBody2d.class);
            flowCol.addComponent(new RigidBody2d(BodyDef.BodyType.StaticBody, physicsManager2d));
        }
    }

    private final Timer.Task resizeTransformTimerTask = new Timer.Task() {
        private final float offset = 0.08f;
        @Override
        public void run() {
            float value = transform.getWidth() - offset;
            transform.setSize(value, value);
            setCollider();
        }

        private void setCollider() {
            if (gameObject.hasComponent(CircleCollider.class))
                gameObject.removeComponent(CircleCollider.class);

            CircleCollider circleCollider = new CircleCollider();
            PhysicsMaterial2d physicsMaterial2d = new PhysicsMaterial2d();
            physicsMaterial2d.bounciness = 0.4f;
            circleCollider.setMaterial(physicsMaterial2d);
            gameObject.addComponent(circleCollider);
        }
    };

    private final Timer.Task removeGameObjectTimerTask = new Timer.Task() {
        @Override
        public void run() {
            if (basketballManager.getRemainingBalls() == 0 && !basketComponent.ballWin) {
                scene.findGameObject("uiManager").getComponent(UIManager.class).gameLost();
            }

            if (basketballManager.creatable) basketballManager.createBall();

            basketComponent.ballWin = false;

            scene.removeGameObject(gameObject);
            cancel();
        }
    };
}
