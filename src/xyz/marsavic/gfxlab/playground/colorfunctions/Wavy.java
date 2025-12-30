package xyz.marsavic.gfxlab.playground.colorfunctions;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.ColorFunction3;
import xyz.marsavic.utils.Numeric;


public class Wavy implements ColorFunction3 {
	
	@Override
	public Color at(double t, Vector p) {
		Vector r = Vector.polar(1, t);
		double a = Numeric.cosT(Numeric.sinT(p.y())*r.x() + p.x()) / 2 + 0.5;
		double b = Numeric.cosT(Numeric.sinT(p.x())*r.y() + p.y()) / 2 + 0.5;
		return Color.rgb(a*Math.sqrt(b), b*Math.sqrt(a), 0);
	}
	
}
