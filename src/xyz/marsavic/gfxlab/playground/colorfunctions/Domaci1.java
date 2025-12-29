package xyz.marsavic.gfxlab.playground.colorfunctions;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.ColorFunctionT;
import xyz.marsavic.utils.Numeric;

/**
 * Left-side cannon that swings between -60..+60 degrees and periodically shoots balls.
 * Balls: gravity + wall bounces; floor bounces lose energy; optional "rest" when slow enough.
 *
 * Best used with TransformationsFromSize.ToIdentity (pixel coordinates).
 * Project default size is 640x640 in playground/GfxLab.java.
 */
public class Domaci1 implements ColorFunctionT {

	// --- "Screen" in pixel coordinates (matches default in GfxLab.java) ---
	private static final double W = 640.0;
	private static final double H = 640.0;

	// --- Cannon ---
	private static final double CANNON_X = 35.0;
	private static final double CANNON_Y = H * 0.5;

	private static final double BASE_W = 28.0;
	private static final double BASE_H = 40.0;

	private static final double BARREL_LEN = 75.0;
	private static final double BARREL_HALF_THICK = 7.0;

	private static final double MOUTH_OFFSET = 18.0; // from pivot along barrel

	// --- Motion / shooting ---
	private static final double SWING_DEG = 60.0;
	private static final double SWING_SPEED = 0.006;     // cycles per unit t (t is ~frame index)
	private static final double SHOT_PERIOD = 75.0;     // in "t" units; ~ every 22 frames
	private static final int    MAX_SHOTS_DRAWN = 32;   // only recent shots are drawn

	// --- Ball physics ---
	private static final double BALL_R = 9.5;

	private static final double V0 = 4.5;              // initial speed (pixels per t-unit)
	private static final double GRAV = 0.12;           // downward acceleration (pixels per t^2)

	private static final double WALL_RESTITUTION = 1.0; // x-bounce (perfect)
	private static final double FLOOR_RESTITUTION = 0.96; // y-bounce (lose energy)

	private static final double REST_VY = 0.40;         // if |vy| below => treat as resting (optional)
	private static final double REST_VX = 0.10;         // if |vx| below => stop x too (optional)

	@Override
	public Color at(double t, Vector p) {
		double x = p.x();
		double y = p.y();

		// Background: subtle animated "woosh" so empty space isn't flat
		Color bg = background(t, x, y);

		// Draw cannon (base + rotated barrel)
		Color cannon = drawCannon(t, x, y);
		if (cannon != null) return cannon;

		// Balls: compute nearest distance to any active ball center
		BallHit hit = nearestBallHit(t, x, y);
		if (hit != null) {
			// Ball shading: bright core + soft edge
			double edge = smoothstep(BALL_R, BALL_R - 2.0, hit.dist);
			double glow = smoothstep(BALL_R + 10.0, BALL_R + 2.0, hit.dist);

			Color ballColor = Color.okhcl(hit.hue, 0.22, 0.65).mul(edge)
					.add(Color.okhcl(hit.hue + 0.05, 0.12, 0.35).mul(glow * 0.5));

			return ballColor.add(bg.mul(1.0 - edge * 0.7));
		}

		return bg;
	}

	// ---------------- Drawing helpers ----------------

	private Color drawCannon(double t, double x, double y) {
		// Base: rectangle centered at (CANNON_X, CANNON_Y)
		if (inRect(x, y, CANNON_X, CANNON_Y, BASE_W, BASE_H)) {
			// metal-ish base
			double v = 0.18 + 0.06 * Numeric.sinT(0.02 * t + 0.01 * y);
			return Color.gray(clamp01(v));
		}

		// Pivot point at top-center of base
		double px = CANNON_X + BASE_W * 0.15;
		double py = CANNON_Y - BASE_H * 0.15;

		double ang = cannonAngleRad(t);
		double ca = Math.cos(ang);
		double sa = Math.sin(ang);

		// Transform point into cannon-local coords (rotate by -ang around pivot)
		double lx =  (x - px) * ca + (y - py) * sa;
		double ly = -(x - px) * sa + (y - py) * ca;

		// Barrel: axis along +x in local coords, thickness in y
		boolean inBarrel =
				(lx >= 0.0 && lx <= BARREL_LEN) &&
				(Math.abs(ly) <= BARREL_HALF_THICK);

		if (inBarrel) {
			// slight highlight
			double highlight = 0.18 + 0.10 * (1.0 - Math.abs(ly) / BARREL_HALF_THICK);
			return Color.gray(clamp01(highlight));
		}

		return null;
	}

