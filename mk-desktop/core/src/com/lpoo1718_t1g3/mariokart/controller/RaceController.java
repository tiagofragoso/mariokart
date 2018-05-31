package com.lpoo1718_t1g3.mariokart.controller;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.lpoo1718_t1g3.mariokart.controller.entities.*;
import com.lpoo1718_t1g3.mariokart.model.GameModel;
import com.lpoo1718_t1g3.mariokart.model.Player;
import com.lpoo1718_t1g3.mariokart.model.TrackPart;
import com.lpoo1718_t1g3.mariokart.model.entities.*;
import com.lpoo1718_t1g3.mariokart.networking.Message;
import com.lpoo1718_t1g3.mariokart.view.RaceView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.lpoo1718_t1g3.mariokart.view.RaceView.PIXEL_TO_METER;

public class RaceController implements ContactListener {

    private final World world;
    private final RaceView raceView;
    private final TrackBody trackBody;
    private HashMap<Integer, KartBody> kartBodies = new HashMap<Integer, KartBody>();
    private ArrayList<MysteryBoxBody> mysteryBoxes = new ArrayList<MysteryBoxBody>();
    private ArrayList<EntityBody> objectBodies = new ArrayList<EntityBody>();
    private float accumulator;
    private boolean gas = false;
    private boolean left = false;
    private boolean right = false;

    RaceController() {
        world = new World(new Vector2(0, 0), true);
        world.clearForces();
        trackBody = new TrackBody(world, GameModel.getInstance().getTrack1());
        raceView = new RaceView();

        GameModel.getInstance().addPlayer(1, "mbaguiar", "Mario");
        GameModel.getInstance().addPlayer(2, "tfragoso", "Luigi");

        for (MysteryBoxModel box : GameModel.getInstance().getTrack1().getBoxes()) {
            mysteryBoxes.add(new MysteryBoxBody(world, box));
        }

        float x = GameModel.getInstance().getTrack1().xStartPosition;
        float y = GameModel.getInstance().getTrack1().yStartPosition;

        for (Player player : GameModel.getInstance().getPlayers()) {
            player.getKartModel().setPosition(x, y);
            x += GameModel.getInstance().getTrack1().incStartPosition;
            kartBodies.put(player.getPlayerId(), new KartBody(world, player.getKartModel(), - (float) Math.PI / 2));
        }


        world.setContactListener(this);

    }

    public RaceView getRaceView() {
        return raceView;
    }

    public World getWorld() {
        return world;
    }

    public void update(float delta) {

        float frameTime = Math.min(delta, 0.25f);
        accumulator += frameTime;
        while (accumulator >= 1 / 60f) {
            world.step(1 / 60f, 6, 2);
            accumulator -= 1 / 60f;
        }

        for (KartBody kartBody : kartBodies.values()) {
            kartBody.update(delta);
        }

        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);


