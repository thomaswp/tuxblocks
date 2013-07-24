package tuxkids.tuxblocks.core.solve.blocks.n.markup;

import playn.core.Canvas;
import playn.core.TextFormat;
import playn.core.TextLayout;
import pythagoras.f.Vector;

public class AddGroupRenderer extends ModifierRenderer {
	
	public AddGroupRenderer(Renderer base, int... operands) {
		super(base, operands);
	}
	
	public AddGroupRenderer(Renderer base, int[] operands, boolean[] highlights) {
		super(base, operands, highlights);
	}

	@Override
	public ExpressionWriter getExpressionWriter(TextFormat textFormat) {
		final ExpressionWriter childWriter = base.getExpressionWriter(textFormat);
		
		return new ExpressionWriter(textFormat) {
			
			TextLayout[] layouts;
			float myHeight;
			
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
				myHeight = height;
				return new Vector(childWriter.width() + width, Math.max(childWriter.height(), myHeight));
			}
			
			@Override
			public void drawExpression(Canvas canvas) {
				childWriter.drawExpression(canvas);
				
				float x = childWriter.width();
				for (int i = 0; i < operands.length; i++) {
					setColor(canvas, highlights[i]);
					canvas.fillText(layouts[i], x, (height() - myHeight) / 2);
					x += layouts[i].width();
				}
			}
		};
	}
}
