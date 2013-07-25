package tuxkids.tuxblocks.core.solve.blocks.n.markup;

import playn.core.Canvas;
import playn.core.TextFormat;
import playn.core.TextLayout;
import pythagoras.f.Vector;
import tuxkids.tuxblocks.core.Constant;

class FactorGroupRenderer extends ModifierGroupRenderer {
	
	protected boolean useNegatives;

	
	public FactorGroupRenderer(int... operands) {
		super(operands);
	}
	
	public FactorGroupRenderer(int[] operands, boolean[] highlights) {
		super(operands, highlights);
	}

	public ExpressionWriter getExpressionWriter(TextFormat textFormat) {
		
		return new ExpressionWriter(textFormat) {
			
			TextLayout[] layouts;
			
			@Override
			protected Vector formatExpression(TextFormat textFormat) {
				layouts = new TextLayout[operands.length];
				float width = 0, height = 0;
				for (int i = 0; i < operands.length; i++) {
					String s = operands[i] == TimesRenderer.UNKNOWN_NUMBER ? "?" : "" + operands[i];
					if (useNegatives && operands.length == 1 && operands[i] == -1) {
						s = "-";
					} else if (i != 0) {
						s = Constant.DOT_SYMBOL + s;	
					}
					layouts[i] = graphics().layoutText(s, textFormat);
					width += layouts[i].width();
					height = Math.max(height, layouts[i].height());
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
