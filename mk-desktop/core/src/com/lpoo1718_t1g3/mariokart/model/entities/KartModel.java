package com.lpoo1718_t1g3.mariokart.model.entities;

import com.badlogic.gdx.Game;
import com.lpoo1718_t1g3.mariokart.controller.GameController;
import com.lpoo1718_t1g3.mariokart.model.GameModel;
import com.lpoo1718_t1g3.mariokart.networking.Message;

import java.util.Random;

import static com.lpoo1718_t1g3.mariokart.model.GameModel.object_type.NULL;
import static com.lpoo1718_t1g3.mariokart.model.entities.KartModel.speed_type.NORMAL;

public class KartModel extends EntityModel {

    public final static float POWER = 100000;
    public final static float MINSTEERANGLE = 30;
    public final static float MAXSTEERANGLE = 45;
    public final static float MAXSPEED = 500;

    public static final float WIDTH = 24;
    public static final float HEIGHT = 28;

    public final static float POWER_HIGH = 100000;
    public final static float MAXSPEED_HIGH = 500;

    public final static float POWER_LOW = 1000;
    public final static float MAXSPEED_LOW = 100;

    public enum speed_type {NORMAL, LOW, HIGH}

    public speed_type speed = NORMAL;

    private GameModel.object_type object;
    private boolean collision;
    boolean isColliding = false;

    private int playerId;

    public KartModel(float x, float y, float rotation, int playerId) {
        super(x, y, rotation);
        object = NULL;
        collision = true;
        this.playerId = playerId;
    }

    public boolean isColliding() {
        return isColliding;
    }

    public void setColliding(boolean colliding) {
        isColliding = colliding;
    }

    public void generateObject() {

        if (object == NULL) {
            Random rand = new Random();
            int n = rand.nextInt(2) + 1;
            object = GameModel.object_type.values()[n];
            Message m = new Message(Message.MESSAGE_TYPE.POWER_UP, Message.SENDER.SERVER);
            m.addOption("powerUp", object);
            GameController.getInstance().writeToClient(m, playerId);
        }
    }

    public GameModel.object_type getObject() {
        if (object == NULL) {
            return NULL;
        }
        System.out.println("Released object " + object);
        int obj = this.object.ordinal();
        this.object = NULL;
        System.out.println(object);
        return GameModel.object_type.values()[obj];
    }

    public boolean isCollision() {
        return collision;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public speed_type getSpeed() {
        return speed;
    }

    public void setSpeed(speed_type speed) {
        this.speed = speed;
    }

    public int getPlayerId() {
        return playerId;
    }
}

