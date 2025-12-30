package xyz.marsavic.graph;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import xyz.marsavic.reactions.elements.Element;


public class VertexInput extends HBox {
	public final Element.Input<?> input;
	
	public final VertexInputJack vertexInputJack;
	public final VertexInputName vertexInputName;
	
	
	public VertexInput(Element.Input<?> input) {
		this.input = input;
		getStyleClass().add("vertex-input");

		setAlignment(Pos.CENTER_LEFT);

		vertexInputJack = new VertexInputJack(input);
		vertexInputName = new VertexInputName(input);

		HBox.setHgrow(vertexInputName, Priority.ALWAYS);

		getChildren().addAll(
				vertexInputJack,
				vertexInputName
		);
	}
}

