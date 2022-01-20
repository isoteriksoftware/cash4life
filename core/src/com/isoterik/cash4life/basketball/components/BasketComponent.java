package com.isoterik.cash4life.basketball.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.isoterik.cash4life.GlobalConstants;
import com.isoterik.cash4life.basketball.components.managers.UIManager;
import io.github.isoteriktech.xgdx.Component;
import io.github.isoteriktech.xgdx.GameObject;
import io.github.isoteriktech.xgdx.XGdx;
import io.github.isoteriktech.xgdx.physics2d.Collision2d;
import io.github.isoteriktech.xgdx.physics2d.Physics2d;
import io.github.isoteriktech.xgdx.physics2d.colliders.BoxCollider;
import io.github.isoteriktech.xgdx.x2d.components.renderer.SpriteRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BasketComponent extends Component {
    private UIManager uiManager;

    private GameObject edgeCol1;
    private GameObject edgeCol2;

    private GameObject flowCol1;
    private GameObject flowCol2;
    private GameObject flowCol3;
    private GameObject flowCol4;
    private GameObject flowCol5;

    public List<Integer> flowIndices;
    private List<Integer> flowScoreIndices;
    private List<Integer> flowScoreIndices2;
    private List<Integer> unIdealFlowScoreIndices;
    private List<Integer> unIdealFlowScoreIndices2;

    public boolean ballWin;

    @Override
    public void start() {
        uiManager = scene.findGameObject("uiManager").getComponent(UIManager.class);

        flowIndices = new ArrayList<>();

        flowScoreIndices = Arrays.asList(1, 2, 3, 4, 5);
        flowScoreIndices2 = Arrays.asList(5, 4, 3, 2, 1, 1, 2, 3, 4, 5);

        unIdealFlowScoreIndices = Arrays.asList(1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
        unIdealFlowScoreIndices2 = Arrays.asList(5, 5, 4, 4, 3, 3, 2, 2, 1, 1, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5);

        setEdgeColliders();
        setFlowColliders();
    }

    private void setEdgeColliders() {
        Vector3 basketSize = gameObject.transform.size;
        Vector3 basketPosition = gameObject.transform.position;

        float colSize = 0.05f;
        String texturePath = GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/images/square.png";

        GameObject col1 = scene.newSpriteObject(XGdx.instance().assets.getTexture(texturePath));
        col1.getComponent(SpriteRenderer.class).setOpacity(0f);
        col1.transform.setSize(colSize / 2, colSize / 2);
        col1.transform.setPosition(basketPosition.x + colSize * 1.5f, basketPosition.y + basketSize.y - colSize * 2f);
        col1.addComponent(new BoxCollider());

        GameObject col2 = scene.newSpriteObject(XGdx.instance().assets.getTexture(texturePath));
        col2.getComponent(SpriteRenderer.class).setOpacity(0f);
        col2.transform.setSize(colSize / 2, colSize / 2);
        col2.transform.setPosition(basketPosition.x + basketSize.x - colSize * 1f, basketPosition.y + basketSize.y - colSize * 2f);
        col2.addComponent(new BoxCollider());

        edgeCol1 = col1;
        edgeCol2 = col2;

        addGameObject(col1);
        addGameObject(col2);
    }

    public GameObject[] getEdgeCols() {
        return new GameObject[]{edgeCol1, edgeCol2};
    }

    private void setFlowColliders() {
        Vector3 basketSize = gameObject.transform.size;
        Vector3 basketPosition = gameObject.transform.position;

        Vector2 colSize = new Vector2(basketSize.x - 0.05f * 3f, 0.05f);
        String texturePath = GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/images/square.png";

        GameObject col1 = scene.newSpriteObject(XGdx.instance().assets.getTexture(texturePath));
        col1.getComponent(SpriteRenderer.class).setOpacity(0);
        col1.addComponent(new BoxCollider());
        col1.transform.setSize(colSize.x - 0.05f * 8, colSize.y);
        col1.transform.setPosition(basketPosition.x + 0.05f * 5.5f, basketPosition.y + basketSize.y - colSize.y * 3);

        BoxCollider boxCollider1 = new BoxCollider();
        boxCollider1.setIsSensor(true);
        col1.addComponent(boxCollider1);
        col1.addComponent(new Physics2d() {
            @Override
            public void onSensorEnter2d(Collision2d collision) {
                if (collision.compareTag("ball")) {
                    flowIndices.add(1);
                }
            }
        });

        GameObject col2 = scene.newSpriteObject(XGdx.instance().assets.getTexture(texturePath));
        col2.getComponent(SpriteRenderer.class).setOpacity(0);
        col2.addComponent(new BoxCollider());
        col2.transform.setSize(colSize.x - 0.05f * 8, colSize.y);
        col2.transform.setPosition(basketPosition.x + 0.05f * 5.5f, basketPosition.y + basketSize.y - colSize.y * 6);

        BoxCollider boxCollider2 = new BoxCollider();
        boxCollider2.setIsSensor(true);
        col2.addComponent(boxCollider2);
        col2.addComponent(new Physics2d() {
            @Override
            public void onSensorEnter2d(Collision2d collision) {
                if (collision.compareTag("ball")) {
                    flowIndices.add(2);
                }
            }
        });

        GameObject col3 = scene.newSpriteObject(XGdx.instance().assets.getTexture(texturePath));
        col3.getComponent(SpriteRenderer.class).setOpacity(0);
        col3.addComponent(new BoxCollider());
        col3.transform.setSize(colSize.x - 0.05f * 8, colSize.y);
        col3.transform.setPosition(basketPosition.x + 0.05f * 5.5f, basketPosition.y + basketSize.y - colSize.y * 10);

        BoxCollider boxCollider3 = new BoxCollider();
        boxCollider3.setIsSensor(true);
        col3.addComponent(boxCollider3);
        col3.addComponent(new Physics2d() {
            @Override
            public void onSensorEnter2d(Collision2d collision) {
                if (collision.compareTag("ball")) {
                    flowIndices.add(3);
                }
            }
        });

        GameObject col4 = scene.newSpriteObject(XGdx.instance().assets.getTexture(texturePath));
        col4.getComponent(SpriteRenderer.class).setOpacity(0);
        col4.addComponent(new BoxCollider());
        col4.transform.setSize(colSize.x - 0.05f * 8, colSize.y);
        col4.transform.setPosition(basketPosition.x + 0.05f * 5.5f, basketPosition.y + basketSize.y - colSize.y * 13);

        BoxCollider boxCollider4 = new BoxCollider();
        boxCollider4.setIsSensor(true);
        col4.addComponent(boxCollider4);
        col4.addComponent(new Physics2d() {
            @Override
            public void onSensorEnter2d(Collision2d collision) {
                if (collision.compareTag("ball")) {
                    flowIndices.add(4);
                }
            }
        });

        GameObject col5 = scene.newSpriteObject(XGdx.instance().assets.getTexture(texturePath));
        col5.getComponent(SpriteRenderer.class).setOpacity(0);
        col5.addComponent(new BoxCollider());
        col5.transform.setSize(colSize.x - 0.05f * 8, colSize.y);
        col5.transform.setPosition(basketPosition.x + 0.05f * 5.5f, basketPosition.y + basketSize.y - colSize.y * 16);

        BoxCollider boxCollider5 = new BoxCollider();
        boxCollider5.setIsSensor(true);
        col5.addComponent(boxCollider5);
        col5.addComponent(new Physics2d() {
            @Override
            public void onSensorEnter2d(Collision2d collision) {
                if (collision.compareTag("ball")) {
                    flowIndices.add(5);
                    checkScore();
                }
            }
        });

        flowCol1 = col1;
        flowCol2 = col2;
        flowCol3 = col3;
        flowCol4 = col4;
        flowCol5 = col5;

        addGameObject(col1);
        addGameObject(col2);
        addGameObject(col3);
        addGameObject(col4);
        addGameObject(col5);

        col1.getComponent(BoxCollider.class).setIsSensor(true);
        col2.getComponent(BoxCollider.class).setIsSensor(true);
        col3.getComponent(BoxCollider.class).setIsSensor(true);
        col4.getComponent(BoxCollider.class).setIsSensor(true);
        col5.getComponent(BoxCollider.class).setIsSensor(true);
    }

    public GameObject[] getFlowCols() {
        return new GameObject[]{flowCol1, flowCol2, flowCol3, flowCol4, flowCol5};
    }

    private void checkScore() {
        //System.out.println(flowIndices.toString());
        if (flowIndices.equals(flowScoreIndices)|| flowIndices.equals(flowScoreIndices2)
            || flowIndices.equals(unIdealFlowScoreIndices)|| flowIndices.equals(unIdealFlowScoreIndices2)
        ) {
            ballWin = true;
            uiManager.updateScoreText();
        }
        else {
            ballWin = false;
        }
    }
}
