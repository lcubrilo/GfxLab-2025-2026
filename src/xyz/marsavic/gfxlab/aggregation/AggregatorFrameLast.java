package xyz.marsavic.gfxlab.aggregation;

import xyz.marsavic.functions.A0;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.ColorFunction3;
import xyz.marsavic.gfxlab.Matrix;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.gui.UtilsGL;
import xyz.marsavic.resources.Rr;
import xyz.marsavic.time.Profiler;
import xyz.marsavic.utils.Hash;
import xyz.marsavic.utils.OnGC;
import xyz.marsavic.utils.Parallel;


public class AggregatorFrameLast extends Aggregator {
	
	private record IFrameAggregate(int iFrame, Aggregate aggregate) {}
	
	private IFrameAggregate iFrameAggregateLast = null; // Must not be changed outside synchronized blocks.
	private final Profiler profilerLoop = UtilsGL.profiler(this, "add sample");
	private final A0 aStopLoop;
	
	
	
	public AggregatorFrameLast(ColorFunction3 colorFunction3, Vec3 size, boolean repeats, boolean motionBlur, Hash hash) {
		super(colorFunction3, size, repeats, motionBlur, hash);
		
		changeActiveAggregate(0);
		
		aStopLoop = Parallel.daemonLoop(this::addSample);
		OnGC.setOnGC(this, aStopLoop);
	}
	
	
	private synchronized IFrameAggregate changeActiveAggregate(int iFrame) {
		IFrameAggregate iFrameAggregateLastOld = iFrameAggregateLast;
		iFrameAggregateLast = new IFrameAggregate(iFrame, createAggregate(iFrame));
		
		if (iFrameAggregateLastOld != null) {
			iFrameAggregateLastOld.aggregate.release();
		}
		
		return iFrameAggregateLast;
	}
	
	
	@Override
	public Rr<Matrix<Color>> rFrame(int iFrame) {
		IFrameAggregate iFrameAggregate;
		synchronized (this) {
			iFrameAggregate = (iFrame == iFrameAggregateLast.iFrame) ?
					iFrameAggregateLast :
					changeActiveAggregate(iFrame);
		}
		
		return iFrameAggregate.aggregate.avgAtLeastOne();
	}
	
	
	private void addSample() {
		profilerLoop.measure(() -> {
			iFrameAggregateLast.aggregate.addSample();
			fireInvalidated(); // TODO Fire EventInvalidatedFrame instead
		});
	}
	
	
	
	@Override
	public synchronized void release() {
		aStopLoop.at();
		iFrameAggregateLast.aggregate.release();
	}
}
