package tuxkids.tuxblocks.core.defense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.util.Clock;
import pythagoras.f.Vector;
import pythagoras.i.Point;
import tripleplay.particle.Emitter;
import tripleplay.particle.Particles;
import tripleplay.particle.TuxParticles;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.defense.projectile.ChainProjectile;
import tuxkids.tuxblocks.core.defense.projectile.Projectile;
import tuxkids.tuxblocks.core.defense.round.Level;
import tuxkids.tuxblocks.core.defense.round.Round;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.walker.Walker;
import tuxkids.tuxblocks.core.effect.Effect;
import tuxkids.tuxblocks.core.layers.ImageLayerTintable;
import tuxkids.tuxblocks.core.tutorial.Highlightable;
import tuxkids.tuxblocks.core.tutorial.Tutorial;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.MultiList;

public class Grid extends PlayNObject implements Highlightable {

	private final static boolean SHOW_GRID = false;
	private final static int DOUBLE_CLICK = 300;
	private final static int LONG_CLICK = 400;

	private final int cellSize;
	private final int rows, cols;
	private final GroupLayer layer, gridLayer, overlayLayer;
	private final ImageLayer selectorLayer;
	private final ImageLayerTintable gridSprite;
	private final boolean[][] passability;
	private final UpgradePanel upgradePanel;
	private final List<Walker> walkers = new ArrayList<Walker>();
	private final List<Projectile> projectiles = new ArrayList<Projectile>();
	private final List<Tower> towers = new ArrayList<Tower>();
	private final List<Effect> effects = new ArrayList<Effect>();
	@SuppressWarnings("unchecked")
	private final MultiList<GridObject> gridObjects = new MultiList<GridObject>(walkers, projectiles, towers, effects);
	private final Point walkerStart, walkerDestination;
	private final Particles particles;
	private final GameState state;
	
	private Tower toPlace;
	private ImageLayer toPlaceRadius;
	private List<Point> currentPath;
	private float targetAlpha = 1;
	private int towerColor;
	private DoubleClickListener doubleClickListener;
	private boolean holdingClick;
	private int startLongClick;
	private Point selectedPoint = new Point();
	
	public UpgradePanel upgradePanel() {
		return upgradePanel;
	}
	
	public void setDoubleClickListener(DoubleClickListener doubleClickListener) {
		this.doubleClickListener = doubleClickListener;
	}

	public Particles particles() {
		return particles;
	}
	
	public Level level() {
		return state.level();
	}

	public int towerColor() {
		return towerColor;
	}

	public int width() {
		return cols * cellSize;
	}

	public int height() {
		return rows * cellSize;
	}

	public int rows() {
		return rows;
	}

	public int cols() {
		return cols;
	}

	public List<Point> currentPath() {
		return currentPath;
	}

	public GroupLayer layer() {
		return layer;
	}

	public boolean[][] getPassability() {
		return passability;
	}

	public float cellSize() {
		return cellSize;
	}

	public GameState gameState() {
		return state;
	}

	public void setTowerColor(int themeColor) {
		this.towerColor = themeColor;
	}

	public Grid(GameState gameState, int rows, int cols, int maxWidth, int maxHeight) {
		this.state = gameState;
		this.rows = rows; this.cols = cols;
		
		passability = new boolean[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				passability[i][j] = true;
			}
		}
		int maxRowSize = maxHeight / rows, maxColSize = maxWidth / cols;
		cellSize = Math.min(maxRowSize, maxColSize);
		
		layer = graphics().createGroupLayer();
		gridLayer = graphics().createGroupLayer();
		layer.add(gridLayer);
		overlayLayer = graphics().createGroupLayer();
		overlayLayer.setDepth(1);
		layer.add(overlayLayer);

		upgradePanel = new UpgradePanel(this, cellSize, state.themeColor());
		upgradePanel.setDepth(10);
		layer.add(upgradePanel.layerAddable());
		
		walkerStart = new Point(rows / 2, 0);
		walkerDestination = new Point(rows / 2, cols - 1);
		for (int i = 0; i < rows; i++) {
			if (i != rows / 2) {
				passability[i][0] = false;
				passability[i][cols - 1] = false;
			}
		}
		for (int i = 0; i < cols; i++) {
			passability[0][i] = false;
			passability[rows - 1][i] = false;
		}
		refreshPath();
		gridSprite = createGridSprite();

