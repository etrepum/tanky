package com.missionbit.tanky;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

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
    OrthographicCamera camera;
    ShapeRenderer shapeRenderer;

	@Override
	public void create () {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);
        world = new World(new Vector2(0, -10), true);
        shapeRenderer = new ShapeRenderer();
        newTerrain();
        Gdx.input.setInputProcessor(new InputAdapter() {
            public boolean touchDown (int x, int y, int pointer, int button) {
                // your touch down code here
                newTerrain();
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

    private void newTerrain() {
        terrain = new TerrainBuilder(new Random(), WIDTH, HEIGHT, 0.6f, 0.5f).build();
        if (terrainBody != null) {
            world.destroyBody(terrainBody);
        }
        if (tankBody != null) {
            world.destroyBody(tankBody);
        }
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, 0);
        ChainShape chain = new ChainShape();
        Vector2[] vertices = new Vector2[2 + terrain.length];
        vertices[0] = new Vector2(0, HEIGHT * 10);
        for (int i = 0; i < terrain.length; i++) {
            vertices[i+1] = new Vector2((float)i, terrain[i]);
        }
        vertices[vertices.length - 1] = new Vector2(vertices.length + 1, HEIGHT * 10);
        chain.createChain(vertices);
        terrainBody = world.createBody(bodyDef);
        terrainBody.createFixture(chain, 0.0f);
        chain.dispose();

        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(WIDTH/2, HEIGHT + 10);

        Vector2[] tankVertices = new Vector2[4];
        tankVertices[0] = new Vector2(TANK_BASE/2, 0);
        tankVertices[1] = new Vector2(TANK_TOP/2, TANK_HEIGHT);
        tankVertices[2] = new Vector2(-TANK_TOP/2, TANK_HEIGHT);
        tankVertices[3] = new Vector2(-TANK_BASE/2, 0);
        PolygonShape tankShape = new PolygonShape();
        tankShape.set(tankVertices);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = tankShape;
        fixtureDef.density = 10f;
        fixtureDef.friction = 0.8f;
        fixtureDef.restitution = 0.2f;

        tankBody = world.createBody(bodyDef);
        tankBody.createFixture(fixtureDef);
        tankShape.dispose();
    }

    public void updateGame () {
    }

    public void drawArrow(float x1, float y1, float theta, float magnitude) {
        float w = 10;
        float arrPercent = 0.1f;
        float hw = 2.5f * w;
        shapeRenderer.setColor(1, 0, 0, 1);
        Matrix4 matrix = new Matrix4(shapeRenderer.getTransformMatrix());
        shapeRenderer.translate(x1, y1, 0);
        shapeRenderer.rotate(0, 0, 1, theta);

        float lineLen = (1 - arrPercent) * magnitude;
        shapeRenderer.rect(0, -w / 2, lineLen, w);
        shapeRenderer.triangle(
                lineLen, -hw/2,
                magnitude, 0,
                lineLen, hw/2);
        shapeRenderer.setTransformMatrix(matrix);
    }

    public void drawTank(float x1, float y1, float theta) {
        float w0 = TANK_BASE;
        float w1 = TANK_TOP;
        float h = TANK_HEIGHT;
        shapeRenderer.setColor(0, 0, 0, 1);
        Matrix4 matrix = new Matrix4(shapeRenderer.getTransformMatrix());
        shapeRenderer.translate(x1, y1, 0);
        shapeRenderer.rotate(0, 0, 1, theta);
        shapeRenderer.triangle(-w0/2, 0, -w1/2, h, w0/2, 0);
        shapeRenderer.triangle(-w1/2, h,  w1/2, h, w0/2, 0);
        shapeRenderer.setTransformMatrix(matrix);
    }

    public void drawGame (Vector3 touchPoint) {
        Gdx.gl.glClearColor(135.0f / 255, 206.0f / 255, 235.0f / 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 0, 1);
        for (int i = 0; i < (int)WIDTH - 1; i++) {
            shapeRenderer.triangle(i, terrain[i], i + 1, 0, i, 0);
            shapeRenderer.triangle(i, terrain[i], i + 1, terrain[i + 1], i + 1, 0);
        }
        float tx = tankBody.getPosition().x;
        float ty = tankBody.getPosition().y;
        drawTank(tx, ty, MathUtils.radDeg * tankBody.getAngle());
        if (touchPoint != null) {
            Vector3 delta = touchPoint.sub(WIDTH/2, HEIGHT/2, 0);
            float theta = MathUtils.radDeg * MathUtils.atan2(-delta.y, -delta.x);
            float magnitude = delta.len();
            drawArrow(tx, ty, theta, magnitude);
        }
        shapeRenderer.end();
    }
}
