package xyz.marsavic.graph;

import javafx.geometry.Pos;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;
import xyz.marsavic.reactions.elements.Element;
import xyz.marsavic.reactions.elements.ElementLong;

import java.util.Collection;
import java.util.List;


public class Vertex_Long extends HBox implements Vertex {
	
	public final ElementLong element;
	
	public final VertexOutputJack vertexOutputJack;
	public final List<VertexOutputJack> outputJacks;
	
	
	public Vertex_Long(ElementLong element) {
		this.element = element;
		
		getStyleClass().add("vertex");
		getStyleClass().add("vertex-long");
		
		vertexOutputJack = new VertexOutputJack(element.out);
		Spinner<Long> spinner = new Spinner<>(new LongSpinnerValueFactory(Long.MIN_VALUE, Long.MAX_VALUE, 0, 1));
		spinner.getValueFactory().setConverter(new LongStringConverter());
		spinner.setPrefWidth(180);
		
		
		Long value = element.out.get();
		spinner.getValueFactory().setValue(value);
		spinner.valueProperty().addListener((observable, oldValue, newValue) -> update(newValue));
		
		
		setAlignment(Pos.CENTER_LEFT);
		getChildren().addAll(
				spinner,
				vertexOutputJack
		);		
		
		
		outputJacks = List.of(vertexOutputJack);
	}
	
	private void update(long newValue) {
		element.setResult(newValue);
	}
	
	
	@Override public Element element() { return element; }	
	@Override public Collection<VertexInputJack>  inputJacks () { return List.of(); }
	@Override public Collection<VertexOutputJack> outputJacks() { return outputJacks; }
	@Override public Region region() { return this; }
	
}


class LongStringConverter extends StringConverter<Long> {
	
	@Override
	public String toString(Long value) {
		if (value == null) {
			return null;
		}
		return String.format("%016X", value);
	}
	
	@Override
	public Long fromString(String string) {
		if (string == null) {
			return null;
		}
		try {
			return Long.parseUnsignedLong(string.trim(), 16);
		} catch (NumberFormatException e) {
			return 0L;
		}
	}
	
}


class LongSpinnerValueFactory extends SpinnerValueFactory<Long> {

	private final long min;
	private final long max;
	private final long step;

	public LongSpinnerValueFactory(long min, long max, long initialValue, long step) {
		this.min = min;
		this.max = max;
		this.step = step;

		setValue(clamp(initialValue));
	}

	private long clamp(long value) {
		return Math.min(max, Math.max(min, value));
	}

	@Override
	public void decrement(int steps) {
		if (getValue() == null) return;
		setValue(clamp(getValue() - step * steps));
	}

	@Override
	public void increment(int steps) {
		if (getValue() == null) return;
		setValue(clamp(getValue() + step * steps));
	}
}