		selectorLayer = graphics().createImageLayer();
		selectorLayer.setImage(CanvasUtils.createCircle(cellSize, Color.argb(0, 0, 0, 0), cellSize / 3, Colors.GRAY));
		selectorLayer.setAlpha(0.75f);
		selectorLayer.setVisible(false);
		selectorLayer.setDepth(10);
		centerImageLayer(selectorLayer);
		layer.add(selectorLayer);
		
		particles = new TuxParticles();
	}

	public Emitter createEmitter(int maxParticles, Image image) {
		Emitter e = particles.createEmitter(maxParticles, image, overlayLayer);
		return e;
	}

	public void fadeIn(float targetAlpha) {
		this.targetAlpha = targetAlpha;
		layer.setAlpha(0);
	}

	public void update(int delta) {
		if (layer.alpha() < targetAlpha * 0.99f) {
			layer.setAlpha(lerpTime(layer.alpha(), targetAlpha, 0.99f, delta));
		} else {
			layer.setAlpha(targetAlpha);
		}

		if (level().waitingForFinish() && walkers.size() == 0) {
			onRoundCompleted(level().currentRound());
			level().finishRound();
			Audio.se().play(Constant.SE_SUCCESS_SPECIAL);
		}
		Walker walker = level().update(delta);
		if (walker != null) {
			addWalker(walker.place(this, walkerStart, walkerDestination, 0));
		}

		int nObjects = gridObjects.size();
		for (int i = 0; i < nObjects; i++) {
			GridObject gridObject = gridObjects.get(i);
			if (gridObject.update(delta)) {
				gridObjects.remove(gridObject);
				i--; nObjects--;
				continue;
			}
		}

		updateToPlace();
		
		upgradePanel.update(delta);
	}

	public void paint(Clock clock) {
		int nObjects = gridObjects.size();
		for (int i = 0; i < nObjects; i++) {
			GridObject gridObject = gridObjects.get(i);
			gridObject.paint(clock);
		}
		particles.paint(clock);
		upgradePanel.paint(clock);
		updateSelecting();
	}
	
	private void updateSelecting() {
		if (holdingClick && PlayN.tick() - startLongClick > LONG_CLICK) {
			selectorLayer.setVisible(true);
			selectorLayer.setTranslation((selectedPoint.y + 0.5f) * cellSize, 
					(selectedPoint.x + 0.5f) * cellSize);
		} else {
			selectorLayer.setVisible(false);
		}
	}

	private void refreshPath() {
		currentPath = Pathing.getPath(this, walkerStart, walkerDestination);
	}

	public void addWalker(Walker walker) {
		walkers.add(walker);
		gridLayer.add(walker.layerAddable());
	}

	private ImageLayerTintable createGridSprite() {

		//List<Point> path = Pathing.getPath(this, new Point(0, 0), new Point(rows - 1, cols - 1));

		CanvasImage image = graphics().createImage(width(), height());
		Canvas canvas = image.canvas();
		canvas.setFillColor(Colors.WHITE);
		canvas.fillRect(0, 0, width(), height());
		canvas.setStrokeColor(Colors.BLACK);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int x = j * cellSize;
				int y = i * cellSize;
				canvas.setFillColor(Colors.WHITE);
				//				if (path != null && path.contains(new Point(i,j))) {
				//					canvas.setFillColor(Colors.BLUE);
				//				} else 
				if (!passability[i][j]) {
					canvas.setFillColor(Colors.GRAY);
				}
				canvas.fillRect(x, y, cellSize, cellSize);
				if (SHOW_GRID) canvas.strokeRect(x, y, cellSize, cellSize);
			}
		}
		ImageLayerTintable gridSprite = new ImageLayerTintable(image);
		//gridSprite.setAlpha(0.2f);
		gridLayer.add(gridSprite.layerAddable());
		gridSprite.setDepth(-1);
		
		gridSprite.addListener(new Listener() {
			private int lastClick;
			
			@Override
			public void onPointerStart(Event event) { 
				hideUpgradePanel();
				startLongClick = PlayN.tick();
				holdingClick = true;
				updateDragPos(event);
			}
			
			@Override
			public void onPointerEnd(Event event) {
				int time = PlayN.tick();
				if (time - lastClick < DOUBLE_CLICK) {
					lastClick = 0;
					if (doubleClickListener != null) {
						doubleClickListener.wasDoubleClicked();
					}
				} else {
					lastClick = time;
				}
				hideUpgradePanel();
				holdingClick = false;
				
				if (time - startLongClick > LONG_CLICK) {
					for (Tower tower : towers) {
						int rMin = tower.coordinates().x;
						int rMax = rMin + tower.rows() - 1;
						int cMin = tower.coordinates().y;
						int cMax = cMin + tower.cols() - 1;
						if (selectedPoint.x >= rMin && selectedPoint.x <= rMax &&
								selectedPoint.y >= cMin && selectedPoint.y <= cMax) {
							upgradePanel.setTower(tower);
							break;
						}
					}
				}
			}
			
			@Override
			public void onPointerDrag(Event event) {
				updateDragPos(event);
			}
			
			private void updateDragPos(Event event) {
				float placeX = getPlaceX(event.x());
				float placeY = getPlaceY(event.y());
				getCell(placeX, placeY, selectedPoint);
			}
			
			@Override
			public void onPointerCancel(Event event) { }
		});
		
		return gridSprite;
	}
	
	public void hideUpgradePanel() {
		upgradePanel.setTower(null);
	}
	
	public void towerClicked(Tower tower) {
		upgradePanel.setTower(tower);
	}

	public Point getCell(float x, float y, Point point) {
		int r = Math.min(Math.max((int)y / cellSize, 0), rows - 1);
		int c = Math.min(Math.max((int)x / cellSize, 0), cols - 1);
		point.setLocation(r, c);
		return point;
	}
	
	public Point getCell(float x, float y) {
		return getCell(x, y, new Point());
	}

	public Point getCell(float x, float y, float width, float height) {
		return getCell(x - width / 2 + cellSize() / 2, y - height / 2 + cellSize() / 2);
	}

	private float getPlaceX(float globalX) {
		float placeX = (globalX - getGlobalTx(gridLayer)) / getGlobalScaleX(gridLayer);
		if (PlayN.platform().touch().hasTouch()) placeX -= cellSize() * 1.5;
		return  placeX;
	}

	private float getPlaceY(float globalY) {
		float placeY = (globalY - getGlobalTy(gridLayer)) / getGlobalScaleY(gridLayer);
		if (PlayN.platform().touch().hasTouch()) placeY -= cellSize() * 1.5;
		return  placeY;
	}

	public void startPlacement(Tower toPlace) {
		this.toPlace = toPlace;
		toPlace.preview(this);
		toPlace.layer().setVisible(false);
		overlayLayer.add(toPlace.layerAddable());
		validPlacementMap.clear();

		toPlaceRadius = graphics().createImageLayer(toPlace.createRadiusImage());
		centerImageLayer(toPlaceRadius);
		gridLayer.add(toPlaceRadius);
		updateToPlace();
		
		upgradePanel.setTower(null);
	}

	public void updatePlacement(float globalX, float globalY) {
		float placeX = getPlaceX(globalX), placeY = getPlaceY(globalY);
		if (toPlace != null) {
			Point cell = getCell(placeX, placeY, toPlace.baseWidth(), toPlace.baseHeight());
			toPlace.setCoordinates(cell);
			toPlace.layer().setVisible(!isOutOfBounds(placeX, placeY));
			updateToPlace();
		}
	}

	public boolean endPlacement(float globalX, float globalY) {
		boolean canPlace = canPlace();
		if (canPlace) {
			toPlace.place(this, toPlace.coordinates());
			overlayLayer.remove(toPlace.layerAddable());
			gridLayer.add(toPlace.layerAddable());
			towers.add(toPlace);
			toPlaceRadius.destroy();
			refreshPath();
			Tutorial.trigger(Trigger.Defense_TowerDropped);
		} else if (toPlace != null) {
			toPlace.layer().destroy();
		}
		toPlace = null;
		toPlaceRadius.destroy();
		toPlaceRadius = null;
		return canPlace;
	}

	private void updateToPlace() {
		if (toPlace == null) return;
		toPlace.layer().setAlpha(canPlace() ? 1 : 0.5f);
		toPlaceRadius.setTranslation(toPlace.position().x, toPlace.position().y);
		toPlaceRadius.setVisible(toPlace.layer().visible() && toPlace.layer().alpha() == 1);
	}

	private boolean canPlace() {
		if (toPlace == null) return false;

		Point p = toPlace.coordinates();
		int rows = toPlace.rows(), cols = toPlace.cols();

		if (p.x < 0 || p.x + rows > this.rows || p.y < 0 || p.y + cols > this.cols){
			return false;
		}

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				for (Walker walker : walkers) {
					Point walkerPos = walker.coordinates();
					if (walkerPos.x == p.x + i && walkerPos.y == p.y + j) {
						return false;
					}
				}
			}
		}

		if (validPlacementMap.containsKey(p)) return validPlacementMap.get(p);
		boolean canPlace = canPlaceStatic(p);
		validPlacementMap.put(p.clone(), canPlace);

		return canPlace;
	}

	private HashMap<Point, Boolean> validPlacementMap = new HashMap<Point, Boolean>();
	private boolean canPlaceStatic(Point p) {

		int rows = toPlace.rows(), cols = toPlace.cols();

		if (p.equals(walkerStart)) return false;

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (!passability[p.x+i][p.y+j]) return false;
			}
		}

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				passability[p.x+i][p.y+j] = false;
			}
		}
		List<Point> path = Pathing.getPath(this, walkerStart, walkerDestination);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				passability[p.x+i][p.y+j] = true;
			}
		}
		return path != null;
		//		return true;
	}

	public boolean fireProjectile(Tower tower) {
		if (walkers.size() == 0) return false;
		Walker target = null;
		float targetDis = Float.MAX_VALUE;
		for (Walker walker : walkers) {
			if (!walker.isAlive()) continue;
			float dis = walker.position().distance(tower.position());
			if (dis < tower.range() * cellSize) {
				if (walker == tower.lastTarget()) {
					target = walker;
					targetDis = dis;
					break;
				} else if (dis < targetDis) {
					target = walker;
					targetDis = dis;
				}
			}
		}
		tower.setLastTarget(target);
		if (target == null) return false;
		Projectile p = tower.createProjectile();
		p.place(this, target, tower);
		gridLayer.add(p.layer());
		projectiles.add(p);
		return true;
	}

	public boolean fireProjectile(ChainProjectile from) {
		if (walkers.size() == 0) return false;
		Walker target = null;
		float targetDis = Float.MAX_VALUE;
		for (Walker walker : walkers) {
			if (!walker.isAlive()) continue;
			if (from.partOfChain(walker)) continue;
			float dis = walker.position().distance(from.target().position());
			if (dis < from.range() * cellSize && dis < targetDis) {
				target = walker;
				targetDis = dis;
			}
		}
		if (target == null) return false;
		ChainProjectile p = from.createProjectile();
		p.place(this, target, from);
		gridLayer.add(p.layer());
		projectiles.add(p);
		return true;
	}

	public void dealDamage(Tower source, Walker target, float damage, Vector hit) {
		if (source.splashRadius() == 0) {
			target.damage(damage);
			source.addBuffs(target);
		} else {
			float actualRadius = source.splashRadius() * cellSize;
			for (Walker walker : walkers) {
				float distance = walker.position().distance(hit);
				float perc = (actualRadius - distance) / actualRadius;
				float dealt = perc * damage;
				if (dealt > 0) {
					walker.damage(dealt);
					source.addBuffs(walker);
				}
			}
		}
	}

	public Walker getHitWalker(Vector position) {
		for (Walker walker : walkers) {
			if (!walker.isAlive()) continue;
			float dx = walker.position().x - position.x;
			float dy = walker.position().y - position.y;
			if (Math.abs(dx) < walker.width() / 2 && Math.abs(dy) < walker.height() / 2) {
				return walker;
			}
		}
		return null;
	}

	public boolean isOutOfBounds(Vector position) {
		return isOutOfBounds(position.x, position.y);
	}

	public boolean isOutOfBounds(float x, float y) {
		return x < 0 || y < 0 || x >= width() || y >= height();
	}

	public void addEffect(Effect effect) {
		effects.add(effect);
		effect.layer().setDepth(5);
		gridLayer.add(effect.layer());
	}

	public void onRoundCompleted(Round round) {
		round.winRound(state);
		Tutorial.trigger(Trigger.Defense_RoundOver);
	}

	public void loseLife() {
		state.loseLife();
	}

	public void addPoints(int points) {
		state.addPoints(points);
	}
	
	public interface DoubleClickListener {
		void wasDoubleClicked();
	}

	private Highlighter highlighter = new Highlighter() {
		@Override
		protected void setTint(int baseColor, int tintColor, float perc) {
			gridSprite.setTint(baseColor, tintColor, perc);
		}
		
		@Override
		protected ColorState colorState() {
			return new ColorState() {
				@Override
				public void reset() {
					gridSprite.setTint(Colors.WHITE);
				}
			};
		}
	};

	@Override
	public Highlighter highlighter() {
		return highlighter;
	}

}
