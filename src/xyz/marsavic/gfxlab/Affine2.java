package xyz.marsavic.gfxlab;


import xyz.marsavic.geometry.Vector;
import xyz.marsavic.utils.Numeric;


public record Affine2(
		double m00, double m01, double m02,
		double m10, double m11, double m12
//               0,          0,          1
) implements Transformation2 {
	
	public static Affine2 IDENTITY = new Affine2(
			1.0, 0.0, 0.0,
			0.0, 1.0, 0.0
	);
	
	
	public static Affine2 unitVectors(Vector ex, Vector ey) {
		return new Affine2(
				ex.x(), ey.x(), 0,
				ex.y(), ey.y(), 0
		);
	}
	
	
	public Affine2 then(Affine2 t) {
		return new Affine2(
				t.m00 * m00 + t.m01 * m10, t.m00 * m01 + t.m01 * m11, t.m00 * m02 + t.m01 * m12,
				t.m10 * m00 + t.m11 * m10, t.m10 * m01 + t.m11 * m11, t.m10 * m02 + t.m11 * m12
		);
	}
	
	
	@Override
	public Vector at(Vector v) {
		return new Vector(
				m00 * v.x() + m01 * v.y() + m02,
				m10 * v.x() + m11 * v.y() + m12
		);
	}
	
	
	public Vector applyWithoutTranslationTo(Vector v) {
		return new Vector(
				m00 * v.x() + m01 * v.y(),
				m10 * v.x() + m11 * v.y()
		);
	}
	
	
	public Affine2 inverse() {
		double det = determinant();
		return new Affine2(
				 m11 / det, -m01 / det,  (m01 * m12 - m02 * m11) / det,
				-m10 / det,  m00 / det, -(m00 * m12 - m02 * m10) / det
		);
	}
	
	
	public Affine2 transposeWithoutTranslation() {
		return new Affine2(
				m00, m10, 0,
				m01, m11, 0
		);
	}
	
	
	public double determinant() {
		//noinspection UnaryPlus
		return (
				+ (m00 * m11)
				- (m01 * m10)
		);
	}
	
	
	public static Affine2 translation(Vector d) {
		return new Affine2(
				1.0, 0.0, d.x(),
				0.0, 1.0, d.y()
		);
	}
	
	public static Affine2 rotation(double angle) {
		return new Affine2(
				Numeric.cosT(angle), -Numeric.sinT(angle), 0.0,
				Numeric.sinT(angle), Numeric.cosT(angle), 0.0
		);
	}
	
	public static Affine2 scaling(double c) {
		return new Affine2(
				c, 0.0, 0.0,
				0.0, c, 0.0
		);
	}
	
	public static Affine2 scaling(Vector c) {
		return new Affine2(
				c.x(), 0.0, 0.0,
				0.0, c.y(), 0.0
		);
	}
	
	
	public static Affine2 chain(Affine2... transformations) {
		Affine2 res = Affine2.IDENTITY;
		for (Affine2 t : transformations) {
			res = res.then(t);
		}
		return res;
	}
	
}
