package xyz.marsavic.graph;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import xyz.marsavic.gfxlab.gui.UtilsGL;
import xyz.marsavic.reactions.elements.Element;
import xyz.marsavic.reactions.elements.ElementDouble;

import java.util.Collection;
import java.util.List;


public class Vertex_Double extends HBox implements Vertex {
	
	public final ElementDouble element;
	
	public final VertexOutputJack vertexOutputJack;
	public final List<VertexOutputJack> outputJacks;
	
	private final Label label;

	
	public Vertex_Double(ElementDouble element) {
		this.element = element;
		
		getStyleClass().add("vertex");
		getStyleClass().add("vertex-double");
		
		vertexOutputJack = new VertexOutputJack(element.out);
		label = new Label();
		
		
		Double value = element.out.get();
		
		Slider slider = new Slider(0, 1, value);
		updateUI(value);
		slider.valueProperty().addListener((observable, _oldValue, newValue) -> update(newValue.doubleValue()));
		
		
		setAlignment(Pos.CENTER_LEFT);
		getChildren().addAll(
				slider,
				label,
				vertexOutputJack
		);		
		
		
		outputJacks = List.of(vertexOutputJack);
	}
	
	private void update(double newValue) {
		updateUI(newValue);
		UtilsGL.parallel.submit(() -> element.setResult(newValue));
	}
	
	private void updateUI(double newValue) {
		label.setText(String.format("%.2f", newValue));
	}
	
	@Override public Element element() { return element; }	
	@Override public Collection<VertexInputJack>  inputJacks () { return List.of(); }
	@Override public Collection<VertexOutputJack> outputJacks() { return outputJacks; }
	@Override public Region region() { return this; }
	
}
