package xyz.marsavic.gfxlab.graphics3d;

import xyz.marsavic.functions.F1;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.graphics3d.solids.Nothing;

import java.util.Collection;
import java.util.List;


public interface Scene {
	Solid       defaultSolid           = Nothing.INSTANCE;
	List<Light> defaultLights          = List.of();
	Color       defaultColorBackground = Color.BLACK;
	
	default Solid             solid          () { return defaultSolid          ; }
	default Collection<Light> lights         () { return defaultLights         ; }
	default Color             colorBackground() { return defaultColorBackground; }
	
	
	record Record(
			Solid             solid ,
			Collection<Light> lights,
			Color             colorBackground
	) implements Scene {}
	
	
	interface T extends F1<Scene, Double> {
		default Solid             solid          (double t) { return defaultSolid          ; }
		default Collection<Light> lights         (double t) { return defaultLights         ; }
		default Color             colorBackground(double t) { return defaultColorBackground; }
		
		@Override
		default Scene at(Double t) {
			return new Record(
					solid          (t),
					lights         (t),
					colorBackground(t)
			);
		}
		
		/** If an element(t) is constant, generate it in ...() method, not in ...(t), because ...(t) is called for each ray. */ 
		class Base implements T {
			public Solid       solid          () { return defaultSolid          ; }
			public List<Light> lights         () { return defaultLights         ; }
			public Color       colorBackground() { return defaultColorBackground; }
			
			protected Solid       solid           = solid          ();
			protected List<Light> lights          = lights         ();
			protected Color       colorBackground = colorBackground();
			
			@Override public Solid             solid          (double t) { return solid          ; }
			@Override public Collection<Light> lights         (double t) { return lights         ; }
			@Override public Color             colorBackground(double t) { return colorBackground; }
		}
	}
	
}
