package xyz.marsavic.gfxlab.playground.colorfunctions;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.ColorFunction3;
import xyz.marsavic.gfxlab.graphics3d.Hit;
import xyz.marsavic.gfxlab.graphics3d.Ray;
import xyz.marsavic.gfxlab.graphics3d.solids.Ball;
import xyz.marsavic.gfxlab.graphics3d.solids.HalfSpace;

import static xyz.marsavic.gfxlab.Vec3.*;


public class RayTracingTest implements ColorFunction3 {
	
	@Override
	public Color at(double t, Vector p) {
		Ball ball = Ball.cr(xyz(0, 0, 3), 2);
		HalfSpace halfSpace = HalfSpace.pn(xyz(0, -2, 0), xyz(0, -1, 0));
		
		Ray ray = Ray.pd(ZERO, zp(1, p));
		
		Hit hit1 = ball.firstHit(ray);
		Hit hit2 = halfSpace.firstHit(ray);

		double dist = Math.min(hit1.t(), hit2.t());
		
		return Color.gray(1/(1 + dist));
	}
}