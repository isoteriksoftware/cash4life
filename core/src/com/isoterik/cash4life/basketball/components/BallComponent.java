package com.isoterik.cash4life.basketball.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Timer;
import com.isoterik.cash4life.basketball.components.managers.BasketballManager;
import io.github.isoteriktech.xgdx.ActorGameObject;
import io.github.isoteriktech.xgdx.Component;
import io.github.isoteriktech.xgdx.Transform;
import io.github.isoteriktech.xgdx.physics2d.PhysicsManager2d;
import io.github.isoteriktech.xgdx.physics2d.RigidBody2d;

public class BallComponent extends Component {
    private BasketballManager basketballManager;

    private ActorGameObject actorGameObject;
    private Transform transform;

    private float timeToCompletion = 0.5f;

    @Override
    public void start() {
        basketballManager = scene.findGameObject("basketballManager").getComponent(BasketballManager.class);

        actorGameObject = (ActorGameObject) gameObject;
        transform = gameObject.transform;
    }

    public void shoot(float swipeMagnitude) {
        if (!gameObject.hasComponent(RigidBody2d.class))
            gameObject.addComponent(new RigidBody2d(BodyDef.BodyType.DynamicBody, PhysicsManager2d.setup(scene)));

        //gameObject.getComponent(RigidBody2d.class).getBody().setGravityScale(0.5f);

        float x = ((transform.getX() + transform.getWidth() / 2) - scene.getGameWorldUnits().getWorldWidth() / 2);
        float xx = 60 + (Math.abs(x) - 1) * 5;
        Vector2 force = new Vector2(
                x * -xx,
                500f + (swipeMagnitude - 1) * 50f
        );

        System.out.println(swipeMagnitude);
        System.out.println(force);
        gameObject.getComponent(RigidBody2d.class).getBody().applyForceToCenter(force, false);
        actorGameObject.actorTransform.actor.addAction(Actions.sizeTo(
                0.4f,
                0.4f,
                timeToCompletion,
                Interpolation.fastSlow
        ));
    }

    public void shoot1(float force) {
        actorGameObject.actorTransform.actor.addAction(Actions.moveTo(
                (scene.getGameWorldUnits().getWorldWidth() / 2f) - (transform.getWidth() / 2),
                force * 4 - (transform.getHeight() / 2),
                timeToCompletion,
                Interpolation.fastSlow
        ));
        actorGameObject.actorTransform.actor.addAction(Actions.sizeTo(
                0.4f,
                0.4f,
                timeToCompletion,
                Interpolation.fastSlow
        ));
        if (!gameObject.hasComponent(RigidBody2d.class))
            gameObject.addComponent(new RigidBody2d(BodyDef.BodyType.KinematicBody, PhysicsManager2d.setup(scene)));
        //Timer.schedule(onCompleteTask, timeToCompletion, 0f);
    }

    private final Timer.Task onCompleteTask = new Timer.Task() {
        @Override
        public void run() {
            if (!gameObject.hasComponent(RigidBody2d.class))
                gameObject.addComponent(new RigidBody2d(BodyDef.BodyType.DynamicBody, PhysicsManager2d.setup(scene)));

            //gameObject.getComponent(RigidBody2d.class).getBody().setActive(true);
            cancel();
        }
    };
}
