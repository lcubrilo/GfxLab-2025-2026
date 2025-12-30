package xyz.marsavic.graph;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import xyz.marsavic.functions.A1;
import xyz.marsavic.reactions.elements.Element;
import xyz.marsavic.reactions.elements.ElementA1;
import xyz.marsavic.reactions.values.EventInvalidated;

import java.util.Collection;
import java.util.List;


public class Vertex_ElementA1 extends VBox implements Vertex {
	
	public final ElementA1<?> element;

	public final VertexHeader vertexHeader;
	public final VertexInputJack vertexInputJack;
	public final List<VertexInputJack> inputJacks;
	
	
	public Vertex_ElementA1(ElementA1<?> element) {
		this.element = element;
	
		getStyleClass().add("vertex");
		getStyleClass().add("vertex-element-a1");
		
		vertexHeader = new VertexHeader(element);
		vertexInputJack = new VertexInputJack(element.in0);
		
		Display display = new Display(element.in0.output());
		
		HBox.setHgrow(vertexHeader, Priority.ALWAYS);
		HBox hBox = new HBox(
				vertexInputJack,
				vertexHeader
		);
		hBox.setAlignment(Pos.CENTER_LEFT);
		
		getChildren().addAll(
				hBox,
				display
		);
		
		inputJacks = List.of(vertexInputJack);
	}
	
	@Override public ElementA1<?> element() { return element; }	
	@Override public Collection<VertexInputJack> inputJacks () { return inputJacks ; }
	@Override public Collection<VertexOutputJack> outputJacks() { return List.of(); }
	@Override public Region region() { return this; }
}


class Display extends Label {
	
	final Element.Output<?> output;
	final A1<EventInvalidated> onInvalidated = this::update;
	
	
	public Display(Element.Output<?> output) {
		this.output = output;
		output.onInvalidated().add(onInvalidated);
	}
	
	private void update(EventInvalidated e) {
		setText(output.get().toString());
	}
}
