package xyz.marsavic.gfxlab;


import xyz.marsavic.functions.F1;
import xyz.marsavic.geometry.Vector;


public interface TransformationFromSize extends F1<Transformation3, Vec3> {
	
	
	class ToIdentity_ implements TransformationFromSize {
		@Override public Transformation3 at(Vec3 s) { return new ToIdentity(s); }
	}
	
	class ToUnitBox_ implements TransformationFromSize {
		@Override public Transformation3 at(Vec3 s) { return new ToUnitBox(s); }
	}
	
	class ToGeometric_ implements TransformationFromSize {
		@Override public Transformation3 at(Vec3 s) { return new ToGeometric(s); }
	}
	
	class ToGeometricT0_ implements TransformationFromSize {
		@Override public Transformation3 at(Vec3 s) { return new ToGeometricT0(s); }
	}
	
	class ToGeometricFit_ implements TransformationFromSize {
		@Override public Transformation3 at(Vec3 s) { return new ToGeometricFit(s); }
	}
	
	class ToGeometricFitT0_ implements TransformationFromSize {
		@Override public Transformation3 at(Vec3 s) { return new ToGeometricFitT0(s); }
	}
	
	// ---
	
	
	record ToIdentity(Vec3 s) implements Transformation3.Independent0 {
		@Override public Vec3 at(Vec3 p) { return p; }
		@Override public double at(double x0) { return x0; }
		@Override public Transformation2 slice0(double x0) { return Transformation2.IDENTITY; }
	}
	
	
	class ToUnitBox implements Transformation3.Independent0 {
		private final Vec3 m;
		private final double mx;
		private final Vector mp;
		
		public ToUnitBox(Vec3 s) {
			this.m = s.reciprocal();
			this.mx = m.x();
			this.mp = m.p12();
		}
		
		@Override public Vec3 at(Vec3 p) { return p.mul(m); }
		@Override public double at(double x0) {return x0 * mx; }
		@Override public Transformation2 slice0(double x0) {return p -> p.mul(mp); }
	}
	
	
	class ToGeometric implements Transformation3.Independent0 {
		private static final Vec3 u = Vec3.xyz( 2,  2, -2);
		
		private static final Vec3 a = Vec3.xyz(-1, -1,  1);
		private static final double ax = a.x();
		private static final Vector ap = a.p12();
		
		private final Vec3 m;
		private final double mx;
		private final Vector mp;
		
		public ToGeometric(Vec3 s) {
			m = u.div(s);
			mx = m.x();
			mp = m.p12();
		}

		@Override public Vec3 at(Vec3 p) { return p.mul(m).add(a); }
		@Override public double at(double x0) {return x0 * mx + ax; }
		@Override public Transformation2 slice0(double x0) {return p -> p.mul(mp).add(ap); }
	}
	
	
	class ToGeometricT0 implements Transformation3.Independent0 {
		private static final Vec3 u = Vec3.xyz(1,  2, -2);
	
		private static final Vec3 a = Vec3.xyz(0, -1,  1);
		private static final double ax = a.x();
		private static final Vector ap = a.p12();

		private final Vec3 m;
		private final double mx;
		private final Vector mp;
		
		public ToGeometricT0(Vec3 s) {
			m = u.div(s);
			mx = m.x();
			mp = m.p12();
		}

		@Override public Vec3 at(Vec3 p) { return p.mul(m).add(a); }
		@Override public double at(double x0) {return x0 * mx + ax; }
		@Override public Transformation2 slice0(double x0) {return p -> p.mul(mp).add(ap); }
	}
	
	

	class ToGeometricFit implements Transformation3.Independent0 {
		private static final Vec3 u = Vec3.xyz( 2,  2, -2);

		private final Vec3 m, a;
		private final double mx, ax;
		private final Vector mp, ap;

		public ToGeometricFit(Vec3 s) {
			double min = s.p12().min();
			m = u.div(Vec3.xyz(s.x(), min, min));
			mx = m.x();
			mp = m.p12();
			a = s.div(-2).mul(m);
			ax = a.x();
			ap = a.p12();
		}
		
		@Override public Vec3 at(Vec3 p) { return p.mul(m).add(a); }
		@Override public double at(double x0) {return x0 * mx + ax; }
		@Override public Transformation2 slice0(double x0) {return p -> p.mul(mp).add(ap); }
	}

	
	class ToGeometricFitT0 implements Transformation3.Independent0 {
		private static final Vec3 u = Vec3.xyz( 1,  2, -2);

		private final Vec3 m, a;
		private final double mx, ax;
		private final Vector mp, ap;

		public ToGeometricFitT0(Vec3 s) {
			double min = s.p12().min();
			m = u.div(Vec3.xyz(s.x(), min, min));
			mx = m.x();
			mp = m.p12();
			a = s.div(-2).mul(m).add(Vec3.xyz(1, 0, 0));
			ax = a.x();
			ap = a.p12();
		}
		
		@Override public Vec3 at(Vec3 p) { return p.mul(m).add(a); }
		@Override public double at(double x0) {return x0 * mx + ax; }
		@Override public Transformation2 slice0(double x0) {return p -> p.mul(mp).add(ap); }
	}
	
}
