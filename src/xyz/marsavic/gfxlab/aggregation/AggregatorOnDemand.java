package xyz.marsavic.gfxlab.aggregation;

import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.ColorFunction3;
import xyz.marsavic.gfxlab.Matrix;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.resources.Rr;
import xyz.marsavic.utils.Hash;


public class AggregatorOnDemand extends Aggregator {
	
	public AggregatorOnDemand(ColorFunction3 colorFunction3, Vec3 size, boolean repeats, boolean motionBlur, Hash hash) {
		super(colorFunction3, size, repeats, motionBlur, hash);
	}

	
	private Aggregate aggregate;
	
	
	@Override
	public Rr<Matrix<Color>> rFrame(int iFrame) {
		if (aggregate != null) {
			aggregate.release();
		}
		
		aggregate = createAggregate(iFrame);
		aggregate.addSample();
		var avg = aggregate.avg();
		aggregate.release();
		return avg;
		
	}
	
	
	@Override
	public void release() {
		if (aggregate != null) {
			aggregate.release();
		}
	}
}
