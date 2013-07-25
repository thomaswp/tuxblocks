package tuxkids.tuxblocks.core.solve.blocks.n.markup;

import playn.core.Canvas;
import playn.core.TextFormat;
import playn.core.TextLayout;
import pythagoras.f.Vector;

class AddGroupRenderer extends ModifierGroupRenderer {
	
	public AddGroupRenderer(int... operands) {
		super(operands);
	}
	
	public AddGroupRenderer(int[] operands, boolean[] highlights) {
		super(operands, highlights);
	}

	@Override
	public ExpressionWriter getExpressionWriter(TextFormat textFormat) {
		
		return new ExpressionWriter(textFormat) {
			
			TextLayout[] layouts;
			
			@Override
			protected Vector formatExpression(TextFormat textFormat) {
				float width = 0, height = 0;
				layouts = new TextLayout[operands.length];
				for (int i = 0; i < operands.length; i++) {
					String s = " " + (operands[i] < 0 ? "- " : "+ ") + Math.abs(operands[i]);
					layouts[i] = graphics().layoutText(s, textFormat);
					width += layouts[i].width();
					height = Math.max(layouts[i].height(), height);
				}
				return new Vector(width, height);
			}
			
			@Override
			public void drawExpression(Canvas canvas) {
				float x = 0;
				for (int i = 0; i < operands.length; i++) {
					setColor(canvas, highlights[i]);
					canvas.fillText(layouts[i], x, 0);
					x += layouts[i].width();
				}
			}
		};
	}
}
