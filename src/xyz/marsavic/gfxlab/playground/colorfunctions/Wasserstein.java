package xyz.marsavic.gfxlab.playground.colorfunctions;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.ColorFunction3;

public class Wasserstein implements ColorFunction3 {

    @Override
    public Color at(double t, Vector p) {
        Vector first = Vector.xy(0.6 * Math.sin(t), 0.6 * Math.cos(t));
        Vector second = Vector.xy(0.5 * Math.sin(t * 3), 0.5 * Math.cos(t * 3));

        double d1 = p.distanceTo(first);
        double d2 = p.distanceTo(second);

        double earthMoverDistance = d1 - d2;
        double stripes = Math.abs(earthMoverDistance * 3);

        return Color.rgb(
                stripes,
                stripes * 2,
                stripes * 3
        ).if01or(Color.DEBUG);
    }
}