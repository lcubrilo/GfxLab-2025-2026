package xyz.marsavic.gfxlab;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.gui.UtilsGL;


public class MatrixColor implements Matrix<Color> {
	
	private final Vector size;
	private final double [][][] data;
	
	
	public MatrixColor(Vector size, Color initialValue) {
		this.size = size.floor();
		data = new double[this.size.yInt()][this.size.xInt()][3];
		
		if (initialValue != null) {
			fill(initialValue);
		}
	}
	
	
	public MatrixColor(Vector size) {
		this(size, null);
	}
	
	
	@Override
	public Vector size() {
		return size;
	}
	
	
	@Override
	public Color at(int x, int y) {
		return Color.rgb(data[y][x][0], data[y][x][1], data[y][x][2]);
	}
	
	
	@Override
	public void set(int x, int y, Color value) {
		data[y][x][0] = value.r;
		data[y][x][1] = value.g;
		data[y][x][2] = value.b;
	}
	
	
	public void fillBlack() {
		UtilsGL.parallel.parallelY(size, y -> {
			int w = data[y].length;
			for (int x = 0; x < w; x++) {
				data[y][x][0] = 0;
				data[y][x][1] = 0;
				data[y][x][2] = 0;
			}
		});
	}

	
	// ------------------------------------------------------------------------------------------------
	
	
	public static void add(Array2<Color> a, Array2<Color> b, Matrix<Color> result) {
		Vector size = Array2.assertEqualSizes(a, result);
		
		int sizeX = size.xInt();
		UtilsGL.parallel.parallelY(size, y -> {
			for (int x = 0; x < sizeX; x++) {
				result.set(x, y, a.at(x, y).add(b.at(x, y)));
			}
		});
	}
	
	
	public static Array2<Color> add(Array2<Color> a, Array2<Color> b) {
		Matrix<Color> result = new MatrixColor(a.size());
		add(a, b, result);
		return result;
	}
	
	
	
	public static void addInPlace(Matrix<Color> toChange, Array2<Color> byHowMuch) {
		Vector size = Array2.assertEqualSizes(toChange, byHowMuch);
		
		int sizeX = size.xInt();
		UtilsGL.parallel.parallelY(size, y -> {
			for (int x = 0; x < sizeX; x++) {
				toChange.set(x, y, toChange.at(x, y).add(byHowMuch.at(x, y)));
			}
		});
	}
	
	
	public static void mul(Array2<Color> a, double k, Matrix<Color> result) {
		Vector size = Array2.assertEqualSizes(a, result);
		
		int sizeX = size.xInt();
		UtilsGL.parallel.parallelY(size, y -> {
			for (int x = 0; x < sizeX; x++) {
				result.set(x, y, a.at(x, y).mul(k));
			}
		});
	}
	
	
	public static Array2<Color> mul(Array2<Color> a, double k) {
		Matrix<Color> result = new MatrixColor(a.size());
		mul(a, k, result);
		return result;
	}
	
	
	public static void mulInPlace(Matrix<Color> toChange, double factor) {
		Vector size = toChange.size();
		
		int sizeX = size.xInt();
		UtilsGL.parallel.parallelY(size, y -> {
			for (int x = 0; x < sizeX; x++) {
				toChange.set(x, y, toChange.at(x, y).mul(factor));
			}
		});
	}
	
	
}
