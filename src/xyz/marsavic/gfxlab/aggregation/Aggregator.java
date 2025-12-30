package xyz.marsavic.gfxlab.aggregation;

import xyz.marsavic.functions.F5;
import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.ColorFunction3;
import xyz.marsavic.gfxlab.Matrix;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.reactions.elements.Invalidatable;
import xyz.marsavic.resources.Rr;
import xyz.marsavic.utils.Hash;


public abstract class Aggregator extends Invalidatable.Base {
	
	protected abstract Rr<Matrix<Color>> rFrame(int iFrame);
	
	/** Call to this method signals that this aggregator will no longer be used, so that the acquired resources can be released. */
	public abstract void release();
	
	
	
	private final ColorFunction3 colorFunction3;
	private final int nFrames;
	private final Vector sizeFrame;
	private final boolean repeats;
	private final boolean motionBlur;
	private final Hash hash;
	
	
	protected Aggregator(ColorFunction3 colorFunction3, Vec3 size, boolean repeats, boolean motionBlur, Hash hash) {
		this.colorFunction3 = colorFunction3;
		this.nFrames = (int) size.x();
		this.sizeFrame = size.p12();
		this.repeats = repeats;
		this.motionBlur = motionBlur;
		this.hash = hash;
	}

	
	public interface F_Aggregator extends F5<Aggregator, ColorFunction3, Vec3, Boolean, Boolean, Hash> {
		@Override
		Aggregator at(ColorFunction3 colorFunction3, Vec3 size, Boolean repeats, Boolean motionBlur, Hash hash);
	}
	
	
	
	protected int nFrames() {
		return nFrames;
	}
	
	public Rr<Matrix<Color>> rFrameAt(int tFrame) {
		return rFrame(iFrame(tFrame));
	}
	
	
	protected int iFrame(int iFrame) {
		return repeats ? Math.floorMod(iFrame, nFrames) : iFrame;
	}
	
	protected Aggregate createAggregate(int iFrame) {
		ColorFunction3 cf3 = motionBlur ?
				colorFunction3 :
				colorFunction3.sliceAt(iFrame).asColorFunction3();
		
		return new Aggregate(
				cf3,
				iFrame,
				sizeFrame,
				hash.add(iFrame)
		);
	}
	
}