    private static double sq(double x){
        return x*x;
    }

    private static Color background(double t, double x, double y) {
        double ny = clamp01(y / H);          // 0 top -> 1 bottom
        double s = 0.0006 * t;               // very slow time

        // small drift in brightness only (no hue swings)
        double drift = 0.015 * Numeric.sinT(s);

        // Top: deep sky blue. Bottom: pale sky.
        double rTop = 0.08, gTop = 0.16, bTop = 0.30;
        double rBot = 0.62, gBot = 0.76, bBot = 0.92;

        // gentle gradient + tiny drift
        double k = smoothstep(0.15, 0.95, ny);
        double r = lerp(rTop, rBot, k) + 0.02 * drift;
        double g = lerp(gTop, gBot, k) + 0.03 * drift;
        double b = lerp(bTop, bBot, k) + 0.04 * drift;

        return Color.rgb(clamp01(r), clamp01(g), clamp01(b));
    }

    private static double lerp(double a, double b, double t) {
        return a * (1.0 - t) + b * t;
    }

    private static Color mix(Color a, Color b, double t) {
        return a.mul(1.0 - t).add(b.mul(t));
    }


	// ---------------- Ball simulation ----------------

	private BallHit nearestBallHit(double t, double x, double y) {
		int iLast = (int) Math.floor(t / SHOT_PERIOD);
		int iFirst = Math.max(0, iLast - MAX_SHOTS_DRAWN);

		BallHit best = null;

		for (int i = iFirst; i <= iLast; i++) {
			double t0 = i * SHOT_PERIOD;
			double dt = t - t0;
			if (dt < 0) continue;

			// Spawn parameters (deterministic but varied)
			double ang = cannonAngleRad(t0);

			// Cannon pivot used same as in drawCannon
			double px = CANNON_X + BASE_W * 0.15;
			double py = CANNON_Y - BASE_H * 0.15;

			double ca = Math.cos(ang);
			double sa = Math.sin(ang);

			double spawnX = px + (MOUTH_OFFSET + BARREL_LEN) * ca;
			double spawnY = py + (MOUTH_OFFSET + BARREL_LEN) * sa;

			// Slight per-shot variation using sin-hash
			double jitter = hash01(i * 17.31 + 0.123);
			double speed = V0 * (0.85 + 0.35 * jitter);

			double vx0 = speed * ca;
			double vy0 = speed * sa;

			// Simulate x with wall reflections (elastic)
			double bx = reflect01(spawnX + vx0 * dt, BALL_R, W - BALL_R);
			double vx = vx0 * WALL_RESTITUTION;

			// Simulate y with gravity + inelastic floor bounces
			YState ys = simulateY(spawnY, vy0, dt);

			double by = ys.y;
			double vy = ys.vy;

			// If resting, optionally stop vx too (so balls can "pile up")
			if (ys.resting && Math.abs(vx) < REST_VX) vx = 0;

			// Distance to this ball
			double dx = x - bx;
			double dy = y - by;
			double d = Math.sqrt(dx * dx + dy * dy);

			if (d <= BALL_R + 10.0) {
				if (best == null || d < best.dist) {
					// Color changes slowly with shot index and speed
					double hue = 0.55 + 0.35 * hash01(i * 19.17 + 0.4);  // fixed random-ish per ball

					best = new BallHit(d, hue);
				}
			}
		}

		return best;
	}

