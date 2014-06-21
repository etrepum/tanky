package com.missionbit.tanky;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by bob on 6/21/14.
 */
public final class Explosion {
    public static final float DURATION = 0.5f;
    public static final float RADIUS = 10f;
    public final Body body;
    public final float time;

    Explosion(Body body, float time) {
        this.time = time;
        this.body = body;
    }

    public static Body createBody(World world, Vector2 position) {
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(position);
        Body body = world.createBody(bodyDef);
        body.createFixture(circleShape, 0.0f);
        circleShape.dispose();
        body.setUserData(new BodyTag(BodyTag.BodyType.EXPLOSION, "some explosion"));
        return body;
    }

    public boolean update(float now) {
        boolean shouldRemove = shouldRemove(now);
        if (!shouldRemove) {
            for (Fixture fixture : body.getFixtureList()) {
                fixture.getShape().setRadius(radius(now));
            }
        }
        return shouldRemove;
    }

    public boolean shouldRemove(float now) {
        return (now - time > DURATION);
    }

    public float radius(float now) {
        return RADIUS * Interpolation.exp5.apply((now - time) / DURATION);
    }

    public void render(ShapeRenderer shapeRenderer, Color color, float now) {
        if (!shouldRemove(now)) {
            Vector2 position = body.getWorldCenter();
            shapeRenderer.setColor(color);
            shapeRenderer.circle(position.x, position.y, this.radius(now));
        }
    }
}
