package tuxkids.tuxblocks.core.defense;

import playn.core.Color;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.TextFormat;
import playn.core.Pointer.Event;
import playn.core.util.Clock;
import pythagoras.f.FloatMath;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.layers.ImageLayerTintable;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.tutorial.Tutorial;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.widget.Button;
import tuxkids.tuxblocks.core.widget.Button.OnReleasedListener;

/**
 * UI Layer that appears when the player clicks on a {@link Tower}
 * in the {@link Grid} so that they can upgrade or delete it.
 */
public class UpgradePanel extends LayerWrapper {
	
	private final static float BUTTON_ALPHA = 0.8f;
	
	protected final Grid grid;
	protected final GroupLayer layer, dotsLayer;
	protected final float cellSize; // Grid's cell size
	protected final ImageLayer circleLayer, numberLayer; 
	protected final ImageLayerTintable confirmLayer;
	protected final Button buttonDelete, buttonUpgrade;
	protected final int color;
	protected final TextFormat format;
	
	protected boolean lastCanUpgrade; // to tell when canUpgrade() changes
	protected Tower tower; // currently selected Tower
	protected float targetAlpha = 1;
	
	public UpgradePanel(Grid grid, float cellSize, int color) {
		super(graphics().createGroupLayer());
		layer = (GroupLayer) layerAddable();
		layer.setAlpha(targetAlpha = 0);
		
		this.grid = grid;
		this.cellSize = cellSize;
		this.color = color;
		
		// draw the circle that goes around the Tower
		float circleRad = cellSize * 1.5f;
		float circleThickness = cellSize / 3;
		circleLayer = graphics().createImageLayer();
		circleLayer.setImage(CanvasUtils.createCircleCached(circleRad + circleThickness / 2, 
				Color.argb(0, 0, 0, 0), circleThickness, Colors.LIGHT_GRAY));
		circleLayer.setAlpha(0.5f);
		centerImageLayer(circleLayer);
		layer.add(circleLayer);
		
		// create the delete button
		float buttonSize = cellSize * 2f;
		buttonDelete = new Button(Constant.BUTTON_CANCEL, buttonSize, buttonSize, true);
		buttonDelete.setPosition(-circleRad, 0);
		buttonDelete.setTint(Colors.darker(color), color);
		buttonDelete.layerAddable().setAlpha(BUTTON_ALPHA);
		buttonDelete.setCancelSound();
		buttonDelete.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (tower != null && inButton) {
					delete();
				}
			}
		});
		layer.add(buttonDelete.layerAddable());
		
		// create the upgrade button
		buttonUpgrade = new Button(Constant.BUTTON_UP, buttonSize, buttonSize, true);
		buttonUpgrade.setPosition(circleRad, 0);
		buttonUpgrade.setTint(Colors.darker(color), color);
		buttonUpgrade.layerAddable().setAlpha(BUTTON_ALPHA);
		buttonUpgrade.setSuccessSound();
		buttonUpgrade.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) upgrade();
			}
		});
		layer.add(buttonUpgrade.layerAddable());
		
		// tiny confirm image that appears to indicate the
		// player must press the delete button again to confirm a delete
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
		
		// number layer to show the cost of upgrading
		format = createFormat(buttonSize / 3);
		numberLayer = graphics().createImageLayer();
		numberLayer.setDepth(1);
		offset = buttonSize / 2 * FloatMath.sqrt(0.5f);
		offset = 0;
		numberLayer.setTranslation(buttonUpgrade.x() + offset, 
				buttonUpgrade.y() + offset);
		layer.add(numberLayer);
		
		// dots to show a tower's current upgrade level
		dotsLayer = graphics().createGroupLayer();
		dotsLayer.setDepth(2);
		dotsLayer.setAlpha(0.75f);
		layer.add(dotsLayer);
		
		// go ahead and create the dots
		float dotRad = cellSize / 8;
		for (int i = 0; i < 3; i++) {
			ImageLayer layer = graphics().createImageLayer();
			layer.setImage(CanvasUtils.createCircleCached(dotRad, Colors.BLACK));
			centerImageLayer(layer);
			dotsLayer.add(layer);
		}
	}

	// can the currently selected Tower upgrade
	private boolean canUpgrade() {
		return tower != null && tower.canUpgrade() && grid.gameState().upgrades() >= tower.upgradeCost();
	}
	
	// upgrade the currently selected Tower
	private void upgrade() {
		if (!canUpgrade()) return;
		
		tower.upgrade();
		refreshDots();
		grid.gameState().useUpgrades(tower.upgradeCost());
		Tutorial.trigger(Trigger.Defense_TowerUpgraded);
	}
	
	// delete (or start to delete) the currently selected tower
	private void delete() {
		if (confirmLayer.visible()) {
			tower.destroy();
			setTower(null);
		} else {
			// show a warning first
			confirmLayer.setVisible(true);
			confirmLayer.setAlpha(0);
		}
	}
	
	/** Sets the Tower this panel is operating on, or null for none */
	public void setTower(Tower tower) {
		if (tower == this.tower) return;
		this.tower = tower;
		refreshNumberLayer();
		refreshDots();
		if (tower == null) { 
			fadeOut();
		} else {
			fadeIn();
			setTranslation(tower.position().x, tower.position().y);
			Tutorial.trigger(Trigger.Defense_TowerSelected);
		}
		confirmLayer.setVisible(false);
		lastCanUpgrade = !canUpgrade();
	}
	
	// redraw the Tower's dots when it's been upgrade
	private void refreshDots() {
		if (tower != null) {
			int level = tower.upgradeLevel();
			
			float rad = level == 1 ? 0 : cellSize / 5.5f;
			float deg = FloatMath.PI * 2 / level;
			
			// draw them in a radial fashion
			for (int i = 0; i < dotsLayer.size(); i++) {
				float x = rad * FloatMath.cos(i * deg);
				float y = rad * FloatMath.sin(i * deg);
				dotsLayer.get(i).setTranslation(x, y);
				dotsLayer.get(i).setVisible(i < level);
			}
		}
	}
	
	// refresh the cost of upgrading when we change towers
	private void refreshNumberLayer() {
		if (tower != null) {
			int upgradeCost = tower.upgradeCost();
			if (upgradeCost == 0) {
				numberLayer.setVisible(false);
			} else {
				numberLayer.setVisible(true);
				int color = canUpgrade() ? Colors.BLACK : Colors.DARK_GRAY;
				numberLayer.setImage(CanvasUtils.createText(
						"" + upgradeCost, format, color));
				centerImageLayer(numberLayer);
			}
		}
	}

	/** Fade in the panel from 0 alpha */
	protected void fadeIn() {
		layer.setAlpha(0);
		targetAlpha = 1;
	}
	
	/** Fade out the panel from 1 alpha */
	protected void fadeOut() {
		layer.setAlpha(1);
		targetAlpha = 0;
	}
	
	public void update(int delta) {
		if (lastCanUpgrade != canUpgrade()) {
			lastCanUpgrade = canUpgrade();
			buttonUpgrade.setEnabled(lastCanUpgrade);
			buttonUpgrade.layerAddable().setAlpha(lastCanUpgrade ? BUTTON_ALPHA : 0.2f);
			refreshNumberLayer();
		}
	}
	
	public void paint(Clock clock) {
		layer.setAlpha(lerpTime(layer.alpha(), targetAlpha, 0.99f, clock.dt(), 0.01f));
		layer.setVisible(layer.alpha() != 0);
		if (confirmLayer.visible()) {
			confirmLayer.setAlpha(lerpTime(confirmLayer.alpha(), 1, 0.99f, clock.dt(), 0.01f));	
		}
	}
}
