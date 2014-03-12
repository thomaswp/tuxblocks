package tuxkids.tuxblocks.core.solve.markup;

import playn.core.TextFormat;

public class ExpressionRenderer extends Renderer {

	private final Renderer base;
	
	public ExpressionRenderer(Renderer base) {
		this.base = base;
	}
	
	@Override
	public ExpressionWriter getExpressionWriter(TextFormat textFormat) {
		return base.getExpressionWriter(textFormat);
	}

	@Override
	public String getPlainText() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(base.getPlainText());
		sb.append("]");
		return sb.toString();
	}

}
