package xyz.marsavic.javafx;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import xyz.marsavic.geometry.Box;
import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Array2;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.MatrixColor;
import xyz.marsavic.gfxlab.MatrixInts;
import xyz.marsavic.gfxlab.gui.UtilsGL;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.IntBuffer;


public class UtilsFX {

	// Converters mars-bits <=> JavaFx ==================================================

	public static Vector imageSize(Image image) {
		return Vector.xy(image.getWidth(), image.getHeight());
	}
	
	
	public static Vector toVector(Point2D p) {
		return Vector.xy(p.getX(), p.getY());
	}
	
	
	public static Vector layoutP(Node node) {
		return Vector.xy(node.getLayoutX(), node.getLayoutY());
	}
	
	public static void setLayoutP(Node node, Vector p) {
		node.setLayoutX(p.x());
		node.setLayoutY(p.y());
	}
	
	public static Vector layoutD(Node node) {
		return Vector.xy(node.getLayoutBounds().getWidth(), node.getLayoutBounds().getHeight());
	}
	
	public static Box layoutBox(Region region) {
		return Box.pd(layoutP(region), layoutD(region));
	}
	
	public static void setPrefD(Region region, Vector d) {
		region.setPrefSize(d.x(), d.y());
	}
	

	public static Box toBox(Bounds bounds) {
		return
				Box.pd(
						Vector.xy(bounds.getMinX(), bounds.getMinY()),
						Vector.xy(bounds.getWidth(), bounds.getHeight())
				);
		
	}

	public static Box toBox(Node node) {
		return UtilsFX.toBox(node.localToScene(node.getLayoutBounds()));
	}
	
	
	public static Point2D toPoint2D(Vector p) {
		return new Point2D(p.x(), p.y());
	}
	
	
	// Image utils ======================================================================
	
	
	public static void copyImageToClipboard(Image image) {
		ClipboardContent content = new ClipboardContent();
		content.putImage(image);
		Clipboard.getSystemClipboard().setContent(content);
	}
	
	
	public static WritableImage createWritableImage(Vector size) {
		return new WritableImage(size.xInt(), size.yInt());
	}
	

	public static void saveImageToFileWithDialog(Image image) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Export Image");
		fileChooser.setInitialDirectory(new File("."));
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Image Files", "*.png"),
				new FileChooser.ExtensionFilter("All Files", "*.*")
		);
		File file = fileChooser.showSaveDialog(null);
		
		if (file != null) {
			try {
				ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	
	static final PixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbPreInstance();
	
	public static void writeArray2ToImage(WritableImage outImage, Array2<Integer> inArray2) {
		int sx = inArray2.size().xInt();
		int sy = inArray2.size().yInt();
		
		if (inArray2 instanceof MatrixInts m) {
			outImage.getPixelWriter().setPixels(0, 0, sx, sy, pixelFormat, m.array(), 0, sx);
		} else {
			UtilsGL.matricesInt.obtain(inArray2.size(), true, m -> {
				m.copyFrom(inArray2);
				outImage.getPixelWriter().setPixels(0, 0, sx, sy, pixelFormat, ((MatrixInts) m).array(), 0, sx);
			});
		}
	}
	
	
	public static Image writeArray2ToImage(Array2<Integer> inArray2) {
		WritableImage image = createWritableImage(inArray2.size());
		writeArray2ToImage(image, inArray2);
		return image;
	}
	
	
	public static MatrixColor matrixFromImage(Image img) {
		Vector size = UtilsFX.imageSize(img);
		MatrixColor matrixColor = new MatrixColor(size);
		
		int[] buffer = new int[size.areaInt()];
		
		img.getPixelReader().getPixels(
				0, 0,
				size.xInt(), size.yInt(),
				WritablePixelFormat.getIntArgbPreInstance(),
				buffer,
				0, size.xInt()
		);
		
		UtilsGL.parallel.parallel(size.yInt(), y -> {
			for (int x = 0; x < size.xInt(); x++) {
				int k = y * size.xInt() + x;
				Color c = Color.code(buffer[k]);
				matrixColor.set(x, y, c);
			}
		});
		
		return matrixColor;
	}

	
	public static MatrixColor matrixFromImage(InputStream is) {
		return matrixFromImage(new Image(is));
	}
	
	
	public static MatrixColor matrixFromImage(String fileName) {
		try {
			return matrixFromImage(new FileInputStream(fileName));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
}
