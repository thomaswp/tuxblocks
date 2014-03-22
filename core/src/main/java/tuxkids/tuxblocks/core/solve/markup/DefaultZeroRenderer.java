package tuxkids.tuxblocks.core.solve.markup;

import playn.core.TextFormat;

/** 
 * Renderer used to wrap others that may have no {@link ExpressionWriter}, 
 * like {@link BlockHolderRenderer} 
 */
public class DefaultZeroRenderer extends Renderer {

	private final static Renderer ZERO_RENDERER = new BaseRenderer("0"); 
	
	private Renderer base;
	
	public DefaultZeroRenderer(Renderer base) {
		this.base = base;
	}
	
	@Override
	public int lines() {
		return base == null ? super.lines() : base.lines();
	}
	
	@Override
	protected boolean fullyHighlighted() {
		return base == null ? super.fullyHighlighted() : base.fullyHighlighted();
	}
	
	@Override
	public ExpressionWriter getExpressionWriter(TextFormat textFormat) {
		ExpressionWriter writer = base == null ? null : base.getExpressionWriter(textFormat);
		return writer == null ? ZERO_RENDERER.getExpressionWriter(textFormat) : writer;
	}

	@Override
	public String getPlainText() {
		return base == null ? ZERO_RENDERER.getPlainText() : base.getPlainText();
	}

}
