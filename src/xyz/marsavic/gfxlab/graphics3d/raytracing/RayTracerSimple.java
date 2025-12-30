package xyz.marsavic.gfxlab.graphics3d.raytracing;

import xyz.marsavic.functions.F1;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;

public class RayTracerSimple extends RayTracer {
	
	public RayTracerSimple(F1<Scene, Double> fScene) {
		super(fScene);
	}
	
	@Override
	public Color sample(Scene scene, Ray ray) {
		Hit hit = scene.solid().firstHit(ray);
		
		Vec3 p = ray.at(hit.t());
		Vec3 n_ = hit.n_();
		Material material = hit.material();
		
		Color colorDiffuse = Color.BLACK;
		for (Light light : scene.lights()) {
			Vec3 l = light.p().sub(p);
			double llSqr = l.lengthSquared();
			double ll = Math.sqrt(llSqr);
			double cosNL = n_.dot(l) / ll;
			
			if (cosNL > 0) {
				Color c = light.c().mul(material.colorDiffuse()).mul(cosNL / llSqr);
				colorDiffuse = colorDiffuse.add(c);
			}
		}
		
		return colorDiffuse;
	}
	
}
