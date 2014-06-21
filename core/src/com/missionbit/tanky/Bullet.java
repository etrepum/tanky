package com.missionbit.tanky;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by bob on 6/18/14.
 */
public final class Bullet {
    static final float BULLET_RADIUS = 1.0f;
    static final float DENSITY       = 10.0f;
    static final float FRICTION      = 0.8f;
    static final float RESTITUTION   = 0.2f;

    public static Body createBody(World world, Vector2 position, Vector2 linearVelocity) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = DENSITY;
        fixtureDef.friction = FRICTION;
        fixtureDef.restitution = RESTITUTION;
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(BULLET_RADIUS);
        fixtureDef.shape = circleShape;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.bullet = true;
        bodyDef.linearVelocity.set(linearVelocity);
        bodyDef.position.set(position);
        Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        circleShape.dispose();
        body.setUserData(new BodyTag(BodyTag.BodyType.BULLET, "some bullet"));
        return body;
    }

    public static void render(ShapeRenderer shapeRenderer, Color color, Vector2 position) {
        shapeRenderer.setColor(color);
        shapeRenderer.circle(position.x, position.y, BULLET_RADIUS);
    }
}
