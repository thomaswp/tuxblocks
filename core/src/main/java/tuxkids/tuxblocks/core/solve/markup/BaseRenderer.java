package tuxkids.tuxblocks.core.solve.markup;

import playn.core.Canvas;
import playn.core.TextFormat;
import playn.core.TextLayout;
import pythagoras.f.Vector;

/** 
 * Simply renders a String. This is usually provided as a parameter to
 * more complex Renders. 
 */
public class BaseRenderer extends Renderer {
	private String text;
	private boolean highlight;
	
	public String text() {
		return text;
	}
	
	public BaseRenderer setHighlight(boolean highlight) {
		this.highlight = highlight;
		return this;
	}
	
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
				setColor(canvas, highlight);
				canvas.fillText(layout, 0, 0);
			}
		};
	}

	@Override
	public String getPlainText() {
		return text;
	}
}
