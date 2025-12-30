package xyz.marsavic.graph;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import xyz.marsavic.reactions.elements.Element;
import xyz.marsavic.reactions.elements.ElementBoolean;

import java.util.Collection;
import java.util.List;


public class Vertex_Boolean extends HBox implements Vertex {
	
	public final ElementBoolean element;
	
	public final VertexOutputJack vertexOutputJack;
	public final List<VertexOutputJack> outputJacks;
	
	
	public Vertex_Boolean(ElementBoolean element) {
		this.element = element;
		
		getStyleClass().add("vertex");
		getStyleClass().add("vertex-boolean");
		
		vertexOutputJack = new VertexOutputJack(element.out);
		
		
		Boolean value = element.out.get();
		
		CheckBox checkBox = new CheckBox();
		checkBox.setSelected(value);
		checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> update(newValue));
		
		
		setAlignment(Pos.CENTER_LEFT);
		getChildren().addAll(
				checkBox,
				vertexOutputJack
		);
		
		
		
		outputJacks = List.of(vertexOutputJack);
	}
	
	private void update(boolean newValue) {
		element.setResult(newValue);
	}
	
	
	@Override public Element element() { return element; }	
	@Override public Collection<VertexInputJack>  inputJacks () { return List.of(); }
	@Override public Collection<VertexOutputJack> outputJacks() { return outputJacks; }
	@Override public Region region() { return this; }
	
}
