package xyz.marsavic.graph;

import xyz.marsavic.reactions.elements.Element;


public record Edge(
		Element.Input<?> input,
		Element.Output<?> output
) {
	
}
