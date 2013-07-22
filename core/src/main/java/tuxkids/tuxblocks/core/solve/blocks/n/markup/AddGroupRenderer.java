package tuxkids.tuxblocks.core.solve.blocks.n.markup;

import playn.core.Canvas;
import playn.core.TextFormat;
import playn.core.TextLayout;
import pythagoras.f.Vector;

public class AddGroupRenderer extends ModifierRenderer {
	
	int[] operands;
	
	public AddGroupRenderer(Renderer base, int... operands) {
		super(base);
		this.operands = operands;
	}

	@Override
	public ExpressionWriter getExpressionWriter(TextFormat textFormat) {
		final ExpressionWriter childWriter = base.getExpressionWriter(textFormat);
		
		return new ExpressionWriter(textFormat) {
			
			TextLayout layout;
			
			@Override
			protected Vector formatExpression(TextFormat textFormat) {
				String s = "";
				for (int i = 0; i < operands.length; i++) {
					s += " " + (operands[i] < 0 ? "- " : "+ ") + Math.abs(operands[i]);
				}
				layout = graphics().layoutText(s, textFormat);
				return new Vector(childWriter.width() + layout.width(), Math.max(childWriter.height(), layout.height()));
			}
			
			@Override
			public void drawExpression(Canvas canvas) {
				childWriter.drawExpression(canvas);
				
				canvas.fillText(layout, width() - layout.width(), 
						(height() - layout.height()) / 2);
			}
		};
	}
}
