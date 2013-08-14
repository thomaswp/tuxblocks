package tuxkids.tuxblocks.core.defense.select;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Image;
import playn.core.Layer;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.Font.Style;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.defense.tower.TowerType;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.markup.ExpressionWriter;
import tuxkids.tuxblocks.core.solve.markup.ExpressionWriter.Config;
import tuxkids.tuxblocks.core.widget.Button;

public class ProblemButton extends Button {

	public final static int MARGIN = 10;
	
	private Problem problem;
	private float minHeight;
	private ProblemButton above, below;
	private int towerColor;
	private float targetAlpha = 1;
	
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
		setSoundPath(Constant.SE_TICK);
	}

	private static Image createImage(Problem problem, float width, float minHeight, int towerColor) {

		float strokeWidth = 5;
		float padding = strokeWidth * 2;
		float rectRad = strokeWidth * 1.5f;
		float eqTextSize = (minHeight - padding * 2) * 0.25f;
		
		TextFormat textFormat = new TextFormat().withFont(graphics().createFont(Constant.FONT_NAME, Style.PLAIN, eqTextSize));
		ExpressionWriter writer = problem.equation().renderer().getExpressionWriter(textFormat);
		
		float eqWidth = writer.width(); 
		float eqHeight = writer.height();
		
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
		
		TowerType reward = problem.reward().tower;
		Image rewardImage = reward.instance().createImage(cellSize, towerColor);
		float rewardImageX = width - padding - (rewardImageSize + rewardImage.width()) / 2;
		float rewardImageY = padding + (rewardImageSize - rewardImage.height()) / 2;
		canvas.drawImage(rewardImage, rewardImageX, rewardImageY);

		canvas.setFillColor(Colors.BLACK);
		TextFormat countFormat = new TextFormat().withFont(
				graphics().createFont(Constant.FONT_NAME, Style.PLAIN, rewardImageSize / 5));
		TextLayout countLayout = graphics().layoutText("x" + problem.reward().count, countFormat);
		canvas.fillText(countLayout, width - padding - rewardImageSize, padding);
		
		float lineX = width - padding * 2 - rewardImageSize;
		canvas.drawLine(lineX, 0, lineX, height);

		canvas.setStrokeWidth(1);
		canvas.setFillColor(Colors.BLACK);
		canvas.setStrokeColor(Colors.BLACK);
		float eqStartX = (lineX - eqWidth) / 2;
		
		canvas.save();
		canvas.translate(eqStartX, (height - writer.height()) / 2);
		writer.drawExpression(canvas, new Config(Colors.BLACK, Colors.BLACK, Colors.BLACK));
		canvas.restore();
		
		
		return image;
	}
	
	public void fadeIn(float targetAlpha) {
		layerAddable().setAlpha(0);
		this.targetAlpha = targetAlpha;
	}
	
	public void fadeOut() {
		this.targetAlpha = 0;
	}
	
	public void paint(Clock clock) {
		Layer layer = layerAddable();
		if (layer.alpha() != targetAlpha) {
			layer.setAlpha(lerpTime(layer.alpha(), targetAlpha, 0.99f, clock.dt()));
			if (Math.abs(layer.alpha() - targetAlpha) < 0.01) layer.setAlpha(targetAlpha);
		}
		
		float desiredY = above == null ? 0 : above.bottom();
		desiredY += MARGIN + height() / 2;
		float y = lerpTime(y(), desiredY, 0.99f, clock.dt());
		setPosition(x(), y);
	}
}
