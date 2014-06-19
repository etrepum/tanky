package com.missionbit.tanky;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by bob on 6/18/14.
 */
public final class Terrain {
    public static Body createBody(World world, float WIDTH, float HEIGHT, float[] terrain, int steps) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, 0);
        ChainShape chain = new ChainShape();
        float dx = WIDTH / steps;
        Vector2[] vertices = new Vector2[2 + steps];
        vertices[0] = new Vector2(0, HEIGHT * 10);
        for (int i = 0; i < steps; i++) {
            vertices[i+1] = new Vector2(i * dx, terrain[i]);
        }
        vertices[vertices.length - 1] = new Vector2(WIDTH, HEIGHT * 10);
        chain.createChain(vertices);
        Body body = world.createBody(bodyDef);
        body.createFixture(chain, 0.0f);
        chain.dispose();
        return body;
    }

    public static void render(ShapeRenderer shapeRenderer, Color color, float WIDTH, float[] terrain, int steps) {
        shapeRenderer.setColor(color);
        float dx = WIDTH / steps;
        for (int i = 0; i < steps - 1; i++) {
            float x0 = i * dx;
            float x1 = x0 + dx;
            float y0 = terrain[i];
            float y1 = terrain[i + 1];
            shapeRenderer.triangle(x0, y0, x1,  0, x0, 0);
            shapeRenderer.triangle(x0, y0, x1, y1, x1, 0);
        }

    }
}
