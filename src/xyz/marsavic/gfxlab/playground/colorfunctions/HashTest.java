package xyz.marsavic.gfxlab.playground.colorfunctions;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.ColorFunction3;
import xyz.marsavic.gfxlab.MatrixInts;
import xyz.marsavic.utils.Hash;


public class HashTest implements ColorFunction3 {
	
	private final MatrixInts c;
	
	private static final long MASK = (1L << 32) - 1;
	
	public HashTest(Vector size) {
		long seed = 0x070B7B0D4B007763L;
		int n = size.areaInt() / 8;
		c = new MatrixInts(size);
		
		for (int i = n; i > 0; i--) {
			long h = new Hash(seed).add(i).get();
			Vector hilo = Vector.xy((h >>> 32) & MASK, h & MASK);
			Vector p = hilo.mul(0x1p-32).mul(size);
			c.set(p, c.at(p) + 1);
		}
	}
	
	@Override
	public Color at(double t, Vector p) {
		return c.at(p) == 0 ? Color.BLACK : Color.WHITE;
	}
}
