package com.isoterik.cash4life.cashpuzzles.components;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.isoterik.cash4life.GlobalConstants;
import com.isoterik.cash4life.cashpuzzles.components.SelectorComponent;
import com.isoterik.cash4life.cashpuzzles.components.managers.LetterManager;
import com.isoterik.cash4life.cashpuzzles.components.managers.WordManager;
import io.github.isoteriktech.xgdx.Component;
import io.github.isoteriktech.xgdx.GameObject;
import io.github.isoteriktech.xgdx.Transform;
import io.github.isoteriktech.xgdx.XGdx;

public class LetterComponent extends Component {
    private WordManager wordManager;
    private LetterManager letterManager;

    private Transform transform;

    private static boolean isTouched;

    private TextureRegion foundedSprite;

    @Override
    public void start() {
        wordManager = gameObject.getHostScene().findGameObject("wordManager").getComponent(WordManager.class);
        letterManager = gameObject.getHostScene().findGameObject("letterManager").getComponent(LetterManager.class);

        transform = gameObject.transform;
    }

    @Override
    public void update(float deltaTime) {
        if (input.isTouched()) {
            Vector2 touchPos = new Vector2(input.getTouchedX(), input.getTouchedY());
            if (touched(touchPos) && !isTouched) {
                GameObject selector = scene.newSpriteObject(XGdx.instance().assets.getTexture(
                        GlobalConstants.CASH_PUZZLES_ASSETS_HOME + "/images/square.png")
                );
                selector.transform.setPosition(transform.getX(), transform.getY());
                selector.transform.setSize(transform.getWidth(), transform.getHeight());
                selector.setTag("selector");

                SelectorComponent selectorComponent = new SelectorComponent();
                selectorComponent.setWords(wordManager.getLoadedWords());
                selectorComponent.setSize(letterManager.getSize());
                selector.addComponent(selectorComponent);
                addGameObject(selector);

                isTouched = true;
            }
        }

        if (! input.isTouched()) {
            isTouched = false;
        }
    }

    private boolean touched(Vector2 touchPos) {
        return touchPos.x >= transform.getX() && touchPos.x <= transform.getX() + transform.getWidth()
                && touchPos.y >= transform.getY() && touchPos.y <= transform.getY() + transform.getHeight();
    }

    public void setFoundedSprite(TextureRegion sprite) {
        foundedSprite = sprite;
    }

    public TextureRegion getFoundedSprite() {
        return foundedSprite;
    }
}
