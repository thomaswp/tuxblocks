package tuxkids.tuxblocks.core.defense.select;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Font.Style;
import playn.core.Image;
import playn.core.Layer;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.util.Callback;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.defense.tower.TowerType;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.markup.ExpressionWriter;
import tuxkids.tuxblocks.core.solve.markup.ExpressionWriter.Config;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.widget.Button;

/**
 * A special {@link Button} for displaying {@link Problem}s
 * on the {@link SelectScreen}.
 */
public class ProblemButton extends Button {

	public final static int MARGIN = 10;
	
	private Problem problem;
	private float minHeight;
	private ProblemButton above, below;
	private int towerColor, secondaryColor;
	private float targetAlpha = 1;
	
	public Equation equation() {
		return problem.equation();
	}
	
	public void setEquation(Equation equation) {
		problem.setEquation(equation);
		setImage(createImage(problem, width(), minHeight, towerColor, secondaryColor));
		float top = top();
		setSize(image().width(), image().height());
		setTop(top);
	}

	/** Sets the ProblemButton this button should position itself below */
	public void setAbove(ProblemButton above) {
		this.above = above;
	}
	
	/** Sets the ProblemButton this button should position itself above */
	public void setBelow(ProblemButton below) {
		this.below = below;
	}
	
	/** Sets the ProblemButton above this one in the chain */
	public ProblemButton above() {
		return above;
	}
	
	/** Sets the ProblemButton below this one in the chain */
	public ProblemButton below() {
		return below;
	}

	public Problem problem() {
		return problem;
	}
	
	/** Returns true if this Button has faded out to near 0 alpha */
	public boolean fadedOut() {
		return targetAlpha == 0 && layerAddable().alpha() < 0.03f;
	}
	
	public ProblemButton(Problem problem, float width, float minHeight, int towerColor, int secondaryColor) {
		super(createImage(problem, width, minHeight, towerColor, secondaryColor), false);
		this.problem = problem;
		this.minHeight = minHeight;
		this.towerColor = towerColor;
		this.secondaryColor = secondaryColor;
		setSoundPath(Constant.SE_TICK);
	}

	private static Image createImage(final Problem problem, final float width, 
			float minHeight, final int towerColor, final int secondaryColor) {
		
		float strokeWidth = 5;
		final float padding = strokeWidth * 2;
		float rectRad = strokeWidth * 1.5f;
		float eqTextSize = (minHeight - padding * 2) * 0.25f;
		
		TextFormat textFormat = new TextFormat().withFont(graphics().createFont(Constant.NUMBER_FONT, Style.PLAIN, eqTextSize));
		final ExpressionWriter writer = problem.equation().renderer().getExpressionWriter(textFormat); // for drawing the equation
		
		final float eqWidth = writer.width(); 
		float eqHeight = writer.height();
		
		final float height = Math.max(eqHeight + padding * 2, minHeight);
		
		CanvasImage image = graphics().createImage(width, height);
		final Canvas canvas = image.canvas();
		
		final float rewardImageSize = minHeight - padding * 2;
		float cellSize = rewardImageSize / 3;
		
		canvas.setFillColor(Colors.WHITE);
		canvas.setStrokeColor(Colors.DARK_GRAY);
		canvas.setStrokeWidth(strokeWidth);

		// button's bg
		canvas.fillRoundRect(strokeWidth / 2, strokeWidth / 2, width - strokeWidth, height - strokeWidth, rectRad);
		canvas.strokeRoundRect(strokeWidth / 2, strokeWidth / 2, width - strokeWidth, height - strokeWidth, rectRad);
		
		// draw the Tower reward
		TowerType reward = problem.reward().tower();
		final Image rewardImage;
		if (problem instanceof StarredProblem) {
			rewardImage = assets().getImage(Constant.IMAGE_STAR);
			rewardImage.addCallback(new Callback<Image>() {
				@Override
				public void onSuccess(Image result) {
					Image tintedRewardImage = CanvasUtils.tintImage(rewardImage, secondaryColor);
					drawButtonImageDependent(problem, width, padding, writer, eqWidth, height, canvas,
							rewardImageSize, tintedRewardImage);
				}

				@Override
				public void onFailure(Throwable cause) {
					cause.printStackTrace();
				}
			});
		} else {
			rewardImage = reward.instance().createImage(cellSize, towerColor);
			drawButtonImageDependent(problem, width, padding, writer, eqWidth, height, canvas,
					rewardImageSize, rewardImage);
		}
		
		return image;
	}

	private static void drawButtonImageDependent(Problem problem, float width, float padding,
			ExpressionWriter writer, float eqWidth, float height,
			Canvas canvas, float rewardImageSize, Image rewardImage) {

		float scale = rewardImageSize / Math.max(rewardImageSize, rewardImage.width() * 1.3f);
		float sw = scale * rewardImage.width();
		float sh = scale * rewardImage.height();
		
		float rewardImageX = width - padding - (rewardImageSize + sw) / 2;
		float rewardImageY = padding + (rewardImageSize - sh) / 2;
		
		canvas.drawImage(rewardImage, rewardImageX, rewardImageY, sw, sh);

		// draw the count for the reward
		canvas.setFillColor(Colors.BLACK);
		TextFormat countFormat = new TextFormat().withFont(
				graphics().createFont(Constant.NUMBER_FONT, Style.PLAIN, rewardImageSize / 5));
		TextLayout countLayout = graphics().layoutText("x" + problem.reward().count(), countFormat);
		canvas.fillText(countLayout, width - padding - rewardImageSize, padding);
		
		// draw the bar between the equation and reward
		float lineX = width - padding * 2 - rewardImageSize;
		canvas.drawLine(lineX, 0, lineX, height);

		// draw the equation
		// TODO: do something with too-large equations to crop/shrink them
		canvas.setStrokeWidth(1);
		canvas.setFillColor(Colors.BLACK);
		canvas.setStrokeColor(Colors.BLACK);
		float eqStartX = (lineX - eqWidth) / 2;
		
		canvas.save();
		canvas.translate(eqStartX, (height - writer.height()) / 2);
		writer.drawExpression(canvas, new Config(Colors.BLACK, Colors.BLACK, Colors.BLACK));
		canvas.restore();
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
