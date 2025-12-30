package xyz.marsavic.gfxlab.gui;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import xyz.marsavic.gfxlab.playground.GfxLab;
import xyz.marsavic.javafx.UtilsFX;
import xyz.marsavic.testing.Testing;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Benchmark {
	
	// - Use AggregatorFixed when benchmarking.
	//      e((f, s, h) -> new AggregatorFixed(f, s, h, 128))
	// - power profile: performance
	// - close browser
	
	private static Image generateImage() {
		var sink = GfxLab.setup();
		
		var rMI = sink.result().at(0);
		Image image = rMI.f(UtilsFX::writeArray2ToImage);
		rMI.release();
		return image;
	}
	
	
	static void main() throws IOException {
		var image = Testing.run(Benchmark::generateImage);		
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
		String directoryPath = "renders/benchmarks";
		Files.createDirectories(Paths.get(directoryPath));
		String fileName = String.format("%s/%s %7.2f.png", directoryPath, timestamp, image.runningTime());
		ImageIO.write(SwingFXUtils.fromFXImage(image.result(), null), "png", new File(fileName));		
	}
	
}
