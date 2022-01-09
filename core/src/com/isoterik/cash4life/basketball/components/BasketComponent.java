package com.isoterik.cash4life.basketball.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.isoterik.cash4life.GlobalConstants;
import io.github.isoteriktech.xgdx.Component;
import io.github.isoteriktech.xgdx.GameObject;
import io.github.isoteriktech.xgdx.XGdx;
import io.github.isoteriktech.xgdx.physics2d.colliders.BoxCollider;

public class BasketComponent extends Component {
    @Override
    public void start() {
        setEdgeColliders();
        setFlowColliders();
    }

    private void setEdgeColliders() {
        Vector3 basketSize = gameObject.transform.size;
        Vector3 basketPosition = gameObject.transform.position;

        float colSize = 0.05f;
        String texturePath = GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/images/square.png";

        GameObject col1 = scene.newSpriteObject(XGdx.instance().assets.getTexture(texturePath));
        col1.addComponent(new BoxCollider());
        col1.transform.setSize(colSize, colSize);
        col1.transform.setPosition(basketPosition.x, basketPosition.y + basketSize.y - colSize);

        GameObject col2 = scene.newSpriteObject(XGdx.instance().assets.getTexture(texturePath));
        col2.addComponent(new BoxCollider());
        col2.transform.setSize(colSize, colSize);
        col2.transform.setPosition(basketPosition.x + basketSize.x - colSize, basketPosition.y + basketSize.y - colSize);

        addGameObject(col1);
        addGameObject(col2);
    }

    private void setFlowColliders() {
        Vector3 basketSize = gameObject.transform.size;
        Vector3 basketPosition = gameObject.transform.position;

        Vector2 colSize = new Vector2(basketSize.x - 0.05f * 3f, 0.05f);
        String texturePath = GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/images/square.png";

        GameObject col1 = scene.newSpriteObject(XGdx.instance().assets.getTexture(texturePath));
        col1.addComponent(new BoxCollider());
        col1.transform.setSize(colSize.x, colSize.y);
        col1.transform.setPosition(basketPosition.x + 0.05f, basketPosition.y + basketSize.y - colSize.y * 2);

        addGameObject(col1);

        GameObject col2 = scene.newSpriteObject(XGdx.instance().assets.getTexture(texturePath));
        col2.addComponent(new BoxCollider());
        col2.transform.setSize(colSize.x - 0.05f, colSize.y);
        col2.transform.setPosition(basketPosition.x + 0.05f * 2, basketPosition.y + basketSize.y - colSize.y * 4);

        GameObject col3 = scene.newSpriteObject(XGdx.instance().assets.getTexture(texturePath));
        col3.addComponent(new BoxCollider());
        col3.transform.setSize(colSize.x - 0.05f * 3, colSize.y);
        col3.transform.setPosition(basketPosition.x + 0.05f * 3, basketPosition.y + basketSize.y - colSize.y * 6);

        GameObject col4 = scene.newSpriteObject(XGdx.instance().assets.getTexture(texturePath));
        col4.addComponent(new BoxCollider());
        col4.transform.setSize(colSize.x - 0.05f * 6, colSize.y);
        col4.transform.setPosition(basketPosition.x + 0.05f * 5, basketPosition.y + basketSize.y - colSize.y * 8);

        GameObject col5 = scene.newSpriteObject(XGdx.instance().assets.getTexture(texturePath));
        col5.addComponent(new BoxCollider());
        col5.transform.setSize(colSize.x - 0.05f * 6, colSize.y);
        col5.transform.setPosition(basketPosition.x + 0.05f * 5, basketPosition.y + basketSize.y - colSize.y * 10);

        addGameObject(col1);
        addGameObject(col2);
        addGameObject(col3);
        addGameObject(col4);
        addGameObject(col5);
    }
}
