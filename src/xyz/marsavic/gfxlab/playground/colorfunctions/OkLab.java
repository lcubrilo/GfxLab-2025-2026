package xyz.marsavic.gfxlab.playground.colorfunctions;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.ColorFunction3;


public record OkLab(
		double hue
) implements ColorFunction3 {
	
	@Override
	public Color at(double t, Vector p) {
		return Color.okhcl(hue, p.x(), p.y()).if01or(Color.BLACK);
	}
	
}
