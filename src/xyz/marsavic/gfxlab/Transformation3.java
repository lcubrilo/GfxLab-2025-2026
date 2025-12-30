package xyz.marsavic.gfxlab;


import xyz.marsavic.functions.F1;


public interface Transformation3 extends F1<Vec3, Vec3> {
	
	default Transformation3 then(Transformation3 outer) {
		return p -> outer.at(at(p));
	}
	
	
	// --------------------
	
	
	Independent0 IDENTITY = new Independent0() {
		@Override public Vec3 at(Vec3 p) { return p; }
		@Override public double at(double x0) { return x0; }
		@Override public Transformation2 slice0(double x0) { return Transformation2.IDENTITY; }
	};


	default Affine3 derivative(Vec3 p, double delta) {
		Vec3 a = at(p);
		return Affine3.unitVectors(
				(at(p.add(Vec3.EX.mul(delta))).sub(a)).div(delta),
				(at(p.add(Vec3.EY.mul(delta))).sub(a)).div(delta),
				(at(p.add(Vec3.EZ.mul(delta))).sub(a)).div(delta)
		);
	}
	
	double EPS = 0x1p-16;
	Vec3 EPS_X = Vec3.EX.mul(EPS);
	Vec3 EPS_Y = Vec3.EY.mul(EPS);
	Vec3 EPS_Z = Vec3.EZ.mul(EPS);
	
	default Affine3 derivative(Vec3 p) {
		Vec3 a = at(p);
		return Affine3.unitVectors(
				(at(p.add(EPS_X)).sub(a)).div(EPS),
				(at(p.add(EPS_Y)).sub(a)).div(EPS),
				(at(p.add(EPS_Z)).sub(a)).div(EPS)
		);
	}

	
	default Transformation2 slice0(double x0) {
		return p -> at(Vec3.xp(x0, p)).p12();
	}
	
	
	static Transformation3 chain(Transformation3... transformation3s) {
		Transformation3 res = Transformation3.IDENTITY;
		for (Transformation3 t : transformation3s) {
			res = res.then(t);
		}
		return res;
	}
	

	/**
	 * Marks a Transformation3 f for which f(x0, x1, x2).x0 = f0(x0).
	 * <p> 
	 * For such an f,
	 * slice(x0) = (x1, x2) -> f(x0, x1, x2).p12().
	 * <p>
	 * Must define either at(Vec3 p) or slice0(double x0). There are default implementations for both, but one must be overridden. 
	 */ 
	interface Independent0 extends Transformation3 {
		double at(double x0);		
		
		@Override
		default Vec3 at(Vec3 p) {
			return Vec3.xp(at(p.x()), slice0(p.x()).at(p.p12()));
		}
		
	}
	
}
