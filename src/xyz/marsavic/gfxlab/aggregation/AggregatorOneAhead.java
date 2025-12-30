package xyz.marsavic.gfxlab.aggregation;

import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.ColorFunction3;
import xyz.marsavic.gfxlab.Matrix;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.gui.UtilsGL;
import xyz.marsavic.resources.Rr;
import xyz.marsavic.utils.Hash;
import xyz.marsavic.utils.Parallel;
import xyz.marsavic.utils.Utils;

import java.util.concurrent.Future;


public class AggregatorOneAhead extends Aggregator {
	
	private Future<Rr<Matrix<Color>>> frFrameAhead;
	private int iFrameAhead;
	
	
	
	public AggregatorOneAhead(ColorFunction3 colorFunction3, Vec3 size, boolean repeats, boolean motionBlur, Hash hash) {
		super(colorFunction3, size, repeats, motionBlur, hash);
		iFrameAhead = 0;
		startGettingAhead(iFrameAhead);
	}
	
	
	private void startGettingAhead(int iFrame) {
		frFrameAhead = UtilsGL.parallel.submit(() -> rFrameCompute(iFrame));
	}
	
	
	private Rr<Matrix<Color>> rFrameCompute(int iFrame_) {
		Aggregate aggregate = createAggregate(iFrame_);
		aggregate.addSample();
		var avg = aggregate.avg();
		aggregate.release();
		return avg;
	}
	
	
	@Override
	public Rr<Matrix<Color>> rFrame(int iFrame) {  // not thread safe
		Rr<Matrix<Color>> rFrame;
		
		if (iFrame == iFrameAhead) {
			rFrame = Parallel.futureGet(frFrameAhead);
		} else {
			releaseRFrameAhead();
			rFrame = rFrameCompute(iFrame);
		}
		iFrameAhead = iFrame(iFrame + 1);
		startGettingAhead(iFrameAhead);
		return rFrame;
	}
	
	
	private void releaseRFrameAhead() {
		var frFrame_ = frFrameAhead;
		UtilsGL.parallel.submit(() -> Parallel.futureGet(frFrame_).release());
	}
	
	
	@Override
	public void release() {
		releaseRFrameAhead();
	}
}
