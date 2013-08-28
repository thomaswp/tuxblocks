package tuxkids.tuxblocks.core.solve.markup;

/**
 * A Renderer which renders a group of integer modifiers,
 * modifying a base Renderer
 */
abstract class ModifierGroupRenderer extends Renderer {
	
	protected int[] operands;
	protected boolean[] highlights;
	
	@Override
	public boolean fullyHighlighted() {
		for (int i = 0; i < highlights.length; i++) if (!highlights[i]) return false;
		return true;
	}
	
	/** Sets the given operand to render in the highlight color */
	protected ModifierGroupRenderer setHighlight(int index, boolean highlight) {
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
