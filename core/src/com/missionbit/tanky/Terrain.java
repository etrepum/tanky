package com.missionbit.tanky;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
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
        body.setUserData(new BodyTag(BodyTag.BodyType.TERRAIN, "world terrain"));
        return body;
    }

    public static void deform(float[] terrain, int steps, Vector2 pos, float r) {
        for (int i = Math.round((pos.x - r) / steps); i < Math.round((pos.y + r) / steps); i++) {
            float x = r * (-1f + (i / r));
            if (Math.abs(x) <= r) {
                // r = x^2 + y^2
                // y^2 = r - x^2
                // y = +-sqrt(r - x^2)
                terrain[i] = MathUtils.clamp(
                        pos.y - (float)Math.sqrt((double)(r - x * x)),
                        0,
                        terrain[i]);
            }
        }
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