        for (Body body : bodies) {

            verifyBounds(body);

            if (!body.getUserData().equals(0)) {

                ((EntityModel) body.getUserData()).setPosition(body.getPosition().x * PIXEL_TO_METER, body.getPosition().y * PIXEL_TO_METER);
                ((EntityModel) body.getUserData()).setRotation(body.getAngle());

                if (body.getUserData() instanceof KartModel) {

                }
            }
        }
    }

    void handleMovement(Message m) {
        if ((Boolean) m.getOptions().get("throttle")) setKartState(KartBody.acc_type.ACC_ACCELERATE, m.getSenderId());
        if ((Boolean) m.getOptions().get("brake")) setKartState(KartBody.acc_type.ACC_BRAKE, m.getSenderId());
        float direction = (Float) m.getOptions().get("direction");
        if (direction >= 5) setKartState(KartBody.steer_type.STEER_RIGHT, m.getSenderId());
        else if (direction <= -5) setKartState(KartBody.steer_type.STEER_LEFT, m.getSenderId());
    }


    private void verifyBounds(Body body) {
        if (body.getPosition().x < 0)
            body.setTransform(0, body.getPosition().y, body.getAngle());

        if (body.getPosition().y < 0)
            body.setTransform(body.getPosition().x, 0, body.getAngle());

        if (body.getPosition().x > 1080)
            body.setTransform(1080, body.getPosition().y, body.getAngle());

        if (body.getPosition().y > 1080)
            body.setTransform(body.getPosition().x, 1080, body.getAngle());
    }

    public void setKartState(KartBody.steer_type value, int playerId) {
        KartBody kartBody = kartBodies.get(playerId);
        if (kartBody != null && kartBody.isUpdate()) kartBody.setSteer(value);
    }

    public void setKartState(KartBody.acc_type value, int playerId) {
        KartBody kartBody = kartBodies.get(playerId);
        if (kartBody != null && kartBody.isUpdate()) kartBody.setAccelerate(value);
    }


    public void addKartBody(Player player) {
        kartBodies.put(player.getPlayerId(), new KartBody(world, player.getKartModel(), (float) Math.PI));
    }

    public void useObject(int playerId) {
        GameModel.object_type obj = ((KartModel) kartBodies.get(playerId).getUserdata()).getObject();

        if (obj != null) {
            switch (obj) {
                case BANANA:
                    useBanana(playerId);
                    break;
                case MUSHROOM:
                    useMushroom(playerId);
                    break;
            }
        }

    }

    public void useBanana(int playerId) {
        KartModel kartModel = ((KartModel) kartBodies.get(playerId).getUserdata());
        kartModel.setCollision(false);
        BananaModel banana = new BananaModel(kartModel.getX() + 0.1f, kartModel.getY(), 0);
        objectBodies.add(new BananaBody(world, banana));
        GameModel.getInstance().getTrack1().addObject(banana);
    }

    public void useMushroom(int playerId) {
        KartBody body = kartBodies.get(playerId);
        body.speedUp();
        System.out.println("used mushroom");


    }

    private KartBody getKartBody(Body body) {
        for (KartBody kartBody : kartBodies.values()) {
            if (kartBody.getBody() == body) {
                return kartBody;
            }
        }

        return null;
    }

    @Override
    public void beginContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        /*
        if (bodyA.getUserData() instanceof TrackPart && bodyB.getUserData() instanceof KartModel) {
                ((KartModel) bodyB.getUserData()).setColliding(true);
                ((KartModel) bodyB.getUserData()).setSpeed(KartModel.speed_type.LOW);
                System.out.println("start collision");
        }

        if (bodyA.getUserData() instanceof KartModel && bodyB.getUserData() instanceof TrackPart) {
            ((KartModel) bodyA.getUserData()).setColliding(true);     
            ((KartModel) bodyA.getUserData()).setSpeed(KartModel.speed_type.LOW);
            System.out.println("start collision");
        }

        */

        if (bodyA.getUserData() instanceof KartModel && bodyB.getUserData() instanceof MysteryBoxModel) {
            mysteryBoxCollision(bodyB, bodyA);
        }

        if (bodyA.getUserData() instanceof MysteryBoxModel && bodyB.getUserData() instanceof KartModel) {
           mysteryBoxCollision(bodyA, bodyB);

        }

        if (bodyA.getUserData() instanceof BananaModel && bodyB.getUserData() instanceof KartModel) {
            if (((KartModel) bodyB.getUserData()).isCollision()) {
                bananaCollision(bodyB);
            }

        }

        if (bodyA.getUserData() instanceof  KartModel && bodyB.getUserData() instanceof BananaModel) {
            if (((KartModel) bodyA.getUserData()).isCollision()) {
                bananaCollision(bodyA);
            }
        }

        if (bodyA.getUserData() instanceof KartModel && bodyB.getUserData() instanceof FinishLineModel) {
            if (bodyA.getLinearVelocity().x < 0) {
                ((KartModel) bodyA.getUserData()).incLaps();

            }
        }

        if (bodyB.getUserData() instanceof KartModel && bodyA.getUserData() instanceof FinishLineModel) {
            if (bodyB.getLinearVelocity().x < 0) {
                ((KartModel) bodyB.getUserData()).incLaps();

            }
        }


    }

    @Override
    public void endContact(Contact contact) {


        final Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        if (bodyA.getUserData() instanceof BananaModel && bodyB.getUserData() instanceof KartModel) {
            if (!((KartModel) bodyB.getUserData()).isCollision()) {
                ((KartModel) bodyB.getUserData()).setCollision(false);
            }
        }

        if (bodyA.getUserData() instanceof  KartModel && bodyB.getUserData() instanceof BananaModel) {
            if (!((KartModel) bodyA.getUserData()).isCollision()) {
                ((KartModel) bodyA.getUserData()).setCollision(true);
            }

        }

        /*

        if (bodyA.getUserData() instanceof TrackPart && bodyB.getUserData() instanceof KartModel) {

             KartModel kart = (KartModel) bodyB.getUserData();
             esparguete(kart);
             kart.setColliding(false);
            //((KartModel) bodyB.getUserData()).setSpeed(KartModel.speed_type.NORMAL);
            System.out.println("end collision");
        }

        if (bodyA.getUserData() instanceof KartModel && bodyB.getUserData() instanceof TrackPart) {

            KartModel kart = (KartModel) bodyA.getUserData();

            esparguete(kart);
            kart.setColliding(false);
            //((KartModel) bodyA.getUserData()).setSpeed(KartModel.speed_type.NORMAL);
            System.out.println("end collision");
        }

        */

    }

    private void esparguete(final KartModel kart)  {
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                if (!kart.isColliding()){
                    kart.setSpeed(KartModel.speed_type.NORMAL);
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(t, 5);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        System.out.println("coiso");
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        System.out.println("coiso");
    }

    private void mysteryBoxCollision(Body mysteryBox, Body kartBody) {
        MysteryBoxModel box = (MysteryBoxModel) mysteryBox.getUserData();
        KartModel kart = (KartModel) kartBody.getUserData();
        if (box.isEnable()) {
            box.disable();
            kart.generateObject();
        }
    }

    private void bananaCollision(Body kartBody) {
        for (KartBody body : kartBodies.values()) {
            if (body.getBody() == kartBody) {
                body.steerHard();
                System.out.println("banana collision");
            }
        }
    }
}
