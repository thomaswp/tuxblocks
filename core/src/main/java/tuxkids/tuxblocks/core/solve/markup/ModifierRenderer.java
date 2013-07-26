package tuxkids.tuxblocks.core.solve.markup;

abstract class ModifierRenderer extends Renderer {
	protected Renderer base, modifier;
	
	public ModifierRenderer(Renderer base, Renderer modifier) {
		this.base = base;
		this.modifier = modifier;
	}
	
	@Override
	public int lines() {
		return Math.max(base.lines(), modifier.lines());
	}
}
