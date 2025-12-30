package xyz.marsavic.gfxlab.playground.colorfunctions;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.ColorFunction3;

import static java.lang.Math.*;


public class PatternB implements ColorFunction3 {
    // https://www.researchgate.net/publication/321973587_Procedural_generation_of_aesthetic_patterns_from_dynamics_and_iteration_processes
    
    private static final double B = 1.0 / sqrt(3);
    private static final double C = 2.0 / sqrt(3);
    private static final double D = sqrt(3) / 2;
    private static final double r = 0.2;
    private static final double s = 0.1;

    private double h1(double x, double y) {
        return 0.5 * x + D * y;
    }

    private double h2(double x, double y) {
        return D * x + 0.5 * y;
    }

    @Override
    public Color at(double t, Vector p) {
        double x = p.x() * 20;
        double y = p.y() * 20;

        double f = 2 * r * sin(x) * cos(C * y)
                + 2 * s * sin(h1(x, -y)) * cos(C * h2(x, y))
                + 2 * (s - r) * sin(h1(-x, -y)) * cos(C * h2(x, -y));

        double g = B * f
                - C * (2 * r * sin(h1(x, -y)) * cos(C * h2(x, y))
                + 2 * s * sin(h1(-x, -y)) * cos(C * h2(x, -y))
                - 2 * (s - r) * sin(x) * cos(C * y));

        double v = Math.sqrt(f * f + g * g * g);
        return Color.gray(v);
    }
}
