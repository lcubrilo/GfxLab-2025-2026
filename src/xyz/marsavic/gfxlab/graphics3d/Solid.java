package xyz.marsavic.gfxlab.graphics3d;


public interface Solid {
	
	/**
	 * Returns the first hit of the ray into the surface of the solid, occurring strictly after the given time.
	 * The default implementation is based on the hits method, but implementations of Solid can choose to override
	 * this method to increase performance when only the first hit is needed.
	 * If the ray misses the Solid, the hit at infinity is returned.
	 * All hits along the same line should alternate between entering and exiting hits, meaning the dot product
	 * between the normal at the hit and line direction should alternate its sign. This should hold also for
	 * the hit at infinity.
	 */
	Hit firstHit(Ray ray, double afterTime);
	
	
	default Hit firstHit(Ray ray) {
		return firstHit(ray, 0);
	}
	
	
	/**
	 * Is there any hit between afterTime and beforeTime.
	 */
	default boolean hitBetween(Ray ray, double afterTime, double beforeTime) {
		double t = firstHit(ray, afterTime).t();
		return t < beforeTime;
	}
	
}