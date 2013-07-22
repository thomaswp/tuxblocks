package tuxkids.tuxblocks.core.solve.blocks.n.markup;

import playn.core.Canvas;
import playn.core.TextFormat;
import playn.core.TextLayout;
import pythagoras.f.Vector;

public class JoinRenderer extends Renderer {

	private Renderer a, b;
	private String symbol;
	
	public JoinRenderer(Renderer a, Renderer b, String symbol) {
		this.a = a;
		this.b = b;
		this.symbol = symbol;
	}
	
	@Override
	public ExpressionWriter getExpressionWriter(TextFormat textFormat) {
		final ExpressionWriter aWriter = a.getExpressionWriter(textFormat);
		final ExpressionWriter bWriter = b.getExpressionWriter(textFormat);
		
		return new ExpressionWriter(textFormat) {
			
			TextLayout layout;
			
			@Override
			protected Vector formatExpression(TextFormat textFormat) {
				layout = graphics().layoutText(symbol, textFormat);
				return new Vector(aWriter.width() + SPACING + layout.width() + SPACING +  bWriter.width(), 
						Math.max(aWriter.height(), bWriter.height()));
			}
			
			@Override
			public void drawExpression(Canvas canvas) {
				canvas.save();
				canvas.translate(0, (height() - aWriter.height()) / 2);
				aWriter.drawExpression(canvas);
				canvas.restore();
				
				canvas.fillText(layout, aWriter.width() + SPACING, (height() - layout.height()) / 2);
				
				canvas.save();
				canvas.translate(width() - bWriter.width(), (height() - bWriter.height()) / 2);
				bWriter.drawExpression(canvas);
				canvas.restore();
			}
		};
	}

}
