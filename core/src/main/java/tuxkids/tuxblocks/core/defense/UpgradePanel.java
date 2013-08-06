package tuxkids.tuxblocks.core.defense;

import playn.core.Color;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Pointer.Event;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Button;
import tuxkids.tuxblocks.core.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class UpgradePanel extends LayerWrapper {
	
	protected final GroupLayer layer;
	protected final float cellSize;
	protected final ImageLayer circleLayer;
	protected final Button buttonDelete, buttonUpgrade;
	protected final int color;
	
	protected Tower tower;
	protected float targetAlpha = 1;
	
	public UpgradePanel(float cellSize, int color) {
		super(graphics().createGroupLayer());
		layer = (GroupLayer) layerAddable();
		layer.setAlpha(targetAlpha = 0);
		
		this.cellSize = cellSize;
		this.color = color;
		
		float circleRad = cellSize * 1.5f;
		float circleThickness = cellSize / 3;
		circleLayer = graphics().createImageLayer();
		circleLayer.setImage(CanvasUtils.createCircleCached(circleRad + circleThickness / 2, 
				Color.argb(0, 0, 0, 0), circleThickness, Colors.LIGHT_GRAY));
		circleLayer.setAlpha(0.5f);
//		circleLayer.setInteractive(true);
		centerImageLayer(circleLayer);
		layer.add(circleLayer);
		
		float buttonSize = cellSize * 2f;
		buttonDelete = new Button(Constant.BUTTON_CANCEL, buttonSize, buttonSize, true);
		buttonDelete.setPosition(-circleRad, 0);
		buttonDelete.setTint(Colors.darker(color), color);
		buttonDelete.layerAddable().setAlpha(0.8f);
		buttonDelete.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (tower != null && inButton) {
					tower.destroy();
					setTower(null);
				}
			}
		});
		layer.add(buttonDelete.layerAddable());
		
		buttonUpgrade = new Button(Constant.BUTTON_UP, buttonSize, buttonSize, true);
		buttonUpgrade.setPosition(circleRad, 0);
		buttonUpgrade.setTint(Colors.darker(color), color);
		buttonUpgrade.layerAddable().setAlpha(0.8f);
		layer.add(buttonUpgrade.layerAddable());
	}
	
	public void setTower(Tower tower) {
		if (tower == this.tower) return;
		if (tower == null) { 
			fadeOut();
		} else {
			fadeIn();
		}
		this.tower = tower;
	}
	
	protected void fadeIn() {
		layer.setAlpha(0);
		targetAlpha = 1;
	}
	
	protected void fadeOut() {
		layer.setAlpha(1);
		targetAlpha = 0;
	}
	
	public void paint(Clock clock) {
		layer.setAlpha(lerpTime(layer.alpha(), targetAlpha, 0.99f, clock.dt(), 0.01f));
	}
}
