package xyz.marsavic.gfxlab.graphics3d;

import xyz.marsavic.functions.F1;
import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.*;


public abstract class RayTracer implements ColorFunction3 {
	
	private final F1<Scene, Double> fScene;
	
	public RayTracer(F1<Scene, Double> fScene) {
		this.fScene = fScene;
	}
	
	public RayTracer(Scene scene) {
		this(t -> scene);
	}
	
	
	public abstract Color sample(Scene scene, Ray ray);
	
	
	public Color at(Scene scene, Vector p) {
		Ray ray = Ray.pd(Vec3.ZERO, Vec3.zp(1.0, p));
		return sample(scene, ray);
	}
	

	@Override
	public Color at(double t, Vector p) {
		return at(fScene.at(t), p);
	}
	
	
	@Override
	public ColorFunction2 sliceAt(double t) {
		Scene scene = fScene.at(t);
		return p -> at(scene, p);
	}
	
}
