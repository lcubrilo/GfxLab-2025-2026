package xyz.marsavic.graph;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import xyz.marsavic.reactions.elements.ElementF;

import java.util.Collection;
import java.util.List;


public class Vertex_ElementF extends VBox implements Vertex {
	
	public final ElementF<?> element;
	
	public final VertexHeader vertexHeader;
	public final VertexOutputJack vertexOutputJack;
	public final VertexInputs vertexInputs;
	public final List<VertexOutputJack> outputJacks;
	
	
	public Vertex_ElementF(ElementF<?> element) {
		this.element = element;
		
		getStyleClass().add("vertex");
		getStyleClass().add("vertex-element-f");
		
		vertexHeader = new VertexHeader(element);
		vertexOutputJack = new VertexOutputJack(element.out);
		vertexInputs = new VertexInputs(element);
		
		HBox.setHgrow(vertexHeader, Priority.ALWAYS);
		HBox hBox = new HBox(
				vertexHeader,
				vertexOutputJack
		);
		hBox.setAlignment(Pos.CENTER_LEFT);
		
		getChildren().addAll(
				hBox,
				vertexInputs
		);
		
		outputJacks = List.of(vertexOutputJack);
	}

	@Override public ElementF<?> element() { return element; }	
	@Override public Collection<VertexInputJack> inputJacks () { return vertexInputs.inputJacks ; }
	@Override public Collection<VertexOutputJack> outputJacks() { return outputJacks; }
	@Override public Region region() { return this; }
}
