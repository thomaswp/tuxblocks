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
import tuxkids.tuxblocks.core.defense.effect.Effect;
import tuxkids.tuxblocks.core.defense.projectile.ChainProjectile;
import tuxkids.tuxblocks.core.defense.projectile.Projectile;
import tuxkids.tuxblocks.core.defense.round.Level;
import tuxkids.tuxblocks.core.defense.round.Round;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.walker.Walker;
import tuxkids.tuxblocks.core.layers.ImageLayerTintable;
import tuxkids.tuxblocks.core.tutorial.Highlightable;
import tuxkids.tuxblocks.core.tutorial.Tutorial;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.MultiList;
import tuxkids.tuxblocks.core.utils.PlayNObject;
import tuxkids.tuxblocks.core.widget.menu.MainMenuLayer;

/**
 * The game grid, upon which the player places {@link Tower}s and which
 * {@link Walker}s try to cross from one side to the other. This class keep 
 * track of both of these, along with the {@link UpgradePanel} and the placing
 * of new {@link Tower}s.
 * <p/>
 * A note about <b>Grid coordinates</b>: they are often represented as {@link Point}s, 
 * and when they are, the x-coordinate of the Point represents the row, and 
 * y-coordinate the columns. This may be counter-intuitive for some, as the
 * row corresponds to a y-value in pixels, and vice versa.
 */
public class Grid extends PlayNObject implements Highlightable {

	/** Draw actual grid lines on the Grid */
	private final static boolean SHOW_GRID = false;
	private final static int DOUBLE_CLICK = 300; // max ms between a double-click
	private final static int LONG_CLICK = 400; // how long (in ms) a click has to be to be "long"

	private final int cellSize;
	private final int rows, cols;
	private final GroupLayer layer, gridLayer, overlayLayer;
	private final ImageLayer selectorLayer;
	private final ImageLayerTintable gridSprite;
	private final boolean[][] passability;
	private final UpgradePanel upgradePanel;
	private final List<Walker> walkers = new ArrayList<Walker>();
	private final List<Projectile> projectiles = new ArrayList<Projectile>();
	protected final List<Tower> towers = new ArrayList<Tower>();
	private final List<Effect> effects = new ArrayList<Effect>();
	@SuppressWarnings("unchecked") // cumulative list of all the GridObjects 
	private final MultiList<GridObject> gridObjects = new MultiList<GridObject>(walkers, projectiles, towers, effects);
	private final Point walkerStart, walkerDestination; // the Walkers try to go from the former to the latter
	private final Particles particles; // for managing Missile explosions
	private final GameState state;
	private final ImageLayer rangeIndicatorLayer;
	
	private Tower toPlace; // tower currently being placed
	private List<Point> currentPath;
	
	private DoubleClickListener doubleClickListener; // for when the Grid is double-clicked
	private boolean holdingClick; // is the player holding down a click
	private int startLongClick; // when did the player start holding a long click
	private Point selectedPoint = new Point(); // location when holding down a click to select a Tower
	
	public UpgradePanel upgradePanel() {
		return upgradePanel;
	}
	
	/** Sets a listener for when the Grid is double-clicked */
	public void setDoubleClickListener(DoubleClickListener doubleClickListener) {
		this.doubleClickListener = doubleClickListener;
	}

	// the current level
	private Level level() {
		return state.level();
	}

	/** The primary {@link Tower} color on this Grid */
	public int towerColor() {
		return state.themeColor();
	}

	/** The Grid's width in pixels */
	public int width() {
		return cols * cellSize;
	}

	/** The Grid's height in pixels */
	public int height() {
		return rows * cellSize;
	}

	public int rows() {
		return rows;
	}

	public int cols() {
		return cols;
	}

	/** The current shortest path from the grid's start to end location */
	public List<Point> currentPath() {
		return currentPath;
	}

	public GroupLayer layer() {
		return layer;
	}

	/** Gets the Grid's passability matrix */
	public boolean[][] getPassability() {
		return passability;
	}

	/** Gets the size (in pixels) of one cell of the Grid */
	public float cellSize() {
		return cellSize;
	}

	/** Gets the GameState associated with this Grid */
	public GameState gameState() {
		return state;
	}

	public Grid(GameState gameState, int rows, int cols, int maxWidth, int maxHeight) {
		this.state = gameState;
		this.rows = rows; this.cols = cols;
		
		// determine cellSize
		int maxRowSize = maxHeight / rows, maxColSize = maxWidth / cols;
		cellSize = Math.min(maxRowSize, maxColSize);
		
		// create group layers
		layer = graphics().createGroupLayer();
		gridLayer = graphics().createGroupLayer();
		layer.add(gridLayer);
		overlayLayer = graphics().createGroupLayer();
		overlayLayer.setDepth(1);
		layer.add(overlayLayer);

		// init UpgradePanel
		upgradePanel = new UpgradePanel(this, cellSize, state.themeColor());
		upgradePanel.setDepth(10);
		layer.add(upgradePanel.layerAddable());
		
		// init passability matrix
		passability = new boolean[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				passability[i][j] = true;
			}
		}
		// make the borders of the Grid impassable
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
		refreshPath(); // create the first path
		
