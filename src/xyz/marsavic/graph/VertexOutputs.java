package xyz.marsavic.graph;

import javafx.scene.layout.VBox;
import xyz.marsavic.reactions.elements.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class VertexOutputs extends VBox {

	private final List<VertexOutputJack> outputJacks_ = new ArrayList<>();
	public final List<VertexOutputJack> outputJacks = Collections.unmodifiableList(outputJacks_);
	
	
	public VertexOutputs(Element element) {
		getStyleClass().add("vertex-outputs");
		
		for (Element.Output<?> output: element.outputs()) {
			VertexOutput vertexOutput = new VertexOutput(output);
			getChildren().addAll(vertexOutput);
			outputJacks_.add(vertexOutput.vertexOutputJack);
		}
	}
	
}
