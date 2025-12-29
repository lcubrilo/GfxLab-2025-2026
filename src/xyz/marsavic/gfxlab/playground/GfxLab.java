package xyz.marsavic.gfxlab.playground;

import xyz.marsavic.elements.ElementF;
import xyz.marsavic.functions.F1;
import xyz.marsavic.gfxlab.Matrix;
import xyz.marsavic.gfxlab.TransformationsFromSize;
import xyz.marsavic.gfxlab.TransformedColorFunction;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.aggregation.AggregatorOneAhead;
import xyz.marsavic.gfxlab.playground.colorfunctions.*;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping2;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping3;
import xyz.marsavic.gfxlab.tonemapping.colortransforms.Identity;
import xyz.marsavic.resources.Rr;
import xyz.marsavic.utils.Hash;

import static xyz.marsavic.elements.ElementF.*;


public class GfxLab {

	public static ElementF<F1<Rr<Matrix<Integer>>, Integer>> setup() {
		return e(setup2D());
	}
	
	
	static F1<Rr<Matrix<Integer>>, Integer> setup2D() {
		var size = Vec3.xyz(640.0, 640.0, 640.0);
		
		var aggregator = new AggregatorOneAhead(
				new TransformedColorFunction(
//						new OkLab()               , new TransformationsFromSize.ToUnitBox  (size)
//						new ColorFunctionExample(), new TransformationsFromSize.ToUnitBox  (size)
//						new Gradient()            , new TransformationsFromSize.ToUnitBox  (size)
//						new ScanLine()            , new TransformationsFromSize.ToIdentity (size)
//						new GammaTest()           , new TransformationsFromSize.ToIdentity (size)
//						new Spirals()             , new TransformationsFromSize.ToGeometric(size)
						// new Blobs(5, 0.1, 0.2)    , new TransformationsFromSize.ToGeometric(size)
//						new Wavy()                , new TransformationsFromSize.ToGeometric(size)
						new Domaci1()              , new TransformationsFromSize.ToIdentity(size)					
),
				size,
				new Hash(0x34EDE7F200EA9AD7L)
		);
		
		
		return new ToneMapping3(
				aggregator::rFrame,
				new ToneMapping2(
					new Identity().asColorTransformFromMatrixColor()
//					new AutoSoft(0x1p-4, 1.0)
				)
		);
	}
	
}
