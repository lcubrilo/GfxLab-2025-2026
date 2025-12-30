package xyz.marsavic.gfxlab.playground.colorfunctions;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.ColorFunction3;
import xyz.marsavic.utils.Numeric;


public class Potato implements ColorFunction3 {
	
	@Override
	public Color at(double t, Vector p) {
		double t2 = (Math.abs(t) * 5 % 100 + 1) / 100;
		double r = Math.abs(Numeric.sinT(-t + 2 * (new Vector(p.x(), t2)).angle())) +
				   Math.abs(Numeric.cosT(-t + 2 * (new Vector(p.x(), t2).angle())));
		double g = Math.abs(Numeric.sinT(t + 1.5 * (new Vector(t2, p.y())).angle())) +
				   Math.abs(Numeric.cosT(t + 1.5 * (new Vector(t2, p.y())).angle()));

		double y = (Math.pow(p.x(), 2) - Math.pow(p.y(), 2));
		double b = Math.abs(Numeric.sinT(2 * t + 8 * (new Vector(t2, y)).angle())) +
				   Math.abs(Numeric.cosT(2 * t + 8 * (new Vector(t2, y)).angle()));
		return Color.rgb(
				r,
				g,
				b
		);
	}
}
