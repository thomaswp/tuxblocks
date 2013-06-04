package tuxkids.tuxblocks.core.blocks;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.GroupLayer;
import playn.core.Layer;
import playn.core.PlayN;
import tuxkids.tuxblocks.core.eqn.BinaryOperation;
import tuxkids.tuxblocks.core.eqn.Expression;
import tuxkids.tuxblocks.core.eqn.ModificationOperation;
import tuxkids.tuxblocks.core.eqn.Number;
import tuxkids.tuxblocks.core.eqn.Variable;

public class CopyOfExpressionBlock {
	
	public final static int NUM_SIZE = 100;
	public final static float RECT_RATIO = 0.2f;
	
	private Expression expression;
	private Layer sprite;
	private int width, height;
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public Layer getSprite() {
		return sprite;
	}
	
	public CopyOfExpressionBlock(Expression expression) {
		this(expression, NUM_SIZE, NUM_SIZE);
	}
	
	public CopyOfExpressionBlock(Expression expression, int width, int height) {
		this.expression = expression;
		this.width = width;
		this.height = height;
		createSprite(width, height);
	}
	
	private void createSprite(int width, int height) {
		CanvasImage image = null;
		if (expression instanceof Number) {
			image = PlayN.graphics().createImage(width, height);
			image.canvas().setFillColor(Color.rgb(150, 150, 0));
			image.canvas().fillRect(0, 0, image.width(), image.height());
			image.canvas().setFillColor(Color.rgb(0, 0, 0));
			image.canvas().drawText("" + ((Number) expression).getValue(), 0, 15);
			sprite = PlayN.graphics().createImageLayer(image);
		} else if (expression instanceof Variable) {
			image = PlayN.graphics().createImage(width, height);
			image.canvas().setFillColor(Color.rgb(150, 0, 150));
			image.canvas().fillRect(0, 0, image.width(), image.height());
			image.canvas().setFillColor(Color.rgb(0, 0, 0));
			image.canvas().drawText("" + ((Variable) expression).getName(), 0, 15);
			sprite = PlayN.graphics().createImageLayer(image);
		} else if (expression instanceof BinaryOperation) {
			BinaryOperation op = (BinaryOperation) expression;
			Expression a = op.getOperandA();
			Expression b = op.getOperandB();
			CopyOfExpressionBlock aBlock = new CopyOfExpressionBlock(a, width, height);
			CopyOfExpressionBlock bBlock = null;
			
			GroupLayer group = PlayN.graphics().createGroupLayer();
			this.sprite = group;
			
			if (op.getPrecedence() == Expression.PREC_ADD) {
				bBlock = new CopyOfExpressionBlock(b, (int)(height * RECT_RATIO), height);
				bBlock.sprite.setTranslation(aBlock.getWidth(), 0);
				this.width *= 1 + RECT_RATIO;
			} else if (op.getPrecedence() == Expression.PREC_MULT) {
				bBlock = new CopyOfExpressionBlock(b, width, (int)(width * RECT_RATIO));
				aBlock.sprite.setTranslation(0, bBlock.getHeight());
				this.height *= 1 + RECT_RATIO;
			}

			group.add(aBlock.sprite);
			group.add(bBlock.sprite);
		} else if (expression instanceof ModificationOperation) {
			ModificationOperation op = (ModificationOperation) expression;
			Expression operand = op.getOperand();
			CopyOfExpressionBlock aBlock = new CopyOfExpressionBlock(operand, width, height);
			
			GroupLayer group = PlayN.graphics().createGroupLayer();
			this.sprite = group;
			
			if (op.getPrecedence() == Expression.PREC_ADD) {
				bBlock = new CopyOfExpressionBlock(b, (int)(height * RECT_RATIO), height);
				bBlock.sprite.setTranslation(aBlock.getWidth(), 0);
				this.width *= 1 + RECT_RATIO;
			} else if (op.getPrecedence() == Expression.PREC_MULT) {
				bBlock = new CopyOfExpressionBlock(b, width, (int)(width * RECT_RATIO));
				aBlock.sprite.setTranslation(0, bBlock.getHeight());
				this.height *= 1 + RECT_RATIO;
			}

			group.add(aBlock.sprite);
			group.add(bBlock.sprite);
		}
		
		
	}
	
}
