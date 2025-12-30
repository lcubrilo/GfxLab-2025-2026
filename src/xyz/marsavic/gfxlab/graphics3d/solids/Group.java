package xyz.marsavic.gfxlab.graphics3d.solids;

import xyz.marsavic.gfxlab.graphics3d.Hit;
import xyz.marsavic.gfxlab.graphics3d.Ray;
import xyz.marsavic.gfxlab.graphics3d.Solid;

import java.util.Collection;

public class Group implements Solid {
	
	private final Solid[] solids;
	
	public Group(Solid... solids) {
		this.solids = solids;
	}
	
	
	public static Group of(Solid... solids) {
		return new Group(solids);
	}
	
	
	public static Group of(Collection<Solid> solids) {
		return new Group(solids.toArray(Solid[]::new));
	}
	
	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		Hit hitMin = Nothing.INSTANCE.firstHit(ray, afterTime);
		double tMin = hitMin.t();
		
		for (Solid solid : solids) {
			Hit hit = solid.firstHit(ray, afterTime);
			double t = hit.t();
			if (t < tMin) {
				hitMin = hit;
				tMin = t;
			}
		}
				
		return hitMin;
	}
	
	
	// TODO HitBetween
}
