package tuxkids.tuxblocks.core.solve.blocks.n.markup;

import playn.core.Canvas;
import playn.core.TextFormat;
import playn.core.TextLayout;
import pythagoras.f.Vector;

public abstract class FactorGroupRenderer extends ModifierRenderer {
	int[] operands;
	
	public FactorGroupRenderer(Renderer base, int... operands) {
		super(base);
		this.operands = operands;
	}

	public ExpressionWriter getFactorWriter(TextFormat textFormat) {
		
		return new ExpressionWriter(textFormat) {
			
			TextLayout layout;
			
			@Override
			protected Vector formatExpression(TextFormat textFormat) {
				String s = "";
				for (int i = 0; i < operands.length; i++) {
					if (i == 0) {
						s += operands[i];
					} else {
						s += " * " + operands[i];
					}
				}
				layout = graphics().layoutText(s, textFormat);
				return new Vector(layout.width(), layout.height());
			}
			
			@Override
			public void drawExpression(Canvas canvas) {
				canvas.fillText(layout, 0, 0);
			}
		};
	}
}
