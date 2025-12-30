package xyz.marsavic.gfxlab.playground.colorfunctions;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.ColorFunction3;


public class Psychedelic implements ColorFunction3 {
	
	@Override
	public Color at(double t, Vector p) {
		return rainbow(t + 1, p.rotate(1.1 * t));
	}

	private Color rainbow(double t, Vector p) {
		double a = 1, b = 1.4;
		double dist = Math.sqrt(p.x() * p.x() / (a * a) + (p.y() * p.y()) / (b * b));
		if (dist < 0.1) {
			return Color.BLACK;
		}
		if (dist > 0.8) {
			return Color.gray((t * 60) % 2 > 1 ? 0 : 1);
		}
		return from(dist * 12 + 61 * t);
	}

	private Color from(double dist) {
		int cur = (int)(dist % 8);
		return Color.hsb(cur / 7.0, 1, 1);
	}
}
