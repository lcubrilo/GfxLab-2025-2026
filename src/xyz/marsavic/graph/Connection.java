package xyz.marsavic.graph;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.CubicCurve;

import java.util.ArrayList;
import java.util.List;


public class Connection extends CubicCurve {

	private static final double dControl = 50.0;
	
	public Connection(Node container, VertexInputJack vertexInputJack, VertexOutputJack vertexOutputJack) {
		getStyleClass().add("connection");
		
		DoubleBinding startX = bindCenterXIn(container, vertexOutputJack);
		DoubleBinding startY = bindCenterYIn(container, vertexOutputJack);
		DoubleBinding endX   = bindCenterXIn(container, vertexInputJack);
		DoubleBinding endY   = bindCenterYIn(container, vertexInputJack);

		startXProperty().bind(startX);
		startYProperty().bind(startY);
		endXProperty().bind(endX);
		endYProperty().bind(endY);

		controlX1Property().bind(startX.add(dControl));
		controlY1Property().bind(startY);
		controlX2Property().bind(endX.subtract(dControl));
		controlY2Property().bind(endY);
	}


	private static DoubleBinding bindCenterXIn(Node ancestor, Node jack) {
		Observable[] deps = dependenciesForDragging(ancestor, jack);

		return Bindings.createDoubleBinding(
				() -> {
					if (jack.getScene() == null || ancestor.getScene() == null) return 0.0;
					Bounds bLocal = jack.getLayoutBounds();
					Point2D centerLocal = new Point2D(bLocal.getCenterX(), bLocal.getCenterY());
					Point2D pScene = jack.localToScene(centerLocal);
					Point2D pTarget = ancestor.sceneToLocal(pScene);
					return pTarget == null ? 0.0 : pTarget.getX();
				},
				deps
		);
	}


	private static DoubleBinding bindCenterYIn(Node ancestor, Node jack) {
		Observable[] deps = dependenciesForDragging(ancestor, jack);

		return Bindings.createDoubleBinding(
				() -> {
					if (jack.getScene() == null || ancestor.getScene() == null) return 0.0;
					Bounds bLocal = jack.getLayoutBounds();
					Point2D centerLocal = new Point2D(bLocal.getCenterX(), bLocal.getCenterY());
					Point2D pScene = jack.localToScene(centerLocal);
					Point2D pTarget = ancestor.sceneToLocal(pScene);
					return pTarget == null ? 0.0 : pTarget.getY();
				},
				deps
		);
	}


	private static Observable[] dependenciesForDragging(Node ancestor, Node jack) {
		List<Observable> deps = new ArrayList<>();

		Node node = jack;
		// We track dependencies up to the scene, because 'ancestor' (paneConnections)
		// and 'jack' (inside paneVertices) are in different branches of the tree.
		while (node != null) {
			deps.add(node.layoutXProperty());
			deps.add(node.layoutYProperty());
			deps.add(node.translateXProperty());
			deps.add(node.translateYProperty());
			deps.add(node.layoutBoundsProperty());
			deps.add(node.localToParentTransformProperty());
			deps.add(node.sceneProperty()); // Trigger when added to scene
			node = node.getParent();
		}

		// Also track the ancestor's position in the scene
		Node anc = ancestor;
		while (anc != null) {
			deps.add(anc.localToParentTransformProperty());
			anc = anc.getParent();
		}

		return deps.toArray(Observable[]::new);
	}
}
