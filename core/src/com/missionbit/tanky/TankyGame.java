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
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Random;

public class TankyGame extends ApplicationAdapter {
    static final float WIDTH = 800;
    static final float HEIGHT = 480;
    static final float TANK_BASE = 40;
    static final float TANK_TOP = 30;
    static final float TANK_HEIGHT = 25;

    float absTime = 0;
    float[] terrain;
    World world;
    Body terrainBody = null;
    Body tankBody = null;
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
        Gdx.input.setInputProcessor(new InputAdapter() {
            public boolean touchDown (int x, int y, int pointer, int button) {
                // your touch down code here
                if (pointer > 0) {
                    reset();
                }
                return true; // return true to indicate the event was handled
            }

            public boolean touchUp (int x, int y, int pointer, int button) {
                // your touch up code here
                return true; // return true to indicate the event was handled
            }
        });
	}

	@Override
	public void render () {
        absTime += Gdx.graphics.getDeltaTime();
        camera.update();
        updateGame();
        Vector3 touchPoint = null;
        if (Gdx.input.isTouched()) {
            touchPoint = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        }
        drawGame(touchPoint);
        world.step(1/45f, 6, 2);
	}

    private void reset() {
        int steps = (int)WIDTH;
        terrain = new TerrainBuilder(new Random(), steps, HEIGHT, 0.6f, 0.5f).build();
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

        terrainBody = Terrain.createBody(world, WIDTH, HEIGHT, terrain, steps);
        tankBody = Tank.createBody(world, new Vector2(WIDTH/2, HEIGHT+10));

    }

    public void updateGame () {
    }

    public void drawGame (Vector3 touchPoint) {
        Gdx.gl.glClearColor(135.0f / 255, 206.0f / 255, 235.0f / 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Terrain.render(shapeRenderer, Color.YELLOW, WIDTH, terrain, (int)WIDTH);
        Vector2 tankPos = tankBody.getPosition();
        Tank.render(shapeRenderer, Color.BLACK, tankPos, tankBody.getAngle());
        if (touchPoint != null) {
            Vector3 delta = touchPoint.sub(WIDTH/2, HEIGHT/2, 0);
            float theta = MathUtils.atan2(-delta.y, -delta.x);
            float magnitude = delta.len();
            Arrow.render(shapeRenderer, Color.RED, tankPos, theta, magnitude);
        }
        shapeRenderer.end();
    }
}
