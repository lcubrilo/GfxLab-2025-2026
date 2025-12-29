package xyz.marsavic.gfxlab.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import xyz.marsavic.functions.A0;
import xyz.marsavic.gfxlab.gui.instruments.InstrumentRenderer;
import xyz.marsavic.gfxlab.playground.GfxLab;
import xyz.marsavic.gfxlab.resources.Resources;
import xyz.marsavic.objectinstruments.Panel;
import xyz.marsavic.objectinstruments.instruments.InstrumentText;


public class App extends Application {
	
	static {
		System.setProperty("prism.forceGPU", "true");
	}
	
	
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("GFX Lab");
		
		Panel panelR = new Panel();
		var sink = GfxLab.setup();
		
		panelR.addInstruments(
				new InstrumentRenderer(sink.out()),
				new InstrumentText(() -> Profiling.infoTextSystem() + Profiling.infoTextProfilers(), 230)
		);
		
		Scene scene = new Scene(panelR.region(), 654, 960);
		scene.getStylesheets().setAll(Resources.stylesheetURL);
		
		primaryStage.setScene(scene);

		
		primaryStage.setFullScreenExitHint("");
		
		primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			A0 action = switch (event.getCode()) {
				case ESCAPE -> Platform::exit;
				case F11 -> () -> primaryStage.setFullScreen(!primaryStage.isFullScreen());
				default -> A0.NOOP;
			};
			
			action.execute();
		});

		primaryStage.sizeToScene();
		primaryStage.show();
	}

	public static void main(){
		Application.launch(App.class);
	}
}
