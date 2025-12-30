package xyz.marsavic.graph;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import xyz.marsavic.geometry.Box;
import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.ElementAnimationSink;
import xyz.marsavic.javafx.UtilsFX;
import xyz.marsavic.reactions.elements.*;

import java.util.*;


public class Graph extends Pane {
	// ---
	private final Map<Element, Vertex> map_element_vertex_ = new HashMap<>();
	private final List<Edge> edges_ = new ArrayList<>();
	private final Map<Element.Input <?>, VertexInputJack > map_input_jack_  = new HashMap<>();
	private final Map<Element.Output<?>, VertexOutputJack> map_output_jack_ = new HashMap<>();
	
	private final List<Vertex> vertices_ = new ArrayList<>();
	private final List<Connection> connections_ = new ArrayList<>();

	// ---
	
	public final Map<Element, Vertex> map_element_vertex = Collections.unmodifiableMap(map_element_vertex_);
	public final List<Edge> edges = Collections.unmodifiableList(edges_);
	public final Map<Element.Input <?>, VertexInputJack > map_input_jack  = Collections.unmodifiableMap(map_input_jack_ );
	public final Map<Element.Output<?>, VertexOutputJack> map_output_jack = Collections.unmodifiableMap(map_output_jack_);
	
	public final List<Vertex> vertices = Collections.unmodifiableList(vertices_);
	public final List<Connection> connections = Collections.unmodifiableList(connections_);
	
	// ---
	
	private final Pane paneConnections = new Pane();
	private final Pane paneVertices    = new Pane();

	private boolean panning;
	private Vector panPress, panStartTranslate;

	

	public Graph() {
		getChildren().addAll(paneConnections, paneVertices);
		paneConnections.setMouseTransparent(true);
		setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		setPickOnBounds(true); // allows dragging even when clicking "empty" background inside the Pane bounds
		installBackgroundPanning();

		// Create a clipping rectangle
		Rectangle clip = new Rectangle();
		clip.widthProperty().bind(widthProperty());
		clip.heightProperty().bind(heightProperty());
		setClip(clip);
	}

	
	private Vertex createVertexByType(Element e) {
		if (e instanceof ElementAnimationSink e_) return new Vertex_AnimationSink(e_);
		if (e instanceof ElementDouble        e_) return new Vertex_Double       (e_);
		if (e instanceof ElementInteger       e_) return new Vertex_Integer      (e_);
		if (e instanceof ElementLong          e_) return new Vertex_Long         (e_);
		if (e instanceof ElementBoolean       e_) return new Vertex_Boolean      (e_);
		if (e instanceof ElementF<?>          e_) return new Vertex_ElementF     (e_);
		if (e instanceof ElementA1<?>         e_) return new Vertex_ElementA1    (e_);
		if (e instanceof Element              e_) return new Vertex_Default      (e_);
		return null;
	}
	

	public Vertex createVertex(Element e) {
		Vertex existing = map_element_vertex.get(e);
		if (existing != null) {
			return existing;
		}
		
		Vertex vertex = createVertexByType(e);
		
		vertices_.add(vertex);
		map_element_vertex_.put(e, vertex);
		paneVertices.getChildren().add(vertex.region());

		for (VertexInputJack vertexInputJack : vertex.inputJacks()) {
			map_input_jack_.put(vertexInputJack.input, vertexInputJack);
		}

		for (VertexOutputJack vertexOutputJack : vertex.outputJacks()) {
			map_output_jack_.put(vertexOutputJack.output, vertexOutputJack);
		}
		
		enableNodeDragging(vertex.region());
		
		return vertex;
	}




	public void createConnection(Element.Input<?> input, Element.Output<?> output) {
		Edge e = new Edge(input, output);
		edges_.add(e);
		
		VertexInputJack vertexInputJack = map_input_jack.get(input);
		VertexOutputJack vertexOutputJack = map_output_jack.get(output);

		Connection c = new Connection(paneConnections, vertexInputJack, vertexOutputJack);
		connections_.add(c);
		paneConnections.getChildren().add(c);
	}
	

