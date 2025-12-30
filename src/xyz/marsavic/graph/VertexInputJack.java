package xyz.marsavic.graph;

import javafx.scene.layout.StackPane;
import xyz.marsavic.reactions.elements.Element;


public class VertexInputJack extends StackPane {
	
	public final Element.Input<?> input;
	
	public VertexInputJack(Element.Input<?> input) {
		this.input = input;
		getStyleClass().add("vertex-input-jack");
	}
	
}
