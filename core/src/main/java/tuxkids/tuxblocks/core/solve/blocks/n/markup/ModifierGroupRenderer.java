package tuxkids.tuxblocks.core.solve.blocks.n.markup;

abstract class ModifierGroupRenderer extends Renderer {
	
	protected int[] operands;
	protected boolean[] highlights;
	
	public boolean fullyHighlighted() {
		for (int i = 0; i < highlights.length; i++) if (!highlights[i]) return false;
		return true;
	}
	
	public ModifierGroupRenderer setHighlight(int index, boolean highlight) {
		highlights[index] = highlight;
		return this;
	}
	
	public ModifierGroupRenderer(int... operands) {
		this(operands, new boolean[operands.length]);
	}
	
	public ModifierGroupRenderer(int[] operands, boolean[] highlights) {
		this.operands = operands;
		this.highlights = highlights;
	}
}
