package tuxkids.tuxblocks.core.solve.markup;

import playn.core.Canvas;
import playn.core.Color;
import playn.core.TextFormat;
import playn.core.TextLayout;
import pythagoras.f.Vector;

public class BlankRenderer extends Renderer {

	@Override
	public ExpressionWriter getExpressionWriter(TextFormat textFormat) {
		return new ExpressionWriter(textFormat) {
			
			@Override
			protected Vector formatExpression(TextFormat textFormat) {
				TextLayout layout = graphics().layoutText("-999", textFormat);
				float fontSize = textFormat.font.size();
				return new Vector(layout.width() + fontSize / 5, layout.height() + fontSize / 5);
			}
			
			@Override
			public void drawExpression(Canvas canvas) {
				float sw = 1;
				canvas.setFillColor(Color.withAlpha(blankColor(), 100));
				canvas.setStrokeColor(blankColor());
				canvas.setStrokeWidth(sw);
				canvas.fillRect(0, 0, width(), height());
				canvas.strokeRect(0, 0, width() - sw / 2, height() - sw / 2);
			}

			@Override
			public Vector blankCenter() {
				return new Vector(width() / 2, height() / 2);
			}
		};
	}

}
