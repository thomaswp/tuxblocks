package tuxkids.tuxblocks.core.solve.blocks.n.markup;

public abstract class ModifierRenderer extends Renderer {
	protected Renderer base;
	
	public ModifierRenderer(Renderer base) {
		this.base = base;
	}
}
