package xyz.marsavic.gfxlab.playground;

import xyz.marsavic.gfxlab.*;
import xyz.marsavic.gfxlab.aggregation.*;
import xyz.marsavic.gfxlab.graphics3d.raytracing.RayTracerSimple;
import xyz.marsavic.gfxlab.graphics3d.scenes.SceneTest;
import xyz.marsavic.gfxlab.playground.colorfunctions.*;
import xyz.marsavic.gfxlab.tonemapping.ColorTransform;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping2;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping3;
import xyz.marsavic.gfxlab.tonemapping.colortransforms.Identity;
import xyz.marsavic.gfxlab.tonemapping.matrixcolor_to_colortransforms.AutoSoft;
import xyz.marsavic.reactions.elements.ElementF;
import xyz.marsavic.utils.Hash;

import static xyz.marsavic.reactions.elements.ElementF.*;


public class GfxLab {

	public static ElementF<Animation> setup() {
		return setup2D();
	}
	

	private static ElementF<Animation> setup2D() {
		
//		new Blobs(5, 0.1, 0.2, new Hash(0x3B2622FB4358FC00L));
//		e(Blobs.class, e(0.1), e(0.2), e(Hash.class, e(0x3B2622FB4358FC00L)));
		
		return
				e(ToneMapping3.class,
						new EAggregator(
//								e(AggregatorFrameLast::new),
//								e(AggregatorOnDemand::new),
								e(AggregatorOneAhead::new),
								
//	        					e(OkLab.class, e(0.0))                 , e(TransformationFromSize.ToUnitBox_.class),
//	        					e(ColorFunction3Example.class)         , e(TransformationFromSize.ToUnitBox_.class),
//	        					e(Gradient.class)                      , e(TransformationFromSize.ToUnitBox_.class),
//	        					e(ScanLine.class)                      , e(TransformationFromSize.ToIdentity_.class),
//	        					e(GammaTest.class)                     , e(TransformationFromSize.ToIdentity_.class),
//	        					e(Spirals.class, e(7), e(0.25), e(0.4)), e(TransformationFromSize.ToGeometricT0_.class),
//	        					e(Blobs.class, e(5), e(0.1), e(0.2), e(Hash.class, e(0xB182847F9F621EB1L))), e(TransformationFromSize.ToGeometricT0_.class),
//	        					e(RayTracingTest.class)                , e(TransformationFromSize.ToGeometricT0_.class),
								
								e(RayTracerSimple.class,
									e(SceneTest.class, e(0.0), e(15), e(4))
								),
								
								e(TransformationFromSize.ToGeometricT0_.class),
								
								e(Vec3.xyz(640, 640, 640)), // (nFrames, width, height)

								e(true),								
								e(true),								
								e(Hash.class, e(0x8EE6B0C4E02CA7B2L))
						),
						e(ToneMapping2.class,
								e(ColorTransform::asColorTransformFromMatrixColor, e(Identity.class))
//								e(AutoSoft.class, e(0x1p-4), e(1.0))
						)
				);
	}
	
}
