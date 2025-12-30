package xyz.marsavic.gfxlab.tonemapping;

import xyz.marsavic.functions.F1;
import xyz.marsavic.gfxlab.Animation;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Matrix;
import xyz.marsavic.resources.Br;
import xyz.marsavic.resources.Rr;


public record ToneMapping3(
		F1<Rr<Matrix<Color>>, Integer> frFrame,
		F1<Rr<Matrix<Integer>>, Br<Matrix<Color>>> frToneMapping2
) implements Animation {
	
	@Override
	public Rr<Matrix<Integer>> at(Integer iFrame) {
		Rr<Matrix<Color>> rFrame = frFrame.at(iFrame);
		Rr<Matrix<Integer>> res = frToneMapping2.at(rFrame.borrow());
		rFrame.release();
		return res;
	}
	
}
