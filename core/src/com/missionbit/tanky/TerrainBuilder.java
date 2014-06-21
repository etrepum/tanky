package com.missionbit.tanky;

import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayDeque;
import java.util.Random;

/**
 * http://www.somethinghitme.com/2013/11/11/simple-2d-terrain-with-midpoint-displacement/
 */
public final class TerrainBuilder {
    final int m_steps;
    final float m_height;
    final float m_roughness;
    final float m_displace;
    final Random m_rng;

    static final class TerrainStep {
        final int m_lo;
        final int m_hi;
        final float m_displace;
        TerrainStep(int lo, int hi, float displace) {
            m_lo = lo;
            m_hi = hi;
            m_displace = displace;
        }
        int mid() {
            return m_lo + (m_hi - m_lo) / 2;
        }
    }

    TerrainBuilder(Random rng, int steps, float height, float displace, float roughness) {
        m_rng = rng;
        m_steps = steps;
        m_height = height;
        m_displace = displace;
        m_roughness = roughness;
    }

    private float calcPoint(float lo, float hi, float displace) {
        return MathUtils.clamp(
                ((lo + hi) / 2) + (m_rng.nextFloat() * 2 * displace) - displace,
                0,
                m_height);
    }

    float[] build() {
        // rounded up to an odd number
        int size = m_steps | 1;
        float[] points = new float[size];
        float displace = m_displace * m_height;
        // Set the initial left and right points
        points[0] = calcPoint(0, m_height, displace * 0.5f);
        points[size - 1] = calcPoint(0, m_height, displace * 0.5f);
        ArrayDeque<TerrainStep> steps = new ArrayDeque<TerrainStep>(size / 2);
        if (size > 2) {
            steps.add(new TerrainStep(0, size - 1, displace));
        }
        while (!steps.isEmpty()) {
            TerrainStep s = steps.removeFirst();
            int mid = s.mid();
            int lo = s.m_lo;
            int hi = s.m_hi;
            displace = s.m_displace * m_roughness;
            points[mid] = calcPoint(points[lo], points[hi], displace);
            if (mid - lo > 1) {
                steps.add(new TerrainStep(lo, mid, displace));
            }
            if (hi - mid > 1) {
                steps.add(new TerrainStep(mid, hi, displace));
            }
        }
        return points;
    }
}
