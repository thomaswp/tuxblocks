package tuxkids.tuxblocks.core;

import static playn.core.PlayN.*;

import java.util.List;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Game;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import pythagoras.f.Point;
import tuxkids.tuxblocks.core.blocks.BaseBlock;
import tuxkids.tuxblocks.core.blocks.Block;
import tuxkids.tuxblocks.core.blocks.ModifierBlock;
import tuxkids.tuxblocks.core.blocks.VariableBlock;
import tuxkids.tuxblocks.core.expression.Expression;
import tuxkids.tuxblocks.core.expression.Number;
import tuxkids.tuxblocks.core.expression.Plus;
import tuxkids.tuxblocks.core.expression.Variable;
import tuxkids.tuxblocks.core.utils.Debug;

public class TuxBlocksGame extends Game.Default implements Listener {

	public TuxBlocksGame() {
		super(33); // call update every 33ms (30 times per second)
	}

	private BaseBlock leftHandSide, rightHandSide;
	private BaseBlock draggingFrom, draggingTo;
	private ModifierBlock dragging;
	private Point dragOffset = new Point();
	private EquationSprite equationSprite;
	
	@Override
	public void init() {
		
		CanvasImage background = graphics().createImage(graphics().width(), graphics().height());
		background.canvas().setFillColor(Color.rgb(255, 255, 255));
		background.canvas().fillRect(0, 0, graphics().width() / 2, graphics().height());
		background.canvas().setFillColor(Color.rgb(100, 100, 100));
		background.canvas().fillRect(graphics().width() / 2, 0, graphics().width() / 2, graphics().height());
		graphics().rootLayer().add(graphics().createImageLayer(background));

		Expression e = new Variable("x").plus(5).over(3).plus(2).times(2);
		System.out.println(e.toMathString());

		leftHandSide = Block.createBlock(e);
		leftHandSide.getGroupLayer().setTy(graphics().height());
		leftHandSide.getGroupLayer().setTx(graphics().width() / 4 - leftHandSide.getGroupWidth() / 2);
		graphics().rootLayer().add(leftHandSide.getGroupLayer());
		leftHandSide.getLastModifier().getSprite().addListener(this);
		
		rightHandSide = Block.createBlock(new Number(5));
		rightHandSide.getGroupLayer().setTy(graphics().height());
		rightHandSide.getGroupLayer().setTx(3 * graphics().width() / 4 - leftHandSide.getGroupWidth() / 2);
		graphics().rootLayer().add(rightHandSide.getGroupLayer());
		//rightHandSide.getSprite().addListener(this);

		equationSprite = new EquationSprite(leftHandSide, rightHandSide);
		refreshEquationSprite();
	}

	private void refreshEquationSprite() {
		equationSprite.refresh();
		ImageLayer layer = equationSprite.getLayer();
		graphics().rootLayer().add(layer);
		layer.setTy(10);
		layer.setTx(graphics().width() / 2);
	}
	
	private int frames;
	private double lastUpdate;
	private ImageLayer fpsLayer;
	private void updateFPS() {
		frames++;
		if (lastUpdate < currentTime() - 1000) {
			lastUpdate = currentTime();
			CanvasImage image = graphics().createImage(40, 13);
			Canvas canvas = image.canvas();
			canvas.setFillColor(Color.rgb(50, 50, 50));
			canvas.drawText(frames + " FPS", 0, image.height());
			if (fpsLayer != null) graphics().rootLayer().remove(fpsLayer);
			fpsLayer =  graphics().createImageLayer(image);
			fpsLayer.setDepth(10000);
			graphics().rootLayer().add(fpsLayer);
			frames = 0;
		}
	}

	@Override
	public void update(int delta) {

	}

	@Override
	public void paint(float alpha) {
		updateFPS();
	}

	@Override
	public void onPointerStart(Event event) {
		if (dragging != null) return;
		dragging = null;
		draggingFrom = null;
		if (event.hit() != null) {
			if (leftHandSide.hasModifier() &&
					leftHandSide.getLastModifier().getSprite() == event.hit()) {
				draggingFrom = leftHandSide;
				draggingTo = rightHandSide;						
			} else if (rightHandSide.hasModifier() &&
					rightHandSide.getLastModifier().getSprite() == event.hit()) {
				draggingFrom = rightHandSide;
				draggingTo = leftHandSide;
			}
			if (draggingFrom != null) {
				dragging = draggingFrom.pop();
				if (draggingFrom.hasModifier()) {
					draggingFrom.getLastModifier().getSprite().addListener(this);
				}
				dragOffset.set(
						draggingFrom.getGroupLayer().tx() + dragging.getSprite().tx() - event.x(), 
						draggingFrom.getGroupLayer().ty() + dragging.getSprite().ty() - event.y());
				dragging.getSprite().setTranslation(event.x() + dragOffset.x, event.y() + dragOffset.y);
				graphics().rootLayer().add(dragging.getSprite());
			}
		}
	}

	@Override
	public void onPointerEnd(Event event) {
		if (dragging != null) {
			BaseBlock dragStop;
			if (dragging.isInverted()) {
				dragStop = draggingTo;
			} else {
				dragStop = draggingFrom;
			}
			graphics().rootLayer().remove(dragging.getSprite());
			dragStop.addModifier(dragging.getModifier());
			dragStop.getLastModifier().getSprite().addListener(this);
			Debug.write(dragStop.getLastModifier().getModifier().toMathString());
			refreshEquationSprite();
		}
		dragging = null;
	}

	@Override
	public void onPointerDrag(Event event) {
		if (dragging != null) {
			dragging.getSprite().setTranslation(
					event.x() + dragOffset.x,
					event.y() + dragOffset.y);
			float distanceX = Math.abs(dragging.getSprite().tx() + dragging.getSprite().width() / 2 - (draggingFrom.getGroupLayer().tx() + draggingFrom.getGroupWidth() / 2));
			if (!dragging.isInverted() && distanceX > graphics().width() / 4 + 5) {
				dragging.invert();
			} else if (dragging.isInverted() && distanceX < graphics().width() / 4 - 5) {
				dragging.invert();
			}
		}
	}

	@Override
	public void onPointerCancel(Event event) {
		// TODO Auto-generated method stub

	}
}
