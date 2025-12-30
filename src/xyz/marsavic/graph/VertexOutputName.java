package xyz.marsavic.graph;

import javafx.scene.control.Label;
import xyz.marsavic.reactions.elements.Element;


public class VertexOutputName extends Label {
	public VertexOutputName(Element.Output<?> output) {
		getStyleClass().add("vertex-output-name");
		setMaxWidth(Double.MAX_VALUE);
		setText(output.name());
	}
}
