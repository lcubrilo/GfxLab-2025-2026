package xyz.marsavic.gfxlab.aggregation;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.ColorFunction3;
import xyz.marsavic.gfxlab.Matrix;
import xyz.marsavic.gfxlab.MatrixColor;
import xyz.marsavic.gfxlab.gui.UtilsGL;
import xyz.marsavic.resources.Rr;
import xyz.marsavic.utils.Hash;
import xyz.marsavic.utils.Utils;

import java.util.SplittableRandom;


public class Aggregate {
	private final ColorFunction3 colorFunction3;
	private final double t;
	private final Vector size;
	private final Hash hash;
	

	private State state;
	
	
	
	private record State(
			int count,
			Rr<Matrix<Color>> rSum
	) {
		private synchronized void release() {
			if (rSum != null) {
				rSum.release();
			}
		}
		
		private synchronized Rr<Matrix<Color>> avg() {
			if (count == 0) {
				throw new IllegalStateException("No samples. Cannot compute average.");
			}
			return rSum.f(sum -> {
				Rr<Matrix<Color>> rAvg = UtilsGL.matricesColor.obtain(sum.size(), true);
				rAvg.a(count == 1 ? 
						avg -> avg.copyFrom(sum) :                         // Optimization only, so we don't divide by 1
						avg -> MatrixColor.mul(sum, 1.0 / count, avg)
				);
				return rAvg;
			});
		}
	}
	
	

	public Aggregate(ColorFunction3 colorFunction3, double t, Vector size, Hash hash) {
		// TODO add a boolean parameter stating whether to immediately do the first sample.
		//  If so, then optimize by sending the first sample straight into the sum matrix
		//  (instead of making a zero matrix, computing the sample, and them adding them together).
		this.colorFunction3 = colorFunction3;
		this.t = t;
		this.size = size;
		this.hash = hash.add(t);
		state = new State(0, null);
	}
	
	
	private synchronized State getState() {
		return state;
	}
	
	
	private synchronized void setState(State stateNew) {
		State stateOld = state;
		state = stateNew;
		stateOld.release();
	}
	
	
	public synchronized void release() {
		if (state != null) {
			state.release();
		}
	}
	
	
	public synchronized void addSample() {
		Hash hashFrame = hash.add(state.count);
		
		// Instead of rewriting the same "sum" matrix, we borrow a new one and release the old one. This is
		// done to avoid dealing with concurrency issues. It would be faster and more memory efficient if we
		// added the new sample in-place, but it's more troublesome.
		
		State stateOld = getState();
		State stateNew = new State(stateOld.count + 1, UtilsGL.matricesColor.obtain(size, true));
		int sizeX = size.xInt();
		stateNew.rSum.a(mSumNew -> {
			if (stateOld.count == 0) {                // For optimization only, no other reason to consider this case separately.
				UtilsGL.parallel.parallelY(size, y -> {
					SplittableRandom rng = new SplittableRandom(hashFrame.add(y).get());
					for (int x = 0; x < sizeX; x++) {
						Color c = colorFunction3.at(t + rng.nextDouble(), Vector.xy(x + rng.nextDouble(), y + rng.nextDouble()));
						mSumNew.set(x, y, c);
					}
				});
			} else {
				stateOld.rSum.a(mSumOld -> {
					UtilsGL.parallel.parallelY(size, y -> {
						SplittableRandom rng = new SplittableRandom(hashFrame.add(y).get());
						for (int x = 0; x < sizeX; x++) {
							Color c = colorFunction3.at(t + rng.nextDouble(), Vector.xy(x + rng.nextDouble(), y + rng.nextDouble()));
							mSumNew.set(x, y, mSumOld.at(x, y).add(c));
						}
					});
				});
			}
		});
		
		setState(stateNew);
		
		synchronized (this) {
			this.notifyAll();
		}
	}
	
	
	public Rr<Matrix<Color>> avg() {
		return getState().avg();
	}
	
	
	/** If there are no samples it will block while waiting for at least one sample before computing the average. */
	public Rr<Matrix<Color>> avgAtLeastOne() {
		return Utils.waitWhile(this, () -> getState() == null || getState().count == 0, this::getState).avg();
	}
	
	
}
