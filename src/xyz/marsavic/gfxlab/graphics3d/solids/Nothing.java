package xyz.marsavic.gfxlab.graphics3d.solids;

import xyz.marsavic.gfxlab.graphics3d.Hit;
import xyz.marsavic.gfxlab.graphics3d.Ray;
import xyz.marsavic.gfxlab.graphics3d.Solid;

public class Nothing implements Solid {
	public static Nothing INSTANCE = new Nothing();
	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		return Hit.AtInfinity.axisAlignedGoingIn(ray.d());
	}
}
