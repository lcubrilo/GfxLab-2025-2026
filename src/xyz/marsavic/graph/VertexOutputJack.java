package xyz.marsavic.graph;

import javafx.scene.layout.StackPane;
import xyz.marsavic.reactions.elements.Element;


public class VertexOutputJack extends StackPane {
	
	public final Element.Output<?> output;
	
	public VertexOutputJack(Element.Output<?> output) {		
		this.output = output;
		getStyleClass().add("vertex-output-jack");
	}
	
}