		gridSprite = createGridSprite();

		// create the selectorLayer for when the player drags a click to select a Tower
		selectorLayer = graphics().createImageLayer();
		selectorLayer.setImage(CanvasUtils.createCircle(cellSize, Color.argb(0, 0, 0, 0), cellSize / 3, Colors.GRAY));
		selectorLayer.setAlpha(0.75f);
		selectorLayer.setVisible(false);
		selectorLayer.setDepth(10);
		centerImageLayer(selectorLayer);
		layer.add(selectorLayer);
		
		particles = new TuxParticles();
		
		// indicates a Tower's range when its clicked on
		rangeIndicatorLayer = graphics().createImageLayer();
		rangeIndicatorLayer.setDepth(0);
		gridLayer.add(rangeIndicatorLayer);
	}

	/** Creates an {@link Emitter} for this Grid's {@link Particles} */
	public Emitter createEmitter(int maxParticles, Image image) {
		Emitter e = particles.createEmitter(maxParticles, image, overlayLayer);
		return e;
	}

	public void update(int delta) {
		if (MainMenuLayer.showing()) return;

		if (level().waitingForFinish() && walkers.size() == 0) {
			// if we're waiting for the player to kill all the Walkers
			// and they have, tell the Level that
			onRoundCompleted(level().currentRound());
			level().finishRound();
			state.finishRound();
			Audio.se().play(Constant.SE_SUCCESS_SPECIAL);
		}
		
		// spawn a new Walker if we need to
		Walker walker = level().popWalker();
		if (walker != null) {
			addWalker(walker.place(this, walkerStart, walkerDestination, 0));
		}

		// update the GridObjects and remove those that are destroyed
		int nObjects = gridObjects.size(); // size is a non-trivial calculation, so cache it
		for (int i = 0; i < nObjects; i++) {
			GridObject gridObject = gridObjects.get(i);
			if (gridObject.update(delta)) {
				// returning true indicates that an Object is destroyed
				gridObjects.remove(gridObject);
				i--; nObjects--;
				continue;
			}
		}

		// update the player's placement of a new Tower
		updateToPlace();
		
		upgradePanel.update(delta);
	}

	public void paint(Clock clock) {
		if (MainMenuLayer.showing()) return;
		
		// paint All the Things
		int nObjects = gridObjects.size();
		for (int i = 0; i < nObjects; i++) {
			GridObject gridObject = gridObjects.get(i);
			gridObject.paint(clock);
		}
		particles.paint(clock);
		upgradePanel.paint(clock);
		updateSelecting();
	}
	
	// update the player's selection of a Tower by clicking and dragging
	private void updateSelecting() {
		if (holdingClick && PlayN.tick() - startLongClick > LONG_CLICK) {
			// if they're holding down a click, update it
			selectorLayer.setVisible(true);
			selectorLayer.setTranslation((selectedPoint.y + 0.5f) * cellSize, 
					(selectedPoint.x + 0.5f) * cellSize);
		} else {
			selectorLayer.setVisible(false);
		}
	}

	// recalculates the path from start to destination
	private void refreshPath() {
		currentPath = Pathing.getPath(this, walkerStart, walkerDestination);
	}

	/** Adds the given walker to this Grid */
	public void addWalker(Walker walker) {
		walkers.add(walker);
		gridLayer.add(walker.layerAddable());
	}

	// creates the background sprite for the Grid
	private final ImageLayerTintable createGridSprite() {

		CanvasImage image = graphics().createImage(width(), height());
		Canvas canvas = image.canvas();
		canvas.setFillColor(Colors.WHITE);
		canvas.fillRect(0, 0, width(), height());
		canvas.setStrokeColor(Colors.BLACK);
		
		// indicate walls that are impassable
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int x = j * cellSize;
				int y = i * cellSize;
				canvas.setFillColor(Colors.WHITE);
				if (!passability[i][j]) {
					canvas.setFillColor(Colors.GRAY);
				}
				canvas.fillRect(x, y, cellSize, cellSize);
				if (SHOW_GRID) canvas.strokeRect(x, y, cellSize, cellSize);
			}
		}
		
		// create the Layer
		ImageLayerTintable gridSprite = new ImageLayerTintable(image);
		gridLayer.add(gridSprite.layerAddable());
		gridSprite.setDepth(-1);
		
		// add a double-click/long-click listener
		gridSprite.addListener(new Listener() {
			private int lastClick;
			
			@Override
			public void onPointerStart(Event event) { 
				hideUpgradePanel();
				// start a potential click
				startLongClick = PlayN.tick();
				holdingClick = true;
				updateDragPos(event);
			}
			
			@Override
			public void onPointerEnd(Event event) {
				int time = PlayN.tick();
				if (time - lastClick < DOUBLE_CLICK) {
					// we have ourselves a double-click
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
					// select a tower if we finished a long-click
					for (Tower tower : towers) {
						int rMin = tower.coordinates().x;
						int rMax = rMin + tower.rows() - 1;
						int cMin = tower.coordinates().y;
						int cMax = cMin + tower.cols() - 1;
						if (selectedPoint.x >= rMin && selectedPoint.x <= rMax &&
								selectedPoint.y >= cMin && selectedPoint.y <= cMax) {
							showUpgradePanel(tower);
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
	
	/** Hides the {@link UpgradePanel} and deselects its {@link Tower} */
	public void hideUpgradePanel() {
		upgradePanel.setTower(null);
		rangeIndicatorLayer.setImage(null);
	}
	
	/** Shows the {@link UpgradePanel} with the given Tower selected */
	public void showUpgradePanel(Tower tower) {
		upgradePanel.setTower(tower);

		rangeIndicatorLayer.setImage(tower.createRadiusImage());
		rangeIndicatorLayer.setTranslation(tower.position().x, tower.position().y);
		rangeIndicatorLayer.setVisible(true);
		rangeIndicatorLayer.setAlpha(0.5f);
		centerImageLayer(rangeIndicatorLayer);
	}

	/** Sets the given Point to the row and column of this (x,y) position */
	public Point getCell(float x, float y, Point point) {
		int r = Math.min(Math.max((int)y / cellSize, 0), rows - 1);
		int c = Math.min(Math.max((int)x / cellSize, 0), cols - 1);
		point.setLocation(r, c);
		return point;
	}
	
	/** Returns a new Point, the row and column of which are set to the give (x,y) position */
	public Point getCell(float x, float y) {
		return getCell(x, y, new Point());
	}

	/**
	 * Calls {@link Grid#getCell(float, float)}, but adjusts the given x and y with the
	 * given width and height such that the Point returned will be the center of the rect
	 * made up of x, y, width and height.
	 */
	public Point getCell(float x, float y, float width, float height) {
		return getCell(x - width / 2 + cellSize() / 2, y - height / 2 + cellSize() / 2);
	}

	// converts "global" touch x and y coordinates to Grid coordinates
	
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

	/** Starts the placement of the given Tower on the Grid */
	public void startPlacement(Tower toPlace) {
		this.toPlace = toPlace;
		toPlace.preview(this); // let the Tower know it's tied to this Grid
		toPlace.layer().setVisible(false); // don't show it until its over the Grid
		overlayLayer.add(toPlace.layerAddable());
		validPlacementMap.clear(); // reset our map of valid position to place a Tower

		hideUpgradePanel();
		
		// show the range indicator
		rangeIndicatorLayer.setImage(toPlace.createRadiusImage());
		rangeIndicatorLayer.setAlpha(1);
		centerImageLayer(rangeIndicatorLayer);
		updateToPlace();
		
	}

	/** Updates the current {@link Tower}'s placement to the given position */
	public void updatePlacement(float globalX, float globalY) {
		float placeX = getPlaceX(globalX), placeY = getPlaceY(globalY);
		if (toPlace != null) {
			Point cell = getCell(placeX, placeY, toPlace.baseWidth(), toPlace.baseHeight());
			toPlace.setCoordinates(cell);
			// make the Tower invisible if it's out of bounds
			toPlace.layer().setVisible(!isOutOfBounds(placeX, placeY));
			updateToPlace();
		}
	}

	/** Finishes the placement of the current {@link Tower} */
	public boolean endPlacement(float globalX, float globalY) {
		boolean canPlace = canPlace();
		if (canPlace) {
			placeTower(toPlace, toPlace.coordinates);
			Tutorial.trigger(Trigger.Defense_TowerDropped, toPlace.coordinates);
		} else if (toPlace != null) {
			toPlace.layer().destroy();
			Tutorial.trigger(Trigger.Defense_BadTowerPlacement);
		}
		toPlace = null;
		rangeIndicatorLayer.setImage(null);
		return canPlace;
	}
	

	/** Adds the given Tower to this Grid at the given coordinates */
	public void placeTower(Tower tower, Point point) {
		tower.place(this, point);
		gridLayer.add(tower.layerAddable());
		towers.add(tower);
		refreshPath();
	}

	/** Cancels the placement of the current {@link Tower} */
	public void cancelPlacement() {
		toPlace.layer().destroy();
		toPlace = null;
		rangeIndicatorLayer.setImage(null);
	}

	// updates placement positions and alpha
	private void updateToPlace() {
		if (toPlace == null) return;
		toPlace.layer().setAlpha(canPlace() ? 1 : 0.5f);
		rangeIndicatorLayer.setTranslation(toPlace.position().x, toPlace.position().y);
		rangeIndicatorLayer.setVisible(toPlace.layer().visible() && toPlace.layer().alpha() == 1);
	}

	// returns true if the Tower currently being placed can be placed at its current location
	private boolean canPlace() {
		if (toPlace == null) return false;

		Point p = toPlace.coordinates();
		int rows = toPlace.rows(), cols = toPlace.cols();

		// if it's out of the grid, return false
		if (p.x < 0 || p.x + rows > this.rows || p.y < 0 || p.y + cols > this.cols){
			return false;
		}

		// can't place on top of a Walker on the Grid
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

		// check if this position would cut off all valid paths to the goal
		// and return false if so
		
		// cache results in the validPlacementMap
		if (validPlacementMap.containsKey(p)) return validPlacementMap.get(p);
		boolean canPlace = canPlaceStatic(p);
		validPlacementMap.put(p.clone(), canPlace);

		return canPlace;
	}

	private HashMap<Point, Boolean> validPlacementMap = new HashMap<Point, Boolean>();
	
	// returns true if the given point is valid for the placement of the current Tower
	// these values are "static" in the sense that they do no change until another 
	// Tower is placed
	private boolean canPlaceStatic(Point p) {

		int rows = toPlace.rows(), cols = toPlace.cols();

		if (p.equals(walkerStart)) return false;

		// return false if any of the tower's would be cells are already occupied
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (!passability[p.x+i][p.y+j]) return false;
			}
		}

		// temporarily set the tower's cells to impassable
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				passability[p.x+i][p.y+j] = false;
			}
		}
		// try to find a valid path
		List<Point> path = Pathing.getPath(this, walkerStart, walkerDestination);
		// and set them back
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				passability[p.x+i][p.y+j] = true;
			}
		}
		
		// return true if we found a valid path
		return path != null;
	}

	/** 
	 * Fires a {@link Projectile} from the given {@link Tower} if there
	 * is a valid {@link Walker} in range to fire at 
	 */
	public boolean fireProjectile(Tower tower) {
		if (walkers.size() == 0) return false;
		Walker target = null;
		float targetDis = Float.MAX_VALUE;
		
		// find the closest Walker
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
		
		// FIRE!
		tower.setLastTarget(target);
		if (target == null) return false; // or not...
		
		Projectile p = tower.createProjectile();
		p.place(this, target, tower);
		if (!p.layer().destroyed()) {
			gridLayer.add(p.layer());
			projectiles.add(p);
		}
		return true;
	}

	/** Chains the given link ChainProjectile and fires a new sub-projectile */
	public boolean fireProjectile(ChainProjectile from) {
		if (walkers.size() == 0) return false;
		Walker target = null;
		
		// find the closest Walker that's not already been hit
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
		
		// FIRE!
		ChainProjectile p = from.createProjectile();
		p.place(this, target, from);
		gridLayer.add(p.layer());
		projectiles.add(p);
		return true;
	}

	/** 
	 * Deals damage from the given Tower's {@link Projectile} to the given Walker and calculates
	 * the appropriate splash damage 
	 */
	public void dealDamage(Tower source, Walker target, float damage, Vector hit) {
		if (source.splashRadius() == 0) {
			target.damage(damage);
			source.addBuffs(target);
		} else {
			// deal splash damage according to distance from the hit
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

	/** Returns the Walker (if any) that would be hit by a {@link Projectile} at this position */
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

	/** Returns true if the given position is off the Grid */
	public boolean isOutOfBounds(Vector position) {
		return isOutOfBounds(position.x, position.y);
	}

	/** Returns true if the given position is off the Grid */
	public boolean isOutOfBounds(float x, float y) {
		return x < 0 || y < 0 || x >= width() || y >= height();
	}

	/** Adds the given Effect to the Grid, which will manage it */
	public void addEffect(Effect effect) {
		effects.add(effect);
		effect.layer().setDepth(5);
		gridLayer.add(effect.layer());
	}

	// called when the Round is over
	private void onRoundCompleted(Round round) {
		round.winRound(state);
		Tutorial.trigger(Trigger.Defense_RoundOver);
	}

	/** Causes the player to lose a life */
	public void loseLife() {
		state.loseLife();
	}

	/** Adds the given number of points to the player's score */
	public void addPoints(int points) {
		state.addPoints(points);
	}
	
	/** Listener for when the Grid is double-clicked */
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
