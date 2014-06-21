package com.missionbit.tanky;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by bob on 6/21/14.
 */
public final class Explosion {
    public static final float DURATION = 0.5f;
    public static final float RADIUS = 10f;
    public final float time;
    public final Vector2 position;
    Explosion(float time, Vector2 position) {
        this.time = time;
        this.position = position;
    }

    public boolean shouldRemove(float now) {
        return (now - time > DURATION);
    }

    public float radius(float now) {
        return RADIUS * Interpolation.exp5.apply((now - time) / DURATION);
    }

    public void render(ShapeRenderer shapeRenderer, Color color, float now) {
        if (!shouldRemove(now)) {
            shapeRenderer.setColor(color);
            shapeRenderer.circle(position.x, position.y, this.radius(now));
        }
    }
}
