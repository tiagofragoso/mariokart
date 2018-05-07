package com.lpoo1718_t1g3.mariokart.view.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lpoo1718_t1g3.mariokart.model.entities.EntityModel;

import static com.lpoo1718_t1g3.mariokart.view.GameView.PIXEL_TO_METER;

abstract class EntityView {

    Sprite sprite;

    public EntityView() {
        sprite = createSprite();
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public abstract Sprite createSprite();

    public void update(EntityModel model) {
        sprite.setCenter(model.getX()/PIXEL_TO_METER, model.getY()/PIXEL_TO_METER);
        sprite.setRotation(model.getRotation());
    }

}
