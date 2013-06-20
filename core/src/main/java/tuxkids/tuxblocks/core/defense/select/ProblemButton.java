package tuxkids.tuxblocks.core.defense.select;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.Font.Style;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Button;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.solve.expression.Equation;
import tuxkids.tuxblocks.core.solve.expression.ExpressionWriter;

public class ProblemButton extends Button {

	public final static int MARGIN = 10;
	
	private Problem problem;
	private float minHeight;
	private ProblemButton above, below;
	private int towerColor;
	
	public Equation equation() {
		return problem.equation();
	}
	
	public void setEquation(Equation equation) {
		problem.setEquation(equation);
		setImage(createImage(problem, width(), minHeight, towerColor));
		float top = top();
		setSize(image().width(), image().height());
		setTop(top);
	}

	public void setAbove(ProblemButton above) {
		this.above = above;
	}
	
	public void setBelow(ProblemButton below) {
		this.below = below;
	}
	
	public ProblemButton above() {
		return above;
	}
	
	public ProblemButton below() {
		return below;
	}

	public Problem problem() {
		return problem;
	}
	
	public ProblemButton(Problem problem, float width, float minHeight, int towerColor) {
		super(createImage(problem, width, minHeight, towerColor), false);
		this.problem = problem;
		this.minHeight = minHeight;
		this.towerColor = towerColor;
	}

	private static Image createImage(Problem problem, float width, float minHeight, int towerColor) {

		float strokeWidth = 5;
		float padding = strokeWidth * 2;
		float rectRad = strokeWidth * 1.5f;
		float eqTextSize = (minHeight - padding * 2) * 0.25f;
		
		TextFormat textFormat = new TextFormat().withFont(graphics().createFont(Constant.FONT_NAME, Style.PLAIN, eqTextSize));
		ExpressionWriter leftEW = problem.equation().leftHandSide().getExpressionWriter(textFormat);
		ExpressionWriter rightEW = problem.equation().rightHandSide().getExpressionWriter(textFormat);
		TextLayout eqLayout = PlayN.graphics().layoutText("=", textFormat);
		
		float eqWidth = leftEW.width() + eqLayout.width() + 
				rightEW.width() + ExpressionWriter.SPACING * 2; 
		float eqHeight = Math.max(leftEW.height(), rightEW.height());
		
		float height = Math.max(eqHeight + padding * 2, minHeight);
		
		CanvasImage image = graphics().createImage(width, height);
		Canvas canvas = image.canvas();
		
		float rewardImageSize = minHeight - padding * 2;
		float cellSize = rewardImageSize / 3;
		
		canvas.setFillColor(Colors.WHITE);
		canvas.setStrokeColor(Colors.DARK_GRAY);
		canvas.setStrokeWidth(strokeWidth);

		canvas.fillRoundRect(strokeWidth / 2, strokeWidth / 2, width - strokeWidth, height - strokeWidth, rectRad);
		canvas.strokeRoundRect(strokeWidth / 2, strokeWidth / 2, width - strokeWidth, height - strokeWidth, rectRad);
		
		Tower reward = problem.reward();
		Image rewardImage = reward.createImage(cellSize, towerColor);
		float rewardImageX = width - padding - (rewardImageSize + rewardImage.width()) / 2;
		float rewardImageY = padding + (rewardImageSize - rewardImage.height()) / 2;
		canvas.drawImage(rewardImage, rewardImageX, rewardImageY);

		canvas.setFillColor(Colors.BLACK);
		TextFormat countFormat = new TextFormat().withFont(
				graphics().createFont(Constant.FONT_NAME, Style.PLAIN, rewardImageSize / 5));
		TextLayout countLayout = graphics().layoutText("x" + problem.rewardCount(), countFormat);
		canvas.fillText(countLayout, width - padding - rewardImageSize, padding);
		
		float lineX = width - padding * 2 - rewardImageSize;
		canvas.drawLine(lineX, 0, lineX, height);

		canvas.setStrokeWidth(1);
		canvas.setFillColor(Colors.BLACK);
		canvas.setStrokeColor(Colors.BLACK);
		float eqStartX = (lineX - eqWidth) / 2;
		
		canvas.save();
		canvas.translate(eqStartX, (height - leftEW.height()) / 2);
		leftEW.drawExpression(canvas, Colors.BLACK);
		canvas.restore();
		
		canvas.save();
		canvas.translate(eqStartX + leftEW.width() + ExpressionWriter.SPACING, (height - eqLayout.height()) / 2);
		canvas.fillText(eqLayout, 0, 0);
		canvas.restore();
		
		canvas.save();
		canvas.translate(eqStartX + eqLayout.width() + leftEW.width() + ExpressionWriter.SPACING * 2, (height - rightEW.height()) / 2);
		rightEW.drawExpression(canvas, Colors.BLACK);
		canvas.restore();
		
		
		return image;
	}
	
	public void paint(Clock clock) {
		float desiredY = above == null ? 0 : above.bottom();
		desiredY += MARGIN + height() / 2;
		float y = lerpTime(y(), desiredY, 0.99f, clock.dt());
		setPosition(x(), y);
	}
}
