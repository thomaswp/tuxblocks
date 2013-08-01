package tuxkids.tuxblocks.core.defense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.PlayN;
import playn.core.util.Clock;
import pythagoras.f.Vector;
import pythagoras.i.Point;
import tripleplay.particle.Emitter;
import tripleplay.particle.Particles;
import tripleplay.particle.TuxParticles;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.defense.projectile.ChainProjectile;
import tuxkids.tuxblocks.core.defense.projectile.Projectile;
import tuxkids.tuxblocks.core.defense.round.Level;
import tuxkids.tuxblocks.core.defense.round.Level.RoundCompletedListener;
import tuxkids.tuxblocks.core.defense.round.Round;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.walker.Walker;
import tuxkids.tuxblocks.core.effect.Effect;
import tuxkids.tuxblocks.core.utils.MultiList;

public class Grid extends PlayNObject implements RoundCompletedListener {

	private final static boolean SHOW_GRID = false;

	private int cellSize;
	private int rows, cols;
	private GroupLayer layer, gridLayer, overlayLayer;
	private ImageLayer gridSprite;
	private boolean[][] passability;
	private List<Walker> walkers = new ArrayList<Walker>();
	private List<Projectile> projectiles = new ArrayList<Projectile>();
	private List<Tower> towers = new ArrayList<Tower>();
	private List<Effect> effects = new ArrayList<Effect>();
	@SuppressWarnings("unchecked")
	private MultiList<GridObject> gridObjects = new MultiList<GridObject>(walkers, projectiles, towers, effects);
	private Point walkerStart, walkerDestination;
	private Tower toPlace;
	private ImageLayer toPlaceRadius;
	private List<Point> currentPath;
	private Level level;
	private float targetAlpha = 1;
	private int towerColor;
	private Particles particles;
	private GameState state;

	public Particles particles() {
		return particles;
	}
	
	public Level level() {
		return level;
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
		createGridSprite();

		particles = new TuxParticles();

		level = Level.generate();
		level.setRoundCompletedListener(this);
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

		Walker walker = level.update(delta);
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
	}

	public void paint(Clock clock) {
		int nObjects = gridObjects.size();
		for (int i = 0; i < nObjects; i++) {
			GridObject gridObject = gridObjects.get(i);
			gridObject.paint(clock);
		}
		particles.paint(clock);
	}

	private void refreshPath() {
		currentPath = Pathing.getPath(this, walkerStart, walkerDestination);
	}

	public void addWalker(Walker walker) {
		walkers.add(walker);
		gridLayer.add(walker.layerAddable());
	}

	private void createGridSprite() {
		if (gridSprite != null) {
			gridLayer.remove(gridSprite);
		}

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
		gridSprite = graphics().createImageLayer(image);
		//gridSprite.setAlpha(0.2f);
		gridLayer.add(gridSprite);
		gridSprite.setDepth(-1);
	}

	public Point getCell(float x, float y) {
		int r = Math.min(Math.max((int)y / cellSize, 0), rows - 1);
		int c = Math.min(Math.max((int)x / cellSize, 0), cols - 1);
		return new Point(r, c);
	}

	public Point getCell(float x, float y, float width, float height) {
		return getCell(x - width / 2 + cellSize() / 2, y - height / 2 + cellSize() / 2);
	}

	private float getPlaceX(float globalX) {
		float placeX = globalX - getGlobalTx(gridLayer);
		if (PlayN.platform().touch().hasTouch()) placeX -= width() / 20;
		return  placeX;
	}

	private float getPlaceY(float globalY) {
		float placeY = globalY - getGlobalTy(gridLayer);
		if (PlayN.platform().touch().hasTouch()) placeY -= width() / 20;
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

	@Override
	public void onRoundCompleted(Round round) {
		round.winRound(state);
	}

	public void loseLife() {
		state.loseLife();
	}

	public void addPoints(int points) {
		state.addPoints(points);
	}
}
