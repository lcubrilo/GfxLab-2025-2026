package xyz.marsavic.gfxlab.gui;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Matrix;
import xyz.marsavic.gfxlab.MatrixColor;
import xyz.marsavic.gfxlab.MatrixInts;
import xyz.marsavic.resources.BorrowManagerMap;
import xyz.marsavic.time.Profiler;
import xyz.marsavic.utils.Parallel;

import java.util.*;
import java.util.concurrent.ForkJoinPool;


final public class UtilsGL {
	
	private UtilsGL() {}
	
	
	// ==================================================================================
	
	
	private static final Set<Profiler> profilers = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));
	
	
	public static Profiler profiler(Object object, String description) {
		String name =
				String.format("%08x", System.identityHashCode(object)) +
						" " +
						object.getClass().getSimpleName() +
						" " +
						description;
		
		Profiler profiler = new Profiler(name);
		profilers.add(profiler);
		return profiler;
	}
	
	
	/**
	 * Live profilers, but not a live collection.
	 * The returned collection is immutable and contains only the profilers present at the moment of calling.
	 */
	public static Collection<Profiler> profilers() {
		synchronized (profilers) {
			return List.of(profilers.toArray(Profiler[]::new));
		}
	}
	

	// ==================================================================================
	
	
	public static final int parallelism;	
	
	static {
//		int p = ForkJoinPool.getCommonPoolParallelism() + 1;
		int p = (ForkJoinPool.getCommonPoolParallelism() + 1) * 3 / 4;
//		p = 1;
/*
		try {
			boolean obsRunning = false;
			obsRunning |= ProcessHandle.allProcesses().anyMatch(ph -> ph.info().command().orElse("").contains("obs64")); // Windows
			obsRunning |= !Utils.runCommand("top -b -n 1 | grep \" obs\"").isEmpty(); // Linux
			obsRunning |= true;
			if (obsRunning) {
				p -= 3;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
*/
		parallelism = p;		
	}
	
	public static final Parallel parallel = new Parallel(new ForkJoinPool(parallelism));
	
	
	// ==================================================================================

	

	public static final BorrowManagerMap<Matrix<Color>, Vector> matricesColor = new BorrowManagerMap<>(
			MatrixColor::new, (m, _sz) -> ((MatrixColor) m).fillBlack()
//			MatrixObject::new, (m, _sz) -> m.fill(Color.BLACK)
	);

	public static final BorrowManagerMap<Matrix<Integer>, Vector> matricesInt = new BorrowManagerMap<>(
			MatrixInts::new, (m, _sz) -> m.fill(0)
	);
	
}
