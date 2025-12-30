package xyz.marsavic.gfxlab.gui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import xyz.marsavic.functions.A0;
import xyz.marsavic.functions.A1;
import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.ElementAnimationSink;
import xyz.marsavic.graph.Graph;
import xyz.marsavic.graph.Vertex;
import xyz.marsavic.graph.VertexInputJack;
import xyz.marsavic.gfxlab.playground.GfxLab;
import xyz.marsavic.gfxlab.resources.Resources;
import xyz.marsavic.graph.Vertex_AnimationSink;
import xyz.marsavic.javafx.UtilsFX;
import xyz.marsavic.reactions.elements.Element;


public class App extends Application {

	static {
		System.setProperty("prism.forceGPU=true", "true");
	}
	
	Stage primaryStage;
	Graph graph;
	Region sidePanel;
	TextArea textArea;
	
	
	
	AnimationTimer animationTimer = new AnimationTimer() {
//		int i = 0;
		@Override
		public void handle(long now) {
//			textArea.setText(Profiling.infoTextSystem() + Profiling.infoTextProfilers());
/*
			i++;
			if ((i % 20) == 0) {				
				primaryStage.getScene().getStylesheets().setAll("file:resources/xyz/marsavic/gfxlab/resources/mars-dark2.css");
			}
*/
		}
	};
	
	
	
	Vector addElements(Graph graph, Element e, Vector p) {
		Vertex vertex = graph.createVertex(e);
		
		Vector s = p; 
		for (VertexInputJack inputJack : vertex.inputJacks()) {
			Element.Input<?> input = inputJack.input;
			Element.Output<?> output = input.output();
			Element eChild = output.element();
			s = addElements(graph, eChild, Vector.xy(p.x() - 220, s.y()));
			graph.createConnection(input, output);
		}

		vertex.region().setLayoutX(p.x());
		vertex.region().setLayoutY(p.y());
		
		return Vector.xy(p.x(), s.y() + 60);
	}
	
	
	void initGraph(Graph graph) {
		var sink = new ElementAnimationSink(GfxLab.setup());		
/*
		var sink = new ElementA1<>(
				e(Double::sum,
						e(Math::min, 
							e(0.23), e(0.45)
						),
						e(Math::max, 
							e(0.23), e(0.45)
						)
				)
		);
*/
		
		addElements(graph, sink, Vector.ZERO);
	}
	
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		
		primaryStage.setTitle("GFX Lab");
		
		graph = new Graph();
		initGraph(graph);
		
//		textArea = new TextArea();
//		textArea.setEditable(false);
//		sidePanel = new VBox(
//				textArea
//		);
//		textArea.setPrefHeight(500);
//		sidePanel.setMinWidth(700);
		
		
		Pane root = new HBox(
				graph
//				rightPane
		);
		Scene scene = new Scene(root, 1800, 1000);
		HBox.setHgrow(graph, Priority.ALWAYS);
		
		
		scene.getStylesheets().setAll(Resources.stylesheetURL);
		primaryStage.getIcons().setAll(Resources.iconsApplication());

		primaryStage.setFullScreenExitHint("");
		
		primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			A0 action = switch (event.getCode()) {
				case ESCAPE -> Platform::exit;
				case F11 -> () -> Platform.runLater(() -> primaryStage.setFullScreen(!primaryStage.isFullScreen()));
				case F1 -> () -> Platform.runLater(() -> graph.layItOut());
				default -> A0.NOOP;
			};
			
			action.at();
		});

		primaryStage.setScene(scene);
		primaryStage.show();
		
		Platform.runLater(() -> graph.layItOut());
		
		for (Vertex v : graph.vertices) {
			if (v instanceof Vertex_AnimationSink vas) {
				vas.onResized().add(onResized);
			}
		}
		
		
//		System.err.println("Using animation timer for CSS auto reload. Remember to remove");
		animationTimer.start();		
	}
	
	
	private final A1<Vertex_AnimationSink.EventResized> onResized = this::onResized;
	
	private void onResized(Vertex_AnimationSink.EventResized eventResized) {
		Platform.runLater(() -> graph.layItOut());
	}
	
	
	static void main() {
		launch();
	}
	
	
}