	public void layItOut() {
		Map<Vertex, Vector> p = new HashMap<>();
		
		int i = 0;

		while (i < 10000) {
			vertices.forEach(v -> p.put(v, UtilsFX.toBox(v.region()).p()));
			
			double k = 1 + 8.0 / (i + 1);
			jiggle(k);
			i++;
			
			double dMax = 0;
			for (Vertex v : vertices) {
				double distance = UtilsFX.toBox(v.region()).p().distanceTo(p.get(v));
				dMax = Math.max(dMax, distance);
			}
			
			if (dMax <= 0.01) break;
		}

		for (Vertex v: vertices) {
			UtilsFX.setLayoutP(v.region(), UtilsFX.layoutP(v.region()).round());
		}
		
		centerContent();
		
/*
		System.out.println("Jiggles: " + i);
		vertices.forEach(v -> p.put(n, box(v).p()));
		jiggle(1);
		double dMax = 0;
		for (Vertex v : vertices) {
			double distance = box(v).p().distanceTo(p.get(v));
			dMax = Math.max(dMax, distance);
		}
		System.out.println("Still jiggling by " + dMax);
*/
	}
	
	
	private void jiggle(double magnitude) {
		double dConnection = 40;
		Vector margin = Vector.xy(20, 20);
		
		Map<Vertex, Vector> forces = new HashMap<>();
		
		for (Edge edge : edges) {
			Vertex ni = map_element_vertex.get(edge.input ().element());
			Vertex no = map_element_vertex.get(edge.output().element());
//			VertexInputJack ji = map_input_jack.get(edge.input ());
//			VertexOutputJack jo = map_output_jack.get(edge.output());
			
//			Vector ci = box(ji).c();
//			Vector co = box(jo).c();
			
			Box bi = UtilsFX.toBox(ni.region());
			Box bo = UtilsFX.toBox(no.region());
			
			Vector ci = Vector.lerp(bi.cornerPP(), bi.cornerPQ(), 0.5);
			Vector co = Vector.lerp(bo.cornerQP(), bo.cornerQQ(), 0.5);
			
			Vector d = ci.sub(co).div(dConnection).sub(Vector.xy(1, 0)).mul(1.0/3);
			
			forces.merge(ni, d.inverse(), Vector::add);
			forces.merge(no, d, Vector::add);
		}
		
		for (Vertex v0 : vertices) {
			for (Vertex v1 : vertices) {
				if (v0 == v1) continue;
				Box b0 = UtilsFX.toBox(v0.region()).extend(margin);
				Box b1 = UtilsFX.toBox(v1.region()).extend(margin);
				Vector d = b0.minOffsetToAvoidOverlap(b1).div(margin).mul(3);
				forces.merge(v0, d, Vector::add);
			}
		}
		
		for (Vertex v : vertices) {
			Vector f = forces.get(v);
			double l = f.lengthSquared();
			translate(v.region(), f.mul(magnitude / (l + 1)));
		}
	}
    
	
	private void setTranslate(Vector t) {
		t = t.round();
		paneVertices.setTranslateX(t.x());
		paneVertices.setTranslateY(t.y());
		paneConnections.setTranslateX(t.x());
		paneConnections.setTranslateY(t.y());
	}
	
	
	public void centerContent() {
        if (vertices.isEmpty()) return;
        
    	Box b = UtilsFX.layoutBox(vertices.getFirst().region());
		
        for (Vertex v : vertices) {
			Box b_ = UtilsFX.layoutBox(v.region());
			b = Box.bounding(b.cornerPP(), b.cornerQQ(), b_.cornerPP(), b_.cornerQQ());
        }

		Box g = UtilsFX.layoutBox(this);
		Vector t = g.c().sub(b.c());
		setTranslate(t);
    }

	
	
	private void installBackgroundPanning() {
		addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
			if (e.getButton() != MouseButton.PRIMARY) return;

			if (isInsideVertex(e.getTarget())) return;

			panning = true;
			panPress = Vector.xy(e.getSceneX(), e.getSceneY());
			panStartTranslate = Vector.xy(paneVertices.getTranslateX(), paneVertices.getTranslateY());
			setCursor(Cursor.MOVE);
			e.consume();
		});

		addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
			if (!panning) return;

			Vector sceneP = Vector.xy(e.getSceneX(), e.getSceneY());
			Vector d = sceneP.sub(panPress);
			setTranslate(panStartTranslate.add(d));

			e.consume();
		});

		addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
			if (!panning) return;
			panning = false;
			setCursor(Cursor.DEFAULT);
			e.consume();
		});
	}

	

//	public static Box box(Region region) {
//		Point2D p = region.localToScene(0, 0);
//		return
//				Box.pd(
//						Vector.xy(p.getX(), p.getY()),
//						Vector.xy(region.getWidth  (), region.getHeight  ())
//				);
//	}
//	
	
	public static void translate(Node n, Vector o) {
		UtilsFX.setLayoutP(n, UtilsFX.layoutP(n).add(o));
	}
	

	private static boolean isInsideVertex(Object eventTarget) {
		if (!(eventTarget instanceof Node n)) return false;

		Parent p = n instanceof Parent ? (Parent) n : n.getParent();
		while (p != null) {
			if (p instanceof Vertex) {
				return true;
			}
			p = p.getParent();
		}
		return false;
	}


	static void enableNodeDragging(Node node) {
		final double[] start = new double[2];
	
		node.setOnMousePressed(e -> {
			node.toFront();
			start[0] = e.getSceneX() - node.getLayoutX();
			start[1] = e.getSceneY() - node.getLayoutY();
		});
	
		node.setOnMouseDragged(e -> {
			node.setLayoutX(e.getSceneX() - start[0]);
			node.setLayoutY(e.getSceneY() - start[1]);
		});
	}	
	
}
