package xyz.marsavic.gfxlab;


import xyz.marsavic.functions.F1;
import xyz.marsavic.geometry.Vector;


public interface Transformation2 extends F1<Vector, Vector> {
	
	default Transformation2 then(Transformation2 outer) {
		return p -> outer.at(at(p));
	}
	
	
	// --------------------
	
	
	Transformation2 IDENTITY = p -> p;


	double EPS = 0x1p-16;
	Vector EPS_X = Vector.UNIT_X.mul(EPS);
	Vector EPS_Y = Vector.UNIT_Y.mul(EPS);
	
	default Affine2 derivative(Vector p) {
		Vector a = at(p);
		return Affine2.unitVectors(
				(at(p.add(EPS_X)).sub(a)).div(EPS),
				(at(p.add(EPS_Y)).sub(a)).div(EPS)
		);
	}

	default Affine2 derivative(Vector p, double eps) {
		Vector a = at(p);
		return Affine2.unitVectors(
				(at(p.add(Vector.UNIT_X.mul(eps))).sub(a)).div(eps),
				(at(p.add(Vector.UNIT_Y.mul(eps))).sub(a)).div(eps)
		);
	}

	
	static Transformation2 chain(Transformation2... transformation2s) {
		Transformation2 res = Transformation2.IDENTITY;
		for (Transformation2 t : transformation2s) {
			res = res.then(t);
		}
		return res;
	}
	
}
