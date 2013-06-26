package tuxkids.tuxblocks.core.solve;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Font;
import playn.core.Font.Style;
import playn.core.ImageLayer;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.TextLayout;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.solve.blocks.BaseBlock;
import tuxkids.tuxblocks.core.solve.blocks.ModifierBlock;
import tuxkids.tuxblocks.core.solve.expression.Expression;
import tuxkids.tuxblocks.core.solve.expression.ExpressionWriter;
import tuxkids.tuxblocks.core.solve.expression.ModificationOperation;

public class EquationSprite {
	
	private ImageLayer layer;
	private static TextFormat textFormat;
	private BaseBlock rightHandSide, leftHandSide;
	
	public ImageLayer layer() {
		return layer;
	}
	
	public EquationSprite(BaseBlock leftHandSide, BaseBlock rightHandSide) {
		this.leftHandSide = leftHandSide;
		this.rightHandSide = rightHandSide;
		if (textFormat == null) {
			Font font = PlayN.graphics().createFont(Constant.FONT_NAME, Style.PLAIN, 20);
			textFormat = new TextFormat().withFont(font);
		}
		refresh(null, null, false);
	}
	
	public void refresh(ModifierBlock dragging, BaseBlock highlight, boolean flipModifier) {
		if (layer != null) layer.destroy();
		Expression leftExpression = leftHandSide.topLevelExpression();
		Expression rightExpression = rightHandSide.topLevelExpression();
		
		BaseBlock toModify = null;
		if (dragging != null) {
			toModify = highlight;
			ModificationOperation modOp = flipModifier ? 
					dragging.getModifier() : dragging.getOriginalModifier();
			if (leftHandSide == toModify) {
				modOp.setOperand(leftExpression);
				leftExpression = modOp;
			} else {
				modOp.setOperand(rightExpression);
				rightExpression = modOp;
			}
		}
		
		ExpressionWriter leftEW = leftExpression.getExpressionWriter(textFormat);
		ExpressionWriter rightEW = rightExpression.getExpressionWriter(textFormat);
		TextLayout eqLayout = PlayN.graphics().layoutText("=", textFormat);
		float width = leftEW.width() + eqLayout.width() + 
				rightEW.width() + ExpressionWriter.SPACING * 2; 
		float height = Math.max(leftEW.height(), rightEW.height());
		
		CanvasImage canvasImage = PlayN.graphics().createImage(width, height);
		Canvas canvas = canvasImage.canvas();
		
		int colorNormal = Color.rgb(0, 0, 0);
		int colorHighlight = Color.rgb(255, 0, 0);
		canvas.setFillColor(colorNormal);
		canvas.setStrokeColor(colorNormal);
		
		canvas.save();
		if (toModify == leftHandSide) {
			canvas.setFillColor(colorHighlight);
			canvas.setStrokeColor(colorHighlight);
		}
		canvas.translate(0, (height - leftEW.height()) / 2);
		leftEW.drawExpression(canvas, colorNormal);
		canvas.restore();
		
		canvas.save();
		canvas.translate(leftEW.width() + ExpressionWriter.SPACING, 
				(height - eqLayout.height()) / 2);
		canvas.fillText(eqLayout, 0, 0);
		canvas.restore();
		
		canvas.save();
		if (toModify == rightHandSide) {
			canvas.setFillColor(colorHighlight);
			canvas.setStrokeColor(colorHighlight);
		}
		canvas.translate(leftEW.width() + ExpressionWriter.SPACING * 2 + eqLayout.width(), 
				(height - rightEW.height()) / 2);
		rightEW.drawExpression(canvas, colorNormal);
		canvas.restore();
		
		layer = PlayN.graphics().createImageLayer(canvasImage);
		layer.setOrigin(leftEW.width() + ExpressionWriter.SPACING + eqLayout.width() / 2, 0);
	}
}
