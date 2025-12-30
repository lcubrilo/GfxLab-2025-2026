package xyz.marsavic.gfxlab;

import xyz.marsavic.utils.Numeric;

import java.util.function.DoubleUnaryOperator;

/**
 * Colors in linear sRGB color space.
 */
public class Color {
	public static final Color BLACK = rgb(0, 0, 0);
	public static final Color WHITE = rgb(1, 1, 1);
	public static final Color DEBUG = rgb(1, 0, 0.5);
	public static final Color NAN = rgb(Double.NaN, Double.NaN, Double.NaN);
	
	
	final double r, g, b;
 
 
 
	private Color(double r, double g, double b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	
	public static Color rgb(double r, double g, double b) {
		return new Color(r, g, b);
	}
	
	
	public static Color rgb(Vec3 v) {
		return rgb(v.x(), v.y(), v.z());
	}
	
	
	public static Color gray(double k) {
		return rgb(k, k, k);
	}
	
	
	public static Color hsb(double h, double s, double b) {
		h = Numeric.mod(h);
		int base = (int) (h * 6.0);
		double f = h * 6.0 - base;
		double p = b * (1.0 - s);
		double q = b * (1.0 - s * f);
		double t = b * (1.0 - s * (1.0 - f));
		return switch (base) {
			case 0 -> Color.rgb(b, t, p);
			case 1 -> Color.rgb(q, b, p);
			case 2 -> Color.rgb(p, b, t);
			case 3 -> Color.rgb(p, q, b);
			case 4 -> Color.rgb(t, p, b);
			case 5 -> Color.rgb(b, p, q);
			default -> throw new RuntimeException("Can't happen");
		};
	}
	
	
	public static Color hsb(Vec3 v) {
		return hsb(v.x(), v.y(), v.z());
	}
	
	
	public static Color oklab(double l, double a, double b) {
		double l_ = l + 0.3963377774 * a + 0.2158037573 * b;
		double m_ = l - 0.1055613458 * a - 0.0638541728 * b;
		double s_ = l - 0.0894841775 * a - 1.2914855480 * b;
		
		double cl = l_ * l_ * l_;
		double cm = m_ * m_ * m_;
		double cs = s_ * s_ * s_;
		
		double cr =  4.0767245293 * cl - 3.3072168827 * cm + 0.2307590544 * cs;
		double cg = -1.2681437731 * cl + 2.6093323231 * cm - 0.3411344290 * cs;
		double cb = -0.0041119885 * cl - 0.7034763098 * cm + 1.7068625689 * cs;
		
		return Color.rgb(cr, cg, cb);
	}
	
	
	public static Color oklab(Vec3 v) {
		return oklab(v.x(), v.y(), v.z());
	}
	
	
	public static Color okhcl(double h, double c, double l) {
		return oklab(
			l,
			c * Numeric.cosT(h),
			c * Numeric.sinT(h)
		);
	}
	
	
	public static Color okhcl(Vec3 v) {
		return okhcl(v.x(), v.y(), v.z());
	}
	
	
	public static Color code(int code) {
		return rgb(
				byteToValue((code >> 16) & 0xFF),
				byteToValue((code >>  8) & 0xFF),
				byteToValue((code      ) & 0xFF)
		);
	}
	

	public Color add(Color o) {
		return rgb(r + o.r, g + o.g, b + o.b);
	}
	
	
	public Color sub(Color o) {
		return rgb(r - o.r, g - o.g, b - o.b);
	}
	
	
	public Color mul(double c) {
		return rgb(r * c, g * c, b * c);
	}
	
	
	public Color mul(Color o) {
		return rgb(r * o.r, g * o.g, b * o.b);
	}
	
	
	public Color div(double c) {
		return rgb(r / c, g / c, b / c);
	}
	
	
	public Color pow(double c) {
		return rgb(
				Math.pow(r, c),
				Math.pow(g, c),
				Math.pow(b, c)
		);
	}
	
	
	public Color f(DoubleUnaryOperator f) {
		return rgb(
				f.applyAsDouble(r),
				f.applyAsDouble(g),
				f.applyAsDouble(b)
		);
	}
	
	
	public double luminance() {
		return
				0.212655 * r +
				0.715158 * g +
				0.072187 * b;
	}
	
	
	/**
	 * For colors with components in [0, 1].
	 */
	public double perceivedLightness() {
		double y = luminance(); // y should be between 0.0 and 1.0
		
		if (y <= 216.0 / 24389.0 ) {      // The CIE standard states   0.008856, but ( 6/29)^3 =   216/24389 is the intent for   0.008856451679036.
			return y * (24389.0 / 27.0);  // The CIE standard states 903.3     , but (29/ 3)^3 = 24389/   27 is the intent for 903.296296296296296.
		} else {
			return Math.cbrt(y) * 116.0 - 16.0;
		}
	}
	
	
	public Vec3 hsb() {
		// TODO Test, this was never tested.
		double brightness = Math.max(Math.max(r, g), b);
		double delta = brightness - Math.min(Math.min(r, g), b);
		double saturation = brightness == 0 ? 0 : delta / brightness;
		double hue;
		
		if (saturation == 0) {
			hue = 0;
		} else {
			double redC   = (brightness - r) / delta;
			double greenC = (brightness - g) / delta;
			double blueC  = (brightness - b) / delta;
			if (r == brightness) {
				hue = blueC - greenC;
			} else if (g == brightness) {
				hue = 2.0 + redC - blueC;
			} else {
				hue = 4.0 + greenC - redC;
			}
			hue = hue / 6.0;
			if (hue < 0) {
				hue = hue + 1.0;
			}
		}
		
		return Vec3.xyz(hue, saturation, brightness);
	}
	
	
	public double max() {
		return Math.max(r, Math.max(g, b));
	}
	
	
	public int code() {
		return
				(0xFF000000)    |
				(valueToByte(r) << 16) |
				(valueToByte(g) <<  8) |
				(valueToByte(b)      );
	}
	
	
	public int codeClamp() {
		return
				(0xFF000000)    |
				(valueToByte(Numeric.clamp(r)) << 16) |
				(valueToByte(Numeric.clamp(g)) <<  8) |
				(valueToByte(Numeric.clamp(b))      );
	}
	
	
	public int codeOrDefault(int d) {
		return this.in01() ? code() : d;
	}
	
	
	public int codeOrBlack() {
		return this.in01() ? code() : 0xFF000000;
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		Color color = (Color) o;
		
		return
				Double.compare(color.r, r) == 0 &&
				Double.compare(color.g, g) == 0 &&
				Double.compare(color.b, b) == 0;
	}
	
	
	@Override
	public int hashCode() {
		return Long.hashCode(
				      Double.doubleToLongBits(r) +
				 31 * Double.doubleToLongBits(g) +
				997 * Double.doubleToLongBits(b)
		);
	}
	
	
	public boolean zero() {
		return (r == 0.0 && g == 0.0 && b == 0.0);
	}
	
	public boolean notZero() {
		return (r != 0.0 || g != 0.0 || b != 0.0);
	}
	
	public boolean one() {
		return (r == 1.0 && g == 1.0 && b == 1.0);
	}
	
	public boolean notOne() {
		return (r != 1.0 || g != 1.0 || b != 1.0);
	}
	
	
	public boolean in01() {
		return
				r >= 0 && r <= 1 &&
				g >= 0 && g <= 1 &&
				b >= 0 && b <= 1;
	}
	
	
	public Color if01or(Color d) {
		return in01() ? this : d;
	}
	
	
	public Color clampTo01() {
		return new Color(
				Numeric.clamp(r),
				Numeric.clamp(g),
				Numeric.clamp(b)
		);
	}
	
	
	@Override
	public String toString() {
		return String.format("(r: %6.3f, g: %6.3f, b: %6.3f | Y: %6.3f)", r, g, b, luminance());
	}
	

	// ====================================================================================================
	
	
	/**
	 * sRGB gamma function. Approx. pow(v, 2.2).
	 */
	public static double sRGBtoLinear(double v) {
		if (v <= 0.04045) {
			return v / 12.92;
		} else {
			return Math.pow((v + 0.055) / 1.055, 2.4);
		}
	}
	
	
	/**
	 * Inverse of sRGB gamma function. Approx. pow(v, 1 / 2.2).
	 */
	public static double linearToSRGB(double v) {
		if (v <= 0.0031308) {
			return v * 12.92;
		} else {
			return 1.055 * Math.pow(v, 1.0 / 2.4) - 0.055;
		}
	}
	
	
	public static int valueToByte(double x) {
		return (int) (linearToSRGB(x) * 255 + 0.5);
	}
	
	
	public static double byteToValue(int x) {
		return sRGBtoLinear(x / 255.0);
	}
	
}
