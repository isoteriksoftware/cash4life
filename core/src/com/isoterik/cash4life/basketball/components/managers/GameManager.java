package com.isoterik.cash4life.basketball.components.managers;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.isoterik.cash4life.GlobalConstants;
import com.isoterik.cash4life.basketball.components.SwipeFilterComponent;
import io.github.isoteriktech.xgdx.Component;
import io.github.isoteriktech.xgdx.GameObject;
import io.github.isoteriktech.xgdx.XGdx;
import io.github.isoteriktech.xgdx.physics2d.Collision2d;
import io.github.isoteriktech.xgdx.physics2d.Physics2d;
import io.github.isoteriktech.xgdx.physics2d.PhysicsManager2d;
import io.github.isoteriktech.xgdx.physics2d.RigidBody2d;
import io.github.isoteriktech.xgdx.physics2d.colliders.BoxCollider;
import io.github.isoteriktech.xgdx.x2d.components.renderer.SpriteRenderer;

public class GameManager extends Component {
    private static float levelTime = 5f;

    public static void setLevelTime(float time) {
        levelTime = time;
    }

    public static float getLevelTime() {
        return levelTime;
    }

    @Override
    public void start() {
        createSwipeFilter();
        createGround();
    }

    private void createSwipeFilter() {
        GameObject filter = scene.newSpriteObject(XGdx.instance().assets.getTexture(
                GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/images/bg.png")
        );
        filter.getComponent(SpriteRenderer.class).setOpacity(0f);
        filter.transform.setSize(scene.getGameWorldUnits().getWorldWidth(), scene.getGameWorldUnits().getWorldHeight());
        filter.addComponent(new BoxCollider());
        filter.addComponent(new SwipeFilterComponent());
        addGameObject(filter);
    }

    private void createGround() {
        GameObject ground = scene.newSpriteObject(XGdx.instance().assets.getTexture(
                GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/images/bg.png")
        );
        ground.setTag("ground");
        ground.transform.setSize(scene.getGameWorldUnits().getWorldWidth(), 0.05f);
        ground.transform.setPosition(0, 2.5f);
        ground.addComponent(new RigidBody2d(BodyDef.BodyType.KinematicBody, PhysicsManager2d.setup(scene)));
        ground.addComponent(new BoxCollider());
        ground.addComponent(new Physics2d() {
            @Override
            public void onCollisionEnter2d(Collision2d collision) {
                if (collision.compareTag("ball")) {
                    System.out.println("Collided");
                }
            }
        });
        //ground.getComponent(BoxCollider.class).setEnabled(false);
        addGameObject(ground);
    }
}