	// Y-motion: repeated floor bounces with restitution.
	// Floor is at y = H - BALL_R ; ceiling at y = BALL_R (optional: reflect at ceiling too, elastic)
	private YState simulateY(double y0, double vy0, double dt) {
		double yMin = BALL_R;
		double yMax = H - BALL_R;

		double y = y0;
		double vy = vy0;
		double timeLeft = dt;

		// Limit iterations to avoid worst-case long loops
		for (int iter = 0; iter < 30 && timeLeft > 1e-9; iter++) {

			// If already "resting" on the floor, just stay there
			if (Math.abs(vy) < REST_VY && y >= yMax - 1e-6) {
				return new YState(yMax, 0.0, true);
			}

			// Predict if we hit floor within timeLeft.
			// Solve y(t) = y + vy*t + 0.5*GRAV*t^2 = yMax
			double hitFloorT = solveHitTime(y, vy, GRAV, yMax);

			// Predict if we hit ceiling (optional elastic bounce)
			double hitCeilT  = solveHitTime(y, vy, GRAV, yMin);

			double tHit = Double.POSITIVE_INFINITY;
			int hitType = 0; // 1 floor, 2 ceiling

			if (hitFloorT >= 0 && hitFloorT < tHit) { tHit = hitFloorT; hitType = 1; }
			if (hitCeilT  >= 0 && hitCeilT  < tHit) { tHit = hitCeilT;  hitType = 2; }

			if (tHit == Double.POSITIVE_INFINITY || tHit > timeLeft) {
				// No collision in remaining time
				y  = y + vy * timeLeft + 0.5 * GRAV * timeLeft * timeLeft;
				vy = vy + GRAV * timeLeft;
				// Clamp just in case of numerical drift
				if (y > yMax) y = yMax;
				if (y < yMin) y = yMin;
				break;
			}

			// Advance to collision time
			y  = y + vy * tHit + 0.5 * GRAV * tHit * tHit;
			vy = vy + GRAV * tHit;
			timeLeft -= tHit;

			if (hitType == 1) {
				// Floor bounce: flip vy, lose energy
				y = yMax;
				vy = -vy * FLOOR_RESTITUTION;

				// If it’s too small, rest
				if (Math.abs(vy) < REST_VY) {
					return new YState(yMax, 0.0, true);
				}
			} else if (hitType == 2) {
				// Ceiling: elastic (or you can damp it too)
				y = yMin;
				vy = -vy * 0.98;
			}
		}

		boolean resting = (Math.abs(vy) < REST_VY && y >= (H - BALL_R - 1e-3));
		return new YState(y, vy, resting);
	}

	// Solve y + v*t + 0.5*a*t^2 = yTarget for smallest non-negative t.
	private static double solveHitTime(double y, double v, double a, double yTarget) {
		double c = y - yTarget;

		// 0.5*a*t^2 + v*t + c = 0
		double A = 0.5 * a;
		double B = v;
		double C = c;

		// If A is ~0, linear
		if (Math.abs(A) < 1e-12) {
			if (Math.abs(B) < 1e-12) return Double.POSITIVE_INFINITY;
			double t = -C / B;
			return t >= 0 ? t : Double.POSITIVE_INFINITY;
		}

		double D = B * B - 4.0 * A * C;
		if (D < 0) return Double.POSITIVE_INFINITY;

		double sD = Math.sqrt(D);
		double t1 = (-B - sD) / (2.0 * A);
		double t2 = (-B + sD) / (2.0 * A);

		double tMin = Double.POSITIVE_INFINITY;
		if (t1 >= 0) tMin = t1;
		if (t2 >= 0 && t2 < tMin) tMin = t2;

		return tMin;
	}

	// Reflect position between [min,max] like a billiard (elastic wall).
	private static double reflect01(double x, double min, double max) {
		double L = max - min;
		if (L <= 0) return min;

		double u = x - min;
		double m = mod(u, 2.0 * L);
		return (m <= L) ? (min + m) : (max - (m - L));
	}

	private static double mod(double a, double m) {
		double r = a % m;
		return r < 0 ? r + m : r;
	}

	private static boolean inRect(double x, double y, double cx, double cy, double w, double h) {
		return Math.abs(x - cx) <= w * 0.5 && Math.abs(y - cy) <= h * 0.5;
	}

	private static double cannonAngleRad(double t) {
		double swing = Numeric.sinT(SWING_SPEED * t); // [-1,1]
		double deg = SWING_DEG * swing;
		return Math.toRadians(deg);
	}

	private static double clamp01(double v) {
		return v < 0 ? 0 : (v > 1 ? 1 : v);
	}

	private static double smoothstep(double edge0, double edge1, double x) {
		// returns 0..1 with smooth transition between edges
		double t = clamp01((x - edge0) / (edge1 - edge0));
		return t * t * (3 - 2 * t);
	}

	// Simple deterministic "hash" in [0,1) from a double
	private static double hash01(double u) {
		double h = Math.sin(u * 127.1 + 311.7) * 43758.5453123;
		return h - Math.floor(h);
	}

	private record BallHit(double dist, double hue) {}
	private record YState(double y, double vy, boolean resting) {}
}
