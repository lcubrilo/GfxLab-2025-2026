package xyz.marsavic.graph;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;
import xyz.marsavic.functions.A1;
import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.ElementAnimationSink;
import xyz.marsavic.gfxlab.Matrix;
import xyz.marsavic.gfxlab.gui.UtilsGL;
import xyz.marsavic.gfxlab.resources.Resources;
import xyz.marsavic.javafx.UtilsFX;
import xyz.marsavic.reactions.Dispatcher;
import xyz.marsavic.reactions.Event;
import xyz.marsavic.reactions.Reactions;
import xyz.marsavic.reactions.values.EventInvalidated;
import xyz.marsavic.resources.BorrowManagerMap;
import xyz.marsavic.resources.Rr;
import xyz.marsavic.time.Profiler;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;


@SuppressWarnings("FieldCanBeLocal")
public class Vertex_AnimationSink extends VBox implements Vertex {
	
	public final ElementAnimationSink element;

	public final VertexHeader vertexHeader;
	public final VertexInputJack vertexInputJack;
	public final List<VertexInputJack> inputJacks;

	final A1<EventInvalidated> onInvalidated = this::update;
	
	private final ImageView imageView;
	private final CheckBox chbEnabled;
	private final Spinner<Integer> spnIFrame;
	private final Button btnCopyToClipboard;
	private final Button btnSaveImage;
	private final Label lblInfo;
	
	
	
	public Vertex_AnimationSink(ElementAnimationSink element) {
		this.element = element;
	
		getStyleClass().add("vertex");
		getStyleClass().add("vertex-animation-sink");
		
		vertexHeader = new VertexHeader(element);
		vertexInputJack = new VertexInputJack(element.in0);
		
		HBox.setHgrow(vertexHeader, Priority.ALWAYS);
		HBox hBox = new HBox(
				vertexInputJack,
				vertexHeader
		);
		hBox.setAlignment(Pos.CENTER_LEFT);
		

		// ------
		
		imageView = new ImageView();
		chbEnabled = new CheckBox();
		chbEnabled.setSelected(true);
		spnIFrame = new Spinner<>();
		btnCopyToClipboard = new Button(null, new FontIcon(Resources.Ikons.COPY));
		btnSaveImage = new Button(null, new FontIcon(Resources.Ikons.SAVE));
		Pane separator = new Pane();
		lblInfo = new Label();
		
		spnIFrame.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, Integer.MAX_VALUE));
		spnIFrame.getValueFactory().setValue(-1);
		
		btnCopyToClipboard.setOnAction(event -> UtilsFX.copyImageToClipboard(image()));
		btnSaveImage.setOnAction(event -> UtilsFX.saveImageToFileWithDialog(image()));
		
		HBox controlPanel = new HBox(
				chbEnabled,
				spnIFrame,
				btnCopyToClipboard,
				btnSaveImage,
				separator,
				lblInfo
		);
		HBox.setHgrow(separator, Priority.ALWAYS);

		controlPanel.setAlignment(Pos.CENTER_LEFT);
		controlPanel.setPadding(new Insets(8.0));
		controlPanel.setSpacing(8.0);
//		controlPanel.setPrefHeight(24);
		
		// ------
		
		
		getChildren().addAll(
				hBox,
				controlPanel,
				imageView
		);
		
		inputJacks = List.of(vertexInputJack);
		
		element.in0.output().onInvalidated().add(onInvalidated);
		animationTimer.start();
	}
	
	AnimationTimer animationTimer = new AnimationTimer() {
		@Override public void handle(long now) {
			update();
		}
	};
	
	
	private Future<?> future = null;
	private final Profiler profilerUpdate = UtilsGL.profiler(this, "update");
	
	public synchronized void update() {
		profilerUpdate.measure(() -> {
			Platform.runLater(() -> {
				lblInfo.setText(String.format("%.1f", profilerFetch.eventsPerSecond()));
			});
			
			if (!chbEnabled.isSelected()) {
				return;
			}
			if (future != null && future.isDone()) {
				future = null;
			}
			if (future == null) {
				int iFrame = spnIFrame.getValue();
				int iFrame_ = iFrame == -1 ? iFrameNext++ : iFrame;				
				future = UtilsGL.parallel.submit(() -> fetch(iFrame_));
			}
		});
	}

	

	private final Profiler profilerFetch = UtilsGL.profiler(this, "fetch");
	private final BorrowManagerMap<WritableImage, Vector> images = new BorrowManagerMap<>(UtilsFX::createWritableImage, null);
	private int iFrameNext = 0;
	private boolean hasUpdate = false;
	
	private Vector size2old = Vector.ZERO;
	
	private void fetch(int iFrame) {
		profilerFetch.measure(() -> {
			Rr<Matrix<Integer>> rMI = element.in0.get().at(iFrame);
			
			rMI.a(mI -> {
				Rr<WritableImage> rImage = images.obtain(mI.size(), true);
				rImage.a(image -> UtilsFX.writeArray2ToImage(image, mI));
					Platform.runLater(() -> {
						rImage.a(imageView::setImage);
						rImage.release();
						Vector size2new = UtilsFX.imageSize(imageView.getImage());
						if (!Objects.equals(size2new, size2old)) {
							size2old = size2new;
							fireResized();							
						}
					});
			});
			
			rMI.release();
		});
		hasUpdate = false;
	}
	
	private Image image() {
		return imageView.getImage();
	}
	
	
	private void update(EventInvalidated e) {
		hasUpdate = true;
	}


	@Override public ElementAnimationSink element() { return element; }	
	@Override public Collection<VertexInputJack> inputJacks () { return inputJacks ; }
	@Override public Collection<VertexOutputJack> outputJacks() { return List.of(); }
	@Override public Region region() { return this; }
	
	
	
	public static class EventResized implements Event {}
	
	public final Dispatcher<EventResized> dispatcherResized = new Dispatcher<>();
	public Reactions<EventResized> onResized() { return dispatcherResized.reactions(); }
	public void fireResized() { dispatcherResized.fire(new EventResized()); }
	
}
