package tuxkids.tuxblocks.core.solve.blocks.n.markup;

import playn.core.Canvas;
import playn.core.Font;
import playn.core.Path;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.Canvas.LineCap;
import pythagoras.f.Vector;

public class TimesGroupRenderer extends FactorGroupRenderer {

	public TimesGroupRenderer(Renderer base, int[] operands) {
		super(base, operands);
	}

	@Override
	public ExpressionWriter getExpressionWriter(TextFormat textFormat) {
		final ExpressionWriter childWriter = base.getExpressionWriter(textFormat);
		final ExpressionWriter factorWriter = getFactorWriter(textFormat);
		
		return new ExpressionWriter(textFormat) {
			
//			TextLayout leftParenLayout, rightParenLayout;
			float w;
			
			@Override
			protected Vector formatExpression(TextFormat textFormat) {
//				Font font = PlayN.graphics().createFont(textFormat.font.name(), 
//						textFormat.font.style(), childWriter.height() * 1f);
//				TextFormat bigFormat = new TextFormat().withFont(font);
//				leftParenLayout = PlayN.graphics().layoutText("(", bigFormat);
//				rightParenLayout = PlayN.graphics().layoutText(")", bigFormat);
				float height = childWriter.height();
				w = height / 7;
				return new Vector(factorWriter.width() + childWriter.width() + w * 4, 
						childWriter.height());
			}
			
			@Override
			public void drawExpression(Canvas canvas) {
				canvas.save();
				canvas.translate(w * 2 + factorWriter.width(), 0);
				childWriter.drawExpression(canvas);
				canvas.restore();
				
				canvas.save();
				canvas.translate(0, (height() - factorWriter.height()) / 2);
				factorWriter.drawExpression(canvas);
				canvas.restore();

//				float ph = leftParenLayout.height();
//				//canvas.strokeRect(factorLayout.width(), (height() - ph) / 2, leftParenLayout.width(), ph);
//				canvas.fillText(leftParenLayout, factorWriter.width(), 
//						(height() - ph) / 2);
//				canvas.fillText(rightParenLayout, width() - rightParenLayout.width(),
//						(height() - ph) / 2);
				
				canvas.setStrokeWidth(textFormat.font.size() / 10);
				canvas.setLineCap(LineCap.ROUND);
				
				Path path = canvas.createPath();
				float x = factorWriter.width() + w * 0.5f, h = height() * 0.9f;
				path.moveTo(x + w, (h + height()) / 2);
				path.quadraticCurveTo(x - w, height() / 2, x + w, height() - h);
				canvas.strokePath(path);
				
				path = canvas.createPath();
				x += childWriter.width() + w * 3;
				path.moveTo(x - w, (h + height()) / 2);
				path.quadraticCurveTo(x + w, height() / 2, x - w, height() - h);
				canvas.strokePath(path);
				
//				canvas.setStrokeWidth(1);
//				canvas.strokeRect(0, 0, width(), height());
			}
		};
	}

}
