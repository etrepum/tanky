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
        float[] vertices = new float[2 * (6 + steps)];
        float dx = WIDTH / steps;
        float REALLY_HIGH = HEIGHT * 10;
        int j = 0;
        for (int i = steps - 1; i >= 0; i--) {
            vertices[j++] = dx * i;
            vertices[j++] = terrain[i];
        }
        // top left 0
        vertices[j++] = 0;
        vertices[j++] = REALLY_HIGH;
        // top left -1
        vertices[j++] = -dx;
        vertices[j++] = REALLY_HIGH;
        // bottom left -1
        vertices[j++] = -dx;
        vertices[j++] = 0;
        // bottom right +1
        vertices[j++] = dx * steps;
        vertices[j++] = 0;
        // top right +1
        vertices[j++] = dx * steps;
        vertices[j++] = REALLY_HIGH;
        // top right 0
        vertices[j++] = dx * (steps - 1);
        vertices[j++] = REALLY_HIGH;
        chain.createLoop(vertices);
        Body body = world.createBody(bodyDef);
        body.createFixture(chain, 0.0f);
        chain.dispose();
        body.setUserData(new BodyTag(BodyTag.BodyType.TERRAIN, "world terrain"));
        return body;
    }

    public static int terrainIndex(float WIDTH, int steps, float x) {
        return Math.round(x * steps / WIDTH);
    }

    public static boolean containsPoint(float WIDTH, float[] terrain, int steps, Vector2 pos) {
        int i = Math.min(steps - 1, Math.max(0, terrainIndex(WIDTH, steps, pos.x)));
        return terrain[i] >= pos.y;
    }

    public static void deform(float WIDTH, float[] terrain, int steps, Vector2 pos, float r) {
        int i0 = Math.max(0, terrainIndex(WIDTH, steps, pos.x - r));
        int i1 = Math.min(steps - 1, terrainIndex(WIDTH, steps, pos.x + r));
        float r2 = r * r;
        float dx = WIDTH / steps;
        for (int i = i0; i <= i1; i += 1) {
            float x = i * dx - pos.x;
            double y2 = (double)(r2 - x * x);
            if (y2 > 0) {
                // r^2 = x^2 + y^2
                // y^2 = r - x^2
                // y = +-sqrt(r^2 - x^2)
                float y = (float)Math.sqrt(y2);
                terrain[i] = MathUtils.clamp(
                        pos.y - y,
                        Math.max(0, terrain[i] - 2 * y),
                        terrain[i]);
            }
        }
    }

    public static void render(ShapeRenderer shapeRenderer, Color color, float WIDTH, float[] terrain, int steps) {
        // TODO: Use one of the triangulation algorithms in libgdx?
        // http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/math/DelaunayTriangulator.html
        // http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/math/EarClippingTriangulator.html
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
