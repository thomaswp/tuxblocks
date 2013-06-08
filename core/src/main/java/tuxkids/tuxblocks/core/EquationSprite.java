package tuxkids.tuxblocks.core;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Font.Style;
import playn.core.Color;
import playn.core.Font;
import playn.core.ImageLayer;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.TextLayout;
import tuxkids.tuxblocks.core.blocks.BaseBlock;
import tuxkids.tuxblocks.core.expression.ExpressionWriter;
import tuxkids.tuxblocks.core.utils.Debug;
import tuxkids.tuxblocks.core.utils.Formatter;

public class EquationSprite {
	
	private ImageLayer layer;
	private static TextFormat textFormat;
	private BaseBlock rightHandSide, leftHandSide;
	
	public ImageLayer getLayer() {
		return layer;
	}
	
	public EquationSprite(BaseBlock leftHandSide, BaseBlock rightHandSide) {
		this.leftHandSide = leftHandSide;
		this.rightHandSide = rightHandSide;
		if (textFormat == null) {
			Font font = PlayN.graphics().createFont("Arial", Style.PLAIN, 20);
			textFormat = new TextFormat().withFont(font);
		}
		refresh();
	}
	
	public void refresh() {
		if (layer != null) layer.destroy();
		ExpressionWriter leftEW = leftHandSide.getTopLevelExpression().getExpressionWriter(textFormat);
		ExpressionWriter rightEW = rightHandSide.getTopLevelExpression().getExpressionWriter(textFormat);
		TextLayout eqLayout = PlayN.graphics().layoutText("=", textFormat);
		float width = leftEW.width() + eqLayout.width() + 
				rightEW.width() + ExpressionWriter.SPACING * 2; 
		float height = Math.max(leftEW.height(), rightEW.height());
		
		CanvasImage canvasImage = PlayN.graphics().createImage(width, height);
		Canvas canvas = canvasImage.canvas();
		canvas.setFillColor(Color.rgb(0, 0, 0));
		canvas.setStrokeColor(Color.rgb(0, 0, 0));
		
		canvas.save();
		canvas.translate(0, (height - leftEW.height()) / 2);
		leftEW.drawExpression(canvas);
		canvas.restore();
		
		canvas.save();
		canvas.translate(leftEW.width() + ExpressionWriter.SPACING, 
				(height - eqLayout.height()) / 2);
		canvas.fillText(eqLayout, 0, 0);
		canvas.restore();
		
		canvas.save();
		canvas.translate(leftEW.width() + ExpressionWriter.SPACING * 2 + eqLayout.width(), 
				(height - rightEW.height()) / 2);
		rightEW.drawExpression(canvas);
		canvas.restore();
		
		layer = PlayN.graphics().createImageLayer(canvasImage);
		layer.setOrigin(leftEW.width() + ExpressionWriter.SPACING + eqLayout.width() / 2, 0);
	}
}
