package tuxkids.tuxblocks.core.solve.markup;

import playn.core.TextFormat;

public class BlockHolderRenderer extends Renderer {
	
	@Override
	public ExpressionWriter getExpressionWriter(TextFormat textFormat) {
		return ExpressionWriter.NOOP;
	}

	@Override
	public String getPlainText() {
		return "[ ]";
	}

}
