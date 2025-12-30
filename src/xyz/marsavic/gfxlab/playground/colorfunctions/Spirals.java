package xyz.marsavic.gfxlab.playground.colorfunctions;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.ColorFunction3;
import xyz.marsavic.utils.Numeric;


public record Spirals(
		int nRays,
		double twisting,
		double blue
) implements ColorFunction3 {
		
	@Override
	public Color at(double t, Vector p) {
		return Color.rgb(
				Math.max(0, Numeric.sinT(-t + nRays * p.angle())),
				Math.max(0, Numeric.sinT(2 * t + twisting / p.length() + p.angle())),
				blue
		);
	}
	
}
