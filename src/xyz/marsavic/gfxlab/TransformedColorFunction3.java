package xyz.marsavic.gfxlab;

public record TransformedColorFunction3(
		ColorFunction3 colorFunction3,
		Transformation3 transformation3
) implements ColorFunction3 {
	
	@Override
	public Color at(Vec3 p) {
		return colorFunction3.at(transformation3.at(p));
	}
	
	@Override
	public ColorFunction2 sliceAt(double t) {
		if (transformation3 instanceof Transformation3.Independent0 ti) {
			double t_ = ti.at(t);
			Transformation2 transformation2 = ti.slice0(t);
			ColorFunction2 colorFunction2 = colorFunction3.sliceAt(t_);
			return p -> colorFunction2.at(transformation2.at(p));
		} else {
			return ColorFunction3.super.sliceAt(t);
		}
	}
}