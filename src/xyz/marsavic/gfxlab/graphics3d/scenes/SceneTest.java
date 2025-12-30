package xyz.marsavic.gfxlab.graphics3d.scenes;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Light;
import xyz.marsavic.gfxlab.graphics3d.Material;
import xyz.marsavic.gfxlab.graphics3d.Scene;
import xyz.marsavic.gfxlab.graphics3d.Solid;
import xyz.marsavic.gfxlab.graphics3d.solids.Ball;
import xyz.marsavic.gfxlab.graphics3d.solids.Group;
import xyz.marsavic.gfxlab.graphics3d.solids.HalfSpace;
import xyz.marsavic.utils.Numeric;

import java.util.List;

import static xyz.marsavic.gfxlab.Vec3.*;


public class SceneTest extends Scene.T.Base {
	
	private final double lightZ;
	private final int a;
	private final int b;
	
	public SceneTest(double lightZ, int a, int b) {
		this.lightZ = lightZ;
		this.a = a;
		this.b = b;
	}
	
	@Override
	public Solid solid(double t) {
		Ball ball1 = Ball.cr(xyz(-1, 0, 3), 2, 
				uv -> Material.diffuse(
						Numeric.mod(uv.dot(Vector.xy(a, b))) < 0.1 || 
						Numeric.mod(uv.dot(Vector.xy(-a, b))) < 0.1 ?
							Color.hsb(0.3, 0.8, 1.0) :
							Color.hsb(0.0, 0.0, 0.2)
						)
		);
		
		Ball ball2 = Ball.cr(xyz(Numeric.sinT(t * 30), 0, 3), 1, Material.diffuse(Color.hsb(1.0/3, 0.8, 1.0)));
		
		HalfSpace halfSpace = HalfSpace.pn(xyz(0, -2, 0), xyz(0, 1, 0), 
				uv -> Material.diffuse(uv.add(Vector.xy(0.05, 0.05)).mod().min() < 0.1 ? 0.8 : 1.0)
		);
		
		return Group.of(
				ball1,
				ball2,
				halfSpace
		);
	}
	
	
	@Override
	public List<Light> lights(double t) {
		return List.of(
				Light.pc(Vec3.xyz(2, 2, -5 + lightZ * 20), Color.hsb(0.7, 0.8, 10.0)),
				Light.pc(ZERO, Color.gray(0.2))		
		);
	}
}
