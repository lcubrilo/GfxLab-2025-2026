package xyz.marsavic.graph;

import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import xyz.marsavic.reactions.elements.Element;

import java.util.Collection;


public class Vertex_Default extends VBox implements Vertex {
	
	public final Element element;
	
	public final VertexHeader vertexHeader;
	public final VertexOutputs vertexOutputs;
	public final VertexInputs vertexInputs;

	
	public Vertex_Default(Element element) {
		this.element = element;
		
		getStyleClass().add("vertex");
		getStyleClass().add("vertex-default");
		
		vertexHeader = new VertexHeader(element);
		vertexOutputs = new VertexOutputs(element);
		vertexInputs = new VertexInputs(element);
		getChildren().addAll(
				vertexHeader,
				vertexOutputs,
				vertexInputs
		);
	}
	
	
	@Override public Element element() { return element; }	
	@Override public Collection<VertexInputJack> inputJacks () { return vertexInputs.inputJacks ; }
	@Override public Collection<VertexOutputJack> outputJacks() { return vertexOutputs.outputJacks; }
	@Override public Region region() { return this; }
	
}
