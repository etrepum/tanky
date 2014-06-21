package com.missionbit.tanky;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public final class TankyGame extends ApplicationAdapter implements ContactListener {
    private static final String TAG = TankyGame.class.getSimpleName();
    static final float WIDTH              = 200f;
    static final float HEIGHT             = 120f;
    static final int   STEPS              = 800;
    static final float GRAVITY            = -10f;
    static final float BULLET_POWER_SCALE = 1f;
    static final float BULLET_TANK_RADIUS = 10f;
    static final float BULLET_MINIMUM_POWER = 5f;

    float absTime = 0;
    float[] terrain;
    boolean shouldReset = false;
    World world;
    Body terrainBody = null;
    Body tankBody = null;
    Vector2 touchVector = null;
    Vector2 firstTouchPoint = null;
    ArrayDeque<Vector2> fireEvents;
    ArrayDeque<Explosion> explosions;
    HashSet<Body> bullets;
    HashSet<Body> explodingBullets;
    OrthographicCamera camera;
    ShapeRenderer shapeRenderer;

	@Override
	public void create () {
        fireEvents = new ArrayDeque<Vector2>();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);
        world = new World(new Vector2(0, GRAVITY), true);
        shapeRenderer = new ShapeRenderer();
        bullets = new HashSet<Body>();
        explodingBullets = new HashSet<Body>();
        explosions = new ArrayDeque<Explosion>();
        Gdx.input.setInputProcessor(new InputAdapter() {
            public boolean touchDown(int x, int y, int pointer, int button) {
                if (pointer == 0) {
                    touchVector = null;
                    firstTouchPoint = screenToWorld(x, y);
                } else {
                    shouldReset = true;
                }
                return true;
            }
            public boolean touchDragged(int x, int y, int pointer) {
                if (pointer == 0) {
                    touchVector = (firstTouchPoint == null) ? null : screenToWorld(x, y).sub(firstTouchPoint);
                }
                return true;
            }
            public boolean touchUp(int x, int y, int pointer, int button) {
                if (pointer == 0 && touchVector != null) {
                    fireEvents.add(touchVector);
                    touchVector = null;
                    firstTouchPoint = null;
                }
                return true;
            }
        });
        world.setContactListener(this);
        reset();
	}

    public void beginContact(Contact contact) {
        Body a = contact.getFixtureA().getBody();
        Body b = contact.getFixtureB().getBody();
        BodyTag aTag = (BodyTag)a.getUserData();
        BodyTag bTag = (BodyTag)b.getUserData();
        // We only care if at least one of the fixtures is a bullet
        if ((aTag.type == BodyTag.BodyType.BULLET || bTag.type == BodyTag.BodyType.BULLET)
                && contact.isTouching()) {
            if (aTag.type == BodyTag.BodyType.BULLET) {
                explodingBullets.add(a);
            }
            if (bTag.type == BodyTag.BodyType.BULLET) {
                explodingBullets.add(b);
            }
        }
    }

    public void endContact(Contact contact) {
    }

    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    private Vector2 screenToWorld(int screenX, int screenY) {
        Vector3 v = camera.unproject(new Vector3(screenX, screenY, 0));
        return new Vector2(v.x, v.y);
    }

    private void fire(Body tankBody, Vector2 delta) {
        if (delta.len() > BULLET_MINIMUM_POWER) {
            Vector2 pos = delta.cpy().nor().scl(BULLET_TANK_RADIUS).add(tankBody.getWorldCenter());
            Vector2 linearVelocity = delta.cpy().scl(BULLET_POWER_SCALE).add(tankBody.getLinearVelocity());
            bullets.add(Bullet.createBody(world, pos, linearVelocity));
        }
    }

	@Override
	public void render () {
        absTime += Gdx.graphics.getDeltaTime();
        camera.update();
        updateGame();
        drawGame(touchVector);
        world.step(1/45f, 6, 2);
	}

    private void reset() {
        touchVector = null;
        firstTouchPoint = null;
        shouldReset = false;
        terrain = new TerrainBuilder(new Random(), STEPS, HEIGHT, 0.6f, 0.5f).build();
        terrainBody = null;
        tankBody = null;
        bullets.clear();
        fireEvents.clear();
        explosions.clear();
        Array<Body> bodies = new Array<Body>(world.getBodyCount());
        world.getBodies(bodies);
        for (Body body : bodies) {
            world.destroyBody(body);
        }
        float tankSpawnX = WIDTH * MathUtils.random(0.05f, 0.40f);
        terrainBody = Terrain.createBody(world, WIDTH, HEIGHT, terrain, STEPS);
        tankBody = Tank.createBody(world, new Vector2(tankSpawnX, HEIGHT));
    }

    public void updateGame () {
        if (shouldReset) {
            reset();
        }
        boolean terrainDirty = false;
        Iterator<Explosion> explosionIterator = explosions.iterator();
        while (explosionIterator.hasNext()) {
            Explosion explosion = explosionIterator.next();
            if (explosion.update(absTime)) {
                explosionIterator.remove();
                Terrain.deform(WIDTH, terrain, STEPS, explosion.body.getWorldCenter(), 10f);
                world.destroyBody(explosion.body);
                terrainDirty = true;
            }
        }
        if (terrainDirty) {
            world.destroyBody(terrainBody);
            terrainBody = Terrain.createBody(world, WIDTH, HEIGHT, terrain, STEPS);
        }
        if (!explodingBullets.isEmpty()) {
            for (Body bullet : explodingBullets) {
                bullets.remove(bullet);
                Body explosionBody = Explosion.createBody(world, bullet.getWorldCenter());
                explosions.add(new Explosion(explosionBody, absTime));
                world.destroyBody(bullet);
            }
            explodingBullets.clear();
        }
        while (!fireEvents.isEmpty()) {
            fire(tankBody, fireEvents.remove());
        }
    }

    public void drawGame (Vector2 shotVector) {
        Gdx.gl.glClearColor(135.0f / 255, 206.0f / 255, 235.0f / 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Terrain.render(shapeRenderer, Color.YELLOW, WIDTH, terrain, STEPS);
        Tank.render(shapeRenderer, Color.RED, tankBody.getPosition(), tankBody.getAngle());
        if (shotVector != null && shotVector.len() > BULLET_MINIMUM_POWER) {
            float theta = MathUtils.atan2(shotVector.y, shotVector.x);
            Arrow.render(shapeRenderer, Color.RED, tankBody.getWorldCenter(), theta, shotVector.len());
        }
        for (Body bullet : bullets) {
            Bullet.render(shapeRenderer, Color.BLACK, bullet.getPosition());
        }
        for (Explosion explosion : explosions) {
            explosion.render(shapeRenderer, Color.DARK_GRAY, absTime);
        }
        shapeRenderer.end();
    }
}
