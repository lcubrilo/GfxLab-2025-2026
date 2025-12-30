package xyz.marsavic.graph;

import javafx.scene.control.Label;
import xyz.marsavic.reactions.elements.Element;

public class VertexHeader extends Label {
	
	public VertexHeader(Element element) {
		getStyleClass().add("vertex-header");
		setMaxWidth(Double.MAX_VALUE);
		setText(element.name());
	}
}
