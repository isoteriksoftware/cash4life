package com.isoterik.cash4life.cashpuzzles.components;

import com.badlogic.gdx.math.Vector2;
import com.isoterik.cash4life.cashpuzzles.GameAssetsManager;
import com.isoterik.cash4life.cashpuzzles.WordManager;
import io.github.isoteriktech.xgdx.Component;
import io.github.isoteriktech.xgdx.GameObject;
import io.github.isoteriktech.xgdx.Transform;

public class LetterComponent extends Component {
    private Transform transform;

    private GameObject selector;

    private char letter;

    //private boolean isTouched;

    @Override
    public void start() {
        transform = gameObject.transform;
    }

    @Override
    public void update(float deltaTime) {
        if (input.isTouched()) {
            Vector2 touchPos = new Vector2(input.getTouchedX(), input.getTouchedY());
            if (
                    touchPos.x >= transform.getX() && touchPos.x <= transform.getX() + transform.getWidth()
                            && touchPos.y >= transform.getY() && touchPos.y <= transform.getY() + transform.getHeight()
                            && !Properties.isTouched
            ) {
                selector = scene.newSpriteObject(GameAssetsManager.getInstance().getSquare());
                selector.transform.setPosition(transform.getX(), transform.getY());
                SelectorComponent selectorComponent = new SelectorComponent();
                selectorComponent.setWords(WordManager.getInstance().getWordsCopy());
                selector.addComponent(selectorComponent);
                addGameObject(selector);
                Properties.isTouched = true;
            }
        }

        if (! input.isTouched()) {
            Properties.isTouched = false;
        }
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }
}
