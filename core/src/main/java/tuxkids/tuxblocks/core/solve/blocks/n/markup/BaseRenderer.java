package tuxkids.tuxblocks.core.solve.blocks.n.markup;

import playn.core.Canvas;
import playn.core.TextFormat;
import playn.core.TextLayout;
import pythagoras.f.Vector;

public class BaseRenderer extends Renderer {
	private String text;
	
	public BaseRenderer(String text) {
		this.text = text;
	}

	@Override
	public ExpressionWriter getExpressionWriter(TextFormat textFormat) {
		return new ExpressionWriter(textFormat) {
			
			TextLayout layout;
			
			@Override
			protected Vector formatExpression(TextFormat textFormat) {
				layout = graphics().layoutText(text, textFormat);
				return new Vector(layout.width(), layout.height());
			}
			
			@Override
			public void drawExpression(Canvas canvas) {
//				canvas.setFillColor(DEFAULT_COLOR);
				canvas.fillText(layout, 0, 0);
			}
		};
	}
}
