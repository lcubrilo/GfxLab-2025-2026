package xyz.marsavic.gfxlab;

import xyz.marsavic.functions.F1;
import xyz.marsavic.geometry.Vector;


/** Implementations should override one of the "at" functions. */
public interface ColorFunction3 extends F1<Color, Vec3> {
	
	default Color at(double t, Vector p) {
		return at(Vec3.xp(t, p));
	}
	
	@Override
	default Color at(Vec3 p) {
		return at(p.x(), p.p12());
	}
	
	default ColorFunction2 sliceAt(double t) {
		return p -> at(t, p);
	}
	
}
