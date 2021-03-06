package com.lpoo1718_t1g3.mariokart.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;

/**
 * Class that represents the button control view
 *
 * @see ControlView
 */
public class ButtonControlView extends ControlView {

    private Touchpad joystick;

    /**
     * Initializes button control view
     */
    public ButtonControlView() {
        throttle.setSize(stage.getWidth() / 2f, stage.getHeight() / 5f);
        throttle.setPosition(stage.getWidth(), 0, Align.bottomRight);

        brake.setSize(stage.getWidth() / 2f, stage.getHeight() / 5f);
        brake.setPosition(0, 0, Align.bottomLeft);

        Touchpad.TouchpadStyle joystickStyle = new Touchpad.TouchpadStyle();
        Texture t = new Texture(Gdx.files.internal("knob.png"));
        Sprite s = new Sprite(t);
        s.setSize(stage.getWidth() / 6f, stage.getWidth() / 6f);
        joystickStyle.knob = new SpriteDrawable(s);
        t = new Texture(Gdx.files.internal("joystick.png"));
        s = new Sprite(t);
        joystickStyle.background = new SpriteDrawable(s);

        this.joystick = new Touchpad(50, joystickStyle);
        this.joystick.setSize(stage.getHeight() * 2f / 5f, stage.getHeight() * 2f / 5f);
        this.joystick.setPosition(stage.getWidth() / 3f, stage.getHeight() * 4f / 5f, Align.center);

        changeControls.setRotation(-90);
        changeControls.setPosition(stage.getWidth() / 5f, stage.getHeight() * 2f / 5f, Align.center);

        powerUp.setRotation(-90);
        powerUp.setPosition(stage.getWidth() / 2f, stage.getHeight() * 2f / 5f, Align.center);
        this.stage.addActor(joystick);

    }

    @Override
    public void render(float delta) {
        super.direction = -joystick.getKnobPercentY() * 10f;
        super.render(delta);
    }
}
