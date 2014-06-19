package com.missionbit.tanky;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by bob on 6/18/14.
 */
public final class Tank {
    // an M1 tank is about 10 meters long
    static final float TANK_BASE   = 10.0f;
    static final float TANK_TOP    = 7.5f;
    static final float TANK_HEIGHT = 6.25f;
    static final float DENSITY     = 10.0f;
    static final float FRICTION    = 0.8f;
    static final float RESTITUTION = 0.2f;

    public static Body createBody(World world, Vector2 position) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = DENSITY;
        fixtureDef.friction = FRICTION;
        fixtureDef.restitution = RESTITUTION;
        PolygonShape tankShape = new PolygonShape();
        tankShape.set(new Vector2[]{
                new Vector2(TANK_BASE / 2, 0),
                new Vector2(TANK_TOP / 2, TANK_HEIGHT),
                new Vector2(-TANK_TOP / 2, TANK_HEIGHT),
                new Vector2(-TANK_BASE / 2, 0)
        });
        fixtureDef.shape = tankShape;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);
        Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        tankShape.dispose();
        return body;
    }

    public static void render(ShapeRenderer shapeRenderer, Color color, Vector2 position, float angleRad) {
        shapeRenderer.setColor(color);
        Matrix4 matrix = new Matrix4(shapeRenderer.getTransformMatrix());
        shapeRenderer.translate(position.x, position.y, 0);
        shapeRenderer.rotate(0, 0, 1, MathUtils.radDeg * angleRad);
        shapeRenderer.triangle(
                -TANK_BASE/2, 0,
                -TANK_TOP/2, TANK_HEIGHT,
                TANK_BASE/2, 0);
        shapeRenderer.triangle(
                -TANK_TOP/2, TANK_HEIGHT,
                TANK_TOP/2, TANK_HEIGHT,
                TANK_BASE/2, 0);
        shapeRenderer.setTransformMatrix(matrix);
    }
}
