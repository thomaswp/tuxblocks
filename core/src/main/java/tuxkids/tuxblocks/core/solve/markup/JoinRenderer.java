package tuxkids.tuxblocks.core.solve.markup;

import playn.core.Canvas;
import playn.core.TextFormat;
import playn.core.TextLayout;
import pythagoras.f.Vector;

/** Renders two terms, joined in the middle by a symbol, such as '=' or '+' */
public class JoinRenderer extends Renderer {

	private Renderer a, b;
	private String symbol;
	
	public JoinRenderer(Renderer a, Renderer b, String symbol) {
		this.a = a;
		this.b = b;
		this.symbol = symbol;
	}
	
	@Override
	public int lines() {
		return Math.max(a.lines(), b.lines());
	}
	
	@Override
	public ExpressionWriter getExpressionWriter(TextFormat textFormat) {
		final ExpressionWriter aWriter = a.getExpressionWriter(textFormat);
		final ExpressionWriter bWriter = b.getExpressionWriter(textFormat);
		
		// If either renderer is null (see BlockHolderRenderer) 
		if (aWriter == null) return bWriter;
		if (bWriter == null) return aWriter;
		
		return new ParentExpressionWriter(textFormat) {
			
			TextLayout layout;
			
			@Override
			protected Vector formatExpression(TextFormat textFormat) {
				layout = graphics().layoutText(symbol, textFormat);
				return new Vector(aWriter.width() + spacing() + layout.width() + spacing() +  bWriter.width(), 
						Math.max(aWriter.height(), bWriter.height()));
			}

			@Override
			protected void addChildren() {
				addChild(aWriter, 0, (height() - aWriter.height()) / 2);
				addChild(bWriter, width() - bWriter.width(), (height() - bWriter.height()) / 2);
			}
			
			@Override
			public void drawExpression(Canvas canvas) {			
				super.drawExpression(canvas);
				
				// render the joining symbol
				setColor(canvas, false);
				canvas.fillText(layout, aWriter.width() + spacing(), (height() - layout.height()) / 2);
			}
		};
	}

	@Override
	public String getPlainText() {
		StringBuilder sb = new StringBuilder();
		sb.append(a.getPlainText());
		sb.append(" ");
		sb.append(symbol);
		sb.append(" ");
		sb.append(b.getPlainText());
		return sb.toString();
	}

}
