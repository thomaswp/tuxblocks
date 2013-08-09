package tuxkids.tuxblocks.core.defense;

import playn.core.Color;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.TextFormat;
import playn.core.Pointer.Event;
import playn.core.util.Clock;
import pythagoras.f.FloatMath;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Button;
import tuxkids.tuxblocks.core.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.layers.ImageLayerTintable;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class UpgradePanel extends LayerWrapper {
	
	private final static float BUTTON_ALPHA = 0.8f;
	
	protected final Grid grid;
	protected final GroupLayer layer;
	protected final float cellSize;
	protected final ImageLayer circleLayer, numberLayer; 
	protected final ImageLayerTintable confirmLayer;
	protected final Button buttonDelete, buttonUpgrade;
	protected final int color;
	protected final TextFormat format;
	
	protected Tower tower;
	protected float targetAlpha = 1;
	
	public UpgradePanel(Grid grid, float cellSize, int color) {
		super(graphics().createGroupLayer());
		layer = (GroupLayer) layerAddable();
		layer.setAlpha(targetAlpha = 0);
		
		this.grid = grid;
		this.cellSize = cellSize;
		this.color = color;
		
		float circleRad = cellSize * 1.5f;
		float circleThickness = cellSize / 3;
		circleLayer = graphics().createImageLayer();
		circleLayer.setImage(CanvasUtils.createCircleCached(circleRad + circleThickness / 2, 
				Color.argb(0, 0, 0, 0), circleThickness, Colors.LIGHT_GRAY));
		circleLayer.setAlpha(0.5f);
		centerImageLayer(circleLayer);
		layer.add(circleLayer);
		
		float buttonSize = cellSize * 2f;
		buttonDelete = new Button(Constant.BUTTON_CANCEL, buttonSize, buttonSize, true);
		buttonDelete.setPosition(-circleRad, 0);
		buttonDelete.setTint(Colors.darker(color), color);
		buttonDelete.layerAddable().setAlpha(BUTTON_ALPHA);
		buttonDelete.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (tower != null && inButton) {
					delete();
				}
			}
		});
		layer.add(buttonDelete.layerAddable());
		
		buttonUpgrade = new Button(Constant.BUTTON_UP, buttonSize, buttonSize, true);
		buttonUpgrade.setPosition(circleRad, 0);
		buttonUpgrade.setTint(Colors.darker(color), color);
		buttonUpgrade.layerAddable().setAlpha(BUTTON_ALPHA);
		buttonUpgrade.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) upgrade();
			}
		});
		layer.add(buttonUpgrade.layerAddable());
		
		float confirmSize = buttonSize / 4;
		confirmLayer = new ImageLayerTintable();
		confirmLayer.setImage(assets().getImage(Constant.BUTTON_CANCEL));
		confirmLayer.setSize(confirmSize, confirmSize);
		confirmLayer.setDepth(1);
		confirmLayer.setVisible(false);
		confirmLayer.setTint(Colors.darker(color));
		float offset = (buttonSize + confirmSize) / 2 * FloatMath.sqrt(0.5f);
		confirmLayer.setTranslation(buttonDelete.x() + offset, 
				buttonDelete.y() - offset);
		centerImageLayer(confirmLayer);
		layer.add(confirmLayer.layerAddable());
		
		format = createFormat(buttonSize / 3);
		numberLayer = graphics().createImageLayer();
		numberLayer.setDepth(1);
		offset = buttonSize / 2 * FloatMath.sqrt(0.5f);
		offset = 0;
		numberLayer.setTranslation(buttonUpgrade.x() + offset, 
				buttonUpgrade.y() + offset);
		layer.add(numberLayer);
	}

	private boolean canUpgrade() {
		return tower != null && tower.canUpgrade() && grid.gameState().upgrades() >= tower.upgradeCost();
	}
	
	private void upgrade() {
		if (!canUpgrade()) return;
		
		tower.upgrade();
		grid.gameState().useUpgrades(tower.upgradeCost());
	}
	
	private void delete() {
		if (confirmLayer.visible()) {
			tower.destroy();
			setTower(null);
		} else {
			confirmLayer.setVisible(true);
			confirmLayer.setAlpha(0);
		}
	}
	
	public void setTower(Tower tower) {
		if (tower == this.tower) return;
		this.tower = tower;
		refreshNumberLayer();
		if (tower == null) { 
			fadeOut();
		} else {
			fadeIn();
			setTranslation(tower.position().x, tower.position().y);
		}
		confirmLayer.setVisible(false);
	}
	
	private void refreshNumberLayer() {
		if (tower != null) {
			int upgradeCost = tower.upgradeCost();
			if (upgradeCost == 0) {
				numberLayer.setVisible(false);
			} else {
				numberLayer.setVisible(true);
				numberLayer.setImage(CanvasUtils.createString(
						format, "" + upgradeCost, Colors.BLACK));
				centerImageLayer(numberLayer);
				if (upgradeCost == 1) {
					numberLayer.setOrigin(numberLayer.originX() + 2, 
							numberLayer.originY());
				}
			}
		}
	}

	protected void fadeIn() {
		layer.setAlpha(0);
		targetAlpha = 1;
	}
	
	protected void fadeOut() {
		layer.setAlpha(1);
		targetAlpha = 0;
	}
	
	public void update(int delta) {
		boolean canUpgrade = canUpgrade();
		buttonUpgrade.setEnabled(canUpgrade);
		buttonUpgrade.layerAddable().setAlpha(canUpgrade ? BUTTON_ALPHA : 0.2f);
		numberLayer.setAlpha(buttonUpgrade.layerAddable().alpha());
	}
	
	public void paint(Clock clock) {
		layer.setAlpha(lerpTime(layer.alpha(), targetAlpha, 0.99f, clock.dt(), 0.01f));
		layer.setVisible(layer.alpha() != 0);
		if (confirmLayer.visible()) {
			confirmLayer.setAlpha(lerpTime(confirmLayer.alpha(), 1, 0.99f, clock.dt(), 0.01f));	
		}
	}
}
