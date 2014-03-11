package tuxkids.tuxblocks.core.solve.blocks;

import tuxkids.tuxblocks.core.utils.Formatter;

public class BlockIndex {
	public final int depth, index;
	
	public BlockIndex(int depth, int index) {
		this.depth = depth;
		this.index = index;
	}
	
	public BlockIndex oneDeeper() {
		return new BlockIndex(depth + 1, index);
	}
	
	public BlockIndex oneShallower() {
		return new BlockIndex(depth - 1, index);
	}
	
	@Override
	public String toString() {
		return Formatter.format("{%d,  %d}", depth, index);
	}
}
