package xyz.marsavic.gfxlab.graphics3d;

import xyz.marsavic.functions.F1;
import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;

public record Material(
		Color colorDiffuse
) implements F1<Material, Vector> {
	
	public static Material diffuse(Color colorDiffuse) { return new Material(colorDiffuse); }
	
	public static Material diffuse(double k) { return diffuse(Color.gray(k)); }
	public static Material diffuse(        ) { return diffuse(1.0); }
	public static Material BLACK = diffuse(0.0);
	public static Material WHITE = diffuse();
	
	public static Material DEFAULT = WHITE;
	
	@Override
	public Material at(Vector vector) {
		return this;
	}
}
