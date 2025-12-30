package xyz.marsavic.gfxlab;

import xyz.marsavic.functions.F1;
import xyz.marsavic.functions.F2;
import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.gui.UtilsGL;


public interface Matrix<E> extends Array2<E> {
	
	
	void set(int x, int y, E value);
	
	
	default void set(Vector p, E value) {
		set(p.xInt(), p.yInt(), value);
	}
	
	
	default void fill(E value) {
		int sizeX = size().xInt();
		UtilsGL.parallel.parallelY(size(), y -> {
			for (int x = 0; x < sizeX; x++) {
				set(x, y, value);
			}
		});
		
//		UtilsGL.parallel(size(), p -> set(p,  value)); // prettier but slower
	}
	
	
	default void fill(F1<E, Vector> f) {
		int sizeX = size().xInt();
		UtilsGL.parallel.parallelY(size(), y -> {
			for (int x = 0; x < sizeX; x++) {
				set(x, y, f.at(Vector.xy(x, y)));
			}
		});

//		UtilsGL.parallel(size(), p -> set(p, f.at(p))); // prettier but slower
	}
	

	default void fill(F2<E, Integer, Integer> f) {
		int sizeX = size().xInt();
		UtilsGL.parallel.parallelY(size(), y -> {
			for (int x = 0; x < sizeX; x++) {
				set(x, y, f.at(x, y));
			}
		});

//		UtilsGL.parallel(size(), p -> set(p, f.at(p))); // prettier but slower
	}
	
	
	default void copyFrom(Array2<E> o) {
		Vector size = Array2.assertEqualSizes(this, o);
		
		int sizeX = size.xInt();
		UtilsGL.parallel.parallelY(size, y -> {
			for (int x = 0; x < sizeX; x++) {
				set(x, y, o.at(x, y));
			}
		});
		
		// A pretty equivalent: UtilsGL.parallelYVec(size, p -> set(p, o.get(p)));
	}
	
	// ...........................
	
	
}
