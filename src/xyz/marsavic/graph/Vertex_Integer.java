package xyz.marsavic.graph;

import javafx.geometry.Pos;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import xyz.marsavic.reactions.elements.Element;
import xyz.marsavic.reactions.elements.ElementInteger;

import java.util.Collection;
import java.util.List;


public class Vertex_Integer extends HBox implements Vertex {
	
	public final ElementInteger element;
	
	public final VertexOutputJack vertexOutputJack;
	public final List<VertexOutputJack> outputJacks;
	
	
	public Vertex_Integer(ElementInteger element) {
		this.element = element;
		
		getStyleClass().add("vertex");
		getStyleClass().add("vertex-integer");
		
		vertexOutputJack = new VertexOutputJack(element.out);
		Spinner<Integer> spinner = new Spinner<>(Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
		
		Integer value = element.out.get();
		spinner.getValueFactory().setValue(value);
		
		spinner.valueProperty().addListener((observable, oldValue, newValue) -> update(newValue));
		
		
		setAlignment(Pos.CENTER_LEFT);
		getChildren().addAll(
				spinner,
				vertexOutputJack
		);
		
		
		
		outputJacks = List.of(vertexOutputJack);
	}
	
	private void update(int newValue) {
		element.setResult(newValue);
	}
	
	
	@Override public Element element() { return element; }	
	@Override public Collection<VertexInputJack>  inputJacks () { return List.of(); }
	@Override public Collection<VertexOutputJack> outputJacks() { return outputJacks; }
	@Override public Region region() { return this; }
	
}
