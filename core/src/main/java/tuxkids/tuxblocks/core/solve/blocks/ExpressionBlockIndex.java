package tuxkids.tuxblocks.core.solve.blocks;

import tuxkids.tuxblocks.core.utils.Formatter;

public class ExpressionBlockIndex {
	private static final int CACHE_SIZE = 7;

	// [depth][index]
	private static ExpressionBlockIndex[][] cache = new ExpressionBlockIndex[CACHE_SIZE][CACHE_SIZE];

	static {
		for (int d = 0; d < CACHE_SIZE; d++) {
			for (int i = 0; i < CACHE_SIZE; i++) {
				cache[d][i] = new ExpressionBlockIndex(d, i);
			}
		}
	}

	public final int depth, index;

	private ExpressionBlockIndex(int depth, int index) {
		this.depth = depth;
		this.index = index;
	}

	public ExpressionBlockIndex oneDeeper() {
		return makeExpressionBlockIndex(depth + 1, index);
	}

	public ExpressionBlockIndex oneShallower() {
		return makeExpressionBlockIndex(depth - 1, index);
	}

	public static ExpressionBlockIndex makeExpressionBlockIndex(int depth, int index) {
		if (depth < CACHE_SIZE && index < CACHE_SIZE) {
			return cache[depth][index];
		}
		return new ExpressionBlockIndex(depth, index);
	}

	@Override
	public String toString() {
		return Formatter.format("{%d, %d}", depth, index);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + depth;
		result = prime * result + index;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExpressionBlockIndex other = (ExpressionBlockIndex) obj;
		if (depth != other.depth)
			return false;
		if (index != other.index)
			return false;
		return true;
	}
	
	
}
