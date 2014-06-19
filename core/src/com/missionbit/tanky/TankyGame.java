package com.missionbit.tanky;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Random;

public class TankyGame extends ApplicationAdapter {
    private static final String TAG = TankyGame.class.getSimpleName();
    static final float WIDTH = 800;
    static final float HEIGHT = 480;
    static final int STEPS = (int)WIDTH;

    float absTime = 0;
    float[] terrain;
    World world;
    Body terrainBody = null;
    Body tankBody = null;
    Vector2 touchPoint = null;
    ArrayList<Body> bullets;
    OrthographicCamera camera;
    ShapeRenderer shapeRenderer;

	@Override
	public void create () {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);
        world = new World(new Vector2(0, -10), true);
        shapeRenderer = new ShapeRenderer();
        bullets = new ArrayList<Body>();
        reset();
	}

    private Vector2 screenToWorld(int screenX, int screenY) {
        Vector3 v = camera.unproject(new Vector3(screenX, screenY, 0));
        return new Vector2(v.x, v.y);
    }

    private void fire(Body body, Vector2 point) {
        Vector2 delta = new Vector2(WIDTH/2, HEIGHT/2).sub(point);
        Vector2 pos = delta.cpy().nor().scl(20).add(body.getPosition());
        bullets.add(Bullet.createBullet(world, pos, delta));
    }

	@Override
	public void render () {
        absTime += Gdx.graphics.getDeltaTime();
        camera.update();
        updateGame();
        drawGame(touchPoint);
        world.step(1/45f, 6, 2);
	}

    private void reset() {
        terrain = new TerrainBuilder(new Random(), STEPS, HEIGHT, 0.6f, 0.5f).build();
        if (terrainBody != null) {
            world.destroyBody(terrainBody);
        }
        if (tankBody != null) {
            world.destroyBody(tankBody);
        }
        for (Body bullet : bullets) {
            world.destroyBody(bullet);
        }
        bullets.clear();

        terrainBody = Terrain.createBody(world, WIDTH, HEIGHT, terrain, STEPS);
        tankBody = Tank.createBody(world, new Vector2(WIDTH/2, HEIGHT+10));

    }

    public void updateGame () {
        if (Gdx.input.isTouched(1) && touchPoint != null) {
            touchPoint = null;
            reset();
        }
        if (Gdx.input.isTouched(0)) {
            touchPoint = screenToWorld(
                    Gdx.input.getX(0),
                    Gdx.input.getY(0));
        } else {
            if (touchPoint != null) {
                fire(tankBody, touchPoint);
            }
            touchPoint = null;
        }
    }

    public void drawGame (Vector2 touchPoint) {
        Gdx.gl.glClearColor(135.0f / 255, 206.0f / 255, 235.0f / 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Terrain.render(shapeRenderer, Color.YELLOW, WIDTH, terrain, STEPS);
        Vector2 tankPos = tankBody.getPosition();
        Tank.render(shapeRenderer, Color.BLACK, tankPos, tankBody.getAngle());
        if (touchPoint != null) {
            Vector2 delta = new Vector2(WIDTH/2, HEIGHT/2).sub(touchPoint);
            float theta = MathUtils.atan2(delta.y, delta.x);
            Arrow.render(shapeRenderer, Color.RED, tankPos, theta, delta.len());
        }
        for (Body bullet : bullets) {
            Bullet.render(shapeRenderer, Color.LIGHT_GRAY, bullet.getPosition());
        }
        shapeRenderer.end();
    }
}
