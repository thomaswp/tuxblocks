package tuxkids.tuxblocks.core.solve.blocks.n.markup;

public abstract class ModifierRenderer extends Renderer {
	protected Renderer base;
	protected int[] operands;
	protected boolean[] highlights;
	
	public ModifierRenderer setHighlight(int index, boolean highlight) {
		highlights[index] = highlight;
		return this;
	}
	
	public ModifierRenderer(Renderer base, int... operands) {
		this(base, operands, new boolean[operands.length]);
	}
	
	public ModifierRenderer(Renderer base, int[] operands, boolean[] highlights) {
		this.base = base;
		this.operands = operands;
		this.highlights = highlights;
	}
}
