package xyz.marsavic.graph;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import xyz.marsavic.reactions.elements.Element;


public class VertexOutput extends HBox {
	public final Element.Output<?> output;
	
	public final VertexOutputJack vertexOutputJack;
	public final VertexOutputName vertexOutputName;
	
	
	public VertexOutput(Element.Output<?> output) {
		this.output = output;
		getStyleClass().add("vertex-output");
		
		setAlignment(Pos.CENTER_LEFT);
		
		vertexOutputJack = new VertexOutputJack(output);
		vertexOutputName = new VertexOutputName(output);
		
		HBox.setHgrow(vertexOutputName, Priority.ALWAYS);
		
		getChildren().addAll(
				vertexOutputName,
				vertexOutputJack
		);
		
		
	}
}

