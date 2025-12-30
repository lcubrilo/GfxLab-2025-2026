package xyz.marsavic.graph;

import javafx.scene.control.Label;
import xyz.marsavic.reactions.elements.Element;

public class VertexInputName extends Label {
	
	public VertexInputName(Element.Input<?> input) {
		getStyleClass().add("vertex-input-name");
		setMaxWidth(Double.MAX_VALUE);
		setText(input.name());
	}
	
}
