package xyz.marsavic.graph;

import javafx.scene.layout.VBox;
import xyz.marsavic.reactions.elements.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class VertexInputs extends VBox {
	
	private final List<VertexInputJack> inputJacks_  = new ArrayList<>();
	public final List<VertexInputJack> inputJacks = Collections.unmodifiableList(inputJacks_);
	
	
	public VertexInputs(Element element) {
		getStyleClass().add("vertex-inputs");
		
		for (Element.Input<?> input: element.inputs()) {
			VertexInput vertexInput = new VertexInput(input);
			getChildren().addAll(vertexInput);
			inputJacks_.add(vertexInput.vertexInputJack);
		}
	}
	
}
