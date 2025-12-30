package xyz.marsavic.graph;

import javafx.scene.layout.Region;
import xyz.marsavic.reactions.elements.Element;

import java.util.Collection;


public interface Vertex {
	Element element();
	Collection<VertexInputJack > inputJacks ();
	Collection<VertexOutputJack> outputJacks();
	Region region();
}
