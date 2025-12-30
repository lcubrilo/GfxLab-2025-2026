package xyz.marsavic.gfxlab;

import xyz.marsavic.reactions.elements.Element;
import xyz.marsavic.reactions.elements.HasOutput;


public class ElementAnimationSink extends Element {
	
	public final Input<? extends Animation> in0;
	
	public ElementAnimationSink(HasOutput<? extends Animation> p0) {
		super("Animation sink");
		in0 = new Input<>(p0);
		
		buildItUpFirstTime();
	}

}