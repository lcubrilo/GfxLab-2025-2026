package xyz.marsavic.gfxlab.playground.colorfunctions;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.ColorFunction3;

public class Mastercard implements ColorFunction3 {
	
	@Override
	public Color at(double t, Vector p) {
		Vector p1 = Vector.polar(0.7, Vector.polar(1.3).mul(3*t));
		Vector p2 = Vector.polar(0.7, Vector.polar(0.4).mul(4*t + 0.5));
		
		double r      = 0.15;
		double d1     = p.distanceTo(p1);
		double d2     = p.distanceTo(p2);
		double dist   = p1.distanceTo(p2);
//		boolean touch = dist < (r * 2);
		
//		double s = touch ? 1.0 : 0.5;
		Color c1 = Color.hsb(0.0 + t, 0.8, 0.5);
		Color c2 = Color.hsb(0.5 + t, 0.8, 0.5);
		Color c = Color.BLACK;
		if (d1 < r) c = c.add(c1);
		if (d2 < r) c = c.add(c2);
//		if (c.zero()) return touch ? Color.gray(0.1) : Color.BLACK;
		return c;
	}
}