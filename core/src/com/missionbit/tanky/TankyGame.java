package com.missionbit.tanky;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Random;

public class TankyGame extends ApplicationAdapter {
    static final float WIDTH = 800;
    static final float HEIGHT = 480;
    float[] terrain;
    OrthographicCamera camera;
    ShapeRenderer shapeRenderer;

	@Override
	public void create () {
        camera = new OrthographicCamera();
        camera.setToOrtho(true, WIDTH, HEIGHT);
        shapeRenderer = new ShapeRenderer();
        newTerrain();
        Gdx.input.setInputProcessor(new InputAdapter() {
            public boolean touchDown (int x, int y, int pointer, int button) {
                // your touch down code here
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
        updateGame();
        drawGame();
	}

    private void newTerrain() {
        terrain = new TerrainBuilder(new Random(), WIDTH, HEIGHT, (float)0.6, (float)0.5).build();
    }

    public void updateGame () {
        if (Gdx.input.isTouched()) {
            newTerrain();
        }
    }

    public void drawGame () {
        Gdx.gl.glClearColor((float)135.0/255, (float)206.0/255, (float)235.0/255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 0, 1);
        int h = (int)HEIGHT;
        for (int i = 0; i < (int)WIDTH - 1; i++) {
            shapeRenderer.triangle(i, terrain[i], i + 1, h, i, h);
            shapeRenderer.triangle(i, terrain[i], i + 1, terrain[i + 1], i + 1, h);
        }
        shapeRenderer.end();
    }
}
