package xyz.marsavic.gfxlab;

import xyz.marsavic.functions.F1;
import xyz.marsavic.geometry.Vector;


@FunctionalInterface
public interface ColorFunction2 extends F1<Color, Vector> {
	
	default ColorFunction3 asColorFunction3() {
		return new ColorFunction3() {
			@Override public Color at(double t, Vector p) { return ColorFunction2.this.at(p); }
		};
	}
	
}
