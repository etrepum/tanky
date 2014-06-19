package com.missionbit.tanky;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by bob on 6/18/14.
 */
public final class Arrow {
    static final float LINE_WIDTH   = 2.5f;
    static final float LINE_PERCENT = 0.9f;
    static final float HEAD_WIDTH   = 2.5f * LINE_WIDTH;

    public static void render(ShapeRenderer shapeRenderer, Color color, Vector2 position, float angleRad, float magnitude) {
        shapeRenderer.setColor(color);
        Matrix4 matrix = new Matrix4(shapeRenderer.getTransformMatrix());
        shapeRenderer.translate(position.x, position.y, 0);
        shapeRenderer.rotate(0, 0, 1, MathUtils.radDeg * angleRad);

        float lineLen = LINE_PERCENT * magnitude;
        shapeRenderer.rect(0, -LINE_WIDTH / 2, lineLen, LINE_WIDTH);
        shapeRenderer.triangle(
                lineLen, -HEAD_WIDTH / 2,
                magnitude, 0,
                lineLen, HEAD_WIDTH / 2);
        shapeRenderer.setTransformMatrix(matrix);
    }
}